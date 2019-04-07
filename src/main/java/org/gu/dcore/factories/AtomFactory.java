package org.gu.dcore.factories;

import java.util.ArrayList;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;

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
