package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.List;

import org.gu.dcore.store.Column;

public class LiftedAtomSet extends AtomSet {
	private Column column;
	
	public LiftedAtomSet(ArrayList<Atom> A, Column column) {
		super(A);
		this.column = column;
	}
	
	public LiftedAtomSet(AtomSet A, Column column) {
		super(A);
		this.column = column;
	}
	
	public Column getColumn() {
		return this.column;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += super.toString() + "\n";
		for(int i = 0; i < column.getArity() - 1; i++) {
			s += "rc_" + i + " ";
		}
		s += "\n";
		s += this.column.toString();
		return s;
	}
	
	public List<AtomSet> expand() {
		return null;
	}
}
