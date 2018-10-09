package org.talend.daikon.pattern.word;

import org.talend.daikon.pattern.character.CharPatternToRegexConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Define the set of all supported word patterns (both case-insensitive and case-sensitive)
 */
public enum WordPattern {
    // @formatter:off
    WORD(
            "[Word]",
            "[" + WordPatternToRegexConstants.CHAR + "]{2,}",
            WordPatternToRegexConstants.UPPER_CHAR + "[" + WordPatternToRegexConstants.LOWER_CHAR + "]{1,}"),
    LOWER_WORD(
            "[word]",
            "[" + WordPatternToRegexConstants.CHAR + "]{2,}",
            "[" + WordPatternToRegexConstants.LOWER_CHAR + "]{2,}"),
    UPPER_WORD(
            "[WORD]",
            "[" + WordPatternToRegexConstants.CHAR + "]{2,}",
            "[" + WordPatternToRegexConstants.UPPER_CHAR + "]{2,}"),
    LOWER_CHAR(
            "[char]",
            "[" + WordPatternToRegexConstants.CHAR + "]",
            "[" + WordPatternToRegexConstants.LOWER_CHAR + "]"),
    UPPER_CHAR(
            "[Char]",
            "[" + WordPatternToRegexConstants.CHAR + "]",
            "[" + WordPatternToRegexConstants.UPPER_CHAR + "]"),
    NUMBER(
            "[number]",
            "[" + WordPatternToRegexConstants.DIGIT + "]{2,}"),
    DIGIT(
            "[digit]",
            "[" + WordPatternToRegexConstants.DIGIT + "]"),
    ALPHANUMERIC(
            "[alnum]",
            "[" + WordPatternToRegexConstants.DIGIT + "|" + WordPatternToRegexConstants.CHAR + "]{2,}"),
    IDEOGRAM(
            "[Ideogram]",
            "[" + WordPatternToRegexConstants.IDEOGRAM + "]"),
    IDEOGRAM_SEQUENCE(
            "[IdeogramSeq]",
            "[" + WordPatternToRegexConstants.IDEOGRAM + "]{2,}"),
    HANGUL(
            "[hangul]",
            CharPatternToRegexConstants.HANGUL.getRegex()),
    HANGUL_SEQUENCE(
            "[hangulSeq]",
            CharPatternToRegexConstants.HANGUL.getRegex() + "{2,}"),
    HIRAGANA(
            "[hira]",
            CharPatternToRegexConstants.HIRAGANA.getRegex()),
    HIRAGANA_SEQUENCE(
            "[hiraSeq]",
            CharPatternToRegexConstants.HIRAGANA.getRegex() + "{2,}"),
    KATAKANA(
            "[kata]",
            WordPatternToRegexConstants.KATAKANA),
    KATAKANA_SEQUENCE(
            "[kataSeq]",
            WordPatternToRegexConstants.KATAKANA + "{2,}");

    // @formatter:on
    private static final Map<String, WordPattern> lookup = new HashMap<>();

    static {
        for (WordPattern value : WordPattern.values()) {
            lookup.put(value.getPattern(), value);
        }
    }

    public static WordPattern get(final String pattern) {
        return lookup.get(pattern);
    }

    private final String pattern;

    private final String caseInsensitive;

    private final String caseSensitive;

    WordPattern(final String pattern, final String caseInsensitive, final String caseSensitive) {
        this.pattern = pattern;
        this.caseInsensitive = caseInsensitive;
        this.caseSensitive = caseSensitive;
    }

    WordPattern(final String pattern, final String caseInsensitive) {
        this(pattern, caseInsensitive, caseInsensitive);
    }

    public String getPattern() {
        return pattern;
    }

    public String getCaseInsensitive() {
        return caseInsensitive;
    }

    public String getCaseSensitive() {
        return caseSensitive;
    }

}
