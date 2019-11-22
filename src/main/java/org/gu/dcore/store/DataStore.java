package org.gu.dcore.store;

import java.util.List;

import org.gu.dcore.model.Atom;
import org.gu.dcore.reasoning.Substitution;

public interface DataStore {
	List<Substitution> getAtomMappings(Atom a);
}
