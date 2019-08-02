package org.gu.dcore.modularization;

import java.util.List;

import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.Rule;

public class Modularizor {
	private List<Rule> onto;
	private BaseMarking marking;
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
			this.ibs.add(this.marking.getBlockRule(r));
		}
	}
	
	public IndexedBlockRuleSet getIndexedBlockOnto() {
		return this.ibs;
	}
	
//	public List<BlockRule> getBlockRules(Rule r, List<Block> blocks) {
//		List<BlockRule> blockRules = new LinkedList<>();
//		
//		blockRules.add(new BlockRule(r));
//		
//		for(int i = 0; i < blockRules.size(); i++) {
//			for(int j = i; j < blocks.size(); j++) {
//				BlockRule nbr = blockRules.get(i).add(blocks.get(j));
//				if(nbr != null) blockRules.add(nbr);
//			}
//		}
//		
//		return blockRules;
//	}
	
	public BaseMarking getMarking() {
		return this.marking;
	}
}
