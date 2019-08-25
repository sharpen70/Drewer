package org.gu.dcore;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLUBM extends TestCase {
	public TestLUBM( String testName )
	{
	    super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
	    return new TestSuite( TestLUBM.class );
	}
	
//	public void testApp() throws IOException
//	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/U/U_m.dlp";
////		String O = "/home/sharpen/projects/benchmarktool/benchmark/owl/U.dlp";
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
////    	ConjunctiveQuery query = new QueryParser().parse("? (A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
////    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#affiliatedOrganizationOf>(B, C).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(A,B) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person>(A),"
//    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf>(A,B), <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course>(B).");
//    	
//    	System.out.println("============");
//    	
//		Rule Qr = RuleFactory.instance().createQueryRule(query);
//		
//    	System.out.println(Qr);
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
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	    assertTrue( true );
//	}
	
	public void testApp1() throws IOException
	{
		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/A/A_m.dlp";
//		String O = "/home/sharpen/projects/benchmarktool/benchmark/owl/U.dlp";
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(O);
    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
//    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#affiliatedOrganizationOf>(B, C).");
    	
    	ConjunctiveQuery query = new QueryParser().parse("?(X0) :- <file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#Device>(X0), "
    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#assistsWith>(X0, X1), "
    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#PhysicalAbility>(X1), "
    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#affects>(X2, X1), "
    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#Quadriplegia>(X2).");
    	
    	System.out.println("============");
    	
		Rule Qr = RuleFactory.instance().createQueryRule(query);
		
    	System.out.println(Qr);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	long start = System.currentTimeMillis();
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nRewritings:" + datalog.size() + "\n");
    	for(Rule r : datalog) {
    		System.out.println(r);
    	}
    	System.out.println("\nTime cost:" + (end - start) + "ms");
    	
	    assertTrue( true );
	}
}
