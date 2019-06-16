package org.gu.dcore.reasoning;

import java.util.List;

import org.gu.dcore.model.Program;
import org.gu.dcore.parsing.DcoreParser;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPartition extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestPartition( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestPartition.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void test1()
    {
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("Q(X) :- A(X), B(X, Y).\n"
    			+ "B(X, Y), A(X) :- C(Y).");
    	
    	System.out.println(P);
    	
    	List<Unifier> us = Unify.getSinglePieceUnifier(P.getRule(1), P.getRule(0));
    	
    	System.out.println("Unifier Num : " + us.size());
    	
    	Assert.assertTrue(us.size() == 1);
    }
    
    
}
