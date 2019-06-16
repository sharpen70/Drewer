package org.gu.dcore.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.reasoning.TermType;

public class Rule {
	private AtomSet head;
	private AtomSet body;
	
	private int ruleIndex;
	
	private Set<Variable> existentials;
	private Map<Integer, Boolean> headVarType = null; 
	
	private int max_var;
	
	public Rule(AtomSet head, AtomSet body, int index, int max_var) {
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
	
	public boolean isExistentialVar(int v) {
		if(this.headVarType == null) this.computeFrontierAndExistentials();
		return this.headVarType.get(v);
	}
	
	private void computeFrontierAndExistentials() {
		Set<Variable> body_vars = this.body.getVariables();
		Set<Variable> head_vars = this.head.getVariables();
		
		this.existentials = new HashSet<>();
		this.headVarType = new HashMap<>();
		
		for(Variable v : head_vars) {
			if(body_vars.contains(v)) this.headVarType.put(v.getValue(), false);
			else {
				this.headVarType.put(v.getValue(), true);
				this.existentials.add(v);
			}
		}
	}
	
	public Set<Variable> getExistentials() {
		if(this.existentials == null) computeFrontierAndExistentials();
		return this.existentials;
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
