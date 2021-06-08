package org.talend.daikon.crypto.digest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * An implementation of PasswordDigester that uses BCrypt with a default strength of 10. Passwords are limited to
 * 72 bytes.
 */
public class BCryptPasswordDigester implements PasswordDigester {

    private final PasswordEncoder passwordEncoder;

    /**
     * Create BCryptPasswordDigester with a default strength of 10, and a default "$2a" algorithm.
     * This should meet most requirements.
     */
    public BCryptPasswordDigester() {
        this(10);
    }

    /**
     * Create BCryptPasswordDigester with the specified strength. Note OWASP recommends "10" as the minimum strength.
     * It uses the "$2a" algorithm by default.
     * 
     * @param strength the strength parameter, can be between 4 and 31
     */
    public BCryptPasswordDigester(int strength) {
        this(strength, "$2a");
    }

    /**
     * Create BCryptPasswordDigester with the specified strength. Note OWASP recommends "10" as the minimum strength.
     *
     * @param strength the strength parameter, can be between 4 and 31
     * @param algorithm the BCrypt version, can be "$2a", "$2y", "$2b". If null defaults to "$2a".
     */
    public BCryptPasswordDigester(int strength, String algorithm) {
        BCryptPasswordEncoder.BCryptVersion version = BCryptPasswordEncoder.BCryptVersion.$2A;
        if (algorithm != null) {
            version = BCryptPasswordEncoder.BCryptVersion.valueOf(algorithm.toUpperCase(Locale.US));
        }
        passwordEncoder = new BCryptPasswordEncoder(version, strength);
    }

    public String digest(String password) throws Exception {
        if (password != null && password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("The password is limited to a maximum of 72 bytes");
        }
        return passwordEncoder.encode(password);
    }

    public boolean validate(String password, String digest) {
        if (password != null && password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("The password is limited to a maximum of 72 bytes");
        }
        return passwordEncoder.matches(password, digest);
    }
}
