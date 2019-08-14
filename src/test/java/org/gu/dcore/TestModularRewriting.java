package org.gu.dcore;

import java.util.List;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestModularRewriting extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestModularRewriting( String testName )
	{
	    super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
	    return new TestSuite( TestModularRewriting.class );
	}
	
	/**
	 * Rigourous Test :-)
	 */
	public void testApp()
	{
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("B(X, Y), A(X) :- C(Y).");
    	
    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- A(X), B(X,Y).");
    	
    	System.out.println(P);
    	System.out.println(query);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	for(Rule r : datalog) {
    		System.out.println(r);
    	}
    	
	    assertTrue( true );
	}
}