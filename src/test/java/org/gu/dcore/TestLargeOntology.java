package org.gu.dcore;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLargeOntology extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestLargeOntology( String testName )
	{
	    super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
	    return new TestSuite( TestLargeOntology.class );
	}
	
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
	
	public void testApp2() throws IOException
	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
		String O = "/home/sharpen/projects/benchmarktool/benchmark/owl/O.dlp";
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(O);
    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
    	
    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#lacks_part>(X, Y), "
    			+ "<http://purl.obolibrary.org/obo/pr#SO_0000418>(Y),"
    			+ "<http://purl.obolibrary.org/obo/pr#has_part>(X, Z)," + 
    			"<http://purl.obolibrary.org/obo/pr#CHEBI_23367>(Z).");
    	
    	System.out.println("============");
    	
		Rule Qr = RuleFactory.instance().createQueryRule(query);
		
    	System.out.println(Qr);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	long start = System.currentTimeMillis();
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nRewritings:" + datalog.size() + "\n");
    	System.out.println("\nTime cost:" + (end - start) + "ms");
    	
	    assertTrue( true );
	}
}