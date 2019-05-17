package org.talend.tqlmongo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.talend.daikon.pattern.character.CharPatternToRegex;
import org.talend.daikon.pattern.word.WordPatternToRegex;
import org.talend.tql.model.AllFields;
import org.talend.tql.model.AndExpression;
import org.talend.tql.model.ComparisonExpression;
import org.talend.tql.model.ComparisonOperator;
import org.talend.tql.model.Expression;
import org.talend.tql.model.FieldBetweenExpression;
import org.talend.tql.model.FieldCompliesPattern;
import org.talend.tql.model.FieldContainsExpression;
import org.talend.tql.model.FieldInExpression;
import org.talend.tql.model.FieldIsEmptyExpression;
import org.talend.tql.model.FieldIsInvalidExpression;
import org.talend.tql.model.FieldIsValidExpression;
import org.talend.tql.model.FieldMatchesRegex;
import org.talend.tql.model.FieldReference;
import org.talend.tql.model.FieldWordCompliesPattern;
import org.talend.tql.model.LiteralValue;
import org.talend.tql.model.NotExpression;
import org.talend.tql.model.OrExpression;
import org.talend.tql.model.TqlElement;
import org.talend.tql.visitor.IASTVisitor;
import org.talend.tqlmongo.excp.TqlMongoException;

/**
 * Created by gmzoughi on 30/06/16.
 */
public class ASTVisitor implements IASTVisitor<Object> {

    public static final String MONGO_REGEX_IGNORE_CASE_OPTION = "i";

    public static final String MONGO_ESCAPE_PATTERN = "[\\.\\^\\$\\*\\+\\?\\(\\)\\[\\{\\\\\\|]";

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
            return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
        return new Criteria().orOperator(criteria.toArray(new Criteria[0]));
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
            return new Criteria().orOperator(criteria.toArray(new Criteria[0]));
        return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
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
        String regex = CharPatternToRegex.toRegex(pattern);
        Pattern regexCompiled = Pattern.compile(regex);
        if (!isNegation)
            return Criteria.where(fieldName).regex(regexCompiled);
        return Criteria.where(fieldName).not().regex(regexCompiled);
    }

    @Override
    public Object visit(FieldWordCompliesPattern elt) {
        String fieldName = (String) elt.getField().accept(this);
        String pattern = elt.getPattern();
        if (StringUtils.isEmpty(pattern)) {
            if (!isNegation)
                return Criteria.where(fieldName).is("");
            return Criteria.where(fieldName).ne("");
        }
        String regex = WordPatternToRegex.toRegex(pattern, true);
        return getRegexpForWordPattern(fieldName, regex, isNegation);
    }

    private Criteria getRegexpForWordPattern(String fieldName, String regex, boolean isWithNegation) {
        return new Criteria() {

            @Override
            public Document getCriteriaObject() {
                Document regexObject = new Document("$regex", regex.replaceAll("script=Han", "Han"));
                if (!isWithNegation)
                    return new Document(fieldName, regexObject);
                return new Document(fieldName, new Document("$not", regexObject));
            }
        };
    }

    @Override
    public Object visit(FieldContainsExpression elt) {
        String options = elt.isCaseSensitive() ? "" : MONGO_REGEX_IGNORE_CASE_OPTION;
        String fieldName = (String) elt.getField().accept(this);
        String value = elt.getValue();
        String regex = value.replaceAll(MONGO_ESCAPE_PATTERN, "\\\\$0");

        if (!isNegation)
            return Criteria.where(fieldName).regex(regex, options);
        return Criteria.where(fieldName).not().regex(regex, options);
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
}
