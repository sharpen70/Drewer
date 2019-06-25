package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;

public class IndexedByBodyPredRuleSet {
	private Map<Predicate, List<Rule>> ruleMap;
	
	public IndexedByBodyPredRuleSet() {
		this.ruleMap = new HashMap<>();
	}
	
	public IndexedByBodyPredRuleSet(List<Rule> onto) {
		this.ruleMap = new HashMap<>();
		
		for(Rule r : onto) {
			this.add(r);
		}
	}
	
	public void add(Rule r) {
		AtomSet body = r.getBody();
		
		for(Atom a : body) {
			List<Rule> c = ruleMap.get(a.getPredicate());
			if(c == null) {
				c = new LinkedList<>();
				ruleMap.put(a.getPredicate(), c);
			}
			c.add(r);
		}
	}
	
	public List<Rule> get(Predicate p) {
		return this.ruleMap.get(p);
	}
}
