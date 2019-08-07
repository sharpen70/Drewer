package org.gu.dcore.modularization;

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
	public Map<Rule, Map<Atom, Set<Integer>>> marked;
	public Map<Rule, Set<Variable>> markedHeadVars;
	
	public RuleBasedMark(Rule rule) {
		this.rule = rule;
		this.marked = new HashMap<>();
		this.markedHeadVars = new HashMap<>();
	}
	
	public List<PredPosition> add(Rule source, PredPosition pp) {
		Map<Atom, Set<Integer>> m = this.marked.get(source);
		Set<Variable> mhv = this.markedHeadVars.get(source);
		
		Set<Variable> newMarked = new HashSet<>();
		
		if(m == null) {
			m = new HashMap<>();
			this.marked.put(source, m);
		}
		
		if(mhv == null) {
			mhv = new HashSet<>();
			this.markedHeadVars.put(source, mhv);
		}
		
		for(Atom a : this.rule.getBody()) {
			if(!a.getPredicate().equals(pp.getPredicate())) continue;
			
			Set<Integer> indice = m.get(a);
			
			if(indice == null) {
				indice = new HashSet<>();
				m.put(a, indice);
			}
			
			for(Integer i : pp.getIndice()) {
				Term t = a.getTerm(i);
				
				if(t instanceof Variable) 
					if(indice.add(i)) newMarked.add((Variable)t);
			}
		}
		
		List<PredPosition> pass = new LinkedList<>();
		
		for(Atom a : this.rule.getHead()) {
			for(Variable v : newMarked) {
				if(a.contains(v)) {
					if(mhv.add(v)) pass.addAll(this.rule.getHeadPositions(v));
				}
			}
		}
		
		return pass;
	}
	
	public List<Block> getBaseBlocks() {
		int b_id = 0;
		
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Rule source = entry.getKey();
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			String b_name = "BLK:" + this.rule.getRuleIndex() + ":" + b_id++;
			
			blocks.add(new Block(b_name, markedPosition.keySet(), source, 
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