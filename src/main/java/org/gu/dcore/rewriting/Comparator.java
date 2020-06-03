package org.gu.dcore.rewriting;

import org.gu.dcore.model.AtomSet;

public interface Comparator {
	public boolean compare(AtomSet source, AtomSet target);
}
