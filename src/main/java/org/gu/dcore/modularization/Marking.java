package org.gu.dcore.modularization;

import java.util.List;

import org.gu.dcore.model.Rule;

public interface Marking {
//	public void mark(Rule r, Rule source, Variable v, PredPosition pp);
	
	public void mark(Rule source);
	
	public List<Block> getBlocks(Rule r);
	
	public void printMarked();
	
	public RuleBasedMark markQueryRule(Rule qr);
}
