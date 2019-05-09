package org.talend.daikon.crypto.digest;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestSources {

    public static DigestSource sha256() {
        return DigestUtils::sha256Hex;
    }
}
