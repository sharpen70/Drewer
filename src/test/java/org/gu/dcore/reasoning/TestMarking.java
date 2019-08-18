package org.gu.dcore.reasoning;

import org.gu.dcore.model.Program;
import org.gu.dcore.modularization.BaseMarking;
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
	
//	public void test1() {
//		DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("Q(X) :- A(X), B(X, Y), C(X).\n"
//    			+ "B(X, Y), A(X) :- C(Y).");
//    	
//    	Modularizor m = new Modularizor(P.getRuleSet());
//    	
//    	m.markRules();
//    	
//    	for(MarkedRule r : m.getMarkedRules()) r.printMarked();
//    	
//    	Assert.assertTrue(true);
//	}
	
	public void test2() {
		DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("B(X, Y), C(X,Y) :- A(X).\n"
    			+ "E(Y,Z) :- C(X,Y), D(X).\n"
    			+ "E(X,Z) :- B(X,Y), E(Y,Z).\n"
    			+ "D(X) :- F(X).\n" 
    			+ "C(Y,Z) :- E(Y,Z).");
    	
    	Modularizor m = new Modularizor(P.getRuleSet());
    	
    	m.modularize();
    	
    	m.getMarking().printMarked();
    	
    	Assert.assertTrue(true);
	}
	
	public void test3() {
		DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("U(X,Y) :- Q(X).\n"
    			+ "V(X,Y,Z) :- U(X,Y), P(X,Z).\n"
    			+ "P(X,Y) :- V(X,Y,Z).\n" 
    			+ "U(Y,X) :- U(X,Y).");
    	
    	Modularizor m = new Modularizor(P.getRuleSet());
    	
    	m.modularize();
    	
    	m.getMarking().printMarked();
    	
    	Assert.assertTrue(true);
	}
}
