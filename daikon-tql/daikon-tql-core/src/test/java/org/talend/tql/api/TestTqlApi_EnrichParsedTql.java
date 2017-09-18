package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.between;
import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.isEmpty;
import static org.talend.tql.api.TqlBuilder.isValid;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.Expression;
import org.talend.tql.model.TqlElement;

/**
 * Created by achever on 04/07/17.
 */
public class TestTqlApi_EnrichParsedTql extends TestTqlParser_Abstract {

    @Test
    public void testApiEnrich1() throws Exception {
        // Expected TQL
        TqlElement expected = doTest("(f1=1 and f2=2) or f3=true");
        // TQL
        TqlElement tqlExpr1 = doTest("f1=1 and f2=2");
        Expression tqlExpr2 = eq("f3", true);
        Expression tqlElement = or((Expression) tqlExpr1, tqlExpr2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiEnrich2() throws Exception {
        // Expected TQL
        TqlElement expected = doTest("(f1=1 and f2=2) and f3=true");
        // TQL
        TqlElement tqlExpr1 = doTest("f1=1 and f2=2");
        Expression tqlExpr2 = eq("f3", true);
        Expression tqlElement = and((Expression) tqlExpr1, tqlExpr2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiEnrich3() throws Exception {
        // Expected TQL
        TqlElement expected = doTest("((f1=1 and f2=2) or f3=3) and f3=true");
        // TQL
        TqlElement tqlExpr1 = doTest("(f1=1 and f2=2) or f3=3");
        Expression tqlExpr2 = eq("f3", true);
        Expression tqlElement = and((Expression) tqlExpr1, tqlExpr2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiEnrich4() throws Exception {
        // Expected TQL
        TqlElement expected = doTest("((f1=1 and f2=2) or f3=3) and (f3 between [1.2, 9.45] or (f4 is valid and f5 is empty))");
        // TQL
        TqlElement tqlExpr1 = doTest("(f1=1 and f2=2) or f3=3");
        Expression tqlExpr2 = or(between("f3", 1.2, 9.45), and(isValid("f4"), isEmpty("f5")));
        Expression tqlElement = and((Expression) tqlExpr1, tqlExpr2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

    @Test
    public void testApiEnrich5() throws Exception {
        // Expected TQL
        TqlElement expected = doTest(
                "((f1=1 and (f2=2 or f2=22)) or f3=3) and (f3 between [1.2, 9.45] or (f4 is valid and f5 is empty))");
        // TQL
        TqlElement tqlExpr1 = doTest("(f1=1 and (f2=2 or f2=22)) or f3=3");
        Expression tqlExpr2 = or(between("f3", 1.2, 9.45), and(isValid("f4"), isEmpty("f5")));
        Expression tqlElement = and((Expression) tqlExpr1, tqlExpr2);
        Assert.assertEquals(expected.toString(), tqlElement.toString());
    }

}
