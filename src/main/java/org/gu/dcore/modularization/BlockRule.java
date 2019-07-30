package org.gu.dcore.modularization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;

public class BlockRule extends Rule {
	private List<Block> blocks;
	private List<Atom> mbody;
	
	private Set<Rule> sourceRules;
	
	public BlockRule(Rule r) {
		super(r);
		this.blocks = new ArrayList<>();
		this.mbody = new LinkedList<>();
		this.sourceRules = new HashSet<>();
		
		for(Atom a : this.body) this.mbody.add(a);
		if(!r.getExistentials().isEmpty()) this.sourceRules.add(r);
	}
	
	public BlockRule(BlockRule r) {
		super(r);
		this.blocks = new ArrayList<>();
		this.mbody = new LinkedList<>();
		this.sourceRules = new HashSet<>();
		
		this.blocks.addAll(r.blocks);
		this.mbody.addAll(r.mbody);
		this.sourceRules.addAll(r.sourceRules);
	}
	
	public void addBlock(Block block) {
		this.blocks.add(block);
		this.sourceRules.addAll(block.getPassSources());
		
		Iterator<Atom> it = this.mbody.iterator();
		
		while(it.hasNext()) {
			Atom a = it.next();
			if(block.contains(a)) it.remove();
		}
	}
	
	public BlockRule add(Block block) {
		for(Block b : this.blocks) {
			if(b.overlap(block)) return null;
		}
		
		BlockRule br = new BlockRule(this);
		br.addBlock(block);
		
		return br;
	}
	
	public List<Block> getBlocks() {
		return this.blocks;
	}
	
	public List<Atom> getNormalAtoms() {
		return this.mbody;
	}
	
	public boolean isNormalRule() {
		return this.blocks.isEmpty();
	}
	
	@Override
	public String toString() {
		String s = "[" + this.getRuleIndex() + "] "; 
		s += head.toString();
		s += " <- ";
		
		boolean first = true;
		
		for(Block b : this.blocks) {
			if(!first) 
				s += ", ";
			first = false;
			s += b.toString();
		}
		
		first = this.blocks.isEmpty();
		for(Atom a : this.mbody) {
			if(!first) s += ", ";
			first = false;
			s += a.toString();
		}
		s += ".";
		
		return s;
	}
}
