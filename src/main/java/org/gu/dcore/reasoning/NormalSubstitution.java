package org.gu.dcore.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class NormalSubstitution implements Substitution {
	private Map<Integer, Term> sMap = null;
	
	public NormalSubstitution() {
		this.sMap = new HashMap<>();
	}
	
	public NormalSubstitution(NormalSubstitution sub) {
		this.sMap = new HashMap<>(sub.sMap);
	}
	
	public boolean add(Integer v, Term t) {
		Term st = this.sMap.get(v);
		if(st != null && !st.equals(t)) return false;
		
		this.sMap.put(v, t);
		return true;
	}
	
	public NormalSubstitution add(NormalSubstitution sub) {
		NormalSubstitution re = new NormalSubstitution(this);
		for(Entry<Integer, Term> entry : sub.sMap.entrySet()) {
			if(!re.add(entry.getKey(), entry.getValue())) return null;
		}
		return re;
	}
	
	public Term getImageOf(Term t, int offset) {
		if(t instanceof Constant) return t;
		int v = ((Variable)t).getValue();
		Term _t = this.sMap.get(v + offset);
		if(_t == null) {
			if(offset != 0) return new Variable(v + offset);
			else return t;
		}
		else return _t;
	}
	
	public Atom getImageOf(Atom a, int offset) {
		ArrayList<Term> terms = a.getTerms();
		ArrayList<Term> substituted_terms = new ArrayList<>();
		
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			substituted_terms.add(getImageOf(t, offset));
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), substituted_terms);
	}
	
	public AtomSet getImageOf(AtomSet atomset, int offset) {
		ArrayList<Atom> atoms = new ArrayList<>();
		
		for(Atom a : atomset) {
			atoms.add(this.getImageOf(a, offset));
		}
		
		return new AtomSet(atoms);
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
