package org.gu.dcore.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.reasoning.TermType;

public class ExRule {
	private AtomSet head;
	private AtomSet body;
	
	private int ruleIndex;
	
	private Map<Variable, TermType> vartype = null; 
	
	private int max_var;
	
	public ExRule(AtomSet head, AtomSet body, int index, int max_var) {
		this.head = head;
		this.body = body;
		this.ruleIndex = index;
		this.max_var = max_var;
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
		this.vartype = new HashMap<>();
		
		for(Variable v : head_vars) {
			if(body_vars.contains(v)) this.vartype.put(v, TermType.FRONTIER);
			else {
				this.vartype.put(v, TermType.EXISTENTIAL);
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
