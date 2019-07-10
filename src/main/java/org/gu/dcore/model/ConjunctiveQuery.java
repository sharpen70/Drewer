package org.gu.dcore.model;

import java.util.HashSet;
import java.util.Set;

public class ConjunctiveQuery {
	Set<Variable> ansVar;
	Set<Atom> querybody;
	
	public ConjunctiveQuery() {
		this.ansVar = new HashSet<>();
		this.querybody = new HashSet<>();
	}
}
