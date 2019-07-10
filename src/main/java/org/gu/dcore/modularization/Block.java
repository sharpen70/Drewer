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
	
	public boolean overlap(Block b) {
		for(Atom a : b.bricks) {
			if(this.bricks.contains(a)) return true;
		}
		
		return false;
	}
	
	public boolean contains(Atom a) {
		return this.bricks.contains(a);
	}
	
	@Override
	public String toString() {
		String s = "{";
		
		boolean first = true;
		
		for(Atom a : bricks) {
			if(!first) s += ", ";
			 first = false; 
			s += a.toString();
		}
		s += "}" + this.source.getRuleIndex();
		
		return s;
	}
}
