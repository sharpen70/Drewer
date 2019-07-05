package org.gu.dcore.modularization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class BaseMarking implements Marking {
	private Map<Rule, RuleBasedMark> marking; 
	private IndexedByBodyPredRuleSet onto;
	
	public BaseMarking(List<Rule> ruleset) {
		this.marking = new HashMap<>();
		this.onto = new IndexedByBodyPredRuleSet(ruleset);
	}
	
	public void mark(Rule source) {
		for(Variable v : source.getExistentials()) {
			for(PredPosition pp : source.getPositions(v)) {
				mark(source, pp);
			}
		}
	}
	
	public void mark(Rule source, PredPosition pp) {
		List<Rule> affected = this.onto.get(pp.getPredicate());
		
		for(Rule r : affected) {
			mark(r, source, pp);
		}
	}
	
	public void mark(Rule r, Rule source, PredPosition pp) {
		RuleBasedMark rbm = this.marking.get(r);
		
		if(rbm == null) {
			rbm = new RuleBasedMark(r);
			this.marking.put(r, rbm);
		}
		
		for(PredPosition npp : rbm.add(source, pp)) {
			mark(source, npp);
		}
	}
	
	@Override
	public Block getBlocks(Rule r) {
		for(Entry<Rule, RuleBasedMark> entry : this.marking.entrySet()) {
			RuleBasedMark m = entry.getValue(); 
		}
	}
	
	public List<Block> getBlocks(RuleBasedMark rbm) {
		List<Block> blocks = new LinkedList<>();
		Map<Rule, Map<Atom, Set<Integer>>> marked = rbm.marked;
		
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry : marked.entrySet()) {
			Rule source = entry.getKey();
			Map<Atom, Set<Integer>> markedPosition = entry.getValue();
			
			blocks.add(new Block(markedPosition.keySet(), source));
		}
		
		return blocks;
	}
	
	public void printMarked() {
		for(Entry<Rule, RuleBasedMark> entry : this.marking.entrySet()) {
			entry.getValue().printMarked();
		}
	}
	
	private class RuleBasedMark {
		private Rule rule;
		private Map<Rule, Map<Atom, Set<Integer>>> marked;
	
		public RuleBasedMark(Rule rule) {
			this.rule = rule;
			this.marked = new HashMap<>();
		}
		
		public List<PredPosition> add(Rule source, PredPosition pp) {
			Map<Atom, Set<Integer>> m = this.marked.get(source);
			Set<Variable> newMarked = new HashSet<>();
			
			if(m == null) {
				m = new HashMap<>();
				this.marked.put(source, m);
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
			
			for(Variable v : newMarked) {
				pass.addAll(this.rule.getPositions(v));
			}
			
			return pass;
		}
		
		public void printMarked() {
			for(Entry<Rule, Map<Atom, Set<Integer>>> entry: this.marked.entrySet()) {
				System.out.print("(" + entry.getKey().getRuleIndex() + ") ");
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
}
