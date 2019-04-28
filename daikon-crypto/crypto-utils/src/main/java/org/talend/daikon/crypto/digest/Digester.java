package org.talend.daikon.crypto.digest;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.crypto.KeySource;

public class Digester {

    private final DigestSource digestSource;

    private final KeySource keySource;

    private final String delimiter;

    public Digester(KeySource keySource, String delimiter) {
        this.keySource = keySource;
        this.delimiter = delimiter;
        this.digestSource = DigestUtils::sha256Hex;
    }

    private String saltValue(String value, String salt) {
        return salt + delimiter + digestSource.digest(value);
    }

    public String digest(String value) throws Exception {
        return saltValue(value, digestSource.digest(new String(keySource.getKey())));
    }

    public boolean validate(String value, String digest) {
        String salt = StringUtils.substringBefore(digest, delimiter);
        return saltValue(value, salt).equals(digest);
    }
}

