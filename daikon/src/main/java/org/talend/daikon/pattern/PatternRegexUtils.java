package org.talend.daikon.pattern;

public class PatternRegexUtils {

    public static final String REGEX_ESCAPE_PATTERN = "[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\]\\{\\}\\\\\\|]";

    public static String escapeCharacters(String current) {
        return escapeCharacters(current, false);
    }

    public static String escapeCharacters(String current, boolean isForJavaScript) {
        String escaped = current.replaceAll(REGEX_ESCAPE_PATTERN, "\\\\$0");
        // For Javascript, '/' needs also to be escaped
        if (isForJavaScript) {
            escaped = escaped.replaceAll("/", "\\\\$0");
        }
        return escaped;
    }
}
