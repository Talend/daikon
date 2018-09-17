package org.talend.daikon.pattern.word;

class WordPatternToRegexConstants {

    // Iso to Character.isDigit
    static final String DIGIT = "\\p{Nd}";

    // almost iso to Character.isIdeographic
    static final String IDEOGRAM = "\\p{script=Han}";

    // Iso to Character.getType(codePoint) == Character.UPPERCASE_LETTER
    static final String UPPER_CHAR = "\\p{Lu}";

    // Iso to Character.getType(codePoint) == Character.LOWERCASE_LETTER
    static final String LOWER_CHAR = "\\p{Ll}";

    static final String CHAR = WordPatternToRegexConstants.UPPER_CHAR + WordPatternToRegexConstants.LOWER_CHAR;

}
