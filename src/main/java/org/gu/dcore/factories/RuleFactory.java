package org.gu.dcore.factories;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;

public class RuleFactory {
	private static RuleFactory factory = null;
	
	private int ruleIndex = 0;
	
	private RuleFactory() {
		
	}
	
	public static RuleFactory instance() {
		if(factory == null) factory = new RuleFactory();
		
		return factory;
	}
	
	public Rule createRule(AtomSet head, AtomSet body, int maxVar) {
		return new Rule(head, body, ruleIndex++, maxVar);
	}
	
	public Rule createRule(AtomSet head, AtomSet body) {
		int maxVar = -1;
		
		for(Variable v : head.getVariables()) if(maxVar < v.getValue()) maxVar = v.getValue();
		for(Variable v : body.getVariables()) if(maxVar < v.getValue()) maxVar = v.getValue();
		
		return new Rule(head, body, ruleIndex++, maxVar);
	}
}
