package org.gu.dcore.factories;

public class RuleFactory {
	private static RuleFactory factory = null;
	
	private RuleFactory() {
		
	}
	
	public static RuleFactory instance() {
		if(factory == null) factory = new RuleFactory();
		
		return factory;
	}
}
