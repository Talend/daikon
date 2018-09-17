package org.talend.daikon.pattern;

public class PatternRegexUtils {

    public static final String REGEX_ESCAPE_PATTERN = "[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\]\\{\\}\\\\\\|]";

    public static String escapeCharacters(String current) {
        return current.replaceAll(REGEX_ESCAPE_PATTERN, "\\\\$0");
    }

}
