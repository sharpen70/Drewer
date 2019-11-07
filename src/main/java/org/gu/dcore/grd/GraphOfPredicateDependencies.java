package org.gu.dcore.grd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;

public class GraphOfPredicateDependencies {
	private List<Rule> onto;
	
	private TGraph gpd;
	private int size;
	
	private Map<Integer, Predicate> pMap;
	private Map<Predicate, Integer> invPMap;
	
	public GraphOfPredicateDependencies(List<Rule> rules, List<Predicate> predicates) {
		this.onto = rules;
		this.pMap = new HashMap<>();
		this.invPMap = new HashMap<>();
		this.size = predicates.size();
		
		int i = 0;
		
		for(Predicate p : predicates) {
			pMap.put(i, p);
			invPMap.put(p, i);
			i++;
		}
		
		buildGraph();
	}
	
	private void buildGraph() {
		this.gpd = new TGraph(this.size);
		
		for(Rule r : onto) {
			for(Atom h : r.getHead()) {
				int i = this.invPMap.get(h.getPredicate());
				for(Atom b : r.getBody()) {
					int j = this.invPMap.get(b.getPredicate());
					this.gpd.addEdge(i, j);
				}
			}
		}	
	}
	
	/*
	 * the root atoms whose predicates are start nodes for searching, which affects the entry atoms of SCCs
	 */
	public void check(AtomSet rootAtoms) {
		List<Integer> start = new LinkedList<Integer>();
		for(Atom a : rootAtoms)
			start.add(this.invPMap.get(a.getPredicate()));
		this.gpd.computeSCCs(start);
	}
	
	public List<List<Predicate>> getSCCPredicates() {
		List<List<Predicate>> predicatesInSCCs = new LinkedList<List<Predicate>>();
		List<List<Integer>> SCCs = this.gpd.getSCCs();
		
		for(List<Integer> nodes : SCCs) {
			List<Predicate> preds = new LinkedList<Predicate>();
			for(Integer i : nodes) preds.add(this.pMap.get(i));
			predicatesInSCCs.add(preds);
		}
		return predicatesInSCCs;
	}
	/*
	 * Get predicates not in SCCs
	 */
	public List<Predicate> getFreePredicates() {
		List<Predicate> preds = new LinkedList<Predicate>();
		List<Integer> nodes = this.gpd.getFreeNodes();
		
		for(Integer i : nodes) preds.add(this.pMap.get(i));
		
		return preds;
	}
//	
//	/*
//	 * Get predicates that are the first to enter a SCC
//	 */
//	public List<Predicate> getEntryPredicates() {
//		List<Predicate> preds = new LinkedList<Predicate>();
//		List<Integer> nodes = this.gpd.getEntryNodes();
//		
//		for(Integer i : nodes) preds.add(this.pMap.get(i));
//		
//		return preds;
//	}
}
