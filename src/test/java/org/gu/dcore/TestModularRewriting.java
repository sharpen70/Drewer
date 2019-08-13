package org.gu.dcore;

import java.util.List;

import org.gu.dcore.model.Program;
import org.gu.dcore.parsing.DcoreParser;
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
    	
    	Program P = parser.parse("Q(X) :- A(X), B(X, Y).\n"
    			+ "B(X, Y), A(X) :- C(Y).");
    	
    	System.out.println(P);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	List<Unifier> us = Unify.getSinglePieceUnifier(P.getRule(0), P.getRule(1));
    	
    	System.out.println("Unifier Num : " + us.size());
    	
    	Assert.assertTrue(us.size() == 1);
    	
	    assertTrue( true );
	}
}