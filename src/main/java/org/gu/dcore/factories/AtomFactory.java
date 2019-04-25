package org.gu.dcore.factories;

import java.util.ArrayList;

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
	
	public Atom createAtom(Predicate p, ArrayList<Term> terms) {
		return null;
	}
}
