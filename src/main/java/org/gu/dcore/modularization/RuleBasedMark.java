package org.gu.dcore.modularization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class RuleBasedMark {
	public Rule rule;
	public Map<Rule, List<Map<Atom, Set<Integer>>>> marked;
	private Map<Rule, Map<Atom, List<Set<Integer>>>> markedAtoms;
	
	public Map<Rule, Set<Variable>> markedHeadVars;
	
	public RuleBasedMark(Rule rule) {
		this.rule = rule;
		this.marked = new HashMap<>();
		this.markedAtoms = new HashMap<>();
		this.markedHeadVars = new HashMap<>();
	}
	
	public List<PredPosition> add(Rule source, PredPosition pp) {
		List<Map<Atom, Set<Integer>>> lm = this.marked.get(source);
		Map<Atom, List<Set<Integer>>> ma = this.markedAtoms.get(source);
		
		Set<Variable> mhv = this.markedHeadVars.get(source);
		
		Set<Variable> newMarked = new HashSet<>();
		
		if(lm == null) {
			lm = new ArrayList<>();
			Map<Atom, Set<Integer>> m = new HashMap<>();
			lm.add(m);
			this.marked.put(source, lm);
		}
		
		if(ma == null) {
			ma = new HashMap<>();
			this.markedAtoms.put(source, ma);
		}
		
		Set<Atom> atomsMarkChanged = new HashSet<>();
		Set<Atom> atomsNewMark = new HashSet<>();
		
		for(Atom a : this.rule.getBody()) {
			if(a.getPredicate().equals(pp.getPredicate())) {
				List<Set<Integer>> markPositions = ma.get(a);
				boolean exist = false;
				if(markPositions != null) {
					for(Set<Integer> pos : markPositions) {
						if(!pos.equals(pp.getIndice())) atomsMarkChanged.add(a);
					}
				}
				else { 
					markPositions = new LinkedList<>();
					ma.put(a, markPositions);
					atomsNewMark.add(a);
				}	 
			}
		}
		
		List<Integer> changed = new LinkedList<>();
		for(Atom a : atomsNewMark) {
			for(int i = 0; i < lm.size(); i++) {
				Map<Atom, Set<Integer>> m = lm.get(i);
				m.put(a, pp.getIndice());
				changed.add(i);
			}
		}
		int start = lm.size();
		for(Atom a : atomsMarkChanged) {
			Map<Atom, Set<Integer>> bodymark = new HashMap<>();
			for(Entry<Atom, List<Set<Integer>>> entry : ma.entrySet()) {
				if(entry.getKey().equals(a)) {
					for(Set<Integer> indice : entry.getValue()) {
						
					}
				}
			}
 		}
		for(Entry<Atom, List<Set<Integer>>> entry : ma.entrySet()) {
			
		}
		
		if(ma.get(pp.getPredicate()) != null)
		if(mhv == null) {
			mhv = new HashSet<>();
			this.markedHeadVars.put(source, mhv);
		}
		
		List<Integer> changed = new LinkedList<>();
		
		for(Atom a : this.rule.getBody()) {
			if(!a.getPredicate().equals(pp.getPredicate())) continue;
			
			boolean exist = false;
			for(int i = 0; i < lm.size(); i++) {
				Map<Atom, Set<Integer>> m = lm.get(i);
				Set<Integer> indice = m.get(a);
				if(indice != null && indice.equals(pp.getIndice())) { 
					exist = true; break;
				}
			}
			if(!exist) {
				
			}
		}
		
		for(int i = 0; i < lm.size(); i++) {
			Map<Atom, Set<Integer>> m = lm.get(i);
			
			for(Atom a : this.rule.getBody()) {
				if(!a.getPredicate().equals(pp.getPredicate())) continue;
				
				Set<Integer> indice = m.get(a);
				
				if(indice == null) {
					indice = new HashSet<>();
					m.put(a, indice);
				}
				
				if(indice.containsAll(pp.getIndice())) {
					continue;
				}
				
				if(pp.getIndice().containsAll(indice)) {
					
				}
				for(Integer index : pp.getIndice()) {
					Term t = a.getTerm(i);
					
					if(t instanceof Variable) 
						if(indice.add(i)) newMarked.add((Variable)t);
				}
			}
		}
		
		
		List<PredPosition> fhead = new LinkedList<>();
		
		if(newMarked.isEmpty()) return fhead;
		
		Set<Term> markedv = new HashSet<>();
		
		for(Entry<Atom, Set<Integer>> entry : m.entrySet()) {
			Set<Integer> idx = entry.getValue();
//				System.out.println(entry.getKey() + " " + idx);
			for(Integer j : idx) {
				Term t = entry.getKey().getTerm(j);
				if(t.isVariable()) markedv.add(t);
			}
		}
		
		boolean through = true;
		
		for(Atom a : this.rule.getBody()) {
			List<Term> lt = a.getTerms();
			
			for(int ti = 0; ti < lt.size(); ti++) {
				Term t = lt.get(ti);
				if(markedv.contains(t)) {
					Set<Integer> indice = m.get(a);
					if(indice == null || !indice.contains(ti)) {
						through = false; break;
					}
				}
			}
			
			if(!through) break;
		}
		
		
		for(Atom a : this.rule.getHead()) {
			for(Variable v : newMarked) {
				if(a.contains(v)) {
					if(mhv.add(v)) fhead.addAll(this.rule.getHeadPositions(v));
				}
			}
		}
		
		return fhead;
	}
	
	public List<Block> getBaseBlocks() {
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Rule source = entry.getKey();
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			blocks.add(new Block(markedPosition.keySet(), source, 
					!this.markedHeadVars.get(source).isEmpty()));
		}
		
		return blocks;
	}
	
	public void printMarked() {
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry: this.marked.entrySet()) {
			System.out.print("    (" + entry.getKey().getRuleIndex() + ") ");
			 Map<Atom, Set<Integer>> mm = entry.getValue();
			 
			for(Atom a : rule.getHead()) {
				Set<Integer> indice = mm.get(a);
				if(indice != null) {
					System.out.print(a.getPredicate().getName() + indice + " ");
				}
			}
			
			System.out.print(": ");
			
			for(Atom a : rule.getBody()) {
				Set<Integer> indice = mm.get(a);
				if(indice != null) {
					System.out.print(a.getPredicate().getName() + indice + " ");
				}
			}
			
			System.out.print("\n");
		}
	}
}