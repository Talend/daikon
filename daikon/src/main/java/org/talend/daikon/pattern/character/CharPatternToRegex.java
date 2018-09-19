package org.talend.daikon.pattern.character;

import static org.talend.daikon.pattern.PatternRegexUtils.escapeCharacters;

public class CharPatternToRegex {

    private CharPatternToRegex() {
        // Do not instantiate
    }

    public static String toRegex(String pattern) {
        StringBuilder stringBuilder = new StringBuilder("^");
        for (int pos = 0; pos < pattern.length(); pos++) {
            int codePoint = pattern.codePointAt(pos);
            switch (codePoint) {
            case 'h':
                stringBuilder.append(CharPatternToRegexConstants.LOWER_HIRAGANA);
                break;
            case 'H':
                stringBuilder.append(CharPatternToRegexConstants.UPPER_HIRAGANA);
                break;
            case 'k':
                stringBuilder.append(CharPatternToRegexConstants.LOWER_KATAKANA);
                break;
            case 'K':
                stringBuilder.append(CharPatternToRegexConstants.UPPER_KATAKANA);
                break;
            case 'C':
                stringBuilder.append(CharPatternToRegexConstants.KANJI);
                break;
            case 'G':
                stringBuilder.append(CharPatternToRegexConstants.HANGUL);
                break;
            case 'a':
                String regexa = buildRegex(CharPatternToRegexConstants.LOWER_LATIN,
                        CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN);
                stringBuilder.append(regexa);
                break;
            case 'A':
                String regexA = buildRegex(CharPatternToRegexConstants.UPPER_LATIN,
                        CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN);
                stringBuilder.append(regexA);
                break;
            case '9':
                String regex9 = buildRegex(CharPatternToRegexConstants.DIGIT, CharPatternToRegexConstants.FULLWIDTH_DIGIT);
                stringBuilder.append(regex9);
                break;
            default:
                String notRecognized = String.valueOf(Character.toChars(codePoint));
                stringBuilder.append(escapeCharacters(notRecognized));
                break;
            }
            if (Character.isSurrogate(pattern.charAt(pos)))
                pos++;
        }
        stringBuilder.append("$");
        return stringBuilder.toString();
    }

    // Utils to remove middle parenthesis of regexes
    private static String buildRegex(String pattern1, String pattern2) {
        return pattern1.substring(0, pattern1.length() - 1) + "|" + pattern2.substring(1);
    }
}
