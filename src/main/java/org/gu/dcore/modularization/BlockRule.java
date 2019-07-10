package org.gu.dcore.modularization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;

public class BlockRule extends Rule {
	private List<Block> blocks;
	private List<Atom> mbody;
	
	public BlockRule(Rule r) {
		super(r);
		this.blocks = new ArrayList<>();
		this.mbody = new LinkedList<>();
		
		for(Atom a : this.body) this.mbody.add(a);
	}
	
	public BlockRule(BlockRule r) {
		super(r);
		this.blocks = new ArrayList<>();
		this.mbody = new LinkedList<>();
		
		this.blocks.addAll(r.blocks);
		this.mbody.addAll(r.mbody);
	}
	
	public void addBlock(Block block) {
		this.blocks.add(block);
		
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