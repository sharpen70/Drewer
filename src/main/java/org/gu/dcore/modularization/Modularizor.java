package org.gu.dcore.modularization;

import java.util.LinkedList;
import java.util.List;

import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.Rule;

public class Modularizor {
	private List<Rule> onto;
	private Marking marking;
	private IndexedBlockRuleSet ibs;
	
	public Modularizor(List<Rule> onto) {
		this.onto = onto;
		
		this.marking = new BaseMarking(onto);
	}
	
	public void modularize() {
		for(Rule r : onto) {
			this.marking.mark(r);
		}
		
		for(Rule r: onto) {
			for(BlockRule br : getBlockRules(r))
				this.ibs.add(br);
		}
	}
	
	public IndexedBlockRuleSet getIndexedBlockOnto() {
		return this.ibs;
	}
	
	public List<BlockRule> getBlockRules(Rule r) {
		List<Block> blocks = this.marking.getBlocks(r);
		
		return getBlockRules(r, blocks);
	}
	
	public List<BlockRule> getBlockRules(Rule r, List<Block> blocks) {
		List<BlockRule> blockRules = new LinkedList<>();
		
		blockRules.add(new BlockRule(r));
		
		for(int i = 0; i < blockRules.size(); i++) {
			for(int j = i; j < blocks.size(); j++) {
				BlockRule nbr = blockRules.get(i).add(blocks.get(j));
				if(nbr != null) blockRules.add(nbr);
			}
		}
		
		return blockRules;
	}
	
	public Marking getMarking() {
		return this.marking;
	}
}
