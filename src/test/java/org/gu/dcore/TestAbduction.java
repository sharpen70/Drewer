package org.gu.dcore;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gu.dcore.abduction.NormalQueryAbduction;
import org.gu.dcore.abduction.AbstactQueryAbduction;
import org.gu.dcore.abduction.SupportedAbduction;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

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
	    return new TestSuite( TestAbduction.class );
	}
	
	public void test() throws ParsingException, IOException {
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("A(X, Y) :- B(X, Y), C(Y).\n"
    			+ "A(X, Y) :- R(X, Y). \n"
    			+ "D(X) :- E(X, Y).");
    	
 //   	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y).");
    	ConjunctiveQuery query = new QueryParser().parse("?() :- D(Y), A(X,Y), C(Y).");
    	
    	DatalogEngine engine = new DatalogEngine();
    	
    	engine.addFacts("A(a, b) . B(a, c) . C(c) .");
    	
    	Set<Predicate> abdu = new HashSet<>();
    	abdu.add(PredicateFactory.instance().getPredicate("D"));
    	abdu.add(PredicateFactory.instance().getPredicate("R"));
    	
 //   	SupportedAbduction abduction = new SupportedAbduction(P.getRuleSet(), query, engine, abdu);
    	NormalQueryAbduction abduction = new NormalQueryAbduction(P.getRuleSet(), query, engine, abdu);
    	
  //  	System.out.println(engine.answerAtomicQuery("A(a, ?b) ."));
    	List<AtomSet> expl = abduction.getExplanations();
    	
    	for(AtomSet atomset : expl) System.out.println(atomset);
	}
}
