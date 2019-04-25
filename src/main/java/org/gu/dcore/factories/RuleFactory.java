package org.gu.dcore.factories;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ExRule;

public class RuleFactory {
	private static RuleFactory factory = null;
	
	private int ruleIndex = 0;
	
	private RuleFactory() {
		
	}
	
	public static RuleFactory instance() {
		if(factory == null) factory = new RuleFactory();
		
		return factory;
	}
	
	public ExRule createRule(AtomSet head, AtomSet body, int var_offset) {
		return new ExRule(head, body, ruleIndex++, var_offset);
	}
}
