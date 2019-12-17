package org.gu.dcore.abduction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.grd.GraphOfPredicateDependencies;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class PatternAbdunction extends QueryAbduction {

	public PatternAbdunction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
	}

	public List<PatternExplanation> getPatternExplanations() throws ParsingException {
		List<PatternExplanation> result = new LinkedList<PatternExplanation>();
		
		/* Compute datalog rewriting of the abduction problem */
		ModularizedRewriting mr = new ModularizedRewriting(this.ontology);
		List<Rule> rewriting = mr.rewrite(query);
		
		this.store.addRules(rewriting);
		
		
		return result; 
	}
}
