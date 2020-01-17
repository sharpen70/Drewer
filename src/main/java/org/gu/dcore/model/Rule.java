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
	
	private Set<Variable> existentials = null;
	private Set<Variable> frontier_vars = null;
	
	boolean query;
	
	public Rule(AtomSet head, AtomSet body, int index) {
		this.head = head;
		this.body = body;
		this.ruleIndex = index;
	}
	
	public Rule(Rule r) {
		this.head = r.head;
		this.body = r.body;
		this.ruleIndex = r.ruleIndex;
		this.query = r.query;
	}
	
	public AtomSet getHead() {
		return this.head;
	}
	
	public AtomSet getBody() {
		return this.body;
	}
	
	public boolean isExistentialVar(int v) {
		if(this.existentials == null) this.computeFrontierAndExistentials();
		return this.existentials.contains(new Variable(v));
	}
	
	private void computeFrontierAndExistentials() {
		Set<Variable> body_vars = this.body.getVariables();
		Set<Term> head_terms = this.head.getTerms();
		
		this.existentials = new HashSet<>();
		this.frontier_vars = new HashSet<>();
		
		for(Term t : head_terms) {
			if(t instanceof Variable) {
				Variable v = (Variable)t;
				if(!body_vars.contains(v)) this.existentials.add(v);
				else this.frontier_vars.add((Variable)t);
			}
//			else {
//				this.frontier_vars.add(t);
//			}
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
				try {
				if(a.getTerm(i).equals(v)) indice.add(i);
				}
				catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					System.out.println(a + " " + a.getPredicate().getArity());
				}
			}
			
			if(!indice.isEmpty()) pp.add(new PredPosition(a.getPredicate(), indice));
		}
		
		return pp;
	}
	
	public Set<Variable> getExistentials() {
		if(this.existentials == null) computeFrontierAndExistentials();
		return this.existentials;
	}
	
	public Set<Variable> getFrontierVariables() {
		if(this.frontier_vars == null) computeFrontierAndExistentials();
		return this.frontier_vars;
	}
	
	public boolean isExRule() {
		return !this.getExistentials().isEmpty();
	}
	
	public boolean isQueryRule() {
		return this.query;
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
		String s = ""; 
		s += head.toString();
		s += " :- ";
		s += body.toString();
		s += " .";
		
		return s;
	}
	
	public String toVLog() {
		String s = "";
		s += head.toVLog(this.getExistentials());
		s += " :- ";
		s += body.toVLog(this.getExistentials());

		s += " .";
		
		return s;
	}
	
	public String toRDFox() {
		String s = "";
		s += body.toRDFox();
		s += " -> ";
		s += head.toRDFox();
		s += " .";
		
		return s;
	}
	
	public String toDLV() {
		if(this.head.size() == 1) {
			String s = "";
			if(!this.getExistentials().isEmpty()) {
				s += "#exists{";
				boolean first = true;
				for(Variable v : this.getExistentials()) {
					if(!first) s += ",";
					else first = false;
					
					s += v.toString();
				}
				s += "} ";
			}
			s += head.toShort();
			s += " :- ";
			s += body.toShort();
	
			s += " .";
			
			return s;
		}
		else {
			String s = "#exists{";
			boolean first = true;
			for(Variable v : this.getExistentials()) {
				if(!first) s += ",";
				else first = false;
				
				s += v.toString();
			}
			s += "} ";
			
			Set<Variable> headvars = new HashSet<>();
			headvars.addAll(this.getExistentials());
			headvars.addAll(this.getFrontierVariables());
			
			String aux = "aux" + this.ruleIndex;
			aux += "(";
			
			first = true;
			for(Variable v : headvars) {
				if(!first) aux += ",";
				else first = false;
				
				aux += v.toString();
			}
			aux += ")";
			
			s += aux;
			s += " :- ";
			s += body.toShort();
	
			s += ".\n";
			
			for(Atom a : head) {
				s += a.toShort() + " :- " + aux + ".\n";
			}
			
			return s;
		}
	}
}
