package org.talend.daikon.crypto;

import java.util.Base64;
import java.util.function.Function;

public class EncodingUtils {

    public static final Function<byte[], String> BASE64_ENCODER = bytes -> Base64.getEncoder().encodeToString(bytes);

    public static final Function<byte[], byte[]> BASE64_DECODER = bytes -> Base64.getDecoder().decode(bytes);

    public static final String ENCODING = "UTF-8";

    private EncodingUtils() {
    }
}
