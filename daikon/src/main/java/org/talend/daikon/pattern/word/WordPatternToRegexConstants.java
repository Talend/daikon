package org.talend.daikon.pattern.word;

import org.talend.daikon.pattern.PatternRegexUtils;
import org.talend.daikon.pattern.character.CharPatternToRegexConstants;

class WordPatternToRegexConstants {

    // Iso to Character.isDigit
    static final String DIGIT = "\\p{Nd}";

    // almost iso to Character.isIdeographic
    static final String IDEOGRAM = "\\p{script=Han}";

    static final String KATAKANA = PatternRegexUtils.buildRegex(CharPatternToRegexConstants.HALFWIDTH_KATAKANA.getRegex(),
            CharPatternToRegexConstants.FULLWIDTH_KATAKANA.getRegex());

    // Iso to Character.getType(codePoint) == Character.UPPERCASE_LETTER
    static final String UPPER_CHAR = "\\p{Lu}";

    // Iso to Character.getType(codePoint) == Character.LOWERCASE_LETTER
    static final String LOWER_CHAR = "\\p{Ll}";

    static final String CHAR = WordPatternToRegexConstants.UPPER_CHAR + WordPatternToRegexConstants.LOWER_CHAR;

}
