package org.gu.dcore.modularization;

import java.util.LinkedList;
import java.util.List;

import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.Rule;

public class Modularizor {
	private List<Rule> onto;
	private BaseMarking marking;
	private IndexedBlockRuleSet ibs;
	private List<BlockRule> blockonto;
	
	public Modularizor(List<Rule> onto) {
		this.onto = onto;
		this.marking = new BaseMarking(onto);
	}
	
	public void modularize() {
		System.out.println("Modularizing ontology ...");
		long start = System.currentTimeMillis();
		
		this.ibs = new IndexedBlockRuleSet();
		this.blockonto = new LinkedList<>();
		
		for(Rule r : onto) {
			this.marking.mark(r);
		}
		
		for(Rule r: onto) {
			BlockRule br = this.marking.getBlockRule(r);
			this.ibs.add(br);
			this.blockonto.add(br);
		}
		System.out.println("Done modularization, taking " + (System.currentTimeMillis() - start) + " ms");
	}
	
	public IndexedBlockRuleSet getIndexedBlockOnto() {
		if(this.ibs == null) modularize();
		return this.ibs;
	}
	
	
	public List<BlockRule> getBlockOnto() {
		if(this.blockonto == null) modularize();
		return this.blockonto;
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
