package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConjunctiveQuery {
	Set<Term> ansVar;
	ArrayList<Atom> querybody;
	
	public ConjunctiveQuery() {
		this.ansVar = new HashSet<>();
		this.querybody = new ArrayList<>();
	}
	
	public Set<Term> getAnsVar() {
		return this.ansVar;
	}
	
	public ArrayList<Atom> getBody() {
		return this.querybody;
	}
}
