package org.gu.dcore.model;

import java.util.Map;

import org.gu.dcore.store.Column;

public class LiftedAtomSet extends AtomSet {
	private Column column;
	private Map<Term, Term> liftedMap;
	
	public LiftedAtomSet(AtomSet A, Column column, Map<Term, Term> liftedMap) {
		super(A);
		this.column = column;
		this.liftedMap = liftedMap;
	}
	
	public Map<Term, Term> getLiftedMap() {
		return this.liftedMap;
	}
	
	public Column getColumn() {
		return this.column;
	}
}
