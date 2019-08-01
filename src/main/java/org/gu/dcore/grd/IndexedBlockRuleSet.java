package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;

public class IndexedBlockRuleSet {
	Map<Predicate, Set<BlockRule>> normalRuleMap;
	Map<Rule, Map<Predicate, Set<BlockRule>>> blockRuleMap;
	
	public IndexedBlockRuleSet() {
		this.normalRuleMap = new HashMap<>();
		this.blockRuleMap = new HashMap<>();
	}
	
	public void add(BlockRule r) {
		if(r.isNormalRule()) {
			for(Atom a : r.getHead()) {
				Set<BlockRule> rs = normalRuleMap.get(a.getPredicate());
				if(rs == null) {
					rs = new HashSet<>();
					normalRuleMap.put(a.getPredicate(), rs);
				}
				rs.add(r);
			}
		}
		else {
			for(Rule s : r.getSourceRules()) {
				Map<Predicate, Set<BlockRule>> smap = this.blockRuleMap.get(s);
				if(smap == null) {
					smap = new HashMap<>();
					this.blockRuleMap.put(s, smap);
				}
				for(Atom a : r.getHead()) {
					Predicate P = a.getPredicate();
					Set<BlockRule> rs = smap.get(P);
					if(rs == null) {
						rs = new HashSet<>();
						smap.put(P, rs);
					}
					rs.add(r);
				}
			}
		}
	}
	
	public Set<BlockRule> getNormalRules(Predicate pred) {
		return this.normalRuleMap.get(pred);
	}
	
	public Set<BlockRule> getBlockRules(Block b, Predicate pred, Set<Rule> sources) {
		Set<BlockRule> br = new HashSet<>();
		
		for(Rule source : b.getSources()) {
			Map<Predicate, Set<BlockRule>> pmap = this.blockRuleMap.get(source);
	
			for(Atom a : b.getBricks()) {
				Set<BlockRule> brs = pmap.get(a.getPredicate());
				if(brs != null) br.addAll(brs);
				else continue;
			}
		}
		return br;
	}
}
