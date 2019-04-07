package org.gu.dcore.model;

import java.util.ArrayList;

public class AtomSet {
	private ArrayList<Atom> atoms;
	
	public AtomSet(ArrayList<Atom> _atoms) {
		this.atoms = _atoms;
	}
	
	public ArrayList<Atom> getAtoms() {
		return this.atoms;
	}
}
