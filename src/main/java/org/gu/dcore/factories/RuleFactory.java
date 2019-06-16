package org.gu.dcore.factories;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;

public class RuleFactory {
	private static RuleFactory factory = null;
	
	private int ruleIndex = 0;
	
	private RuleFactory() {
		
	}
	
	public static RuleFactory instance() {
		if(factory == null) factory = new RuleFactory();
		
		return factory;
	}
	
	public Rule createRule(AtomSet head, AtomSet body, int var_offset) {
		return new Rule(head, body, ruleIndex++, var_offset);
	}
}
