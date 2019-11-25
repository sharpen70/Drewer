package org.gu.dcore.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Term;
import org.gu.dcore.reasoning.Substitution;

public class Relation {
	private Predicate predicate;
	private List<ArrayList<Long>> tuples;
	private int arity;
	
	public Relation(Predicate predicate) {
		this.predicate = predicate;
		this.arity = predicate.getArity();
	}
	
	public void add(ArrayList<Long> tuple) {
		this.tuples.add(tuple);
	}
	
	public List<ArrayList<Long>> getTuples() {
		return this.tuples;
	}
	
	public Predicate getPredicate() {
		return this.predicate;
	}
	
	public Map<Term, List<Long>> getMatchTerms(ArrayList<Term> map_terms) {
		return null;
	}
}
