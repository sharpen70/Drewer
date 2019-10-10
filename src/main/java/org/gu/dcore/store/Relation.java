package org.gu.dcore.store;

import java.util.HashSet;
import java.util.List;

import org.gu.dcore.model.Predicate;

public class Relation {
	private Predicate predicate;
	private HashSet<List<Long>> tuples;
	
	public Relation(Predicate predicate) {
		this.predicate = predicate;
	}
	
	public void add(List<Long> tuple) {
		this.tuples.add(tuple);
	}
	
	public HashSet<List<Long>> getTuples() {
		return this.tuples;
	}
	
	public Predicate getPredicate() {
		return this.predicate;
	}
}
