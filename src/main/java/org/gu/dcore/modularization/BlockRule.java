package org.gu.dcore.modularization;

import java.util.List;

import org.gu.dcore.model.Rule;

public class BlockRule extends Rule {
	private List<Block> blocks;
	private List<Block> singleBlocks;
	
	public BlockRule(Rule r) {
		super(r);
	}
	
	public void add(Block block) {
		
	}
}
