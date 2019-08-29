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

term        : '<' DESCRIPTION '>' | DESCRIPTION;

predicate   : '<' DESCRIPTION '>' | DESCRIPTION;

/*
 * Lexer Rules
 */
DESCRIPTION : ["a-zA-Z]["a-zA-Z0-9_#:/\\.\\~-]* ;

WS          : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines.
