package org.gu.dcore.reasoning;

import org.gu.dcore.model.Program;
import org.gu.dcore.modularization.MarkedRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestMarking extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestMarking( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestMarking.class );
    }
	
	public void test1() {
		DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("Q(X) :- A(X), B(X, Y), C(X).\n"
    			+ "B(X, Y), A(X) :- C(Y).");
    	
    	Modularizor m = new Modularizor(P.getRuleSet());
    	
    	m.markRules();
    	
    	for(MarkedRule r : m.getMarkedRules()) r.printMarked();
    	
    	Assert.assertTrue(true);
	}
}
