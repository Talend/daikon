/* Parser grammar for Tql filters parsing.*/

parser grammar TqlParser;

options { tokenVocab=TqlLexer; }

/**
 * Syntax
 **/

allFields : ALL_FIELDS;

comparisonOperator : EQ | LT | GT | NEQ | LET | GET;

booleanValue : TRUE | FALSE;

booleanComparison :  (FIELD | allFields | ~INT | ~DECIMAL) EQ booleanValue | (FIELD | allFields | ~INT | ~DECIMAL) NEQ booleanValue;

literalValue : QUOTED_VALUE | INT | DECIMAL;

fieldReference : (FIELD_REFERENCE | allFields) LPAREN FIELD RPAREN;

literalComparison : (FIELD | allFields | ~INT | ~DECIMAL) comparisonOperator literalValue;

fieldComparison : (FIELD | allFields | ~INT | ~DECIMAL) comparisonOperator fieldReference;

fieldIsEmpty : (FIELD | allFields | ~INT | ~DECIMAL) IS EMPTY;

fieldIsValid : (FIELD | allFields | ~INT | ~DECIMAL) IS VALID;

fieldIsInvalid : (FIELD | allFields | ~INT | ~DECIMAL) IS INVALID;

fieldContains : (FIELD | allFields | ~INT | ~DECIMAL) CONTAINS QUOTED_VALUE;

fieldMatchesRegexp : (FIELD | allFields | ~INT | ~DECIMAL) MATCHES QUOTED_VALUE;

fieldCompliesPattern : (FIELD | allFields | ~INT | ~DECIMAL) COMPLIES QUOTED_VALUE;

fieldBetween : (FIELD | allFields | ~INT | ~DECIMAL) BETWEEN LBRACK literalValue COMMA literalValue RBRACK;

fieldIn : (FIELD | allFields | ~INT | ~DECIMAL) IN LBRACK (literalValue | booleanValue) (COMMA (literalValue | booleanValue))* RBRACK;

notExpression : NOT LPAREN expression RPAREN;

expression : orExpression;
orExpression : andExpression (OR andExpression)*;
andExpression : atom (AND atom)*;

atom : booleanComparison
 | literalComparison
 | fieldComparison
 | fieldIsEmpty
 | fieldIsValid
 | fieldIsInvalid
 | fieldContains
 | fieldMatchesRegexp
 | fieldCompliesPattern
 | fieldBetween
 | fieldIn
 | notExpression
 | LPAREN expression RPAREN ;