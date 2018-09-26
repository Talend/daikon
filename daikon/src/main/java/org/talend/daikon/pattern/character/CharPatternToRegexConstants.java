package org.talend.daikon.pattern.character;

public enum CharPatternToRegexConstants {

    DIGIT("([\\x{30}-\\x{39}])", "([\\u0030-\\u0039])"),

    LOWER_LATIN("([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}])", "([\\u0061-\\u007a\\u00DF-\\u00F6\\u00F8-\\u00FF])"),

    UPPER_LATIN("([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}])", "([\\u0041-\\u005A\\u00C0-\\u00D6\\u00D8-\\u00DE])"),

    FULLWIDTH_DIGIT("([\\x{FF10}-\\x{FF19}])", "([\\uFF10-\\uFF19])"),

    FULLWIDTH_LOWER_LATIN("([\\x{FF41}-\\x{FF5A}])", "([\\uFF41-\\uFF5A])"),

    FULLWIDTH_UPPER_LATIN("([\\x{FF21}-\\x{FF3A}])", "([\\uFF21-\\uFF3A])"),

    LOWER_HIRAGANA(
            "(\\x{3041}|\\x{3043}|\\x{3045}|\\x{3047}|\\x{3049}|\\x{3063}|\\x{3083}|\\x{3085}|\\x{3087}|\\x{308E}|\\x{3095}|\\x{3096})",
            "([\\u3041\\u3043\\u3045\\u3047\\u3049\\u3063\\u3083\\u3085\\u3087\\u308E\\u3095\\u3096])"),

    UPPER_HIRAGANA(
            "(\\x{3042}|\\x{3044}|\\x{3046}|\\x{3048}|[\\x{304A}-\\x{3062}]|[\\x{3064}-\\x{3082}]|\\x{3084}|\\x{3086}|[\\x{3088}-\\x{308D}]|[\\x{308F}-\\x{3094}])",
            "([\\u3042\\u3044\\u3046\\u3048\\u304A-\\u3062\\u3064-\\u3082\\u3084\\u3086\\u3088-\\u308D\\u308F-\\u3094])"),

    LOWER_KATAKANA(
            "(\\x{30A1}|\\x{30A3}|\\x{30A5}|\\x{30A7}|\\x{30A9}|\\x{30C3}|\\x{30E3}|\\x{30E5}|\\x{30E7}|\\x{30EE}|\\x{30F5}|\\x{30F6}" // FullWidth
                    + "|[\\x{31F0}-\\x{31FF}]" // Phonetic extension
                    + "|[\\x{FF67}-\\x{FF6F}])", // HalfWidth
            "([\\u30A1\\u30A3\\u30A5\\u30A7\\u30A9\\u30C3\\u30E3\\u30E5\\u30E7\\u30EE\\u30F5\\u30F6\\u31F0-\\u31FF\\uFF67-\\uFF6F])"),

    UPPER_KATAKANA(
            "(\\x{30A2}|\\x{30A4}|\\x{30A6}|\\x{30A8}|[\\x{30AA}-\\x{30C2}]|[\\x{30C4}-\\x{30E2}]|\\x{30E4}|\\x{30E6}|[\\x{30E8}-\\x{30ED}]|[\\x{30EF}-\\x{30F4}]|[\\x{30F7}-\\x{30FA}]" // FullWidth
                    + "|\\x{FF66}|[\\x{FF71}-\\x{FF9D}])",
            "([\\u30A2\\u30A4\\u30A6\\u30A8\\u30AA-\\u30C2\\u30C4-\\u30E2\\u30E4\\u30E6\\u30E8-\\u30ED\\u30EF-\\u30F4\\u30F7-\\u30FA\\uFF66\\uFF71-\\uFF9D])"),

    KANJI(
            "([\\x{4E00}-\\x{9FEF}]" + "|[\\x{3400}-\\x{4DB5}]" + // Extension A
                    "|[\\x{20000}-\\x{2A6D6}]" + // Extension B
                    "|[\\x{2A700}-\\x{2B734}]" + // Extension C
                    "|[\\x{2B740}-\\x{2B81D}]" + // Extension D
                    "|[\\x{2B820}-\\x{2CEA1}]" + // Extension E
                    "|[\\x{2CEB0}-\\x{2EBE0}]" + // Extension F
                    "|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]" + // Compatibility Ideograph
                    "|[\\x{2F800}-\\x{2FA1D}]" + // Compatibility Ideograph Supplement
                    "|[\\x{2F00}-\\x{2FD5}]" + // KangXi Radicals
                    "|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}]" + // Radical Supplement
                    "|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}]" + // Symbol and punctuation added for TDQ-11343
                    ")", //
            "([\\u4E00-\\u9FEF]" + "|[\\u3400-\\u4DB5]" + // Extension A
                    "|[\\ud840-\\ud868][\\udc00-\\udfff]|\\ud869[\\udc00-\\uded6]" + // Extension B
                    "|[\\ud86a-\\ud86c][\\udc00-\\udfff]|\\ud869[\\udf00-\\udfff]|\\ud86d[\\udc00-\\udf34]" + // Extension C
                    "|\\ud86d[\\udf40-\\udfff]|\\ud86e[\\udc00-\\udc1d]" + // Extension D
                    "|[\\ud86f-\\ud872][\\udc00-\\udfff]|\\ud86e[\\udc20-\\udfff]|\\ud873[\\udc00-\\udea1]" + // Extension E
                    "|[\\ud874-\\ud879][\\udc00-\\udfff]|\\ud873[\\udeb0-\\udfff]|\\ud87a[\\udc00-\\udfe0]" + // Extension F
                    "|[\\uF900-\\uFA6D]|[\\uFA70-\\uFAD9]" + // Compatibility Ideograph
                    "|\\ud87e[\\udc00-\\ude1d]" + // Compatibility Ideograph Supplement
                    "|[\\u2F00}-\\u2FD5]" + // KangXi Radicals
                    "|[\\u2E80}-\\u2E99]|[\\u2E9B-\\u2EF3]" + // Radical Supplement
                    "|\\u3005|\\u3007|[\\u3021-\\u3029]|[\\u3038-\\u303B]" + // Symbol and punctuation added for TDQ-11343
                    ")"),

    HANGUL("([\\x{AC00}-\\x{D7AF}])", "([\\uAC00-\\uD7AF])");

    private String regex;

    private String javaScriptRegex;

    CharPatternToRegexConstants(String regex, String javaScriptRegex) {
        this.regex = regex;
        this.javaScriptRegex = javaScriptRegex;
    }

    public String getRegex() {
        return regex;
    }

    public String getJavaScriptRegex() {
        return javaScriptRegex;
    }
}
