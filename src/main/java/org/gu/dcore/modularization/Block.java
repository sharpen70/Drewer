package org.gu.dcore.modularization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;

public class Block {
	private List<Rule> sources;
	private List<Rule> pass_sources;
	
	private List<Block> nestedBlock;	
	private Set<Atom> bricks;
	
	public Block(Block ... blocks) {
		this.nestedBlock = new LinkedList<>();
		this.sources = new LinkedList<>();
		this.pass_sources = new LinkedList<>();
		this.bricks = new HashSet<>();
		
		for(Block b : blocks) {
			this.nestedBlock.add(b);
			this.sources.addAll(b.sources);
			this.pass_sources.addAll(b.pass_sources);
			this.bricks.addAll(b.bricks);
		}
	}
	
	public Block(Set<Atom> bricks, Rule source, boolean pass) {
		this.bricks = bricks;
		this.sources = new LinkedList<>();
		this.sources.add(source);
		this.pass_sources = new LinkedList<>();
		if(pass) this.pass_sources.add(source);
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
	
	public List<Rule> getPassSources() {
		return this.pass_sources;
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
