package org.talend.daikon.pattern.character;

import static org.talend.daikon.pattern.PatternRegexUtils.escapeCharacters;

public class CharPatternToRegex {

    private CharPatternToRegex() {
        // Do not instantiate
    }

    public static String toRegex(String pattern) {
        return computeRegex(pattern, false);
    }

    public static String toJavaScriptRegex(String pattern) {
        return computeRegex(pattern, true);
    }

    private static String computeRegex(String pattern, boolean isForJavaScript) {
        StringBuilder stringBuilder = new StringBuilder("^");
        int pos = 0;
        while (pos < pattern.length()) {
            int codePoint = pattern.codePointAt(pos);
            int consecutiveValues = getConsecutiveCodepoints(codePoint, pattern, pos + 1);
            switch (codePoint) {
            case 'h':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.LOWER_HIRAGANA, isForJavaScript),
                        consecutiveValues);
                break;
            case 'H':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.UPPER_HIRAGANA, isForJavaScript),
                        consecutiveValues);
                break;
            case 'k':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.LOWER_KATAKANA, isForJavaScript),
                        consecutiveValues);
                break;
            case 'K':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.UPPER_KATAKANA, isForJavaScript),
                        consecutiveValues);
                break;
            case 'C':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.KANJI, isForJavaScript), consecutiveValues);
                break;
            case 'G':
                buildString(stringBuilder, getRegex(CharPatternToRegexConstants.HANGUL, isForJavaScript), consecutiveValues);
                break;
            case 'a':
                String regexa = buildRegex(getRegex(CharPatternToRegexConstants.LOWER_LATIN, isForJavaScript),
                        getRegex(CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN, isForJavaScript));
                buildString(stringBuilder, regexa, consecutiveValues);
                break;
            case 'A':
                String regexA = buildRegex(getRegex(CharPatternToRegexConstants.UPPER_LATIN, isForJavaScript),
                        getRegex(CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN, isForJavaScript));
                buildString(stringBuilder, regexA, consecutiveValues);
                break;
            case '9':
                String regex9 = buildRegex(getRegex(CharPatternToRegexConstants.DIGIT, isForJavaScript),
                        getRegex(CharPatternToRegexConstants.FULLWIDTH_DIGIT, isForJavaScript));
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

    private static String getRegex(CharPatternToRegexConstants patternConstant, boolean isForJavaScript) {
        return (isForJavaScript) ? patternConstant.getJavaScriptRegex() : patternConstant.getRegex();
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
