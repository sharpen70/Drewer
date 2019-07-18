package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BlockRule;

public class IndexedBlockRuleSet {
	Map<Predicate, List<Rule>> indexedByHeadMap;
	Map<Predicate, List<BlockRule>> blockRuleMap;
	Map<Predicate, List<Rule>> normalRuleMap;
	
	public IndexedBlockRuleSet(List<Rule> ruleset) {
		this.indexedByHeadMap = new HashMap<>();
		for(Rule r : ruleset) this.add(r);
	}
	
	public void add(Rule r) {
		
	}
	
	public List<Rule> getRulesByHead(Predicate pred) {		
		return this.indexedByHeadMap.get(pred);
	}
	
	public List<Rule> getNormalRule(Predicate pred) {
		List<Rule> nrs = new LinkedList<>();
		
		return nrs;
	}
	
	public List<BlockRule> getBlockRule(Predicate pred, Rule source) {
		List<BlockRule> brs = new LinkedList<>();
		
		return brs;
	}
}
