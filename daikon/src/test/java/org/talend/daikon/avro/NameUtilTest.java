package org.talend.daikon.avro;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class NameUtilTest {

    private String[] wrongNames = { "P1_Vente_Qté", "P1_Vente_PVEscpteNet", "P1_Vente_PVEscpteBrut", "TVA", "CA HT",
            "Numéro de ticket", "Article unique", "N° référence", "Désignation", "Photo n°", "Date_PV", "éà", "+" };

    @Test
    public void testCorrect() {
        Set<String> previousNames = new HashSet<String>();
        int i = 0;
        for (String each : wrongNames) {
            String name = NameUtil.correct(each, i++, previousNames);
            Assert.assertTrue("too many underline", countUnderLine(name) <= (name.length() / 2));
            previousNames.add(name);
        }
        Assert.assertEquals("after the correct, we miss some one or create a duplicated one, please check the NameUtil class",
                wrongNames.length, previousNames.size());
    }

    private int countUnderLine(String str) {
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                result++;
            }
        }
        return result;
    }

}
