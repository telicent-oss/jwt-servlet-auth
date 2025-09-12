/**
 * Copyright (C) Telicent Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.telicent.servlet.auth.jwt.verification;

import io.jsonwebtoken.security.*;
import io.jsonwebtoken.security.SecurityException;
import io.telicent.servlet.auth.jwt.errors.KeyLoadException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities relating to keys
 */
public class KeyUtils {

    /**
     * RSA Key Algorithm
     */
    public static final String RSA = "RSA";
    /**
     * EC Key Algorithm
     */
    public static final String EC = "EC";

    private KeyUtils() {
    }

    /**
     * Loads an RSA Public Key expressed in PEM encoding
     *
     * @param pemEncoding PEM encoded RSA Public Key
     * @return Public Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static Key loadRsaPublicKeyFromPem(String pemEncoding) throws KeyLoadException {
        byte[] encoded = getKeyBytes(pemEncoding);

        return loadPublicKey(RSA, encoded);
    }

    /**
     * Loads a public key
     *
     * @param algorithm Key algorithm
     * @param encoded   Bytes encoding a X509 specification for the public key
     * @return Public Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static PublicKey loadPublicKey(String algorithm, byte[] encoded) throws KeyLoadException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);
        } catch (Throwable e) {
            throw new KeyLoadException(e);
        }
    }

    /**
     * Loads a public key from an input stream
     *
     * @param algorithm Algorithm
     * @param input     Input stream
     * @return Public Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static PublicKey loadPublicKey(String algorithm, InputStream input) throws KeyLoadException {
        try {
            List<String> lines = IOUtils.readLines(input, StandardCharsets.US_ASCII);
            String pemEncoding = StringUtils.join(lines, "\n");
            return loadPublicKey(algorithm, getKeyBytes(pemEncoding));
        } catch (UncheckedIOException e) {
            throw new KeyLoadException("Failed to read key input", e);
        }
    }

    /**
     * Loads a public key from a file
     *
     * @param algorithm Algorithm
     * @param keyFile   Key file
     * @return Public Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static PublicKey loadPublicKey(String algorithm, File keyFile) throws KeyLoadException {
        try {
            List<String> lines = Files.readAllLines(keyFile.toPath(), StandardCharsets.US_ASCII);
            String pemEncoding = StringUtils.join(lines, "\n");
            return loadPublicKey(algorithm, getKeyBytes(pemEncoding));
        } catch (IOException e) {
            throw new KeyLoadException("Failed to read key input", e);
        }
    }

    /**
     * Strips out the PEM format guard lines to produce only the Base 64 encoded key bytes.  This assumes that the given
     * encoding is only the public key and not from a PEM file that contained multiple keys
     *
     * @param pemEncoding PEM encoding
     * @return Decoded key bytes
     */
    private static byte[] getKeyBytes(String pemEncoding) {
        String rawKey = pemEncoding.lines().filter(l -> !l.startsWith("---")).collect(Collectors.joining());
        return Base64.getDecoder().decode(rawKey);
    }

    /**
     * Loads an ECDSA Public Key expressed in PEM encoding
     *
     * @param pemEncoding PEM encoded ECDSA Public Key
     * @return Public Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static Key loadEcdsaPublicKeyFromPem(String pemEncoding) throws KeyLoadException {
        byte[] encoded = getKeyBytes(pemEncoding);

        return loadPublicKey(EC, encoded);
    }

    /**
     * Loads a secret key
     * <p>
     * Allows the secret key file to contain either a raw byte sequence, or Base64 encoded.  Assumes no additional
     * padding or other encoding within the file.
     * </p>
     *
     * @param keyFile Secret Key File
     * @return Secret Key
     * @throws KeyLoadException Thrown if the key cannot be loaded
     */
    public static SecretKey loadSecretKey(File keyFile) throws KeyLoadException {
        if (keyFile == null) {
            throw new KeyLoadException("No secret key file provided");
        }
        try (FileInputStream input = new FileInputStream(keyFile)) {
            byte[] data = IOUtils.toByteArray(input);
            return loadSecretKey(data);
        } catch (IOException e) {
            throw new KeyLoadException("Secret Key File '" + keyFile.getAbsolutePath() + "' was not a valid file");
        }
    }

    /**
     * Loads a secret key
     * <p>
     * Allows for either a raw byte sequence, or Base64 encoded.
     * </p>
     *
     * @param encoded Encoded key bytes
     * @return Secret Key
     * @throws KeyLoadException Thrown if the key cannot be loaded or is too weak
     */
    public static SecretKey loadSecretKey(byte[] encoded) throws KeyLoadException {
        if (encoded == null) {
            throw new KeyLoadException("No encoded key bytes provided");
        }
        try {
            encoded = attemptBase64Decode(encoded);
            return Keys.hmacShaKeyFor(encoded);
        } catch (WeakKeyException e) {
            throw new KeyLoadException(e.getMessage());
        }
    }

    /**
     * Loads a JWKS from a file
     *
     * @param f File
     * @return JWKS
     * @throws KeyLoadException Thrown if the JWKS cannot be loaded successfully
     */
    public static JwkSet loadJwks(File f) throws KeyLoadException {
        JwkSet jwks;
        try (FileInputStream input = new FileInputStream(f)) {
            jwks = Jwks.setParser().build().parse(input);
        } catch (SecurityException e) {
            throw new KeyLoadException("JWKS file '" + f.getAbsolutePath() + "' contained an invalid key set", e);
        } catch (IOException e) {
            throw new KeyLoadException(
                    "JWKS file '" + f.getAbsolutePath() + "' that could not be read successfully",
                    e);
        }
        return jwks;
    }

    /**
     * Loads a JWKS from an HTTP URI
     *
     * @param jwksURI HTTP URI
     * @param client  HTTP Client
     * @return JWKS
     * @throws KeyLoadException Thrown if the JWKS cannot be loaded successfully
     */
    public static JwkSet loadJwks(URI jwksURI, HttpClient client) throws KeyLoadException {
        if (jwksURI == null) {
            throw new KeyLoadException("JWKS URI was not valid");
        }
        if (client == null) {
            throw new KeyLoadException("A HTTP Client must be provided to use when loading the JWKS");
        }
        if (!Strings.CS.equalsAny(jwksURI.getScheme(), "http", "https")) {
            throw new KeyLoadException("JWKS URI must use http/https scheme");
        }
        HttpRequest request = HttpRequest.newBuilder(jwksURI).GET().build();
        try {
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return Jwks.setParser().build().parse(response.body());
        } catch (SecurityException e) {
            throw new KeyLoadException("JWKS URI " + jwksURI + " returned an invalid key set");
        } catch (IOException e) {
            throw new KeyLoadException(
                    "JWKS URI " + jwksURI + " could not be read successfully");
        } catch (InterruptedException e) {
            throw new KeyLoadException(
                    "Interrupted while attempting to read from JWKS URI " + jwksURI);
        }
    }

    /**
     * Attempts to decode a potential Base64 encoded byte sequence.
     * <p>
     * If it fails (Illegal Argument Exception), return the original raw sequence.
     * </p>
     *
     * @param data Encoded key bytes
     * @return byte sequence
     */
    private static byte[] attemptBase64Decode(final byte[] data) {
        try {
            return Base64.getDecoder().decode(data);
        } catch (IllegalArgumentException e) {
            return data;
        }
    }
}
