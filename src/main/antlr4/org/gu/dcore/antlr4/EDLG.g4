grammar EDLG;

/*
 * Parser Rules
 */
program     : prule* EOF ;

prule       : exrule | constraint ;

exrule      : atomset ':-' atomset '.' ;

constraint  : '!' ':-' atomset '.' ;

atomset     : atom | atom ',' atomset ;

atom        : predicate | predicate '(' terms ')' ;

terms       : term | term ',' terms ;

term        : BRACKETED | DESCRIPTION | STRING;

predicate   : BRACKETED | DESCRIPTION ;

/*
 * Lexer Rules
 */
BRACKETED   : '<' (~[>])+ '>' ; 

DESCRIPTION : [a-zA-Z][a-zA-Z0-9_#:/'.''~'-]* ;

STRING      : '"' (~["])+ '"' ;

WS          : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines.
