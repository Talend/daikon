package org.talend.daikon.pattern.word;

public class WordPatternToRegexConstants {

    // Iso to Character.isDigit
    public static final String DIGIT = "\\p{Nd}";

    // Iso to Character.isLetter
    public static final String LETTER = "\\p{L}";

    // Iso to Character.isIdeographic
    public static final String IDEOGRAM = "\\p{InHangul_Jamo}|\\p{InHangul_Compatibility_Jamo}|\\p{InHangul_Syllables}|\\p{script=Han}";

    public static final String CHAR = WordPatternToRegexConstants.LETTER + "&&[^" + IDEOGRAM + "]"; // Except ideograms

    // Iso to Character.isUpperCase
    public static final String UPPER_CHAR = "\\p{Lu}";

    public static final String LOWER_CHAR = CHAR + "&&[^" // Except uppers because only lower won't contain insensitive letters
            + WordPatternToRegexConstants.UPPER_CHAR + "]";

    public static final String REGEX_ESCAPE_PATTERN = "[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\]\\{\\}\\\\\\|]";

}
