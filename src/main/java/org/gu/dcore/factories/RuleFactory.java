package org.gu.dcore.factories;

import java.util.ArrayList;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;

public class RuleFactory {
	private static RuleFactory factory = null;
	
	private int ruleIndex = 1;
	
	private RuleFactory() {
		
	}
	
	public static RuleFactory instance() {
		if(factory == null) factory = new RuleFactory();
		
		return factory;
	}
	
	public Rule createRule(AtomSet head, AtomSet body) {
		return new Rule(head, body, ruleIndex++); 
	}
	
	public Rule createQueryRule(ConjunctiveQuery q) {
		Predicate Q = new Predicate("Q", 0, q.getAnsVar().size());
		Atom Qhead = AtomFactory.instance().createAtom(Q, q.getAnsVar());
		AtomSet body = new AtomSet(q.getBody());
		if(!q.getAnsVar().isEmpty()) {
			Predicate ansP = new Predicate("ANS", -1, q.getAnsVar().size());
			ArrayList<Term> ans_t = new ArrayList<>();
			ans_t.addAll(q.getAnsVar());
			Atom ansAtom = new Atom(ansP, ans_t);
			body.add(ansAtom);
		}
		return new Rule(new AtomSet(Qhead), body, 0);
	}
	
	public Rule createRule(AtomSet head, AtomSet... bodies) {
		AtomSet body = new AtomSet();
		
		for(AtomSet b : bodies) body.addAll(b);
		
		return new Rule(head, body, ruleIndex++);
	}
}
