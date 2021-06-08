package org.talend.daikon.crypto.digest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * An implementation of PasswordDigester that uses Argon2 (Argon2id).
 */
public class Argon2PasswordDigester implements PasswordDigester {

    private static final int SALT_LENGTH = 16;

    private static final int HASH_LENGTH = 32;

    private static final int PARALLELISM = 1;

    private static final int MEMORY = 4096;

    private static final int ITERATIONS = 3;

    private final PasswordEncoder passwordEncoder;

    /**
     * Create Argon2PasswordDigester with the following defaults:
     * - Salt Length: 16
     * - Hash Length: 32
     * - Iterations: 3
     * - Parallelism: 1
     * - Memory: 4096 (Kb)
     */
    public Argon2PasswordDigester() {
        this(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, ITERATIONS);
    }

    /**
     * Create Argon2PasswordDigester with the following defaults:
     * - Salt Length: 16
     * - Hash Length: 32
     * 
     * @param parallelism the parallelism, e.g. 1
     * @param memory the memory in KB e.g. 4096
     * @param iterations the iterations, e.g. 3
     */
    public Argon2PasswordDigester(int parallelism, int memory, int iterations) {
        this(SALT_LENGTH, HASH_LENGTH, parallelism, memory, iterations);
    }

    /**
     * Create Argon2PasswordDigester
     * 
     * @param saltLength the salt length, e.g. 16
     * @param hashLength the hash length, e.g. 32
     * @param parallelism the parallelism, e.g. 1
     * @param memory the memory in KB e.g. 4096
     * @param iterations the iterations, e.g. 3
     */
    public Argon2PasswordDigester(int saltLength, int hashLength, int parallelism, int memory, int iterations) {
        passwordEncoder = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    public String digest(String password) throws Exception {
        return passwordEncoder.encode(password);
    }

    public boolean validate(String password, String digest) {
        return passwordEncoder.matches(password, digest);
    }
}
