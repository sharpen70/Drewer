package org.gu.dcore.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.grd.PredPosition;

public class Rule {
	protected AtomSet head;
	protected AtomSet body;
	
	private int ruleIndex;
	
	private Set<Variable> existentials;
	private Set<Integer> exVar = null;
	private Set<Term> frontier_terms = null;
	
	private int max_var;
	
	public Rule(AtomSet head, AtomSet body, int index, int max_var) {
		this.head = head;
		this.body = body;
		this.ruleIndex = index;
		this.max_var = max_var;
	}
	
	public Rule(Rule r) {
		this.head = r.head;
		this.body = r.body;
		this.ruleIndex = r.ruleIndex;
		this.max_var = r.max_var;
	}
	
	public AtomSet getHead() {
		return this.head;
	}
	
	public AtomSet getBody() {
		return this.body;
	}
	
	public int getMaxVar() {
		return this.max_var;
	}
	
	public boolean isExistentialVar(int v) {
		if(this.exVar == null) this.computeFrontierAndExistentials();
		return this.exVar.contains(new Variable(v));
	}
	
	private void computeFrontierAndExistentials() {
		Set<Variable> body_vars = this.body.getVariables();
		Set<Term> head_terms = this.head.getTerms();
		
		this.existentials = new HashSet<>();
		this.frontier_terms = new HashSet<>();
		this.exVar = new HashSet<>();
		
		for(Term t : head_terms) {
			if(t instanceof Variable) {
				Variable v = (Variable)t;
				if(!body_vars.contains(v)) this.existentials.add(v);
				else this.frontier_terms.add(t);
			}
			else {
				this.frontier_terms.add(t);
			}
		}
	}
	
	public int getRuleIndex() {
		return this.ruleIndex;
	}
	
	public List<PredPosition> getHeadPositions(Variable v) {
		List<PredPosition> pp = new LinkedList<>();
		
		for(Atom a : this.head) {
			Set<Integer> indice = new HashSet<>();
			
			for(int i = 0; i < a.getPredicate().getArity(); i++) {
				if(a.getTerm(i).equals(v)) indice.add(i);
			}
			
			if(!indice.isEmpty()) pp.add(new PredPosition(a.getPredicate(), indice));
		}
		
		return pp;
	}
	
	public Set<Variable> getExistentials() {
		if(this.existentials == null) computeFrontierAndExistentials();
		return this.existentials;
	}
	
	public Set<Term> getFrontierTerm() {
		return this.frontier_terms;
	}
	
	public boolean isExRule() {
		return !this.getExistentials().isEmpty();
	}
	
	@Override
	public int hashCode() {
		return this.ruleIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Rule)) return false;
		Rule _obj = (Rule)obj;
		return _obj.ruleIndex == this.ruleIndex;
	}
	
	@Override
	public String toString() {
		String s = "[" + this.ruleIndex + "] "; 
		s += head.toString();
		s += " <- ";
		s += body.toString();
		s += ".";
		
		return s;
	}
}
