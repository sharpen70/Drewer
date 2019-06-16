package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Term;
import org.gu.dcore.parsing.DcoreParser;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPreUnifier extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestPreUnifier( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestPreUnifier.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void test1()
    {
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("Q(X) :- A(X), B(X, Y).\n"
    			+ "B(X, Y), A(X) :- C(Y).");
    	
    	Rule r1 = P.getRule(0);
    	Rule r2 = P.getRule(1);
    	
    	System.out.println(r1 + "\n" + r2);
    	
    	Map<Atom, List<Unifier>> preUnifiers = new HashMap<>();
		List<Unifier> singlePieceUnifiers = new LinkedList<>();
    	
    	System.out.println("preUnifier Num: " + preUnifiers.size() + 
    			", SinglePieceUnifier Num: " + singlePieceUnifiers.size());
    	
    	Assert.assertTrue(preUnifiers.size() == 2 && singlePieceUnifiers.size() == 0);
    }
    
}
