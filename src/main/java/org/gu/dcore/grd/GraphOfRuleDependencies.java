package org.gu.dcore.grd;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;

public class GraphOfRuleDependencies {
	private TGraph grd;
	private List<Rule> onto;
	
	private Map<Integer, Rule> ruleMap;
	private List<List<Rule>> loopsRule;
	private boolean computed = false;
	
	public GraphOfRuleDependencies(List<Rule> rules) {
		this.onto = rules;
		this.ruleMap = new HashMap<>();
		
		buildGraph();
	}
	
	private void buildGraph() {
		this.grd = new TGraph(onto.size());
		
		int i = 0;
		for(Rule r : onto) {
			this.ruleMap.put(i, r);
			i++;
			int j = 0;
			for(Rule r1 : onto) {
				boolean e = false;
				for(Atom a : r.getBody()) {
					Predicate p = a.getPredicate();
					for(Atom h : r1.getHead()) {
						if(h.getPredicate().equals(p)) {
							grd.addEdge(i, j);
							e = true;
							break;
						}
					}
					if(e) break;
				}
				j++;
			}
		}	
	}
	
	public List<List<Rule>> getRulesInSCCs() {
		if(!computed) compute();		
		return this.loopsRule;
	}
	
	public List<Rule> getRulesNotInSCCs() {
		if(!computed) compute();
		List<Rule> rs = new LinkedList<>();
		for(Rule r : this.ruleMap.values()) rs.add(r);
		return rs;
	}
	
	private void compute() {
		this.loopsRule  = new LinkedList<List<Rule>>();
		List<List<Integer>> loops = this.grd.getSCCs();
		
		for(List<Integer> loop : loops) {
			List<Rule> rs = new LinkedList<>();
			for(Integer i : loop) {
				Rule r = this.ruleMap.get(i);
				this.ruleMap.remove(i);
				rs.add(r);
			}
			loopsRule.add(rs);
		}
	}
}
