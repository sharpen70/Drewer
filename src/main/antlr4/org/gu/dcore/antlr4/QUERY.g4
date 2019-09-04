grammar QUERY;

/*
 * Parser Rules
 */
query       : ansVar ':-' atomset '.' EOF ;

ansVar 	    : '?' '(' terms ')' | '?()';

atomset     : atom | atom ',' atomset ;

atom        : predicate | predicate '(' terms ')' ;

terms       : term | term ',' terms ;

term        : '<' DESCRIPTION '>' | DESCRIPTION | STRING;

predicate   : '<' DESCRIPTION '>' | DESCRIPTION ;

/*
 * Lexer Rules
 */
DESCRIPTION : ["a-zA-Z]["a-zA-Z0-9_#:/\\.~-]* ;

STRING      : '"' (~["])+ '"' ;

WS          : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines.
