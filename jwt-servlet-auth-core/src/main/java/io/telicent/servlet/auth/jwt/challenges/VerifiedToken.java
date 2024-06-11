package io.telicent.servlet.auth.jwt.challenges;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Objects;

/**
 * A verified JSON Web Token (JWT)
 *
 * @param rawToken      The raw JWT
 * @param verifiedToken The parsed and verified JWT
 */
public record VerifiedToken(String rawToken, Jws<Claims> verifiedToken) {
    /**
     * Creates a new verified token
     * @param rawToken The raw token
     * @param verifiedToken The verified token
     */
    public VerifiedToken(String rawToken, Jws<Claims> verifiedToken) {
        this.rawToken = Objects.requireNonNull(rawToken, "rawToken cannot be null");
        this.verifiedToken = Objects.requireNonNull(verifiedToken, "verifiedToken cannot be null");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VerifiedToken{");
        sb.append("rawToken='").append(rawToken).append('\'');
        sb.append(", verifiedToken=").append(verifiedToken);
        sb.append('}');
        return sb.toString();
    }
}
