package org.gu.dcore.model;

import java.util.Map;

import org.gu.dcore.store.Column;

public class LiftedAtomSet extends AtomSet {
	private Column column;
	
	public LiftedAtomSet(AtomSet A, Column column) {
		super(A);
		this.column = column;
	}
	
	public Column getColumn() {
		return this.column;
	}
}
