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
import io.telicent.servlet.auth.jwt.verification.SignedJwtVerifier;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.Key;

import static org.mockito.Mockito.*;

public class TestAwsElbKeyResolver {

    public static final String TEST_AWS_REGION = "eu-west-1";

    private static final String TEST_AWS_ELB_KEY_ID = "00770e84-91d7-4a1d-bab7-2fbe9de4b5ab";

    private static final String TEST_JWT =
            "eyJ0eXAiOiJKV1QiLCJraWQiOiIwMDc3MGU4NC05MWQ3LTRhMWQtYmFiNy0yZmJlOWRlNGI1YWIiLCJhbGciOiJFUzI1NiIsImlzcyI6Imh0dHBzOi8vY29nbml0by1pZHAuZXUtd2VzdC0xLmFtYXpvbmF3cy5jb20vZXUtd2VzdC0xX0hSMWMxWGozTiIsImNsaWVudCI6IjU2NXZqOW42ZnQ5b2ZicnFvbTA5NW10cWIiLCJzaWduZXIiOiJhcm46YXdzOmVsYXN0aWNsb2FkYmFsYW5jaW5nOmV1LXdlc3QtMTowOTg2Njk1ODk1NDE6bG9hZGJhbGFuY2VyL2FwcC9UZWxpYy1BcHBsaS02TDhBMFhPVkJQOFgvMGQ0MzA0NWI3NmVlNjFhNSIsImV4cCI6MTY1NDYxNjkxOH0=.eyJzdWIiOiI1Zjc0ZmNjYS1jZjBhLTRjZGQtOGM4ZC1iZmM4NjhjYWY0NGMiLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJlbWFpbCI6InRvbUB0ZWxpY2VudC5pbyIsInVzZXJuYW1lIjoiNWY3NGZjY2EtY2YwYS00Y2RkLThjOGQtYmZjODY4Y2FmNDRjIiwiZXhwIjoxNjU0NjE2OTE4LCJpc3MiOiJodHRwczovL2NvZ25pdG8taWRwLmV1LXdlc3QtMS5hbWF6b25hd3MuY29tL2V1LXdlc3QtMV9IUjFjMVhqM04ifQ==.SdlxvcVug6g4xM6seIUIfsq56CW4A9aZynvlWmT3ry939KgrZc9JXoYe9zBVptPxs_7FHkFzBSfocAp4A7I1Mg==";

    private Key verifyKeyResolution(String region, String keyId) {
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(region);
        JwsHeader header = mock(JwsHeader.class);
        when(header.getKeyId()).thenReturn(keyId);
        return resolver.locate(header);
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
        Assert.assertTrue(StringUtils.contains(verifier.toString(), "verificationMethod=Locator"));
        Assert.assertTrue(StringUtils.contains(verifier.toString(), resolver.toString()));
    }

    @Test
    public void givenValidRegionAndKey_whenResolving_thenSuccess() {
        // Given and When
        Key key = verifyKeyResolution(TEST_AWS_REGION, TEST_AWS_ELB_KEY_ID);

        // Then
        Assert.assertNotNull(key);
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void givenExpiredJwtAndWrappedLocator_whenVerifying_thenError() {
        // Given
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(TEST_AWS_REGION);
        SignedJwtVerifier verifier = new SignedJwtVerifier(resolver);

        // When and Then
        verifier.verify(TEST_JWT);
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void givenExpiredJwt_whenResolving_thenError() {
        // Given
        AwsElbJwtVerifier verifier = new AwsElbJwtVerifier(TEST_AWS_REGION);

        // When and Then
        verifier.verify(TEST_JWT);
    }

    @Test
    public void givenExpiredJwtAndVerifierWithMaxClockSkew_whenVerifying_thenSuccess() {
        // Given
        AwsElbKeyResolver resolver = new AwsElbKeyResolver(TEST_AWS_REGION);
        SignedJwtVerifier verifier = new SignedJwtVerifier(Jwts.parser().keyLocator(resolver)
                                                               // We know the test JWT has long expired but want to verify that if we ignore the expiry we can
                                                               // successfully verify.  We're setting the largest possible clock skew here to achieve this.
                                                               .clockSkewSeconds(Long.MAX_VALUE / 1000).build());

        // When and Then
        Jws<Claims> jws = verifier.verify(TEST_JWT);
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
