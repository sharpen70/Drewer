package org.gu.dcore.model;

import org.gu.dcore.store.Column;

public class LiftedAtomSet extends AtomSet {
	private Column column;
	
	public LiftedAtomSet(AtomSet A, Column column) {
		super(A);
		this.column = column;
	}
}
