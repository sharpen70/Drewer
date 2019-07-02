package org.gu.dcore.modularization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Rule;
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
				mark(source, v, pp);
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
		
		
	}
	
	@Override
	public Block getBlocks(Rule r) {
		return null;
	}
	
	private class RuleBasedMark {
		private Rule rule;
		private Map<Rule, Map<Atom, Set<Integer>>> marked;
	
		public RuleBasedMark(Rule rule) {
			this.rule = rule;
			this.marked = new HashMap<>();
		}
		
		public boolean add(Rule source, PredPosition pp) {
			Map<Atom, Set<Integer>> m = this.marked.get(source);
			boolean changed = false;
			
			if(m == null) {
				m = new HashMap<>();
				this.marked.put(source, m);
			}
			
			for(Atom a : rule.getBody()) {
				if(!a.getPredicate().equals(pp.getPredicate())) continue;
				
				Set<Integer> indice = m.get(a);
				
				if(indice == null) {
					indice = new HashSet<>();
					m.put(a, indice);
				}
				
				for(Integer i : pp.getIndice()) {
					if(!(a.getTerm(i) instanceof Constant)) changed = indice.add(i); 
				}
			}
			
			return changed;
		}
	}
}
