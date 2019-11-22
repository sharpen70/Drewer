package org.gu.dcore;

import java.util.List;

import org.gu.dcore.grd.TGraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestTarjan extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestTarjan( String testName )
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
		TGraph g = new TGraph(7);
		
		g.addEdge(1, 2);
		g.addEdge(2, 3);
		g.addEdge(2, 4);
		g.addEdge(4, 3);
		g.addEdge(4, 1);
		g.addEdge(1, 5);
		g.addEdge(5, 6);
		g.addEdge(6, 4);
		g.addEdge(3, 3);
		g.addEdge(0, 0);
		
		List<List<Integer>> loops = g.getSCCs();
		
		for(List<Integer> loop : loops) System.out.println(loop);
	}
}
