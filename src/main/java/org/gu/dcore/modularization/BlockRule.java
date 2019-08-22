package org.gu.dcore.modularization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;

public class BlockRule extends Rule {
	private List<Block> blocks;
	private List<Block> passblocks;
	private List<Block> mblocks;
	private List<Atom> mbody;
	
	private Set<Rule> sourceRules;
	
//	public BlockRule(Rule r, List<Block> blocks, List<Rule> sources) {
//		super(r);
//		this.blocks = blocks;
//		this.mbody = new LinkedList<>();
//		this.sourceRules = new HashSet<>();
//		
//		for(Atom a : this.body) {
//			for(Block b : blocks) 
//				if(b.contains(a)) this.mbody.add(a);
//		}
//		this.sourceRules.addAll(sources);
//		if(!r.getExistentials().isEmpty()) this.sourceRules.add(r);
//	}
	
	public BlockRule(Rule r, List<Block> blocks) {
		super(r);
		this.blocks = blocks;
		this.passblocks = new LinkedList<>();
		this.mblocks = new LinkedList<>();
		this.mbody = new LinkedList<>(this.body.getAtoms());
		this.sourceRules = new HashSet<>();		
		
		for(Block b : blocks) {
			if(b.pass) {
				this.passblocks.add(b);
				this.sourceRules.addAll(b.getSources());
			}
			else this.mblocks.add(b);
			
			for(Atom a : b.getBricks()) this.mbody.remove(a);
		}
		
		if(!r.getExistentials().isEmpty()) this.sourceRules.add(r);
	}
	
	public Set<Rule> getSourceRules() {
		return this.sourceRules;
	}

	public List<Block> getBlocks() {
		return this.blocks;
	}
	
	public List<Block> getPassBlocks() {
		return this.passblocks;
	}
	
	public List<Block> getMblocks() {
		return this.mblocks;
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
