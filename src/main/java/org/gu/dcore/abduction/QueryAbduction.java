package org.gu.dcore.abduction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.grd.GraphOfPredicateDependencies;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.reasoning.Substitution;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.store.DataStore;
import org.gu.dcore.utils.Pair;
import org.gu.dcore.utils.Utils;

public class QueryAbduction {
	private ConjunctiveQuery query;
	private List<Rule> ontology;
	private DataStore store;
	private Set<Predicate> abducibles;
	 
	public QueryAbduction(List<Rule> onto, ConjunctiveQuery q, DataStore D, Set<Predicate> abdu) {
		this.abducibles = abdu;
		this.store = D;
		this.ontology = onto;
		this.query = q;
	}
	
	public List<Explanation> getExplanations() {
		return null;
	}
	
	public List<PatternExplanation> getPatternExplanations() {
		List<PatternExplanation> result = new LinkedList<PatternExplanation>();
		
		/* Compute datalog rewriting of the abduction problem */
		ModularizedRewriting mr = new ModularizedRewriting(this.ontology);
		Pair<Rule, List<Rule>> rewriting = mr.pRewrite(query);
		
		AtomSet rewrited_observation = rewriting.a.getBody();
		List<Rule> rewrited_program = rewriting.b;
		
		/* Compute the set of predicates that may incur loops */
		List<Predicate> plist = PredicateFactory.instance().getPredicateSet();

		GraphOfPredicateDependencies gpd = new GraphOfPredicateDependencies(rewrited_program, plist);
		List<List<Predicate>> sccs = gpd.getSCCPredicates();
		Set<Predicate> scc_predicates = new HashSet<>();
		
		for(List<Predicate> scc : sccs) {
			scc_predicates.addAll(scc);
		}
		
		/* Compute Pattern rules for predicates in loops */
		Iterator<Predicate> sit = scc_predicates.iterator();
		while(sit.hasNext()) {
			Predicate sp = sit.next();
			
		}
		
		/* Minimal rewriting to compute explanations */
		IndexedByHeadPredRuleSet rs = new IndexedByHeadPredRuleSet(rewrited_program);
		
		LinkedList<AtomSet> to_explore = new LinkedList<>();
		LinkedList<AtomSet> finalSet = new LinkedList<>();
		
		to_explore.add(rewrited_observation);
		
		while(!to_explore.isEmpty()) {
			AtomSet e = to_explore.poll();
			finalSet.add(e);
			
			for(Atom a : e) {
				Predicate p = a.getPredicate();
				List<Substitution> substitutions = this.store.getAtomMappings(a);
				
				
				/* For predicates that don't involve in loops, perform traditional rewriting*/
				if(!scc_predicates.contains(p)) {
					for(Rule r : rs.getRulesByPredicate(p)) {
						List<Unifier> unifiers = Unify.getSinglePieceUnifiers(new AtomSet(a), e, r, new HashSet<>());	
						List<AtomSet> rewritings = new LinkedList<>();
						
						for(Unifier u : unifiers) {
							 rewritings.add(Utils.rewrite(e, r.getBody(), u));
						}
						
						Utils.removeSubsumed(rewritings, finalSet);
						Utils.removeSubsumed(to_explore, rewritings);
						Utils.removeSubsumed(finalSet, rewritings);
						
						to_explore.addAll(rewritings);
					}	
				}
				/* */
				else {
					
				}
			}

		}
		
		return result; 
	}
	
	private List<Pair<Atom, List<Substitution>>> explanation_reduce(AtomSet e) {
		return null;
	}
	
	private List<Rule> getPatternRules(Predicate predicate) {
		List<Rule> patterns = new LinkedList<>();
		return null;
	}
	
	public List<Explanation> getLevelExplanations(int i) {
		return null;
	}
}
