package org.gu.dcore;

import java.util.List;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.rewriting.ModularizedRewriting;

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
//	public void testApp()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("B(X, Y), A(Y) :- C(X).\n" +
//    	"D(X, Y), E(Y) :- A(X).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?() :- B(X,Y), D(Y,Z), E(Z).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	
//	    assertTrue( true );
//	}
//	
//	public void testApp1()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("D(X), A(X, Y) :- B(Y).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	
//	    assertTrue( true );
//	}
//	
//	public void testApp2()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("D(X), A(X, Y) :- B1(Y).\n" +
//    	"D(X), A(X,Y) :- B2(Y). \n"
//    	+ "B(Z), C(Z, Y) :- B3(Y).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y), B(Z), C(Z, Y).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	
//	    assertTrue( true );
//	}
//	
//	public void testApp3()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("A(X, Y) :- B(X, Z), A(Z, Y).\n");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	
//	    assertTrue( true );
//	}
//	
//	public void testApp4()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("P(X, Y) :- R(X, Z), R(Z, X).\n"
//    			+ "R(Y, Z) :- P(X, Y).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- P(X, Y), A(X).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//	    assertTrue( true );
//	}
//	
//	public void testApp5()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("S(Y, Z) :- P(X,Y).\n"
//    			+ "P(Y, X) :- S(X, Y).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- P(X, Y), A(Y).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//	    assertTrue( true );
//	}
//	
//	public void testApp6()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("P(X, Y) :- T(X,Y).\n"
//    			+ "A(X) :- S(X)."
//    			+ "T(X,Y), S(Y) :- B(X).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- P(X, Y), A(Y).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//	    assertTrue( true );
//	}
//	
//	public void testApp7()
//	{
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parse("p(X, Y) :- b(X). p(X,Y) :- a(Y).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?() :- r(U,V),r(V,W),p(U,Z),p(V,Z),p(V,T),p(W,T),p1(U),p2(W).");
//    	
//    	System.out.println("============");
//    	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//	    assertTrue( true );
//	}
	
	public void testApp8()
	{
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("p(X, Y) :- b(X). a(X, Y, Z, T) :- p(X, Y), p(Z, T).");
    	
    	ConjunctiveQuery query = new QueryParser().parse("?() :- a(X, Y, Z, T).");
    	
    	System.out.println("============");
    	System.out.println(P);
    	System.out.println(query);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	System.out.println("\nRewritings:\n");
    	for(Rule r : datalog) {
    		System.out.println(r);
    	}
	    assertTrue( true );
	}
}