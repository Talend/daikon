package org.talend.daikon.pattern.word;

public class WordPatternToRegexConstants {

    // Iso to Character.isDigit
    public static final String DIGIT = "\\p{Nd}";

    // almost iso to Character.isIdeographic
    public static final String IDEOGRAM = "\\p{script=Han}";

    // Iso to Character.getType(codePoint) == Character.UPPERCASE_LETTER
    public static final String UPPER_CHAR = "\\p{Lu}";

    // Iso to Character.getType(codePoint) == Character.LOWERCASE_LETTER
    public static final String LOWER_CHAR = "\\p{Ll}";

    public static final String CHAR = WordPatternToRegexConstants.UPPER_CHAR + WordPatternToRegexConstants.LOWER_CHAR;

    public static final String REGEX_ESCAPE_PATTERN = "[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\]\\{\\}\\\\\\|]";

}
