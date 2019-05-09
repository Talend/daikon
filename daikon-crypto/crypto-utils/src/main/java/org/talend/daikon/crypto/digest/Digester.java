package org.talend.daikon.crypto.digest;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.crypto.KeySource;
import org.talend.daikon.crypto.KeySources;

public class Digester {

    private final DigestSource digestSource;

    private final KeySource keySource;

    private final String delimiter;

    public Digester(DigestSource digestSource) {
        this(KeySources.random(8), "", digestSource);
    }

    public Digester(KeySource keySource) {
        this(keySource, "", DigestSources.sha256());
    }

    public Digester(KeySource keySource, DigestSource digestSource) {
        this(keySource, "", digestSource);
    }

    public Digester(KeySource keySource, String delimiter, DigestSource digestSource) {
        this.keySource = keySource;
        this.delimiter = delimiter;
        this.digestSource = digestSource;
    }

    private String saltValue(String value, String salt) {
        return digestSource.digest(salt) + delimiter + digestSource.digest(value);
    }

    public String digest(String value) throws Exception {
        return saltValue(value, digestSource.digest(new String(keySource.getKey())));
    }

    public boolean validate(String value, String digest) {
        String salt = StringUtils.substringBefore(digest, delimiter);
        return (salt + delimiter + digestSource.digest(value)).equals(digest);
    }
}
