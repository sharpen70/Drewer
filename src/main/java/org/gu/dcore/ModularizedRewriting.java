package org.gu.dcore;

import java.util.List;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BlockRule;

public class ModularizedRewriting {
	List<Rule> ruleset;
	List<BlockRule> blockRuleset;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.ruleset = onto;
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {
		
		
		return null;
	}
}
