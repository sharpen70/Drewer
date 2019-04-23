grammar EDLG;

/*
 * Parser Rules
 */
program     : exrule* EOF ;

exrule      : atomset ':-' atomset '.' ;

atomset     : atom | atom ',' atomset ;

atom        : predicate | predicate '(' terms ')' ;

terms       : term | term ',' terms ;

term        : DESCRIPTION ;

predicate   : DESCRIPTION ;

/*
 * Lexer Rules
 */
DESCRIPTION : [!$a-zA-Z][a-zA-Z0-9_]* ;

WS          : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines.
