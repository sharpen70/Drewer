package org.gu.dcore.modularization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.model.Rule;
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
		Set<Variable> existentials = r.getExistentials();
		
		for(Variable v : existentials) {
			existentialForwardChaining(r, v);
		}
		
		return null;
	}
	
	private List<Rule> existentialForwardChaining(Rule r, Variable v) {
		return null;
	}
}
