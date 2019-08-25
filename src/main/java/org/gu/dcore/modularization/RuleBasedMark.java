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
	
	/*
	 * Get more accurate Blocks
	 */
	public List<Block> getConBlocks() {
		int b_id = 0;
		
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Rule source = entry.getKey();
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			if(markedPosition.isEmpty()) continue;
			
			String b_name = "BLK_" + this.rule.getRuleIndex() + "_" + b_id++;
			Set<Atom> bricks = markedPosition.keySet();
			
			Set<Variable> shared_vars = new HashSet<>();
			Set<Variable> marked_vars = new HashSet<>();
			
			boolean validBlock = true;
			boolean possibleBlock = true;
			
			for(Entry<Atom, Set<Integer>> mp : markedPosition.entrySet()) {
				Atom atom = mp.getKey();
				Set<Integer> positions = mp.getValue();
				
				if(shared_vars.isEmpty()) {
					for(Integer i : positions) {
						Term t = atom.getTerm(i);
						if(t instanceof Variable) {	
							shared_vars.add((Variable)t);
							marked_vars.add((Variable)t);
						}
					}
				}
				else {
					Set<Variable> t_shared_vars = new HashSet<>();
					for(Integer i : positions) {
						Term t = atom.getTerm(i);
						if(t instanceof Variable) {
							marked_vars.add((Variable)t);
							if(shared_vars.contains(t))
								t_shared_vars.add((Variable)t);
						}
					}
					shared_vars = t_shared_vars;
					if(shared_vars.isEmpty()) {
						possibleBlock = false;
						break;
					}
				}
			}
			
			if(possibleBlock) {
				for(Atom a : this.rule.getBody()) {
					if(!markedPosition.keySet().contains(a)) {
						for(Variable v : a.getVariables()) {
							if(shared_vars.contains(v)) {
								validBlock = false;
								break;
							}
						}
					}
					if(!validBlock) break;
				}			
			
				if(validBlock) {
					boolean pass = false;
					
					for(Variable v : marked_vars) {
						if(this.rule.getFrontierVariables().contains(v)) {
							pass = true;
							break;
						}
					}
					
					Block b = new Block(b_name, bricks, source, 
							pass);
					
					blocks.add(b);
				}
			}
		}
		
		return blocks;
	}
	
	public List<Block> getBaseBlocks() {
		int b_id = 0;
		
		List<Block> blocks = new LinkedList<>();
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : this.marked.entrySet()) {
			Rule source = entry.getKey();
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
			
			
			blocks.add(new Block(b_name, markedPosition.keySet(), source, 
					pass));
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