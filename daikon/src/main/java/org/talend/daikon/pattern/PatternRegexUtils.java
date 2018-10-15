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

    // Utils to remove middle parenthesis of regexes
    public static String buildRegex(String pattern1, String pattern2) {
        return pattern1.substring(0, pattern1.length() - 1) + "|" + pattern2.substring(1);
    }

}
