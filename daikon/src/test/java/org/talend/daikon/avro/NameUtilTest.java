package org.talend.daikon.avro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class NameUtilTest {

    private String[] wrongNames = { "P1_Vente_Qté", "P1_Vente_PVEscpteNet", "P1_Vente_PVEscpteBrut", "TVA", "CA HT",
            "Numéro de ticket", "Article unique", "N° référence", "Désignation", "Photo n°", "Date_PV", "éà", "+" };

    private String[] expectedNames = { "P1_Vente_Qt_", "P1_Vente_PVEscpteNet", "P1_Vente_PVEscpteBrut", "TVA", "CA_HT",
            "Num_ro_de_ticket", "Article_unique", "N__r_f_rence", "D_signation", "Photo_n_", "Date_PV", "Column11", "Column12" };

    @Test
    public void testCorrect() {
        Set<String> resultNames = new HashSet<String>();
        int i = 0;

        Set<String> expectedSet = new HashSet<>();
        expectedSet.addAll(Arrays.asList(expectedNames));

        for (String each : wrongNames) {
            String name = NameUtil.correct(each, i++, resultNames);
            resultNames.add(name);
        }

        Assert.assertEquals(expectedSet, resultNames);
    }

}
