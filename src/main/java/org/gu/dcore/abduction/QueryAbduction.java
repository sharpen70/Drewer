package org.gu.dcore.abduction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.grd.GraphOfPredicateDependencies;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.Substitution;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DataStore;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
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
	
	private List<Rule> rule_reduce(Rule r, IndexedByHeadPredRuleSet dependencies) {
		AtomSet body = r.getBody();
		AtomSet head = r.getHead();
		int size = body.size();
		
		/* build the index of variable in retrieved table */
		Set<Variable> vars = body.getVariables();
		Map<Variable, Integer> var_index = new HashMap<>();
		int index = 0;
		for(Variable v : vars) {
			var_index.put(v, index++);
		}
		
		List<Rule> reduce_result = new LinkedList<>();
		
		LinkedList<Tuple<Integer, boolean[], Column>> queue = new LinkedList<>();
		Tuple<Integer, boolean[], Column> init = new Tuple<>(0, new boolean[size], new Column(vars.size()));
		queue.add(init);
		
		while(queue.isEmpty()) {
			Tuple<Integer, boolean[], Column> p = queue.pop();
			int level = p.a;
			boolean[] selected_atoms = p.b;
			List<String[]> tuples = p.c.getTuples();
						
			if(level >= body.size()) {
//				
//				AtomSet liftedhead = new AtomSet();
//				for(Atom a : head) {
//					ArrayList<Term> headterms = new ArrayList<Term>();
//					for(int i = 0; i < a.getTerms().size(); i++) {
//						Term t = a.getTerm(i);
//						if(t.isVariable()) {
//							RepConstant rc = repConstant_map.get(t);
//							if(rc != null) headterms.add(rc);
//							else headterms.add(t);
//						}
//						else headterms.add(t);
//					}
//					liftedhead.add(AtomFactory.instance().createAtom(a.getPredicate(), headterms));
//				}
//				Rule liftedrule = RuleFactory.instance().createRule(liftedhead, atomset_lifted);
//				
				continue;
			}
			
			
		}
		
		return null;
	}
	
	private LiftedAtomSet liftAtomSet(AtomSet A, Map<Variable, Integer> var_index, boolean[] selected_atoms, Column column) {
		Map<Term, RepConstant> repConstant_map = new HashMap<>();
		ArrayList<Integer> column_index = new ArrayList<>();
		
		boolean[] position_blank = column.getPosition_blank();
		
		int map_index = 0;
		
		AtomSet atomset_lifted = new AtomSet();
		for(int i = 0; i < A.size(); i++) {
			if(!selected_atoms[i]) {
				Atom a = A.getAtom(i);
				Predicate pred = a.getPredicate();
				ArrayList<Term> terms = a.getTerms();
				ArrayList<Term> new_terms = new ArrayList<>(); 
				
				for(int ti = 0; ti < terms.size(); ti++) {
					Term t = terms.get(ti);
					if(t.isVariable()) {
						RepConstant c = repConstant_map.get(t);
						if(c == null) {
							int v_idx = var_index.get(t);
							if(position_blank[v_idx]) {
								RepConstant rc = TermFactory.instance().getRepConstant(map_index++);
								repConstant_map.put(t, rc);
								column_index.add(v_idx);
								new_terms.add(rc);
							}
							else new_terms.add(t);
						}
					}
					else new_terms.add(t);
				}
				Atom liftedAtom = AtomFactory.instance().createAtom(pred, new_terms);
				atomset_lifted.add(liftedAtom);
			}
		}
		column.remap(column_index);
		
		return new LiftedAtomSet(atomset_lifted, column);
	}
	
	private List<Pair<Atom, List<Substitution>>> atomset_reduce(AtomSet e) {
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
