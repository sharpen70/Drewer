package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.LiftedRule;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.SinglePieceUnifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.rewriting.RewriteUtils;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
import org.semanticweb.vlog4j.parser.ParsingException;

public abstract class AbstactQueryAbduction implements QueryAbduction {
	protected ConjunctiveQuery query;
	protected List<Rule> ontology;
	protected DatalogEngine store;
	protected Set<Predicate> abducibles;
	
	protected IndexedByHeadPredRuleSet irs;
	
	protected boolean relax_abdu = false;
	
	public AbstactQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		this.abducibles = abdu;
		if(abdu.isEmpty()) this.relax_abdu = true;
		
		this.store = D;
		this.ontology = onto;
		this.query = q;
	}
	
	protected List<Pair<LiftedAtomSet, Map<Term, Term>>> atomset_reduce(AtomSet e) throws IOException, ParsingException {
		List<Pair<LiftedAtomSet, Map<Term, Term>>> result = new LinkedList<>();
		int size = e.size();
		
		/* build the index of variable in retrieved table */
		Set<Variable> vars = e.getVariables();
		Map<Variable, Integer> var_index = new HashMap<>();
		int index = 0;
		for(Variable v : vars) {
			var_index.put(v, index++);
		}
		
		e.resort();
		
		Column[] columns = new Column[size];
		
		for(int i = 0; i < size; i++) {
			Atom a = e.getAtom(i);
			Set<Variable> vs = a.getVariables();
			int[] mapping = new int[vs.size()];
			int m = 0;
			for(Variable v : vs) {
				mapping[m++] = var_index.get(v);
			}
			columns[i] = this.store.answerAtomicQuery(a, mapping, vars.size());
		}		
		
		LinkedList<Tuple<Integer, boolean[], Column>> queue = new LinkedList<>();
		Tuple<Integer, boolean[], Column> init = new Tuple<>(0, new boolean[size], new Column(vars.size()));
		queue.add(init);
		
		while(!queue.isEmpty()) {
			Tuple<Integer, boolean[], Column> p = queue.pop();
			int level = p.a;
			boolean[] selected_atoms = p.b;
			Column current_column = p.c;
			
			if(level >= e.size()) {
				if(current_column.size() != 0) {
					Pair<LiftedAtomSet, Map<Term, Term>> la = liftAtomSet(e, var_index, selected_atoms, current_column);
					result.add(la);
				}
				continue;
			}
			
			boolean[] current_t = selected_atoms.clone();
			current_t[level] = true;
			boolean[] current_f = selected_atoms.clone();
			current_f[level] = false;
			
			Column next_column = columns[level];
			level++;	
			
			if(next_column.getTuples().isEmpty()) {
				queue.add(new Tuple<>(level, current_f, current_column));
			}
			else if(current_column.getTuples().isEmpty()) {				
				queue.add(new Tuple<>(level, current_t, next_column));
			}					
			else {
				Column join_column = current_column.full_join(next_column);
				
				queue.add(new Tuple<>(level, current_t, join_column));
				queue.add(new Tuple<>(level, current_f, current_column));
			}
		}
		
		return result;
	}
	
	protected List<LiftedRule> rule_reduce(Rule r) throws IOException, ParsingException {
		List<LiftedRule> result = new LinkedList<>();
		
		List<Pair<LiftedAtomSet, Map<Term, Term>>> liftedAtomset = atomset_reduce(r.getBody());
		
		for(Pair<LiftedAtomSet, Map<Term, Term>> la : liftedAtomset) {
			AtomSet nhead = RewriteUtils.substitute(r.getHead(), la.b);
			Rule nr = RuleFactory.instance().createRule(nhead, la.a);
			
			result.add(new LiftedRule(nr, la.a.getColumn()));
		}
		
		return result;
	}
	
	protected Pair<LiftedAtomSet, Map<Term, Term>> liftAtomSet(AtomSet A, Map<Variable, Integer> var_index, boolean[] selected_atoms, Column column) {
		Map<Term, Term> repConstant_map = new HashMap<>();
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
						Term c = repConstant_map.get(t);
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
		LiftedAtomSet result = new LiftedAtomSet(atomset_lifted, column);
		return new Pair<>(result, repConstant_map);
	}
	
	protected boolean allAbducibles(AtomSet atomset) {
		if(this.relax_abdu) return true;
		
		for(Atom a : atomset) {
			if(!this.abducibles.contains(a.getPredicate()))
				return false;
		}
		
		return true;
	}
	
	protected List<AtomSet> rewrite(AtomSet atomset) {
		List<AtomSet> rewritings = new LinkedList<>();
		
		for(Atom a : atomset) {
			Set<Rule> rules_to_rewrite = this.irs.getRulesByPredicate(a.getPredicate());
			if(!relax_abdu && !this.abducibles.contains(a.getPredicate()) &&
					rules_to_rewrite.isEmpty()) return new LinkedList<>();
			
			for(Rule r : rules_to_rewrite) {
				List<SinglePieceUnifier> unifiers = Unify.getSinglePieceUnifiers(atomset, r);
				
				for(SinglePieceUnifier u : unifiers) {
					 rewritings.add(RewriteUtils.rewrite(atomset, r.getBody(), u));
				}
			}	
		}
		
		return rewritings;
	}
}
