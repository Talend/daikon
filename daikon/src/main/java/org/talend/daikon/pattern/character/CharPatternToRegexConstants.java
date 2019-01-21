package org.talend.daikon.pattern.character;

public enum CharPatternToRegexConstants {

    DIGIT("([\\x{30}-\\x{39}])", "([\\u0030-\\u0039])"),

    LOWER_LATIN("([\\x{61}-\\x{7a}])", "([\\u0061-\\u007a])"),

    LOWER_LATIN_RARE("([\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}])", "([\\u00DF-\\u00F6\\u00F8-\\u00FF])"),

    UPPER_LATIN("([\\x{41}-\\x{5A}])", "([\\u0041-\\u005A])"),

    UPPER_LATIN_RARE("([\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}])", "([\\u00C0-\\u00D6\\u00D8-\\u00DE])"),

    FULLWIDTH_DIGIT("([\\x{FF10}-\\x{FF19}])", "([\\uFF10-\\uFF19])"),

    FULLWIDTH_LOWER_LATIN("([\\x{FF41}-\\x{FF5A}])", "([\\uFF41-\\uFF5A])"),

    FULLWIDTH_UPPER_LATIN("([\\x{FF21}-\\x{FF3A}])", "([\\uFF21-\\uFF3A])"),

    HIRAGANA(
            "([\\x{3041}-\\x{3096}]|\\x{309D}|\\x{309E})",
            "([\\x{3041}-\\x{3096}]|\\x{309D}|\\x{309E}|\\x{30FC})",
            "([\\u3041-\\u3096]|\\u309D|\\u309E|\\u30FC)"),

    HALFWIDTH_KATAKANA("([\\x{FF66}-\\x{FF9D}])", "([\\uFF66-\\uFF9D])"),

    FULLWIDTH_KATAKANA(
            "([\\x{30A1}-\\x{30FA}]|\\x{30FD}|\\x{30FE}|[\\x{31F0}-\\x{31FF}])",
            "([\\x{30A1}-\\x{30FA}]|\\x{30FD}|\\x{30FE}" // FullWidth
                    + "|[\\x{31F0}-\\x{31FF}]" + // Phonetic extension
                    "|\\x{30FC})", // KATAKANA-HIRAGANA PROLONGED SOUND MARK
            "([\\u30A1-\\u30FA\\u31F0-\\u31FF]|\\u30FC\\u30FD\\u30FE)"),

    KANJI(
            "([\\x{4E00}-\\x{9FEF}]" + "|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}])", // Symbol and
                                                                                                            // punctuation added
                                                                                                            // for TDQ-11343
            "([\\u4E00-\\u9FEF]" + "|\\u3005|\\u3007|[\\u3021-\\u3029]|[\\u3038-\\u303B])"), // Symbol and punctuation added for
                                                                                             // TDQ-11343

    KANJI_RARE("([\\x{3400}-\\x{4DB5}]" + // Extension A
            "|[\\x{20000}-\\x{2A6D6}]" + // Extension B
            "|[\\x{2A700}-\\x{2B734}]" + // Extension C
            "|[\\x{2B740}-\\x{2B81D}]" + // Extension D
            "|[\\x{2B820}-\\x{2CEA1}]" + // Extension E
            "|[\\x{2CEB0}-\\x{2EBE0}]" + // Extension F
            "|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]" + // Compatibility Ideograph
            "|[\\x{2F800}-\\x{2FA1D}]" + // Compatibility Ideograph Supplement
            "|[\\x{2F00}-\\x{2FD5}]" + // KangXi Radicals
            "|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}])" // Radical Supplement
            , "([\\u3400-\\u4DB5]" + // Extension A
                    "|[\\ud840-\\ud868][\\udc00-\\udfff]|\\ud869[\\udc00-\\uded6]" + // Extension B
                    "|[\\ud86a-\\ud86c][\\udc00-\\udfff]|\\ud869[\\udf00-\\udfff]|\\ud86d[\\udc00-\\udf34]" + // Extension C
                    "|\\ud86d[\\udf40-\\udfff]|\\ud86e[\\udc00-\\udc1d]" + // Extension D
                    "|[\\ud86f-\\ud872][\\udc00-\\udfff]|\\ud86e[\\udc20-\\udfff]|\\ud873[\\udc00-\\udea1]" + // Extension E
                    "|[\\ud874-\\ud879][\\udc00-\\udfff]|\\ud873[\\udeb0-\\udfff]|\\ud87a[\\udc00-\\udfe0]" + // Extension F
                    "|[\\uF900-\\uFA6D]|[\\uFA70-\\uFAD9]" + // Compatibility Ideograph
                    "|\\ud87e[\\udc00-\\ude1d]" + // Compatibility Ideograph Supplement
                    "|[\\u2F00-\\u2FD5]" + // KangXi Radicals
                    "|[\\u2E80-\\u2E99]|[\\u2E9B-\\u2EF3])"), // Radical Supplement),

    HANGUL("([\\x{AC00}-\\x{D7AF}])", "([\\uAC00-\\uD7AF])");

    private String range;

    private String regex;

    private String javaScriptRegex;

    CharPatternToRegexConstants(String regex, String javaScriptRegex) {
        this(regex, regex, javaScriptRegex);
    }

    CharPatternToRegexConstants(String range, String regex, String javaScriptRegex) {
        this.range = range;
        this.regex = regex;
        this.javaScriptRegex = javaScriptRegex;
    }

    public String getRange() {
        return range;
    }

    public String getRegex() {
        return regex;
    }

    public String getJavaScriptRegex() {
        return javaScriptRegex;
    }
}
