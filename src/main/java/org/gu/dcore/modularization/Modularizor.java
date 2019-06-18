package org.gu.dcore.modularization;

import java.util.ArrayList;
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
	private List<Rule> er;
	private IndexedByBodyPredRuleSet onto;
	
	public Modularizor(List<Rule> onto) {
		this.onto = new IndexedByBodyPredRuleSet(onto);
		er = new ArrayList<>();
		
		for(Rule r: onto) {
			if(!r.getExistentials().isEmpty()) er.add(r);
		}
	}
	
	public List<Rule> modularize(List<Rule> onto) {
		List<Rule> modularizedRules = new LinkedList<>();
		
		for(Rule r : this.er) {
			modularizedRules.addAll(this.existentialForwardChaining(r));
		}
		
		return modularizedRules;
	}
	
	private List<Rule> existentialForwardChaining(Rule r) {
		List<PredPosition> predPositions = getExistentialPositions(r);
		
		for(PredPosition predPosition : predPositions) {
			existentialForwardMarking(r, predPosition);
		}
		
		return null;
	}
	
	private List<Rule> existentialForwardMarking(Rule r, PredPosition predPosition) {
		
		return null;
	}
	
	private List<PredPosition> getExistentialPositions(Rule r) {
		List<PredPosition> predPositions = new LinkedList<>();
		
		for(Atom a : r.getHead()) {
			List<Integer> indice = new LinkedList<>();
			
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
