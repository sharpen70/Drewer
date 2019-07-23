package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BlockRule;

public class IndexedBlockRuleSet {
	Map<Predicate, List<Rule>> indexedByHeadMap;
	Map<Predicate, List<BlockRule>> blockRuleMap;
	Map<Predicate, List<BlockRule>> normalRuleMap;
	
	public IndexedBlockRuleSet() {
		this.indexedByHeadMap = new HashMap<>();
	}
	
	public void add(BlockRule r) {
		if(r.isNormalRule()) add(r, this.normalRuleMap);
		else add(r, this.blockRuleMap);
	}
	
	private void add(BlockRule r, Map<Predicate, List<BlockRule>> map) {
		for(Atom a : r.getHead()) {
			List<BlockRule> rs = map.get(a.getPredicate());
			if(rs == null) {
				rs = new LinkedList<>();
				map.put(a.getPredicate(), rs);
			}
			rs.add(r);
		}
	}
	
	public List<Rule> getRulesByHead(Predicate pred) {		
		return this.indexedByHeadMap.get(pred);
	}
	
	public List<BlockRule> getNormalRule(Predicate pred) {
		return this.normalRuleMap.get(pred);
	}
	
	public List<BlockRule> getBlockRule(Block b) {
		return this.blockRuleMap.get(pred);
	}
}
