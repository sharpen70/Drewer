package org.gu.dcore;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAbduction extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestAbduction( String testName )
	{
	    super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
	    return new TestSuite( TestTarjan.class );
	}
	
	public void test() {
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("A(X, Y) :- B(X, Z), A(Z, Y).\n");
    	
    	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y).");
    	
    	
	}
}
