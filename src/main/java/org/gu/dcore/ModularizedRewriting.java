package org.gu.dcore;

import java.util.List;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BlockRule;

public class ModularizedRewriting {
	List<Rule> ruleset;
	List<BlockRule> blockRuleset;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.ruleset = onto;
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {		
		Predicate Q = PredicateFactory.instance().createPredicate("Q", q.getAnsVar().size());
		Atom Qhead = AtomFactory.instance().createAtom(Q, q.getAnsVar());
		Rule Qr = RuleFactory.instance().createRule(new AtomSet(Qhead), new AtomSet(q.getBody()));
		
		
		return null;
	}
}
