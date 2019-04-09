package org.gu.dcore.reasoning;

import org.gu.dcore.model.Variable;

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
    	Partition p = new Partition();
    	
    	p.add(new Variable(1), new Variable(2));
    	p.add(new Variable(2), new Variable(3));
    	
    	System.out.println(p.getCategories());
    	
        assertTrue( p.getCategories().size() == 1 );
    }
    
    public void test2()
    {
    	Partition p = new Partition();
    	
    	p.add(new Variable(1), new Variable(2));
    	p.add(new Variable(2), new Variable(3));
    	p.add(new Variable(5), new Variable(6));
    	p.add(new Variable(1), new Variable(6));
    	p.add(new Variable(1), new Variable(2));
    	
    	System.out.println(p.getCategories());
    	
        assertTrue( p.getCategories().size() == 1 );
    }
    
}
