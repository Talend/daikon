/* Parser grammar for Tql filters parsing.*/

parser grammar TqlParser;

options { tokenVocab=TqlLexer; }

/**
 * Syntax
 **/

comparisonOperator : EQ | LT | GT | NEQ | LET | GET;

booleanValue : TRUE | FALSE;

booleanComparison :  (FIELD | ~INT | ~DECIMAL) EQ booleanValue | (FIELD | ~INT | ~DECIMAL) NEQ booleanValue;

literalValue : QUOTED_VALUE | INT | DECIMAL;

fieldReference : FIELD_REFERENCE LPAREN FIELD RPAREN;

literalComparison : (FIELD | ~INT | ~DECIMAL) comparisonOperator literalValue;

fieldComparison : (FIELD | ~INT | ~DECIMAL) comparisonOperator fieldReference;

fieldIsEmpty : (FIELD | ~INT | ~DECIMAL) IS EMPTY;

fieldIsValid : (FIELD | ~INT | ~DECIMAL) IS VALID;

fieldIsInvalid : (FIELD | ~INT | ~DECIMAL) IS INVALID;

fieldContains : (FIELD | ~INT | ~DECIMAL) CONTAINS QUOTED_VALUE;

fieldMatchesRegexp : (FIELD | ~INT | ~DECIMAL) MATCHES QUOTED_VALUE;

fieldCompliesPattern : (FIELD | ~INT | ~DECIMAL) COMPLIES QUOTED_VALUE;

fieldBetween : (FIELD | ~INT | ~DECIMAL) BETWEEN LBRACK literalValue COMMA literalValue RBRACK;

fieldIn : (FIELD | ~INT | ~DECIMAL) IN LBRACK (literalValue | booleanValue) (COMMA (literalValue | booleanValue))* RBRACK;

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