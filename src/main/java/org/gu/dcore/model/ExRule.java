package org.gu.dcore.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.reasoning.TermType;

public class ExRule {
	private AtomSet head;
	private AtomSet body;
	
	private int ruleIndex;
	
	private Map<Variable, Boolean> headVarType = null; 
	
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
	
	public boolean isExistentialVar(Variable v) {
		if(this.headVarType == null) this.computeFrontierAndExistentials();
		return this.headVarType.get(v);
	}
	
	private void computeFrontierAndExistentials() {
		Set<Variable> body_vars = this.body.getVariables();
		Set<Variable> head_vars = this.head.getVariables();
		
		this.headVarType = new HashMap<>();
		
		for(Variable v : head_vars) {
			if(body_vars.contains(v)) this.headVarType.put(v, false);
			else {
				this.headVarType.put(v, true);
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
