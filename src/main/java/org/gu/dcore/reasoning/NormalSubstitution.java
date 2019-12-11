package org.gu.dcore.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class NormalSubstitution implements Substitution {
	private Map<Term, Term> sMap = null;
	
	public NormalSubstitution() {
		this.sMap = new HashMap<>();
	}
	
	public NormalSubstitution(NormalSubstitution sub) {
		this.sMap = new HashMap<>(sub.sMap);
	}
	
	public boolean add(Term v, Term t) {
		Term st = this.sMap.get(v);
		if(st != null && !st.equals(t)) return false;
		
		this.sMap.put(v, t);
		return true;
	}
	
	public NormalSubstitution add(NormalSubstitution sub) {
		NormalSubstitution re = new NormalSubstitution(this);
		for(Entry<Term, Term> entry : sub.sMap.entrySet()) {
			if(!re.add(entry.getKey(), entry.getValue())) return null;
		}
		return re;
	}
	
	public Term getImageOf(Term t, int v_offset, int rc_offset) {
		if(t instanceof Constant) return t;
		if(t instanceof RepConstant) {
			t = TermFactory.instance().getRepConstant(((RepConstant)t).getValue() + rc_offset);
		}
		if(t instanceof Variable) {
			t = TermFactory.instance().getVariable(((Variable)t).getValue() + v_offset);
		}
		Term _t = this.sMap.get(t);
		if(_t == null) return _t;
		else return t;
	}
	
	public Atom getImageOf(Atom a, int v_offset, int rc_offset) {
		ArrayList<Term> terms = a.getTerms();
		ArrayList<Term> substituted_terms = new ArrayList<>();
		
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			substituted_terms.add(getImageOf(t, v_offset, rc_offset));
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), substituted_terms);
	}
	
	public AtomSet getImageOf(AtomSet atomset, int v_offset, int rc_offset) {
		ArrayList<Atom> atoms = new ArrayList<>();
		
		for(Atom a : atomset) {
			atoms.add(this.getImageOf(a, v_offset, rc_offset));
		}
		
		AtomSet image = new AtomSet(atoms);
		
		return image;
	}
	
//	public Rule getImageOf(Rule rule, int offset) {
//		AtomSet head = rule.getHead();
//		AtomSet body = rule.getBody();
//		
//		AtomSet substituted_head = this.getImageOf(head, offset);
//		AtomSet substituted_body = this.getImageOf(body, offset);
//		
//		return RuleFactory.instance().createRule(substituted_head, substituted_body, rule.getMaxVar());
//	}
	
//	public Set<Term> getImageOf(Set<Variable> vars) {
//		Set<Term> image = new HashSet<>();
//		for(Variable v : vars) {
//			Term t = this.sMap.get(v);
//			if(t != null) image.add(t);
//		}		
//		return image;
//	}
}
