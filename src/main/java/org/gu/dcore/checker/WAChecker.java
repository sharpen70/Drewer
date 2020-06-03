package org.gu.dcore.checker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.grd.TGraph;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;

public class WAChecker {
	private List<Rule> onto;
	private Collection<Predicate> pset;
	private TGraph graph;
	private Map<PredPosition, Integer> pmap = new HashMap<>();
	private Map<Integer, Set<Integer>> vlink = new HashMap<>();
	
	public WAChecker(List<Rule> onto, Collection<Predicate> pset) {
		this.onto = onto;
		this.pset = pset;
		
		buildGraph();
	}
	
	private void buildGraph() {
		int pn = 0;
		
		for(Predicate p : pset) {
			for(int i = 0; i < p.getArity(); i++) {
				pmap.put(new PredPosition(p, i), pn++);
				
//				System.out.println(p + " " + i + ": " + (pn - 1));
			}
		}
		
		this.graph = new TGraph(pmap.keySet().size());
		
		for(Rule r : this.onto) {
			Set<Variable> vars = r.getFrontierVariables();
			Set<Variable> evars = r.getExistentials();
			
			Set<Integer> enode = new HashSet<>();
			
//			System.out.println(r);
//			System.out.println(evars);
			
			for(Variable v : evars) {
				for(Atom a : r.getHead()) {
					for(int i = 0; i < a.getTerms().size(); i++) {
						if(a.getTerm(i).equals(v)) {
							PredPosition pp = new PredPosition(a.getPredicate(), i);
							enode.add(pmap.get(pp));
						}
					}
				}
			}
			
			for(Variable v : vars) {
				Set<Integer> bnode = new HashSet<>();
				Set<Integer> hnode = new HashSet<>();
				
				for(Atom a : r.getBody()) {
					for(int i = 0; i < a.getTerms().size(); i++) {
						if(a.getTerm(i).equals(v)) {
							PredPosition pp = new PredPosition(a.getPredicate(), i);
					
							bnode.add(pmap.get(pp));
						}
					}
				}
				
				for(Atom a : r.getHead()) {
					for(int i = 0; i < a.getTerms().size(); i++) {
						if(a.getTerm(i).equals(v)) {
							PredPosition pp = new PredPosition(a.getPredicate(), i);
							hnode.add(pmap.get(pp));
						}
					}
				}
				
//				System.out.println("Edges:");
				
				for(int i : bnode) {
					for(int j : hnode) {
						this.graph.addEdge(i, j);
//						System.out.println(i + " " + j);
					}
				}
				
				for(int i : bnode) {
					for(int j : enode) {
						this.graph.addEdge(i, j);
//						System.out.println(i + " " + j);
						Set<Integer> links = this.vlink.get(i);
						if(links == null) {
							links = new HashSet<>();
							this.vlink.put(i, links);
						}
						links.add(j);
					}
				}
			}
		}
	}
	
	public boolean check() {
		List<List<Integer>> sccs = this.graph.getSCCs();
		
		boolean wa = true;
		
//		for(Entry<Integer, Set<Integer>> entry : this.vlink.entrySet()) {
//			System.out.println("Vlink: " + entry.getKey() + " " + entry.getValue());
//		}
//		
		for(List<Integer> scc : sccs) {
//			System.out.println("scc " + scc);
			for(int i : scc) {
				Set<Integer> vnode = vlink.get(i);
				
				if(vnode != null) {
					for(int j : scc) {
						if(vnode.contains(j)) return false;
					}
				}
			}
		}
		
		return wa;
	}
}
