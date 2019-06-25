package org.gu.dcore.modularization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Modularizor {
	private List<MarkedRule> onto;
	private IndexedByBodyPredRuleSet indexedRuleSet;
	
	public Modularizor(List<Rule> onto) {
		this.indexedRuleSet = new IndexedByBodyPredRuleSet();
		this.onto = new LinkedList<>();
		
		for(Rule r: onto) {
			MarkedRule mr = new MarkedRule(r);
			this.onto.add(mr);
			this.indexedRuleSet.add(mr);
		}
	}
	
	public List<Rule> modularize() {
		List<Rule> modularizedRules = new LinkedList<>();
		
		this.markRules();
		
		return modularizedRules;
	}
	
	public void markRules() {
		for(Rule r : this.onto) {
			this.existentialForwardChaining(r);
		}
	}
	
	public List<MarkedRule> getMarkedRules() {
		return this.onto;
	}
	
	private void existentialForwardChaining(Rule r) {
		List<PredPosition> predPositions = getExistentialPositions(r);
		
		for(PredPosition predPosition : predPositions) {
			List<Rule> rs = this.indexedRuleSet.get(predPosition.getPredicate());
			if(rs != null) {
				for(Rule r1 : rs) {
					existentialForwardMarking(r1, predPosition, r);
				}
			}
		}
	}
	
	private void existentialForwardMarking(Rule r1, PredPosition predPosition, Rule r) {
		MarkedRule mr = (MarkedRule)r1;
		List<PredPosition> predPositions = mr.mark(r, predPosition);
		
		for(PredPosition pp : predPositions) {
			List<Rule> rs = this.indexedRuleSet.get(pp.getPredicate());
			if(rs != null) {
				for(Rule _r : rs) {
					existentialForwardMarking(_r, predPosition, r);
				}
			}
		}
	}
	
	private List<PredPosition> getExistentialPositions(Rule r) {
		List<PredPosition> predPositions = new LinkedList<>();
		
		for(Atom a : r.getHead()) {
			Set<Integer> indice = new HashSet<>();
			
			for(int i = 0; i < a.getTerms().size(); i++) {
				Term t = a.getTerm(i);
				if(t instanceof Variable) {
					int value = ((Variable) t).getValue();
					if(r.isExistentialVar(value)) indice.add(i);
				}
			}
			
			if(!indice.isEmpty()) predPositions.add(new PredPosition(a.getPredicate(), indice));
		}
		
		return predPositions;
	}
}
