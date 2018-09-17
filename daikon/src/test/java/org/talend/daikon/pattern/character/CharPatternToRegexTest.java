package org.talend.daikon.pattern.character;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharPatternToRegexTest {

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
    }

    @Test
    public void mixedLatin() {
        assertMatches("D d", CharPatternToRegex.toRegex("A a"));
        assertMatches("aBcDeFgHiJkL", CharPatternToRegex.toRegex("aAaAaAaAaAaA"));
    }

    @Test
    public void unbalancedPattern() {
        final String pattern = "[...";
        String regex = CharPatternToRegex.toRegex(pattern);
        assertMatches("[...", regex);
    }

    @Test
    public void number() {
        assertMatches("123", CharPatternToRegex.toRegex("999"));
        assertMatches("1 2;3", CharPatternToRegex.toRegex("9 9;9"));
    }

    @Test
    public void email() {
        assertMatches("toto@talend.com", CharPatternToRegex.toRegex("aaaa@aaaaaa.aaa"));
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
    }

    @Test
    public void hangul() {
        assertMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("GGGGG"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("aaaaa"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("AAAAA"));
        assertNoMatches("괛괜괝괞괟", CharPatternToRegex.toRegex("CCCCC"));
    }

    @Test
    public void hiragana() {
        assertMatches("こんにちは", CharPatternToRegex.toRegex("HHHHH"));
        assertMatches("っゃゅょゎ", CharPatternToRegex.toRegex("hhhhh"));
        assertMatches("ぁあいぃうえ", CharPatternToRegex.toRegex("hHHhHH"));
    }

    @Test
    public void katakana() {
        assertMatches("ㇾㇿｧｨｩ", CharPatternToRegex.toRegex("kkkkk"));
        assertMatches("モヤユ", CharPatternToRegex.toRegex("KKK"));
        assertMatches("モヤユㇿｧ", CharPatternToRegex.toRegex("KKKkk"));
    }

    @Test
    public void mixedAll() {
        assertMatches("0aAぁあァア一가", CharPatternToRegex.toRegex("9aAhHkKCG"));
    }

    @Test
    public void fullWidth() {
        assertMatches("0123456789０１２３４５６７８９", CharPatternToRegex.toRegex("99999999999999999999"));
        assertMatches("ＶＷＸＹＺVWXYZ", CharPatternToRegex.toRegex("AAAAAAAAAA"));
        assertMatches("ａｂｃｄｅabcde", CharPatternToRegex.toRegex("aaaaaaaaaa"));
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
