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

fieldIsNull : (FIELD | allFields | ~INT | ~DECIMAL) IS NULL;

fieldIsEmpty : (FIELD | allFields | ~INT | ~DECIMAL) IS EMPTY;

fieldIsValid : (FIELD | allFields | ~INT | ~DECIMAL) IS VALID;

fieldIsInvalid : (FIELD | allFields | ~INT | ~DECIMAL) IS INVALID;

fieldContains : (FIELD | allFields | ~INT | ~DECIMAL) CONTAINS QUOTED_VALUE;

fieldContainsIgnoreCase : (FIELD | allFields | ~INT | ~DECIMAL) CONTAINS_IGNORE_CASE QUOTED_VALUE;

fieldMatchesRegexp : (FIELD | allFields | ~INT | ~DECIMAL) MATCHES QUOTED_VALUE;

fieldCompliesPattern : (FIELD | allFields | ~INT | ~DECIMAL) COMPLIES QUOTED_VALUE;

fieldWordCompliesPattern : (FIELD | allFields | ~INT | ~DECIMAL) WORD_COMPLIES QUOTED_VALUE;

fieldBetween : (FIELD | allFields | ~INT | ~DECIMAL) BETWEEN (LBRACK | RBRACK) literalValue COMMA literalValue (LBRACK | RBRACK);

fieldIn : (FIELD | allFields | ~INT | ~DECIMAL) IN LBRACK (literalValue | booleanValue) (COMMA (literalValue | booleanValue))* RBRACK;

notExpression : NOT LPAREN expression RPAREN;

expression : orExpression;
orExpression : andExpression (OR andExpression)*;
andExpression : atom (AND atom)*;

atom : booleanComparison
 | literalComparison
 | fieldComparison
 | fieldIsEmpty
 | fieldIsNull
 | fieldIsValid
 | fieldIsInvalid
 | fieldContains
 | fieldContainsIgnoreCase
 | fieldMatchesRegexp
 | fieldCompliesPattern
 | fieldWordCompliesPattern
 | fieldBetween
 | fieldIn
 | notExpression
 | LPAREN expression RPAREN ;