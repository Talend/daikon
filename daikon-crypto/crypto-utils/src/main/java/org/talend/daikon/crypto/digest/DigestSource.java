package org.talend.daikon.crypto.digest;

@FunctionalInterface
public interface DigestSource {

    String digest(String value, byte[] salt);
}
