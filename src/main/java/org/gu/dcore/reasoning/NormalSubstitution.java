package org.gu.dcore.reasoning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class NormalSubstitution {
	private Map<Term, Term> sMap = null;
	
	public NormalSubstitution() {
		this.sMap = new HashMap<>();
	}
	
	public NormalSubstitution(NormalSubstitution sub) {
		this.sMap = new HashMap<>(sub.sMap);
	}
	
	public boolean bijection() {
		HashSet<Term> hit = new HashSet<>();
		
		for(Term value : this.sMap.values()) {
			if(hit.add(value)) return false;
		}		
		
		return true;
	}
	
	public boolean add(Term v, Term t, boolean bijection) {
		if(bijection) {
			boolean hit = false;
			for(Entry<Term, Term> entry : this.sMap.entrySet()) {
				Term st = entry.getKey();
				Term tt = entry.getValue();
				
				if(st.equals(v)) hit = true;
				else if(tt.equals(t)) return false;
			}
			if(!hit) this.sMap.put(v, t);
			return true;
		}
		else {
			Term st = this.sMap.get(v);
			if(st != null && !st.equals(t)) return false;
			
			this.sMap.put(v, t);
			return true;
		}
	}	
	
	public NormalSubstitution add(NormalSubstitution sub, boolean strict) {
		NormalSubstitution re = new NormalSubstitution(this);
		for(Entry<Term, Term> entry : sub.sMap.entrySet()) {
			if(!re.add(entry.getKey(), entry.getValue(), strict)) return null;
		}
		return re;
	}
	
	public Term getImageOf(Term t, int v_offset, int rc_offset, int num) {
		Term default_t = t; 
				
		if(t instanceof Constant) return t;
		
		if(num != 0) {
			if(t instanceof RepConstant) {
				t = TermFactory.instance().getRepConstant(((RepConstant)t).getValue() + rc_offset);
				default_t = TermFactory.instance().getRepConstant(((RepConstant)t).getValue() + (num * rc_offset));
			}
			if(t instanceof Variable) {
				t = TermFactory.instance().getVariable(((Variable)t).getValue() + v_offset);
				default_t = TermFactory.instance().getVariable(((Variable)t).getValue() + (num * v_offset));
			}
		}
		
		Term _t = this.sMap.get(t);
		if(_t == null) return default_t;
		else return _t;
	}
	
	public Atom getImageOf(Atom a, int v_offset, int rc_offset, int num) {
		ArrayList<Term> terms = a.getTerms();
		ArrayList<Term> substituted_terms = new ArrayList<>();
		
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			substituted_terms.add(getImageOf(t, v_offset, rc_offset, num));
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), substituted_terms);
	}
	
	public AtomSet getImageOf(AtomSet atomset, int v_offset, int rc_offset, int num) {
		ArrayList<Atom> atoms = new ArrayList<>();
		
		for(Atom a : atomset) {
			atoms.add(this.getImageOf(a, v_offset, rc_offset, num));
		}
		
		AtomSet image;
		
		if(atomset instanceof LiftedAtomSet) {
			image = new LiftedAtomSet(atoms, ((LiftedAtomSet) atomset).getColumn());
		}
		else image = new AtomSet(atoms);
		
		return image;
	}
	
	/* get Image without offset */
	public AtomSet getImageOf(AtomSet atomset) {
		return getImageOf(atomset, 0, 0, 0);
	}
}
