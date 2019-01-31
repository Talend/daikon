package org.talend.daikon.pattern.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum CharPattern {

    DIGIT('9', CharPatternToRegexConstants.DIGIT),

    LOWER_LATIN('a', CharPatternToRegexConstants.LOWER_LATIN),

    LOWER_LATIN_RARE('a', CharPatternToRegexConstants.LOWER_LATIN_RARE),

    UPPER_LATIN('A', CharPatternToRegexConstants.UPPER_LATIN),

    UPPER_LATIN_RARE('A', CharPatternToRegexConstants.UPPER_LATIN_RARE),

    FULLWIDTH_DIGIT('9', CharPatternToRegexConstants.FULLWIDTH_DIGIT),

    FULLWIDTH_LOWER_LATIN('a', CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN),

    FULLWIDTH_UPPER_LATIN('A', CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN),

    HIRAGANA('H', CharPatternToRegexConstants.HIRAGANA),

    HALFWIDTH_KATAKANA('k', CharPatternToRegexConstants.HALFWIDTH_KATAKANA),

    FULLWIDTH_KATAKANA('K', CharPatternToRegexConstants.FULLWIDTH_KATAKANA),

    KANJI('C', CharPatternToRegexConstants.KANJI),

    KANJI_RARE('C', CharPatternToRegexConstants.KANJI_RARE),

    HANGUL('G', CharPatternToRegexConstants.HANGUL);

    private Character replaceChar;

    private CharPatternToRegexConstants pattern;

    // Useful for quick contain
    private Set<Integer> codePointSet = new HashSet<>();

    // Useful for quick get
    private List<Integer> codePointList = new ArrayList<>();

    private static final Map<Character, CharPattern> lookup = new HashMap<>();

    static {
        for (CharPattern value : CharPattern.values()) {
            lookup.put(value.getReplaceChar(), value);
        }
    }

    public static CharPattern get(final String pattern) {
        return lookup.get(pattern);
    }

    CharPattern(char replace, CharPatternToRegexConstants pattern) {
        replaceChar = replace;
        this.pattern = pattern;
        buildCharacters(pattern.getRange());
    }

    private void buildCharacters(String pattern) {
        for (String subPattern : pattern.substring(1, pattern.length() - 1).split("\\|")) {
            if (isInterval(subPattern)) {
                String[] startEnd = subPattern.split("-");
                Integer start = getCodePointAt(startEnd[0].substring(1));
                Integer end = getCodePointAt(startEnd[1].substring(0, startEnd[1].length() - 1));
                for (int i = start; i <= end; i++) {
                    fillSetAndList(i, subPattern);
                }
            } else {
                Integer codePoint = getCodePointAt(subPattern);
                fillSetAndList(codePoint, subPattern);
            }
        }
    }

    private void fillSetAndList(int i, String subPattern) {
        if (codePointSet.contains(i))
            throw new IllegalArgumentException("Pattern " + subPattern + " is in conflict with another pattern");
        codePointSet.add(i);
        codePointList.add(i);
    }

    private boolean isInterval(String subPattern) {
        return subPattern.contains("-");
    }

    private Integer getCodePointAt(String s) {
        return Integer.parseInt(s.substring(3, s.length() - 1), 16);
    }

    public Character getReplaceChar() {
        return replaceChar;
    }

    public CharPatternToRegexConstants getPattern() {
        return pattern;
    }

    public boolean contains(Integer codePoint) {
        return codePointSet.contains(codePoint);
    }

    public Integer getCodePointAt(int position) {
        return codePointList.get(position);
    }

    public int getCodePointSize() {
        return codePointList.size();
    }
}
