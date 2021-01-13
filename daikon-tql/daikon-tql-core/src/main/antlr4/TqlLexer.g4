/* Lexer grammar for Tql filters lexicon.*/

lexer grammar TqlLexer;

/**
 * Tokens
 **/

// language structure tokens
LPAREN : '(';
RPAREN : ')';
LBRACK : '[';
RBRACK : ']';
COMMA : ',';
WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip ;

// logical operators
NOT: 'not';
AND : 'and';
OR : 'or';

// comparison operators
EQ : '=';
NEQ: '!=';
LT : '<';
LET: '<=';
GT : '>';
GET: '>=';

// advanced comparison operators
IS : 'is';
CONTAINS : 'contains';
CONTAINS_IGNORE_CASE : 'containsIgnoreCase';
MATCHES : '~';
COMPLIES : 'complies';
WORD_COMPLIES : 'wordComplies';
BETWEEN : 'between';
IN : 'in';
ALL_FIELDS: '*';
FIELD_REFERENCE: 'field';

// special values
EMPTY : 'empty';
VALID : 'valid';
INVALID : 'invalid';
TRUE : 'true';
FALSE : 'false';
NULL : 'null';

// data types
INT: ('-')?('0' .. '9')+;
DECIMAL: ('-')?('0' .. '9')+('.')('0' .. '9')*;

// Field name
// Side note: order highly matters so INT matches first (to prevent "n = 1000" to be a "field = field" expression)
FIELD: ('a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )+;

// literal values
fragment ESC : '\\\'';
QUOTED_VALUE : ( '\'' (ESC|.)*? '\'' );
