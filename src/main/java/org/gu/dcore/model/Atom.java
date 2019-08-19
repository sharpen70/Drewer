package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Atom class, without functional terms
 * @author sharpen
 * @version 1.0, April 2018. 
 */
public class Atom {
	private Predicate p;
	private ArrayList<Term> terms;
	private Set<Variable> vars;
	
	public Atom(Predicate p, ArrayList<Term> terms) {
		this.p = p;
		this.terms = terms;
	}
	/**
	 * @return the p
	 */
	public Predicate getPredicate() {
		return p;
	}
	/**
	 * @param p the p to set
	 */
	public void setP(Predicate p) {
		this.p = p;
	}
	/**
	 * @return the terms
	 */
	public ArrayList<Term> getTerms() {
		return terms;
	}
	
	public Term getTerm(int i) {
		return this.terms.get(i);
	}
	
	/**
	 * @param terms the terms to set
	 */
	public void setTerms(ArrayList<Term> terms) {
		this.terms = terms;
	}
	
	public Set<Variable> getVariables() {
		if(this.vars == null) vars();
		return this.vars;
	}
	
	public boolean contains(Variable v) {
		if(this.vars == null) vars();
		return this.vars.contains(v);
	}
	
	private void vars() {
		this.vars = new HashSet<>();
		
		for(Term t : this.terms) {
			if(t instanceof Variable) vars.add((Variable) t);
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		hash = 31 * hash + this.p.hashCode();
		
		for(Term t : this.terms) {
			hash = 31 * hash + t.hashCode();
		}
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Atom)) return false;
		
		Atom a = (Atom)obj;
		
		if(!this.p.equals(a.p)) return false;
//		
//		for(int i = 0; i < this.terms.size(); i++) {
//			if(!this.terms.get(i).equals(a.terms.get(i))) return false;
//		}
		if(!this.terms.equals(a.terms)) return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		String out = p.toString();
		out = out + "(";
		
		for(int i = 0; i < terms.size(); i++) {
			Term t = terms.get(i);
			out = out + t;
			if(i != terms.size() - 1) {
				out = out + ", ";
			}
		}
		
		out = out + ")";
		
		return out;
	}
}
