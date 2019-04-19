package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ExRule;
import org.gu.dcore.model.Variable;

public class Unifier {
	private Partition partition;
	private AtomSet pB;
	private AtomSet H;
	
	private AtomSet B;
	private ExRule rule;
	
	private Boolean isPieceUnifier = null;
	
	public Unifier(AtomSet _pB, AtomSet _H, Partition _partition, AtomSet _B, ExRule _rule) {
		this.pB = _pB;
		this.H = _H;
		this.partition = _partition;
		this.B = _B;
		this.rule = _rule;
	}
	
	public Atom getImageOf(Atom a) {
		return null;
	}
	
	public AtomSet getImageOf(AtomSet atomset) {
		return null;
	}
	
	public boolean isPieceUnifier() {
		if(this.isPieceUnifier == null) this.isPieceUnifier = checkPieceUnifier();
		return this.isPieceUnifier;
	}
	
	private boolean checkPieceUnifier() {
		Set<Term> separating = new HashSet<>();
		AtomSet minus = AtomSetUtils.minus(this.B, this.pB);
		
		Set<Variable> var_minus = minus.getVariables();
		
		for(Variable v : pB.getVariables()) {
			if(var_minus.contains(v)) separating.add(v);
		}
		
		for(Entry<Set<Term>, boolean[]> category : this.partition.categories.entrySet()) {
			if(category.getValue()[1]) {
				for(Term t : separating) 
					if(category.getKey().contains(t)) return false;
			}
		}
		
		return true;
	}
}
