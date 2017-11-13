package org.talend.tql.model;

import org.talend.tql.visitor.IASTVisitor;

/**
 * Represents <i>all fields</i> (character "*" in grammar): it is a way to specify a condition that applies to
 * <b>all</b> fields in expression.
 * <p>
 * For example:
 * </p>
 * <code>
 *     (field0&lt;1) or (field1&lt;1) or ... (fieldN&lt;1)
 * </code>
 * <p>
 * is equivalent to:
 * </p>
 * <code>
 *     (*&lt;1)
 * </code>
 */
public class AllFields implements TqlElement {

    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "AllFields{}";
    }
}
