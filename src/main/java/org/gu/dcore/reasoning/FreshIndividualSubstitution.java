package org.gu.dcore.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class FreshIndividualSubstitution implements Substitution {
	private int fresh_id = 0;
	private Map<Variable, Constant> varMap;
	
	public FreshIndividualSubstitution() {
		this.varMap = new HashMap<>();
	}
	
	public Atom imageOf(Atom a) {
		ArrayList<Term> terms = new ArrayList<Term>();
		
		for(Term t : a.getTerms()) {
			if(t instanceof Variable) {
				Variable v = (Variable)t;
				
				Constant fi = varMap.get(v);
				if(fi == null) {
					fi = getFreshIndividual();
					varMap.put(v, fi);
				}
				terms.add(fi);
			}
			else terms.add(t);
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), terms);
	}
	
	public AtomSet imageOf(AtomSet atomset) {
		AtomSet image = new AtomSet();
		
		for(Atom atom : atomset) {
			image.add(imageOf(atom));
		}
		
		return image;
	}
	
	private Constant getFreshIndividual() {
		return TermFactory.instance().createConstant("U_" + (this.fresh_id++));
	}
}
