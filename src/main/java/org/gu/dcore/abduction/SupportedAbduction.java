package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.reasoning.FreshIndividualSubstitution;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class SupportedAbduction extends QueryAbduction {
	
	public SupportedAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
		this.irs = new IndexedByHeadPredRuleSet(onto);
	}
	
	public List<AtomSet> getExplanations() throws IOException, ParsingException {
		List<AtomSet> result = new LinkedList<>();		
		List<AtomSet> final_set = new LinkedList<>();
		LinkedList<AtomSet> exploration_set = new LinkedList<>();
		
		exploration_set.add(this.query.getBody());
		
		while(!exploration_set.isEmpty()) {
			AtomSet current = exploration_set.poll();
			
			List<Pair<LiftedAtomSet, Map<Term, Term>>> cleaned = atomset_reduce(current);
			List<AtomSet> rewritings = new LinkedList<>();
			
			if(cleaned.isEmpty()) {
				if(allAbducibles(current))
					final_set.add(current);
				rewritings.addAll(rewrite(current));
			}
			else {
				for(Pair<LiftedAtomSet, Map<Term, Term>> la : cleaned) {
					AtomSet as = la.a;
					if(allAbducibles(as))
						final_set.add(as);
					rewritings.addAll(rewrite(as));
				}
			}
			
			Utils.removeSubsumed(rewritings, final_set, false);
			Utils.removeSubsumed(rewritings, exploration_set, false);
			Utils.removeSubsumed(exploration_set, rewritings, false);
			Utils.removeSubsumed(final_set, rewritings, false);
			
			exploration_set.addAll(rewritings);
		}
		
		for(AtomSet atomset : final_set) {
			FreshIndividualSubstitution sub = new FreshIndividualSubstitution();
			result.add(sub.imageOf(atomset));
		}
		
		return result;
	}
}
