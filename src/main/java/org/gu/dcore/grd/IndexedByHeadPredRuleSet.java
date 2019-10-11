package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BlockRule;

public class IndexedByHeadPredRuleSet {
	private Map<Predicate, Set<Rule>> ruleMap;
	
	public IndexedByHeadPredRuleSet() {
		this.ruleMap = new HashMap<>();
	}
	
	public IndexedByHeadPredRuleSet(List<Rule> onto) {
		this.ruleMap = new HashMap<>();
		
		for(Rule r : onto) {
			this.add(r);
		}
	}
	
	public void add(Rule r) {
		AtomSet head = r.getHead();
		
		for(Atom a : head) {
			Set<Rule> c = ruleMap.get(a.getPredicate());
			if(c == null) {
				c = new HashSet<>();
				ruleMap.put(a.getPredicate(), c);
			}
			c.add(r);
		}
	}
	
	public Set<Rule> get(Predicate p) {
		Set<Rule> re = this.ruleMap.get(p);
		if(re == null) return new HashSet<>();
		return re;
	}
	
	public Set<Rule> getRules(AtomSet atomset) {
		Set<Rule> br = new HashSet<>();
		
		for(Atom a : atomset) {
			Set<Rule> rs = this.ruleMap.get(a.getPredicate());
			if(rs != null)
				br.addAll(rs);
		}
		
		return br;
	}
}
