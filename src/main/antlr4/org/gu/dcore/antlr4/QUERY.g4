grammar QUERY;

/*
 * Parser Rules
 */
query       : ansVar ':-' atomset '.' EOF ;

ansVar 	    : '?' '(' terms ')' ;

atomset     : atom | atom ',' atomset ;

atom        : predicate | predicate '(' terms ')' ;

terms       : term | term ',' terms ;

term        : DESCRIPTION ;

predicate   : '<' DESCRIPTION '>' | DESCRIPTION ;

/*
 * Lexer Rules
 */
DESCRIPTION : [!?a-zA-Z][a-zA-Z0-9_]* ;

WS          : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines.