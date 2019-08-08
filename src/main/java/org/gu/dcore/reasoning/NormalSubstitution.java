package org.gu.dcore.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class NormalSubstitution implements Substitution {
	private Map<Variable, Term> sMap = null;
	
	public NormalSubstitution() {
		this.sMap = new HashMap<>();
	}
	
	public NormalSubstitution(NormalSubstitution sub) {
		this.sMap = new HashMap<>(sub.sMap);
	}
	
	public boolean add(Variable v, Term t) {
		Term st = this.sMap.get(v);
		if(st != null && !st.equals(t)) return false;
		
		this.sMap.put(v, t);
		return true;
	}
	
	public NormalSubstitution add(NormalSubstitution sub) {
		NormalSubstitution re = new NormalSubstitution(this);
		for(Entry<Variable, Term> entry : sub.sMap.entrySet()) {
			if(!re.add(entry.getKey(), entry.getValue())) return null;
		}
		return re;
	}
	
	public Term getImageOf(Term t) {
		if(t instanceof Constant) return t;
		Term _t = this.sMap.get(t);
		if(_t == null) return t;
		else return _t;
	}
	
	public Atom getImageOf(Atom a) {
		ArrayList<Term> terms = a.getTerms();
		ArrayList<Term> substituted_terms = new ArrayList<>();
		
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			if(t instanceof Variable) {
				Term _t = this.sMap.get((Variable)t);
				if(_t == null) substituted_terms.add(i, t);
				else substituted_terms.add(i, _t);
			}
			else substituted_terms.add(i, t);
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), substituted_terms);
	}
	
	public AtomSet getImageOf(AtomSet atomset) {
		ArrayList<Atom> atoms = new ArrayList<>();
		
		for(Atom a : atomset) {
			atoms.add(this.getImageOf(a));
		}
		
		return new AtomSet(atoms);
	}
	
	public Rule getImageOf(Rule rule) {
		AtomSet head = rule.getHead();
		AtomSet body = rule.getBody();
		
		AtomSet substituted_head = this.getImageOf(head);
		AtomSet substituted_body = this.getImageOf(body);
		
		return RuleFactory.instance().createRule(substituted_head, substituted_body, rule.getMaxVar());
	}
	
	public Set<Term> getImageOf(Set<Variable> vars) {
		Set<Term> image = new HashSet<>();
		for(Variable v : vars) {
			Term t = this.sMap.get(v);
			if(t != null) image.add(t);
		}		
		return image;
	}
}
