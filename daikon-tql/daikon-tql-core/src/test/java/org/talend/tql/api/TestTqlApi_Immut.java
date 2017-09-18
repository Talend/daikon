package org.talend.tql.api;

import static org.talend.tql.api.TqlBuilder.and;
import static org.talend.tql.api.TqlBuilder.between;
import static org.talend.tql.api.TqlBuilder.eq;
import static org.talend.tql.api.TqlBuilder.eqFields;
import static org.talend.tql.api.TqlBuilder.gt;
import static org.talend.tql.api.TqlBuilder.lt;
import static org.talend.tql.api.TqlBuilder.not;
import static org.talend.tql.api.TqlBuilder.or;

import org.junit.Assert;
import org.junit.Test;
import org.talend.tql.TestTqlParser_Abstract;
import org.talend.tql.model.Expression;

/**
 * Created by achever on 04/07/17.
 */
public class TestTqlApi_Immut extends TestTqlParser_Abstract {

    @Test
    public void testApiImmut1() throws Exception {
        // TQL
        Expression tqlExpr1 = eqFields("f1", "1");
        String tql1Str = tqlExpr1.toString();
        Expression tqlExpr2 = eqFields("f1", "1");
        String tql2Str = tqlExpr2.toString();
        or(tqlExpr1, tqlExpr2);
        and(tqlExpr1, tqlExpr2);
        and(not(tqlExpr1), not(tqlExpr2));
        // Check operations leave intial expressions unchanged
        Assert.assertEquals(tql1Str, tqlExpr1.toString());
        Assert.assertEquals(tql2Str, tqlExpr2.toString());
    }

    @Test
    public void testApiImmut2() throws Exception {
        // TQL
        Expression tqlExpr1 = or(eqFields("f1", "1"), eqFields("f2", "2"), and(lt("f3", 3), gt("f4", 4.02)));
        String tql1Str = tqlExpr1.toString();
        Expression tqlExpr2 = and(eq("f1", true), eqFields("f2", "2"),
                and(between("f3", 3.4586, 7.45), TqlBuilder.lte("f4", 12)));
        String tql2Str = tqlExpr2.toString();
        or(tqlExpr1, tqlExpr2);
        and(tqlExpr1, tqlExpr2);
        or(not(tqlExpr1), not(tqlExpr2));
        // Check operations leave intial expressions unchanged
        Assert.assertEquals(tql1Str, tqlExpr1.toString());
        Assert.assertEquals(tql2Str, tqlExpr2.toString());
    }

}
