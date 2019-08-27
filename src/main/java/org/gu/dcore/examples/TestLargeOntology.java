package org.gu.dcore.examples;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class TestLargeOntology 
{	
//	public void testApp() throws IOException
//	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#has_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#CHEBI_23367>(Y).");
//    	
//    	System.out.println("============");
//  //  	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	    assertTrue( true );
//	}
	
//	public void testApp1() throws IOException
//	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#lacks_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#SO_0000418>(Y).");
//    	
//    	System.out.println("============");
//  //  	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:" + datalog.size() + "\n");
////    	for(Rule r : datalog) {
////    		System.out.println(r);
////    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	    assertTrue( true );
//	}
	
	public static void main(String[] args) throws IOException
	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		String O = "/home/sharpen/projects/benchmarktool/benchmark/owl/O.dlp";
		String O = "/home/sharpen/projects/evaluations/Benchmarks/OG/OG.dlp";
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(O);
    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#lacks_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#SO_0000418>(Y),"
//    			+ "<http://purl.obolibrary.org/obo/pr#has_part>(X, Z)," + 
//    			"<http://purl.obolibrary.org/obo/pr#CHEBI_23367>(Z).");
//      	
    	ConjunctiveQuery query = new QueryParser().parse("?(X0) :- <file:///c:/tmp/OpenGALEN2_FULL_WithPropertyChains.owl#isSpecificConsequenceOf>(X0, X1), "
    			+ "<file:///c:/tmp/OpenGALEN2_FULL_WithPropertyChains.owl#InfectionProcess>(X1).");
    	
        
    	System.out.println("============");
    	
		Rule Qr = RuleFactory.instance().createQueryRule(query);
		
    	System.out.println(Qr);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	System.out.println("Finish modularize");
    	
    	long start = System.currentTimeMillis();
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nRewritings:" + datalog.size() + "\n");
    	System.out.println("\nTime cost:" + (end - start) + "ms");
 
	}
}