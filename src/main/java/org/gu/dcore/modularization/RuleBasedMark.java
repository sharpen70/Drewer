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
import org.gu.dcore.utils.Pair;

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
	
	/*
	 * Get more accurate Blocks
	 */
	public List<Block> getConBlocks() {
		int b_id = 0;
		
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			if(markedPosition.isEmpty()) continue;
			
			Set<Variable> markedVarSet = new HashSet<>();
			for(Entry<Atom, Set<Integer>> e : markedPosition.entrySet()) {
				Atom a = e.getKey();
				Set<Integer> positions = e.getValue();
				
				for(int i : positions) {
					Term t = a.getTerm(i);
					if(t instanceof Variable) markedVarSet.add((Variable)t);
				}
			}
			
			List<Pair<Set<Atom>, Variable>> possibleBlocks = new LinkedList<>();
			
			for(Variable v : markedVarSet) {
				Set<Atom> blockAtoms = new HashSet<>();
				
				boolean valid = true;
				
				for(Entry<Atom, Set<Integer>> e : markedPosition.entrySet()) {
					Atom a = e.getKey();
					Set<Integer> positions = e.getValue();
					
					boolean join = false;

					for(int i = 0; i < a.getTerms().size(); i++) {
						Term t = a.getTerm(i);
						if(t instanceof Variable) {
							Variable tv = (Variable)t;
							if(positions.contains(i)) { 
								if (tv.equals(v)) join = true; 
							}
							else if (tv.equals(v)) valid = false;
						}
					}
					
					if(valid) {
						if(join) blockAtoms.add(a);
					}
					else break;	
				}
				
				if(valid) possibleBlocks.add(new Pair<>(blockAtoms, v));
			}
			
			for(Pair<Set<Atom>, Variable> possibleBlock : possibleBlocks) {
				Set<Atom> markedAtoms = markedPosition.keySet();
				boolean valid = true;
				
				for(Atom a : this.rule.getBody()) {
					if(!markedAtoms.contains(a)) {
						if(a.getVariables().contains(possibleBlock.b)) {
							valid = false;
							break;
						}
					}
				}
				
				if(valid) {
					String b_name = "BLK_" + this.rule.getRuleIndex() + "_" + b_id++;
					boolean pass = this.rule.getFrontierVariables().contains(possibleBlock.b);
					blocks.add(new Block(b_name, possibleBlock.a, pass));
				}
			}		
		}
		
		return blocks;
	}
	
	public List<Block> getBaseBlocks() {
		int b_id = 0;
		
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			String b_name = "BLK_" + this.rule.getRuleIndex() + "_" + b_id++;
			Set<Atom> bricks = markedPosition.keySet();
			boolean pass = false;
			
			for(Atom a : bricks) {
				for(Variable v : a.getVariables()) 
					if(this.rule.getFrontierVariables().contains(v)) {
						pass = true;
						break;
					}
				if(pass) break;
			}
			
			
			blocks.add(new Block(b_name, markedPosition.keySet(), pass));
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