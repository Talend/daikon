package org.talend.tqlmongo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;
import org.talend.tql.model.*;
import org.talend.tql.visitor.IASTVisitor;
import org.talend.tqlmongo.excp.TqlMongoException;

/**
 * Created by gmzoughi on 30/06/16.
 */
public class ASTVisitor implements IASTVisitor<Object> {

    private boolean isNegation = false;

    @Override
    public Object visit(TqlElement elt) {
        throw new TqlMongoException("Should not enter here!!");
    }

    @Override
    public ComparisonOperator.Enum visit(ComparisonOperator elt) {
        return elt.getOperator();
    }

    @Override
    public Object visit(LiteralValue elt) {
        LiteralValue.Enum literal = elt.getLiteral();
        String value = elt.getValue();
        switch (literal) {
        case INT:
            return Long.valueOf(value);
        case DECIMAL:
            return Double.valueOf(value);
        case QUOTED_VALUE:
            return value;
        case BOOLEAN:
            return Boolean.valueOf(value);
        default:
            throw new TqlMongoException("Unknown literal value type: " + literal);
        }
    }

    @Override
    public String visit(FieldReference elt) {
        return getFieldName(elt.getPath());
    }

    @Override
    public Criteria visit(Expression elt) {
        throw new TqlMongoException("Should not enter here!!");
    }

    @Override
    public Object visit(AndExpression elt) {
        Expression[] expressions = elt.getExpressions();
        List<Criteria> criteria = Arrays.stream(expressions).map(expression -> {
            try {
                return (Criteria) expression.accept(this);
            } catch (Exception e) {
                throw new TqlMongoException(e.getMessage(), e);
            }
        }).collect(Collectors.toList());
        if (criteria.size() == 1)
            return criteria.get(0);
        if (!isNegation)
            return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
        return new Criteria().orOperator(criteria.toArray(new Criteria[criteria.size()]));
    }

    @Override
    public Object visit(OrExpression elt) {
        Expression[] expressions = elt.getExpressions();
        List<Criteria> criteria = Arrays.stream(expressions).map(e -> {
            try {
                return (Criteria) e.accept(this);
            } catch (Exception e1) {
                throw new TqlMongoException(e1.getMessage(), e1);
            }
        }).collect(Collectors.toList());
        if (criteria.size() == 1)
            return criteria.get(0);
        if (!isNegation)
            return new Criteria().orOperator(criteria.toArray(new Criteria[criteria.size()]));
        return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
    }

    @Override
    public Object visit(ComparisonExpression elt) {
        TqlElement field = elt.getField();
        ComparisonOperator operator = elt.getOperator();
        TqlElement valueOrField = elt.getValueOrField();
        String f = (String) field.accept(this);
        ComparisonOperator.Enum o = (ComparisonOperator.Enum) operator.accept(this);
        Object v = valueOrField.accept(this);

        Criteria cr = Criteria.where(f);
        switch (o) {
        case EQ:
            if (!isNegation)
                return cr.is(v);
            return cr.ne(v);
        case NEQ:
            if (!isNegation)
                return cr.ne(v);
            return cr.is(v);
        case LT:
            if (!isNegation)
                return cr.lt(v);
            return cr.gte(v);
        case GT:
            if (!isNegation)
                return cr.gt(v);
            return cr.lte(v);
        case LET:
            if (!isNegation)
                return cr.lte(v);
            return cr.gt(v);
        case GET:
            if (!isNegation)
                return cr.gte(v);
            return cr.lt(v);
        default:
            throw new TqlMongoException("Unknown operator.");
        }
    }

    @Override
    public Object visit(FieldInExpression elt) {
        String fieldName = (String) elt.getField().accept(this);
        LiteralValue[] valueNodes = elt.getValues();
        List<Object> values = Arrays.stream(valueNodes).map(e -> {
            try {
                return e.accept(this);
            } catch (Exception e1) {
                throw new TqlMongoException(e1.getMessage(), e1);
            }
        }).collect(Collectors.toList());
        if (!isNegation)
            return Criteria.where(fieldName).in(values);
        return Criteria.where(fieldName).nin(values);
    }

