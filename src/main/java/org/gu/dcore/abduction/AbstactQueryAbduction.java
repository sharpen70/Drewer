package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.semanticweb.vlog4j.parser.ParsingException;

public abstract class AbstactQueryAbduction implements QueryAbduction {
	protected AtomSet query;
	protected List<Rule> ontology;
	protected DatalogEngine store;
	protected Set<Predicate> abducibles;
	
	protected IndexedByHeadPredRuleSet irs;
	
	protected boolean abduce_all = true;
	
	public AbstactQueryAbduction(List<Rule> onto, AtomSet q, DatalogEngine D) {		
		this.store = D;
		this.ontology = onto;
		this.query = q;
		
		this.irs = new IndexedByHeadPredRuleSet(onto);
	}
	
	public AbstactQueryAbduction(List<Rule> onto, AtomSet q, DatalogEngine D, Set<Predicate> abdu) {
		this(onto, q, D);
		
		this.abduce_all = false;
		this.abducibles = abdu;
	}
	
	protected LiftedAtomSet liftAtomSet(AtomSet A, Map<Variable, Integer> var_index, boolean[] selected_atoms, Column column) {
		Map<Term, Term> repConstant_map = new HashMap<>();
		ArrayList<Integer> rep_index = new ArrayList<>();
		
		boolean[] position_blank = column.getPosition_blank();
		boolean[] actual_occupy = new boolean[column.getArity()];
		
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
							Integer v_idx = var_index.get(t);
							if(v_idx != null && position_blank[v_idx]) {
								actual_occupy[v_idx] = true;
								RepConstant rc = TermFactory.instance().getRepConstant(map_index++);
								repConstant_map.put(t, rc);
								rep_index.add(v_idx);
								new_terms.add(rc);
							}
							else new_terms.add(t);
						}
						else new_terms.add(c);

					}
					else new_terms.add(t);
				}
				Atom liftedAtom = AtomFactory.instance().createAtom(pred, new_terms);
				atomset_lifted.add(liftedAtom);
			}
		}
		
		column.distinct(actual_occupy);
		column.setRepMap(rep_index);
		
		return new LiftedAtomSet(atomset_lifted, column);
	}
	
	protected LiftedAtomSet liftAtomSet(AtomSet A, Map<Variable, Integer> var_index, boolean[] selected_atoms, 
			Column column, ArrayList<Column> columns) {
		Map<Term, Term> repConstant_map = new HashMap<>();
		ArrayList<Integer> rep_index = new ArrayList<>();
		
		boolean[] position_blank = column.getPosition_blank();
		boolean[] actual_occupy = new boolean[column.getArity()];
		
		int map_index = 0;
		
		AtomSet atomset_lifted = new AtomSet();
		Set<Variable> mapped_variables = new HashSet<>();
		
		for(int i = 0; i < A.size(); i++) {
			Atom a = A.getAtom(i);
			if(!selected_atoms[i]) {
				Predicate pred = a.getPredicate();
				ArrayList<Term> terms = a.getTerms();
				ArrayList<Term> new_terms = new ArrayList<>(); 
				
				for(int ti = 0; ti < terms.size(); ti++) {
					Term t = terms.get(ti);
					if(t.isVariable()) {
						Term c = repConstant_map.get(t);
						if(c == null) {
							Integer v_idx = var_index.get(t);
							if(v_idx != null && position_blank[v_idx]) {
								actual_occupy[v_idx] = true;
								RepConstant rc = TermFactory.instance().getRepConstant(map_index++);
								repConstant_map.put(t, rc);
								rep_index.add(v_idx);
								new_terms.add(rc);
							}
							else new_terms.add(t);
						}
						else new_terms.add(c);

					}
					else new_terms.add(t);
				}
				Atom liftedAtom = AtomFactory.instance().createAtom(pred, new_terms);
				atomset_lifted.add(liftedAtom);
			}
			else mapped_variables.addAll(a.getVariables());
		}
		
		for(int i = 0; i < A.size(); i++) {
			if(!selected_atoms[i]) {
				Atom a = A.getAtom(i);
				if(mapped_variables.containsAll(a.getVariables())) {
					column.outerJoin(columns.get(i));
				}
			}
		}		
		
		column.distinct(actual_occupy);
		column.setRepMap(rep_index);
		
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
	
	protected AtomSet pre_reduce(AtomSet obs) throws ParsingException, IOException {
		AtomSet result = new AtomSet();
		
		for(Atom a : obs) {
			if(a.getVariables().size() == 0) {
				Column col = this.store.answerAtomicQuery(a, new ArrayList<>());
				if(col.size() == 0) result.add(a);
			}
			else result.add(a);
		}
		
		return result;
	}
}
