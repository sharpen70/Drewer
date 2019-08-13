package org.gu.dcore.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Term;

public class AtomFactory {
	private static AtomFactory factory = null;
	
	private AtomFactory() {
		
	}
	
	public static AtomFactory instance() {
		if(factory == null) factory = new AtomFactory();
		
		return factory;
	}
	
	public Atom createAtom(Predicate p, Collection<Term> terms) {
		ArrayList<Term> ts = new ArrayList<>();
		
		for(Term t : terms) ts.add(t);
		
		return new Atom(p, ts);
	}
	
	public Atom createAtom(Predicate p, ArrayList<Term> terms) {
		return new Atom(p, terms);
	}
	
	public Atom getBottom() {
		return null;
	}
}
