package org.gu.dcore.abduction;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.grd.GraphOfDependencies;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.store.DataStore;
import org.gu.dcore.utils.Pair;
import org.gu.dcore.utils.Utils;

public class QueryAbduction {
	private ConjunctiveQuery query;
	private List<Rule> ontology;
	private DataStore store;
	private List<Predicate> abducibles;
	 
	public QueryAbduction(List<Rule> onto, ConjunctiveQuery q, DataStore D, List<Predicate> abdu) {
		this.abducibles = abdu;
		this.store = D;
		this.ontology = onto;
		this.query = q;
	}
	
	public List<Explanation> getExplanations() {
		return null;
	}
	
	public List<PatternExplanation> getPatternExplanations() {
		ModularizedRewriting mr = new ModularizedRewriting(this.ontology);
		Pair<Rule, List<Rule>> rewriting = mr.pRewrite(query);
		
		GraphOfDependencies grd = new GraphOfDependencies(rewriting.b);
		
		List<Rule> roots = grd.getRulesNotInSCCs();
		
		return null;
	}
	
	private List<AtomSet> getUnfoldedExplanations(AtomSet root_e, List<Rule> roots) {
		IndexedByHeadPredRuleSet rs = new IndexedByHeadPredRuleSet(roots);
		
		List<AtomSet> explanations = new LinkedList<>();
		LinkedList<AtomSet> to_explore = new LinkedList<>();
		
		to_explore.add(root_e);
		
		while(!to_explore.isEmpty()) {
			AtomSet e = to_explore.poll();
			
			List<AtomSet> obtained = new LinkedList<>();
			
			for(Rule r : rs.getRules(e)) {
				List<Unifier> unifiers = Unify.getUnifiers(e, r);		
				for(Unifier u : unifiers) {
					obtained.add(Utils.rewrite(e, r.getBody(), u));
				}
			}
			
			
		}		
	}
	
	public List<Explanation> getLevelExplanations(int i) {
		return null;
	}
}
