package org.gu.dcore.modularization;

import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;

public class Block {
	private Rule source;
	
	private Set<Atom> bricks;
	
	public Block() {
		
	}
	
//	public Block(AtomSet bricks, Rule source) {
//		this.bricks = bricks;
//		this.source = source;
//	}
	
	public Block(Set<Atom> bricks, Rule source) {
		this.bricks = bricks;
		this.source = source;
	}
}
