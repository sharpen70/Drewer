package org.gu.dcore.store;

import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Term;

public interface DataStore {
	Map<Term, List<Long>> getAtomMappings(Atom a);
}
