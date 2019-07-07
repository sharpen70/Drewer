package org.gu.dcore.modularization;

import java.util.List;

import org.gu.dcore.model.Rule;

public interface Marking {
//	public void mark(Rule r, Rule source, Variable v, PredPosition pp);
	
	public List<Block> getBlocks(Rule r);
}
