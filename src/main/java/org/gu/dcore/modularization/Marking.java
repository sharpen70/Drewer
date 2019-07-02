package org.gu.dcore.modularization;

import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;

public interface Marking {
//	public void mark(Rule r, Rule source, Variable v, PredPosition pp);
	
	public Block getBlocks(Rule r);
}
