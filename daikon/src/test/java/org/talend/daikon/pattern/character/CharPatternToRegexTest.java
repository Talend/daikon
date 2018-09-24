package org.talend.daikon.pattern.character;

import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharPatternToRegexTest {

    @Test
    public void checkRegexes() {
        assertTrue(CharPatternToRegex.toRegex("9999").equals("^([\\x{30}-\\x{39}]|[\\x{FF10}-\\x{FF19}]){4}$"));
        assertTrue(CharPatternToRegex.toRegex("AA")
                .equals("^([\\x{41}-\\x{5A}]|[\\x{C0}-\\x{D6}]|[\\x{D8}-\\x{DE}]|[\\x{FF21}-\\x{FF3A}]){2}$"));
        assertTrue(CharPatternToRegex.toRegex("h").equals(
                "^(\\x{3041}|\\x{3043}|\\x{3045}|\\x{3047}|\\x{3049}|\\x{3063}|\\x{3083}|\\x{3085}|\\x{3087}|\\x{308E}|\\x{3095}|\\x{3096})$"));
        assertTrue(CharPatternToRegex.toRegex("HHH").equals(
                "^(\\x{3042}|\\x{3044}|\\x{3046}|\\x{3048}|[\\x{304A}-\\x{3062}]|[\\x{3064}-\\x{3082}]|\\x{3084}|\\x{3086}|[\\x{3088}-\\x{308D}]|[\\x{308F}-\\x{3094}]){3}$"));
        assertTrue(CharPatternToRegex.toRegex("k").equals(
                "^(\\x{30A1}|\\x{30A3}|\\x{30A5}|\\x{30A7}|\\x{30A9}|\\x{30C3}|\\x{30E3}|\\x{30E5}|\\x{30E7}|\\x{30EE}|\\x{30F5}|\\x{30F6}|[\\x{31F0}-\\x{31FF}]|[\\x{FF67}-\\x{FF6F}])$"));
        assertTrue(CharPatternToRegex.toRegex("K").equals(
                "^(\\x{30A2}|\\x{30A4}|\\x{30A6}|\\x{30A8}|[\\x{30AA}-\\x{30C2}]|[\\x{30C4}-\\x{30E2}]|\\x{30E4}|\\x{30E6}|[\\x{30E8}-\\x{30ED}]|[\\x{30EF}-\\x{30F4}]|[\\x{30F7}-\\x{30FA}]|\\x{FF66}|[\\x{FF71}-\\x{FF9D}])$"));
        assertTrue(CharPatternToRegex.toRegex("C")
                .equals("^([\\x{4E00}-\\x{9FEF}]" + "|[\\x{3400}-\\x{4DB5}]"
                        + "|[\\x{20000}-\\x{2A6D6}]|[\\x{2A700}-\\x{2B734}]|[\\x{2B740}-\\x{2B81D}]"
                        + "|[\\x{2B820}-\\x{2CEA1}]|[\\x{2CEB0}-\\x{2EBE0}]|[\\x{F900}-\\x{FA6D}]|[\\x{FA70}-\\x{FAD9}]"
                        + "|[\\x{2F800}-\\x{2FA1D}]|[\\x{2F00}-\\x{2FD5}]|[\\x{2E80}-\\x{2E99}]|[\\x{2E9B}-\\x{2EF3}]"
                        + "|\\x{3005}|\\x{3007}|[\\x{3021}-\\x{3029}]|[\\x{3038}-\\x{303B}])$"));
        assertTrue(CharPatternToRegex.toRegex("G").equals("^([\\x{AC00}-\\x{D7AF}])$"));
        assertTrue(CharPatternToRegex.toRegex("aaaa@aaaaa.aaa")
                .equals("^([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){4}@"
                        + "([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){5}\\."
                        + "([\\x{61}-\\x{7a}]|[\\x{DF}-\\x{F6}]|[\\x{F8}-\\x{FF}]|[\\x{FF41}-\\x{FF5A}]){3}$"));
    }

    @Test
    public void lowerLatin() {
        final String pattern = "a";

        String regex = CharPatternToRegex.toRegex(pattern);
        assertMatches("a", regex);
        assertMatches("b", regex);
        assertNoMatches("こ", regex);
        assertNoMatches("0", regex);
        assertNoMatches("袁", regex);
        assertNoMatches("a b", regex);

        String jsRegex = CharPatternToRegex.toJavaScriptRegex(pattern);
        assertJavaScriptMatches("a", jsRegex);
        assertJavaScriptMatches("b", jsRegex);
        assertJavaScriptNoMatches("こ", jsRegex);
        assertJavaScriptNoMatches("0", jsRegex);
        assertJavaScriptNoMatches("袁", jsRegex);
        assertJavaScriptNoMatches("a b", jsRegex);
    }

    @Test
    public void upperLatin() {
        final String pattern = "A";
        String regex = CharPatternToRegex.toRegex(pattern);
        assertMatches("A", regex);
        assertMatches("B", regex);
        assertNoMatches("b", regex);
        assertNoMatches("0", regex);
        assertNoMatches("A B", regex);

        String jsRegexegex = CharPatternToRegex.toJavaScriptRegex(pattern);
        assertJavaScriptMatches("A", jsRegexegex);
        assertJavaScriptMatches("B", jsRegexegex);
        assertJavaScriptNoMatches("b", jsRegexegex);
        assertJavaScriptNoMatches("0", jsRegexegex);
        assertJavaScriptNoMatches("A B", jsRegexegex);
    }

    @Test
    public void mixedLatin() {
        assertMatches("D d", CharPatternToRegex.toRegex("A a"));
        assertMatches("aBcDeFgHiJkL", CharPatternToRegex.toRegex("aAaAaAaAaAaA"));

        assertJavaScriptMatches("D d", CharPatternToRegex.toJavaScriptRegex("A a"));
        assertJavaScriptMatches("aBcDeFgHiJkL", CharPatternToRegex.toJavaScriptRegex("aAaAaAaAaAaA"));
    }

    @Test
    public void unbalancedPattern() {
        final String pattern = "[...";
        String regex = CharPatternToRegex.toRegex(pattern);
        assertMatches("[...", regex);
        String jsRegexegex = CharPatternToRegex.toJavaScriptRegex(pattern);
        assertJavaScriptMatches("[...", jsRegexegex);
    }

    @Test
    public void number() {
        assertMatches("123", CharPatternToRegex.toRegex("999"));
        assertMatches("1 2;3", CharPatternToRegex.toRegex("9 9;9"));

        assertJavaScriptMatches("123", CharPatternToRegex.toJavaScriptRegex("999"));
        assertJavaScriptMatches("1 2;3", CharPatternToRegex.toJavaScriptRegex("9 9;9"));

    }

    @Test
    public void email() {
        assertMatches("toto@talend.com", CharPatternToRegex.toRegex("aaaa@aaaaaa.aaa"));
        assertJavaScriptMatches("toto@talend.com", CharPatternToRegex.toJavaScriptRegex("aaaa@aaaaaa.aaa"));
    }

    @Test
    public void chineseIdeogram() {
        final String pattern = "C";
        String regex = CharPatternToRegex.toRegex(pattern);
        assertMatches("袁", regex);
        assertMatches("蘭", regex);
        assertNoMatches("9", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);

        String jsRegex = CharPatternToRegex.toJavaScriptRegex(pattern);
        assertJavaScriptMatches("袁", jsRegex);
        assertJavaScriptMatches("蘭", jsRegex);
        assertJavaScriptNoMatches("9", jsRegex);
        assertJavaScriptNoMatches("a", jsRegex);
        assertJavaScriptNoMatches(".aaa", jsRegex);
        assertJavaScriptNoMatches("a袁", jsRegex);
        assertJavaScriptNoMatches("ac袁", jsRegex);
    }

    @Test
    public void hangul() {
        assertMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("GGGGG"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("aaaaa"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("AAAAA"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("CCCCC"));

        assertJavaScriptMatches("괛괜괝괞괟", CharPatternToRegex.toJavaScriptRegex("GGGGG"));
        assertJavaScriptNoMatches("괛괜괝괞괟", CharPatternToRegex.toJavaScriptRegex("aaaaa"));
        assertJavaScriptNoMatches("괛괜괝괞괟", CharPatternToRegex.toJavaScriptRegex("AAAAA"));
        assertJavaScriptNoMatches("괛괜괝괞괟", CharPatternToRegex.toJavaScriptRegex("CCCCC"));
    }

    @Test
    public void hiragana() {
        assertMatches("こんにちは", CharPatternToRegex.toRegex("HHHHH"));
        assertMatches("っゃゅょゎ", CharPatternToRegex.toRegex("hhhhh"));
        assertMatches("ぁあいぃうえ", CharPatternToRegex.toRegex("hHHhHH"));

        assertJavaScriptMatches("こんにちは", CharPatternToRegex.toJavaScriptRegex("HHHHH"));
        assertJavaScriptMatches("っゃゅょゎ", CharPatternToRegex.toJavaScriptRegex("hhhhh"));
        assertJavaScriptMatches("ぁあいぃうえ", CharPatternToRegex.toJavaScriptRegex("hHHhHH"));
    }

    @Test
    public void katakana() {
        assertMatches("ㇾㇿｧｨｩ", CharPatternToRegex.toRegex("kkkkk"));
        assertMatches("モヤユ", CharPatternToRegex.toRegex("KKK"));
        assertMatches("モヤユㇿｧ", CharPatternToRegex.toRegex("KKKkk"));

        assertJavaScriptMatches("ㇾㇿｧｨｩ", CharPatternToRegex.toJavaScriptRegex("kkkkk"));
        assertJavaScriptMatches("モヤユ", CharPatternToRegex.toJavaScriptRegex("KKK"));
        assertJavaScriptMatches("モヤユㇿｧ", CharPatternToRegex.toJavaScriptRegex("KKKkk"));
    }

    @Test
    public void mixedAll() {
        assertMatches("0aAぁあァア一가", CharPatternToRegex.toRegex("9aAhHkKCG"));

        assertJavaScriptMatches("0aAぁあァア一가", CharPatternToRegex.toJavaScriptRegex("9aAhHkKCG"));
    }

    @Test
    public void fullWidth() {
        assertMatches("0123456789０１２３４５６７８９", CharPatternToRegex.toRegex("99999999999999999999"));
        assertMatches("ＶＷＸＹＺVWXYZ", CharPatternToRegex.toRegex("AAAAAAAAAA"));
        assertMatches("ａｂｃｄｅabcde", CharPatternToRegex.toRegex("aaaaaaaaaa"));

        assertJavaScriptMatches("0123456789０１２３４５６７８９", CharPatternToRegex.toJavaScriptRegex("99999999999999999999"));
        assertJavaScriptMatches("ＶＷＸＹＺVWXYZ", CharPatternToRegex.toJavaScriptRegex("AAAAAAAAAA"));
        assertJavaScriptMatches("ａｂｃｄｅabcde", CharPatternToRegex.toJavaScriptRegex("aaaaaaaaaa"));
    }

    @Test
    public void escapedCharacters() {
        assertMatches("a b", CharPatternToRegex.toRegex("a a"));
        assertMatches("a,b", CharPatternToRegex.toRegex("a,a"));
        assertMatches("a)b", CharPatternToRegex.toRegex("a)a"));
        assertMatches("a(b", CharPatternToRegex.toRegex("a(a"));
        assertMatches("a[b", CharPatternToRegex.toRegex("a[a"));
        assertMatches("a]b", CharPatternToRegex.toRegex("a]a"));
        assertMatches("a+b", CharPatternToRegex.toRegex("a+a"));
        assertMatches("a*b", CharPatternToRegex.toRegex("a*a"));
        assertEquals("At least one of the characters [({^+*|\\.?$})] is not well escaped",
                CharPatternToRegex.toRegex("[({^+*|\\.?$})]"), "^\\[\\(\\{\\^\\+\\*\\|\\\\\\.\\?\\$\\}\\)\\]$");
    }

    private void assertMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertTrue(String.format("Regex %s won't match %s", regex, example), matcher.find());
    }

    private void assertNoMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertFalse(String.format("Regex %s match %s", regex, example), matcher.find());
    }
}
