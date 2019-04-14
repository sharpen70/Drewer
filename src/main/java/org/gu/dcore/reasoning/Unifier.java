package org.gu.dcore.reasoning;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;

public class Unifier {
	private Partition partition;
	private AtomSet B;
	private AtomSet H;
	
	public Unifier(AtomSet _B, AtomSet _H, Partition _partition) {
		this.B = _B;
		this.H = _H;
		this.partition = _partition;
	}
	
	public Atom getImageOf(Atom a) {
		return null;
	}
	
	public AtomSet getImageOf(AtomSet atomset) {
		return null;
	}
}