    @Override
    public Object visit(FieldIsEmptyExpression elt) {
        String fieldName = (String) elt.getField().accept(this);
        if (!isNegation) {
            return new Criteria().orOperator(Criteria.where(fieldName).is(""), Criteria.where(fieldName).is(null));
        }
        return new Criteria().andOperator(Criteria.where(fieldName).ne(""), Criteria.where(fieldName).ne(null));
    }

    @Override
    public Object visit(FieldIsValidExpression elt) {
        throw new TqlMongoException("Unsupported expression");
    }

    @Override
    public Object visit(FieldIsInvalidExpression elt) {
        throw new TqlMongoException("Unsupported expression");
    }

    @Override
    public Object visit(FieldMatchesRegex elt) {
        String fieldName = (String) elt.getField().accept(this);
        String regex = elt.getRegex();
        if (StringUtils.isEmpty(regex)) {
            if (!isNegation)
                return Criteria.where(fieldName).is("");
            return Criteria.where(fieldName).ne("");
        }

        Pattern regexCompiled = Pattern.compile(regex);
        if (!isNegation)
            return Criteria.where(fieldName).regex(regexCompiled);
        return Criteria.where(fieldName).not().regex(regexCompiled);
    }

    @Override
    public Object visit(FieldCompliesPattern elt) {
        String fieldName = (String) elt.getField().accept(this);
        String pattern = elt.getPattern();
        if (StringUtils.isEmpty(pattern)) {
            if (!isNegation)
                return Criteria.where(fieldName).is("");
            return Criteria.where(fieldName).ne("");
        }
        String regex = this.patternToMongoRegex(pattern);
        Pattern regexCompiled = Pattern.compile(regex);
        if (!isNegation)
            return Criteria.where(fieldName).regex(regexCompiled);
        return Criteria.where(fieldName).not().regex(regexCompiled);
    }

    @Override
    public Object visit(FieldContainsExpression elt) {
        String fieldName = (String) elt.getField().accept(this);
        String value = elt.getValue();
        if (!isNegation)
            return Criteria.where(fieldName).regex(value);
        return Criteria.where(fieldName).not().regex(value);
    }

    @Override
    public Object visit(AllFields allFields) {
        throw new UnsupportedOperationException("Not supported on MongoDB");
    }

    @Override
    public Object visit(FieldBetweenExpression elt) {
        String fieldName = (String) elt.getField().accept(this);
        LiteralValue left = elt.getLeft();
        Object leftValue = left.accept(this);
        LiteralValue right = elt.getRight();
        Object rightValue = right.accept(this);

        Criteria criteria = Criteria.where(fieldName);
        if (elt.isLowerOpen()) {
            criteria = criteria.gt(leftValue);
        } else {
            criteria = criteria.gte(leftValue);
        }
        if (elt.isUpperOpen()) {
            criteria = criteria.lt(rightValue);
        } else {
            criteria = criteria.lte(rightValue);
        }

        if (isNegation) {
            criteria = criteria.not();
        }
        return criteria;
    }

    @Override
    public Criteria visit(NotExpression elt) {
        Expression expression = elt.getExpression();
        // Negate sub-tree
        this.isNegation = !this.isNegation;
        Criteria c = (Criteria) expression.accept(this);
        // Reset negation
        this.isNegation = !this.isNegation;

        return c;
    }

    // May be needed in sub-classes.
    protected boolean isNegation() {
        return isNegation;
    }

    private String getFieldName(String fieldName) {
        return fieldName;
    }

    protected String patternToMongoRegex(String pattern) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            switch (c) {
            case 'a':
                sb.append("[a-z|à-ÿ]");
                break;
            case 'A':
                sb.append("[A-Z|À-ß]");
                break;
            case '9':
                sb.append("[0-9]");
                break;
            default:
                // Special characters for PCRE syntax (used by mongoDB for regex) need to be escaped.
                sb.append(String.valueOf(c).replaceAll("[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\{\\\\\\|]", "\\\\$0"));
                break;
            }
        }
        sb.append("$");
        return sb.toString();
    }

}
