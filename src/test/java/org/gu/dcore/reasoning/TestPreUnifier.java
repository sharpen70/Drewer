package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.ExRule;
import org.gu.dcore.model.Program;
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
    	
    	ExRule r1 = P.getRule(0);
    	ExRule r2 = P.getRule(1);
    	
    	System.out.println(r1 + "\n" + r2);
    	
    	Map<Atom, List<Unifier>> preUnifiers = new HashMap<>();
		List<Unifier> singlePieceUnifiers = new LinkedList<>();
		
		for(Atom a : r1.getBody()) {
			for(Atom b : r2.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition();
					boolean valid = true;
					
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						TermType atType = at instanceof Constant ? TermType.CONSTANT : TermType.DEFAULT;
						if(!partition.add(at, atType, bt, r2.getTermType(bt))) {
							valid = false; break;
						}
					}
					
					if(valid) {
						Set<Atom> B = new HashSet<>(); B.add(a);
						Set<Atom> H = new HashSet<>(); H.add(b);
						
						Unifier u = new Unifier(B, H, partition, r1.getBody());
						if(u.isPieceUnifier()) singlePieceUnifiers.add(u);
						else {
							List<Unifier> unifiers = preUnifiers.get(a);
							if(unifiers == null) {
								unifiers = new LinkedList<>();
								unifiers.add(u);
								preUnifiers.put(a, unifiers);
							}
							else unifiers.add(u);
						}
					}
				}
			}
		}
    	
    	System.out.println("preUnifier Num: " + preUnifiers.size() + 
    			", SinglePieceUnifier Num: " + singlePieceUnifiers.size());
    	
    	Assert.assertTrue(preUnifiers.size() == 2 && singlePieceUnifiers.size() == 0);
    }
    
}
