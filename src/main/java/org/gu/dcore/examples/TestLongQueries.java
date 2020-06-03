package org.gu.dcore.examples;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.rewriting.ModularizedRewriting2;
import org.gu.dcore.rewriting.ModularizedRewriting3;

public class TestLongQueries {
	 
	public static void main(String[] args) throws IOException
	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
		String O = "S(X,Y) :- P(X,Y). R(Y,X) :- P(X,Y). P(X,Y) :- AP(X). AP(X) :- P(X,Y). API(X) :- P(Y,X). P(Y,X) :- API(X).";
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse(O);
  	
    	ConjunctiveQuery query = new QueryParser().parse("?(X0,X15) :- R(X0,X1),R(X1,X2),S(X2,X3),R(X3,X4),S(X4,X5),R(X5,X6),"
    			+ "S(X6,X7), R(X7,X8),R(X8,X9),S(X9,X10),R(X10,X11),R(X11,X12),S(X12,X13),S(X13,X14),R(X14,X15).");
    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(X1, X6) :- P(X2,X1),P(X2,X3),P(X4,X3),P(X4,X5),P(X6,X5).");
    	System.out.println("============");
    	
		Rule Qr = RuleFactory.instance().createQueryRule(query);
		
    	System.out.println(Qr);
    	
  //  	ModularizedRewriting2 mr = new ModularizedRewriting2(P.getRuleSet());
    	ModularizedRewriting3 mr = new ModularizedRewriting3(P.getRuleSet());
    	
    	long start = System.currentTimeMillis();
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nRewritings:" + datalog.size() + "\n");
    	for(Rule r : datalog) {
    		System.out.println(r);
    	}
    	System.out.println("\nTime cost:" + (end - start) + "ms");    	   	
    	
	}
}
