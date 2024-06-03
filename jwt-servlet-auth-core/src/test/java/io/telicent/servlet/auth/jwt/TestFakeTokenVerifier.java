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
package io.telicent.servlet.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.telicent.servlet.auth.jwt.verification.FakeTokenVerifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;

public class TestFakeTokenVerifier {

    @Test
    public void fake_token_verifier_01() {
        FakeTokenVerifier verifier = new FakeTokenVerifier();
        Jws<Claims> jws = verifier.verify("test");
        Assert.assertEquals(jws.getPayload().getSubject(), "test");
    }

    @Test
    public void fake_token_verifier_02() {
        FakeTokenVerifier verifier = new FakeTokenVerifier();
        Jws<Claims> jws = verifier.verify("foo");
        Assert.assertEquals(jws.getPayload().getSubject(), "foo");
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void fake_token_verifier_expired_01() {
        FakeTokenVerifier verifier = new FakeTokenVerifier(-1, ChronoUnit.MINUTES);
        verifier.verify("test");
    }
}
