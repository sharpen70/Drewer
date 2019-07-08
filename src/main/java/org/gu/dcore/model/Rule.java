package org.gu.dcore.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.grd.PredPosition;

public class Rule {
	protected AtomSet head;
	protected AtomSet body;
	
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
	
	public int getRuleIndex() {
		return this.ruleIndex;
	}
	
	public List<PredPosition> getPositions(Variable v) {
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
