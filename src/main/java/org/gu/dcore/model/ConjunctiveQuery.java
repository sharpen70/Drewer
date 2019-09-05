package org.gu.dcore.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConjunctiveQuery {
	Set<Term> ansVar;
	AtomSet querybody;
	
	public ConjunctiveQuery(List<Term> ansVar, AtomSet body) {
		this.ansVar = new HashSet<>();
		this.ansVar.addAll(ansVar);
		this.querybody = body;
	}
	
	public Set<Term> getAnsVar() {
		return this.ansVar;
	}
	
	public AtomSet getBody() {
		return this.querybody;
	}
	
	@Override
	public String toString() {
		String s = "?(";
		
		boolean first = true;
		
		for(Term t : ansVar) {
			if(!first) s += ",";
			s += t;
			first = false;
		}
		
		s += ") :- ";
		s += this.querybody;
		s += " .";
		
		return s;
	}
	
	public String toRDFox() {
		String s = "q(";
		
		boolean first = true;
		
		for(Term t : ansVar) {
			if(!first) s += ",";
			s += t.toRDFox();
			first = false;
		}
		
		s += ") <- ";
		s += this.querybody.toRDFox();
		s += " .";
		
		return s;
	}
}
