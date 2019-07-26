package org.gu.dcore.modularization;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;

public class Block {
	private List<Rule> sources;
	
	private List<Block> nestedBlock;	
	private Set<Atom> bricks;
	
	public Block(List<Block> blocks) {
		this.sources = new LinkedList<>();
		for(Block b : blocks) {
			this.sources.addAll(b.sources);
		}
		this.nestedBlock = blocks;
	}
//	public Block(AtomSet bricks, Rule source) {
//		this.bricks = bricks;
//		this.source = source;
//	}
	
	public Block(Set<Atom> bricks, Rule source) {
		this.bricks = bricks;
		this.sources = new LinkedList<>();
		this.sources.add(source);
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
	
	public List<Rule> getSources() {
		return this.sources;
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
		s += "}";
		
		return s;
	}
}
