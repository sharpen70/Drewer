package org.gu.dcore.modularization;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;

public class Block {
	private Rule source;
	
	private AtomSet bricks;
	
	public Block() {
		
	}
	
	public Block(AtomSet bricks, Rule source) {
		this.bricks = bricks;
		this.source = source;
	}
}
