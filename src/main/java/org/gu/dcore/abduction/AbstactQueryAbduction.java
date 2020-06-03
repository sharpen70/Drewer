package org.gu.dcore.abduction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.TermFactory;
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
import org.gu.dcore.reasoning.SinglePieceUnifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.rewriting.RewriteUtils;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;

public abstract class AbstactQueryAbduction implements QueryAbduction {
	protected ConjunctiveQuery query;
	protected List<Rule> ontology;
	protected DatalogEngine store;
	protected Set<Predicate> abducibles;
	
	protected IndexedByHeadPredRuleSet irs;
	
	protected boolean abduce_all = true;
	
	public AbstactQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D) {		
		this.store = D;
		this.ontology = onto;
		this.query = q;
		
		this.irs = new IndexedByHeadPredRuleSet(onto);
	}
	
	public AbstactQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		this(onto, q, D);
		
		abduce_all = false;
		this.abducibles = abdu;
	}
	
	protected LiftedAtomSet liftAtomSet(AtomSet A, Map<Variable, Integer> var_index, boolean[] selected_atoms, Column column) {
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
		
		column.setRepMap(column_index);
		
		return new LiftedAtomSet(atomset_lifted, column);
	}
	
	protected boolean allAbducibles(AtomSet atomset) {
		if(this.abduce_all) return true;
		
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
			if(!this.abduce_all && !this.abducibles.contains(a.getPredicate()) &&
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
