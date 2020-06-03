package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.AggregateUnifier;
import org.gu.dcore.reasoning.Partition;
import org.gu.dcore.reasoning.SinglePieceUnifier;
import org.gu.dcore.rewriting.CompactComparator;
import org.gu.dcore.rewriting.MinComparator;
import org.gu.dcore.rewriting.RewriteUtils;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class NormalQueryAbduction extends AbstactQueryAbduction {

	public NormalQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
	}
	
	@Override
	public List<AtomSet> getExplanations() throws IOException, ParsingException {	
		List<AtomSet> rewriting_set = new LinkedList<>();
		List<AtomSet> compact_explanationn_set = new LinkedList<>();
		
		LinkedList<AtomSet> exploration_set = new LinkedList<>();
		
		exploration_set.add(this.query.getBody());
		
		while(!exploration_set.isEmpty()) {
			AtomSet current = exploration_set.poll();
			
			if(allAbducibles(current)) rewriting_set.add(current);
		
			List<AtomSet> rewritings = new LinkedList<>();
			
			compact_explanationn_set.addAll(atomset_reduce(current));
			rewritings.addAll(rewrite(current));
			
			Utils.removeSubsumed(rewritings, rewriting_set);
			Utils.removeSubsumed(rewritings, exploration_set);
			Utils.removeSubsumed(exploration_set, rewritings);
			Utils.removeSubsumed(rewriting_set, rewritings);
			
			exploration_set.addAll(rewritings);
		}
		
		compact_reduce(compact_explanationn_set);
		
		return compact_explanationn_set;
	}
	
	private void compact_reduce(List<AtomSet> rewritings) {
		Iterator<AtomSet> it1 = rewritings.iterator();
		
		while(it1.hasNext()) {
			AtomSet rew1 = it1.next();			
			Iterator<AtomSet> it2 = rewritings.iterator();
			while(it2.hasNext()) {
				AtomSet rew2 = it2.next();
				if(!rew1.equals(rew2)) {
					if(!(rew1 instanceof LiftedAtomSet)) {
						if(Utils.isMoreGeneral(rew1, rew2)) it2.remove();
					}
					else if (rew2 instanceof LiftedAtomSet) {
						CompactComparator cc = new CompactComparator(rew1, rew2);
						List<Partition> parts = cc.getCompactUnifiers(new MinComparator());
						if(!parts.isEmpty()) {
							LiftedAtomSet lrew1 = (LiftedAtomSet) rew1;
							LiftedAtomSet lrew2 = (LiftedAtomSet) rew2;
							
							Utils.refineCompactExplanation(lrew2, lrew1, parts);
						}
					}
				}
			}
		}
	}
	
	protected List<LiftedAtomSet> atomset_reduce(AtomSet e) throws IOException, ParsingException {
		int size = e.size();
		
		/* build the index of variable in retrieved table */
		Set<Variable> vars = e.getVariables();
		Map<Variable, Integer> var_index = new HashMap<>();
		int index = 0;
		for(Variable v : vars) {
			var_index.put(v, index++);
		}
		
		e.resort();
		
		ArrayList<Column> columns = new ArrayList<>();
		
		for(int i = 0; i < size; i++) {
			Atom a = e.getAtom(i);
			Set<Variable> vs = a.getVariables();
			int[] mapping = new int[vs.size()];
			int m = 0;
			for(Variable v : vs) {
				mapping[m++] = var_index.get(v);
			}
			columns.add(this.store.answerAtomicQuery(a, mapping, vars.size()));
		}		
		
		LinkedList<Tuple<Column, boolean[], Integer>> queue = new LinkedList<>();
		for(int i = 0; i < size; i++) {
			boolean[] selected_atoms = new boolean[size];
			selected_atoms[i] = true;
			queue.add(new Tuple<>(columns.get(i), selected_atoms, i+1));
		}
		
		List<LiftedAtomSet> result = new LinkedList<>();
		
		while(!queue.isEmpty()) {
			Tuple<Column, boolean[], Integer> current_t = queue.poll();
			Column cur_column = current_t.a;
			boolean[] selected_atoms = current_t.b;
			int next = current_t.c;
			
			if(cur_column.size() == 0) continue;
			else {
				result.add(liftAtomSet(e, var_index, selected_atoms, cur_column));
			}
			if(next < size) {
				Column next_column = columns.get(next);
				if(next_column.size() != 0) {
					Column join_column = Utils.innerJoinColumn(cur_column, next_column);
					boolean[] next_selected_atoms = selected_atoms.clone();
					next_selected_atoms[next] = true;
					queue.add(new Tuple<>(join_column, next_selected_atoms, next + 1));
				}
				boolean[] next_selected_atoms = selected_atoms.clone();
				next_selected_atoms[next] = false;
				queue.add(new Tuple<>(cur_column, next_selected_atoms, next + 1));
			}			
		}
		return result;
	}
//	private List<AtomSet> rewrite_with_data(AtomSet atomset) throws ParsingException, IOException {
//		List<AtomSet> result = new LinkedList<>();
//		
//		for(Atom a : atomset) {
//			AtomSet r = rewrite_with_facts(atomset, a);
//			if(r != null) result.add(r);
//		}
//		
//		return result;
//	}
//	
//	private AtomSet rewrite_with_facts(AtomSet atomset, Atom a) throws ParsingException, IOException {
//		AtomSet ur = HomoUtils.minus(atomset, new AtomSet(a));
//		
//		Map<Term, Term> vr_map = new HashMap<>();
//		int m = atomset.getRepConstants().size();
//		int n = 0;
//		ArrayList<Integer> mask = new ArrayList<>();
//		Set<Term> reserve = new HashSet<>();
//		
//		for(int i = 0; i < a.getTerms().size(); i++) {
//			Term t = a.getTerm(i);
//			if(t instanceof Variable) {
//				if(reserve.add(t)) {
//					if(Utils.is_join_variable(a, (Variable)t, atomset)) {
//						mask.add(n++);
//						vr_map.put(t, new RepConstant(m++));
//					}
//					else n++;
//				}				
//			}
//			else if(t instanceof RepConstant) {
//				if(reserve.add(t)) mask.add(n++);
//			}
//			else n++;
//		}
//		
//		Column answers = this.store.answerAtomicQuery(a, mask);
//		
//		if(answers.size() == 0) return null;
//		
//		if(atomset instanceof LiftedAtomSet) {
//			Column c = ((LiftedAtomSet) atomset).getColumn();
//			int max_jk_size = atomset.getRepConstants().size();
//			
//			int[] jk1 = new int[max_jk_size];
//			int[] jk2 = new int[max_jk_size];
//			int l = 0;
//			
//			for(int i = 0; i < a.getTerms().size(); i++) {
//				Term t = a.getTerm(i);
//				if(t instanceof RepConstant) {
//					jk1[l] = ((RepConstant) t).getValue();
//					jk2[l] = i;
//					l++;
//				}
//			}
//			Column nc = c.join(answers, jk1, jk2, l).a;
//			ur = RewriteUtils.substitute(ur, vr_map);
//			ur = new LiftedAtomSet(ur, nc);
//		}
//		else {
//			ur = RewriteUtils.substitute(ur, vr_map);
//			ur = new LiftedAtomSet(ur, answers);
//		}
//			
//		return ur;
//	}
}
