package org.gu.dcore.reasoning;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;

public interface Substitution {
	
	public Atom getImageOf(Atom a);
	
	public AtomSet getImageOf(AtomSet atomset);
	
	public Rule getImageOf(Rule rule);
}
