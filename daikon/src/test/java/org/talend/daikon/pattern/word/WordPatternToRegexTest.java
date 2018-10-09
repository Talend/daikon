package org.talend.daikon.pattern.word;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordPatternToRegexTest {

    @Test
    public void lowerLatinCharInsensitive() {
        final String pattern = "[char]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("a", regex);
        assertMatches("b", regex);
        assertNoMatches("こ", regex);
        assertNoMatches("0", regex);
        assertNoMatches("袁", regex);
        assertNoMatches("a b", regex);

    }

    @Test
    public void escapedCharacters() {
        assertMatches("a b", WordPatternToRegex.toRegex("[char] [char]", true));
        assertMatches("a,b", WordPatternToRegex.toRegex("[char],[char]", true));
        assertMatches("a)b", WordPatternToRegex.toRegex("[char])[char]", true));
        assertMatches("a(b", WordPatternToRegex.toRegex("[char]([char]", true));
        assertMatches("a[b", WordPatternToRegex.toRegex("[char][[char]", true));
        assertMatches("a]b", WordPatternToRegex.toRegex("[char]][char]", true));
        assertMatches("a+b", WordPatternToRegex.toRegex("[char]+[char]", true));
        assertMatches("a*b", WordPatternToRegex.toRegex("[char]*[char]", true));

        assertEquals("At least one of the characters [({^+*|\\.?$})] is not well escaped",
                WordPatternToRegex.toRegex("[({^+*|\\.?$})]", true), "^\\[\\(\\{\\^\\+\\*\\|\\\\\\.\\?\\$\\}\\)\\]$");
    }

    @Test
    public void lowerLatinCharSensitive() {
        final String pattern = "[char]";
        String regex = WordPatternToRegex.toRegex(pattern, true);
        assertMatches("a", regex);
        assertMatches("b", regex);
        assertNoMatches("B", regex);
        assertNoMatches("0", regex);
        assertNoMatches("a b", regex);
    }

    @Test
    public void upperLatinCharInsensitive() {
        final String pattern = "[Char]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("A", regex);
        assertMatches("B", regex);
        assertMatches("b", regex);
        assertNoMatches("0", regex);
        assertNoMatches("a b", regex);
    }

    @Test
    public void upperLatinCharSensitive() {
        final String pattern = "[Char]";
        String regex = WordPatternToRegex.toRegex(pattern, true);
        assertMatches("A", regex);
        assertMatches("B", regex);
        assertNoMatches("b", regex);
        assertNoMatches("0", regex);
        assertNoMatches("A B", regex);
    }

    @Test
    public void lowerLatinWordInsensitive() {
        final String pattern = "[word]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("Aa", regex);
        assertMatches("vba", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
        assertNoMatches("ab cd", regex);

    }

    @Test
    public void lowerLatinWordSensitive() {
        final String pattern = "[word]";

        String regex = WordPatternToRegex.toRegex(pattern, true);
        assertNoMatches("Aa", regex);
        assertMatches("vba", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertNoMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
        assertNoMatches("ab cd", regex);
    }

    @Test
    public void twoLatinWordsSeparatedBySpaceInsensitive() {
        final String pattern = "[word] [word]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("rainbow dash", regex);
        assertMatches("Big McIntosh", regex);
        assertNoMatches("too many spaces", regex);
        assertNoMatches("double  spaces", regex);
    }

    @Test
    public void twoLatinWordsSeparatedBySpaceSensitive() {
        final String pattern = "[word] [word]";

        String regex = WordPatternToRegex.toRegex(pattern, true);
        assertMatches("rainbow dash", regex);
        assertNoMatches("Big McIntosh", regex);
        assertNoMatches("too many spaces", regex);
    }

    @Test
    public void upperLatinWordInsensitive() {
        final String pattern = "[WORD]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("WOWOWO", regex);
        assertMatches("vba", regex);
        assertMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
    }

    @Test
    public void upperLatinWordSensitive() {
        final String pattern = "[WORD]";

        String regex = WordPatternToRegex.toRegex(pattern, true);
        assertMatches("WOWOWO", regex);
        assertNoMatches("vba", regex);
        assertNoMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertNoMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸ", regex);
    }

    @Test
    public void latinWordInsensitive() {
        final String pattern = "[Word]";

        String regex = WordPatternToRegex.toRegex(pattern, false);

        assertMatches("WOWOWO", regex);
        assertMatches("vba", regex);
        assertMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
    }

    @Test
    public void latinWordSensitive() {
        final String pattern = "[Word]";

        String regex = WordPatternToRegex.toRegex(pattern, true);

        assertNoMatches("WOWOWO", regex);
        assertNoMatches("vba", regex);
        assertMatches("Aa", regex);
    }

    @Test
    public void unbalancedPattern() {
        final String pattern = "[...";
        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches("[...", regex);
    }

    @Test
    public void number() {
        final String pattern = "[number]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("42", regex);
        assertNoMatches("0", regex);
        assertNoMatches("vba0", regex);
    }

    @Test
    public void digit() {
        final String pattern = "[digit]";
        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("9", regex);
        assertNoMatches("1337", regex);
        assertNoMatches("vba0", regex);
    }

    @Test
    public void latinAlphanumeric() {
        final String pattern = "[alnum]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("aaa8", regex);
        assertNoMatches("9", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);
    }

    @Test
    public void twoPatternSeparatedByDot() {
        final String pattern = "[number].[Word]";
        String regex = WordPatternToRegex.toRegex(pattern, false);

        assertMatches("42.pony", regex);
        assertNoMatches("42!pony", regex);
        assertNoMatches("42Ppony", regex);
        assertNoMatches(".42.pony", regex);
        assertNoMatches("42..pony", regex);
    }

    @Test
    public void patternWithParenthesis() {
        final String pattern = "([number])";
        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("(42)", regex);
        assertMatches("(1337)", regex);
        assertNoMatches("()42", regex);
        assertNoMatches("()", regex);
        assertNoMatches("(4)", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithBrackets() {
        final String pattern = "[[number]]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("[42]", regex);
        assertMatches("[1337]", regex);
        assertNoMatches("[]42", regex);
        assertNoMatches("[]", regex);
        assertNoMatches("[4]", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithQuestionMark() {
        final String pattern = "[number]?";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("42?", regex);
        assertMatches("1337?", regex);
        assertNoMatches("?42", regex);
        assertNoMatches("?", regex);
        assertNoMatches("4?", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithPlusAndStar() {

        final String pattern = "[number]+*";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("42+*", regex);
        assertMatches("1337+*", regex);
        assertNoMatches("+*42", regex);
        assertNoMatches("+*", regex);
        assertNoMatches("4+*", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void defaultText() {
        final String example = "C'est un TEXTE Test d'obSERVatIon des 8 pATTERNS possibles (sur plus de 10)";
        final String pattern = "[Char]'[word] [word] [WORD] [Word] [char]'[word][WORD][word][Word] [word] [digit] [char][WORD] [word] ([word] [word] [word] [number])";

        String insensitive = WordPatternToRegex.toRegex(pattern, false);
        assertMatches(example, insensitive);
        assertMatches(example.toLowerCase(), insensitive);
        assertMatches(example.toUpperCase(), insensitive);

        String sensitive = WordPatternToRegex.toRegex(pattern, true);
        assertMatches(example, sensitive);
        assertNoMatches(example.toLowerCase(), sensitive);
        assertNoMatches(example.toUpperCase(), sensitive);

    }

    @Test
    public void email() {
        final String example = "Example123@protonmail.com";
        final String pattern = "[alnum]@[word].[word]";

        String insensitive = WordPatternToRegex.toRegex(pattern, false);
        assertMatches(example, insensitive);
        assertMatches(example.toLowerCase(), insensitive);
        assertMatches(example.toUpperCase(), insensitive);

        String sensitive = WordPatternToRegex.toRegex(pattern, true);
        assertMatches(example, sensitive);
        assertMatches(example.toLowerCase(), sensitive);
        assertNoMatches(example.toUpperCase(), sensitive);
    }

    @Test
    public void chineseIdeogram() {
        final String pattern = "[Ideogram]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));

        assertMatches("袁", regex);
        assertMatches("蘭", regex);
        assertNoMatches("9", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);
    }

    @Test
    public void chinese() {
        final String example = "袁 花木蘭88";
        final String patternNoCase = "[Ideogram] [IdeogramSeq][number]";

        testCaseInsensitivePattern(example, patternNoCase);

        final String patternCase = "[Ideogram] [IdeogramSeq][number]";

        testCaseInsensitivePattern(example, patternCase);
    }

    @Test
    public void chineseText() {
        final String example = "木兰辞\n" + "\n" + "唧唧复唧唧，木兰当户织。\n" + "不闻机杼声，唯闻女叹息。\n" + "问女何所思？问女何所忆？\n" + "女亦无所思，女亦无所忆。\n"
                + "昨夜见军帖，可汗大点兵，\n" + "军书十二卷，卷卷有爷名。\n" + "阿爷无大儿，木兰无长兄，\n" + "愿为市鞍马，从此替爷征。";
        final String pattern = "[IdeogramSeq]\n" + "\n[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]。\n"
                + "[IdeogramSeq]？[IdeogramSeq]？\n" + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n"
                + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n" + "[IdeogramSeq]，[IdeogramSeq]。";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void chineseQuestion() {
        final String example = "不亦1說乎？有";
        final String pattern = "[IdeogramSeq][digit][IdeogramSeq]？[Ideogram]";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void japaneseAlphanumeric() {
        final String example = "こんにちは123";
        final String pattern = "[hiraSeq][alnum]";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void japaneseWord() {
        final String example = "こんにちは";
        final String pattern = "[hiraSeq]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches(example, regex);

        String regexCase = WordPatternToRegex.toRegex(pattern, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void japaneseCJKNoCase() {
        final String example = "日本語123 日本語？你好/Hello!";
        final String patternNoCase = "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[word]!";
        String regex = WordPatternToRegex.toRegex(patternNoCase, false);
        assertMatches(example, regex);

        String regexCase = WordPatternToRegex.toRegex(patternNoCase, true);
        assertNoMatches(example, regexCase);
    }

    @Test
    public void japaneseCJKCase() {
        final String example = "日本語123 日本語？你好/Hello!";
        final String patternCase = "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!";

        String regex = WordPatternToRegex.toRegex(patternCase, false);
        assertMatches(example, regex);

        String regexCase = WordPatternToRegex.toRegex(patternCase, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void japaneseSentence() {
        final String example = "クリスマスは かぞくと いっしょに すごします。";
        final String pattern = "[kataSeq][hira] [hiraSeq] [hiraSeq] [hiraSeq]。";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches(example, regex);

        String regexCase = WordPatternToRegex.toRegex(pattern, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void koreanWord() {
        final String example = "그러던데";
        final String pattern = "[hangulSeq]";

        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertMatches(example, regex);

        String regexCase = WordPatternToRegex.toRegex(pattern, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void surrogatePair() {
        testCaseInsensitivePattern("𠀐", "[Ideogram]");
        testCaseInsensitivePattern("𠀐𠀑我𠀒𠀓", "[IdeogramSeq]");
        testCaseInsensitivePattern("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
        testCaseInsensitivePattern("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
    }

    private void assertMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertTrue(String.format("Regex %s won't match %s", regex, example), matcher.find());
    }

    private void assertNoMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertFalse(String.format("Regex %s match %s", regex, example), matcher.find());
    }

    private void testCaseInsensitivePattern(String example, String pattern) {
        String regex = WordPatternToRegex.toRegex(pattern, false);
        assertEquals(regex, WordPatternToRegex.toRegex(pattern, true));
        assertMatches(example, regex);
    }
}