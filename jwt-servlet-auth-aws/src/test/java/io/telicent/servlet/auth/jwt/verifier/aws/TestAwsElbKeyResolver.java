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
package io.telicent.servlet.auth.jwt.verifier.aws;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Jwk;
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import org.apache.commons.lang3.Strings;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static org.mockito.Mockito.*;

public class TestAwsElbKeyResolver extends AbstractAwsKeyResolverTests {

    public static final String TEST_AWS_REGION = "eu-west-1";

    private Key verifyKeyResolution(String region, String keyId) {
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(region);
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        return resolver.locate(header);
    }

    @BeforeMethod
    public void testSetup() {
        // August 2026 - AWS changed something that means the previous Test Key ID we used no longer resolved (produces
        //               a 403 error) so have to use a fake key server instead which is less than ideal but we don't
        //               have a good way to reliably get an AWS Key ID that we can reliably access outside AWS infra
        AwsElbKeyUrlRegistry.register(TEST_AWS_REGION, this.keyServer.getUrl() + "/%s");
    }

    /**
     * Gets the Test Key ID in use
     *
     * @return Test Key ID
     */
    public String getTestKeyId() {
        return (String) this.keyIds()[0][0];
    }

    public String prepareJwt(UnaryOperator<JwtBuilder> customiser) {
        String testKeyId = getTestKeyId();
        JwtBuilder builder = Jwts.builder()
                                 .header()
                                 .keyId(testKeyId)
                                 .and()
                                 .subject("test")
                                 .expiration(Date.from(
                                         Instant.now().plusSeconds(15)))
                                 .signWith(this.jwks.getKeys()
                                                    .stream()
                                                    .filter(k -> Objects.equals(k.getId(), testKeyId))
                                                    .map(Jwk::toKey)
                                                    .findFirst()
                                                    .orElse(null));
        return customiser.apply(builder).compact();
    }

    @Test(expectedExceptions = InvalidKeyException.class)
    public void givenInvalidRegion_whenVerifying_thenError() {
        // Given, When and Then
        verifyKeyResolution("bad-region", "bad-key-id");
    }

    @Test
    public void givenLocator_whenCreatingSignedVerifier_thenToStringContainsRegionDetails() {
        // Given
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(TEST_AWS_REGION);

        // When
        SignedJwtVerifier verifier = new SignedJwtVerifier(resolver);

        // Then
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), "verificationMethod=Locator"));
        Assert.assertTrue(Strings.CS.contains(verifier.toString(), resolver.toString()));
    }

    @Test
    public void givenValidRegionAndKey_whenResolving_thenSuccess() {
        // Given and When
        Key key = verifyKeyResolution(TEST_AWS_REGION, getTestKeyId());

        // Then
        Assert.assertNotNull(key);
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void givenExpiredJwtAndWrappedLocator_whenVerifying_thenError() {
        // Given
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(TEST_AWS_REGION);
        SignedJwtVerifier verifier = new SignedJwtVerifier(resolver);
        String jwt = prepareJwt(b -> b.expiration(Date.from(Instant.now().minusSeconds(10))));

        // When and Then
        verifier.verify(jwt);
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void givenExpiredJwt_whenResolving_thenError() {
        // Given
        AwsElbJwtVerifier verifier = new AwsElbJwtVerifier(TEST_AWS_REGION);
        String jwt = prepareJwt(b -> b.expiration(Date.from(Instant.now().minusSeconds(10))));

        // When and Then
        verifier.verify(jwt);
    }

    @Test
    public void givenExpiredJwtAndVerifierWithMaxClockSkew_whenVerifying_thenSuccess() {
        // Given
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(TEST_AWS_REGION);
        SignedJwtVerifier verifier = new SignedJwtVerifier(Jwts.parser().keyLocator(resolver)
                                                               // We know the test JWT has expired but want to verify that if we ignore the expiry we can
                                                               // successfully verify.  We're setting the largest possible clock skew here to achieve this.
                                                               .clockSkewSeconds(Long.MAX_VALUE / 1000).build());
        String jwt = prepareJwt(b -> b.expiration(Date.from(Instant.now().minusSeconds(10))));

        // When and Then
        Jws<Claims> jws = verifier.verify(jwt);
        Assert.assertNotNull(jws);
    }

    @Test(expectedExceptions = InvalidKeyException.class, expectedExceptionsMessageRegExp = ".*no Key ID \\(kid\\) in Header.*")
    public void givenJwtHeaderWithNoKeyId_whenLocatingKey_thenFails() {
        // Given
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(null);
        AwsElbKeyResolver locator = new AwsElbKeyResolver(TEST_AWS_REGION);

        // When and Then
        locator.locate(header);
    }
}
