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

import io.jsonwebtoken.Identifiable;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.JwkSetBuilder;
import io.jsonwebtoken.security.Jwks;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractAwsKeyResolverTests {
    private static final AtomicInteger TEST_PORT = new AtomicInteger(24542);
    protected AwsElbServer keyServer;
    protected JwkSet jwks;
    private Object[][] keyIds;

    @BeforeClass
    public void setup() throws Exception {
        List<KeyPair> keyPairs = List.of(Jwts.SIG.ES256.keyPair().build(), Jwts.SIG.ES384.keyPair().build(),
                                         Jwts.SIG.ES512.keyPair().build());
        JwkSetBuilder privateJwks = Jwks.set();
        JwkSetBuilder publicJwks = Jwks.set();
        keyPairs.forEach(p -> {
            privateJwks.add(Jwks.builder().keyPair(p).idFromThumbprint().build());
            publicJwks.add(Jwks.builder().key(p.getPublic()).idFromThumbprint().build());
        });
        this.jwks = privateJwks.build();

        this.keyIds = new Object[keyPairs.size()][];
        for (int i = 0; i < this.jwks.getKeys().size(); i++) {
            this.keyIds[i] =
                    new Object[] { this.jwks.getKeys().stream().skip(i).map(Identifiable::getId).findFirst().orElse(null) };
        }

        this.keyServer = new AwsElbServer(TEST_PORT.getAndIncrement(), publicJwks.build());
        this.keyServer.start();
    }

    @AfterMethod
    public void testCleanup() {
        AwsElbKeyUrlRegistry.reset();
    }

    @AfterClass
    public void teardown() throws Exception {
        this.keyServer.stop();
    }

    @DataProvider(name = "keyIds")
    public Object[][] keyIds() {
        return this.keyIds;
    }
}
