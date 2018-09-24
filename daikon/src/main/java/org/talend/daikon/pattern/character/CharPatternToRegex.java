package org.talend.daikon.pattern.character;

import static org.talend.daikon.pattern.PatternRegexUtils.escapeCharacters;

public class CharPatternToRegex {

    private CharPatternToRegex() {
        // Do not instantiate
    }

    public static String toRegex(String pattern) {
        StringBuilder stringBuilder = new StringBuilder("^");
        int pos = 0;
        while (pos < pattern.length()) {
            int codePoint = pattern.codePointAt(pos);
            int consecutiveValues = getConsecutiveCodepoints(codePoint, pattern, pos + 1);
            switch (codePoint) {
            case 'h':
                buildString(stringBuilder, CharPatternToRegexConstants.LOWER_HIRAGANA.getRegex(), consecutiveValues);
                break;
            case 'H':
                buildString(stringBuilder, CharPatternToRegexConstants.UPPER_HIRAGANA.getRegex(), consecutiveValues);
                break;
            case 'k':
                buildString(stringBuilder, CharPatternToRegexConstants.LOWER_KATAKANA.getRegex(), consecutiveValues);
                break;
            case 'K':
                buildString(stringBuilder, CharPatternToRegexConstants.UPPER_KATAKANA.getRegex(), consecutiveValues);
                break;
            case 'C':
                buildString(stringBuilder, CharPatternToRegexConstants.KANJI.getRegex(), consecutiveValues);
                break;
            case 'G':
                buildString(stringBuilder, CharPatternToRegexConstants.HANGUL.getRegex(), consecutiveValues);
                break;
            case 'a':
                String regexa = buildRegex(CharPatternToRegexConstants.LOWER_LATIN.getRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN.getRegex());
                buildString(stringBuilder, regexa, consecutiveValues);
                break;
            case 'A':
                String regexA = buildRegex(CharPatternToRegexConstants.UPPER_LATIN.getRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN.getRegex());
                buildString(stringBuilder, regexA, consecutiveValues);
                break;
            case '9':
                String regex9 = buildRegex(CharPatternToRegexConstants.DIGIT.getRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_DIGIT.getRegex());
                buildString(stringBuilder, regex9, consecutiveValues);
                break;
            default:
                String notRecognized = escapeCharacters(String.valueOf(Character.toChars(codePoint)));
                buildString(stringBuilder, notRecognized, consecutiveValues);
                break;
            }
            pos += consecutiveValues;
        }
        stringBuilder.append("$");
        return stringBuilder.toString();
    }

    public static String toJavaScriptRegex(String pattern) {
        StringBuilder stringBuilder = new StringBuilder("^");
        int pos = 0;
        while (pos < pattern.length()) {
            int codePoint = pattern.codePointAt(pos);
            int consecutiveValues = getConsecutiveCodepoints(codePoint, pattern, pos + 1);
            switch (codePoint) {
            case 'h':
                buildString(stringBuilder, CharPatternToRegexConstants.LOWER_HIRAGANA.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'H':
                buildString(stringBuilder, CharPatternToRegexConstants.UPPER_HIRAGANA.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'k':
                buildString(stringBuilder, CharPatternToRegexConstants.LOWER_KATAKANA.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'K':
                buildString(stringBuilder, CharPatternToRegexConstants.UPPER_KATAKANA.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'C':
                buildString(stringBuilder, CharPatternToRegexConstants.KANJI.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'G':
                buildString(stringBuilder, CharPatternToRegexConstants.HANGUL.getJavaScriptRegex(), consecutiveValues);
                break;
            case 'a':
                String regexa = buildRegex(CharPatternToRegexConstants.LOWER_LATIN.getJavaScriptRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN.getJavaScriptRegex());
                buildString(stringBuilder, regexa, consecutiveValues);
                break;
            case 'A':
                String regexA = buildRegex(CharPatternToRegexConstants.UPPER_LATIN.getJavaScriptRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN.getJavaScriptRegex());
                buildString(stringBuilder, regexA, consecutiveValues);
                break;
            case '9':
                String regex9 = buildRegex(CharPatternToRegexConstants.DIGIT.getJavaScriptRegex(),
                        CharPatternToRegexConstants.FULLWIDTH_DIGIT.getJavaScriptRegex());
                buildString(stringBuilder, regex9, consecutiveValues);
                break;
            default:
                String notRecognized = escapeCharacters(String.valueOf(Character.toChars(codePoint)));
                buildString(stringBuilder, notRecognized, consecutiveValues);
                break;
            }
            pos += consecutiveValues;
        }
        stringBuilder.append("$");
        return stringBuilder.toString();
    }

    private static void buildString(StringBuilder stringBuilder, String regex, int consecutiveValues) {
        if (consecutiveValues == 1)
            stringBuilder.append(regex);
        else
            stringBuilder.append(regex + "{" + consecutiveValues + "}");
    }

    private static int getConsecutiveCodepoints(int codePoint, String pattern, int currentPos) {
        int lastPos = currentPos;
        while (lastPos < pattern.length() && pattern.codePointAt(lastPos) == codePoint) {
            if (Character.isSurrogate(pattern.charAt(lastPos)))
                lastPos += 2;
            else
                lastPos++;

        }
        return (lastPos - currentPos + 1);
    }

    // Utils to remove middle parenthesis of regexes
    private static String buildRegex(String pattern1, String pattern2) {
        return pattern1.substring(0, pattern1.length() - 1) + "|" + pattern2.substring(1);
    }
}
