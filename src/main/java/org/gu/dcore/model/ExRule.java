package org.gu.dcore.model;

import java.util.Map;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.reasoning.TermType;

public class ExRule {
	private AtomSet head;
	private AtomSet body;
	
	private long ruleIndex;
	
	private Set<Variable> existentials = null;
	private Set<Variable> frontier = null;
	private Map<Variable, TermType> vartype = null; 
	
	private int var_bound = -1;
	
	public ExRule(AtomSet head, AtomSet body, long index) {
		this.head = head;
		this.body = body;
		this.ruleIndex = index;
	}
	
	public AtomSet getHead() {
		return this.head;
	}
	
	public AtomSet getBody() {
		return this.body;
	}
	
//	public Set<Variable> getExistentials() {
//		if(this.existentials == null) this.computeFrontierAndExistentials();
//		return this.existentials;
//	}
//	
//	public Set<Variable> getFrontier() {
//		if(this.frontier == null) this.computeFrontierAndExistentials();
//		return this.frontier;
//	}
	
	public TermType getTermType(Term t) {
		if(t instanceof Constant) return TermType.CONSTANT;
		if(this.vartype == null) this.computeFrontierAndExistentials();
		return this.vartype.get((Variable)t);
	}
	
	public int getVarBound() {
		if(this.var_bound == -1) this.computeFrontierAndExistentials();
		return this.var_bound;
	}
	
//	private void computeFrontierAndExistentials() {
//		Set<Variable> body_vars = this.body.getVariable();
//		Set<Variable> head_vars = this.head.getVariable();
//		
//		this.existentials = new HashSet<>();
//		this.frontier = new HashSet<>();
//		
//		for(Variable v : head_vars) {
//			if(body_vars.contains(v)) this.frontier.add(v);
//			else this.existentials.add(v);
//		}
//		
//		this.var_bound = body_vars.size() + this.existentials.size();
//	}
	
	private void computeFrontierAndExistentials() {
		Set<Variable> body_vars = this.body.getVariables();
		Set<Variable> head_vars = this.head.getVariables();
		
//		this.existentials = new HashSet<>();
//		this.frontier = new HashSet<>();
		this.var_bound = body_vars.size();
		
		for(Variable v : head_vars) {
			if(body_vars.contains(v)) this.vartype.put(v, TermType.FRONTIER);
			else {
				this.vartype.put(v, TermType.EXISTENTIAL);
				this.var_bound++;
			}
		}
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
