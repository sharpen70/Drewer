package org.gu.dcore.modularization;

import java.util.ArrayList;
import java.util.List;

import org.gu.dcore.model.Rule;

public class BlockRule extends Rule {
	private List<Block> blocks;
	private List<Block> singleBlocks;
	
	public BlockRule(Rule r, Block block) {
		super(r);
		this.blocks = new ArrayList<>();
		this.blocks.add(block);
	}
	
	public BlockRule(Rule r, List<Block> blocks) {
		super(r);
		this.blocks = blocks;
	}
	
	public BlockRule add(Block block) {
		for(Block b : this.blocks) {
			if(b.overlap(block)) return null;
		}
		
		List<Block> nb = new ArrayList<>();
		nb.addAll(this.blocks);
		nb.add(block);
		
		return new BlockRule(this, nb);
	}
}
