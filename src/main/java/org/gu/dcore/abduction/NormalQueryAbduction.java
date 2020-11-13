package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.Partition;
import org.gu.dcore.rewriting.CompactComparator;
import org.gu.dcore.rewriting.RepComparator;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Tuple;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class NormalQueryAbduction extends AbstactQueryAbduction {
	public NormalQueryAbduction(List<Rule> onto, AtomSet q, DatalogEngine D) {
		super(onto, q, D);
	}
	
	public NormalQueryAbduction(List<Rule> onto, AtomSet q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
	}
	
	@Override
	public List<AtomSet> getExplanations() throws IOException, ParsingException {
    	System.out.println("Observation: " + this.query);
    	
		AtomSet observation = pre_reduce(this.query);
		if(observation.isEmpty()) return null;
				
		List<AtomSet> rewriting_set = new LinkedList<>();
		List<AtomSet> compact_explanationn_set = new LinkedList<>();
		
		LinkedList<AtomSet> exploration_set = new LinkedList<>();
		
		exploration_set.add(observation);	
		
		while(!exploration_set.isEmpty()) {
			AtomSet current = exploration_set.poll();
			
			if(allAbducibles(current)) rewriting_set.add(current);
		
			List<AtomSet> rewritings = new LinkedList<>();
			for(AtomSet rewriting : rewrite(current)) {
				AtomSet reduced_rewriting = pre_reduce(rewriting);
				if(reduced_rewriting.isEmpty()) return null;
				rewritings.add(rewriting);
			}
			
			Utils.removeSubsumed(rewritings, rewriting_set);
			Utils.removeSubsumed(rewritings, exploration_set);
			Utils.removeSubsumed(exploration_set, rewritings);
			Utils.removeSubsumed(rewriting_set, rewritings);
			
			exploration_set.addAll(rewritings);
		}
		Utils.computeCoverSet(rewriting_set);
		
		for(AtomSet rewriting : rewriting_set) {
			compact_explanationn_set.add(rewriting);
			List<AtomSet> reduce_result = atomset_reduce(rewriting);
			if(reduce_result == null) return null;
			else compact_explanationn_set.addAll(reduce_result);
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
					if(!(rew2 instanceof LiftedAtomSet)) {
						if(new RepComparator().compare(rew2, rew1)) {
							it1.remove();
							break;
						}
					}
					else if (rew1 instanceof LiftedAtomSet) {
						CompactComparator cc = new CompactComparator(rew2, rew1);
						List<Partition> parts = cc.getCompactUnifiers(new RepComparator());
						if(!parts.isEmpty()) {
							LiftedAtomSet lrew1 = (LiftedAtomSet) rew1;
							LiftedAtomSet lrew2 = (LiftedAtomSet) rew2;
							
							Utils.refineCompactExplanation(lrew1, lrew2, parts);
						}
					}
				}
			}
		}
	}
	
	protected List<AtomSet> atomset_reduce(AtomSet e) throws IOException, ParsingException {
		int size = e.size();
		
		/* build the index of variable in retrieved table */
		Set<Variable> vars = e.getJoinVariables();
		Map<Variable, Integer> var_index = new HashMap<>();
		int index = 0;
		for(Variable v : vars) {
			var_index.put(v, index++);
		}
		
		e.resort();
		
		ArrayList<Column> columns = new ArrayList<>();
		
		for(int i = 0; i < size; i++) {
			Atom a = e.getAtom(i);
			int[] mapping = new int[a.getTerms().size()];
			for(int m = 0; m < a.getTerms().size(); m++) {
				Term t = a.getTerm(m);
				if(t instanceof Variable) {
					Variable v = (Variable)a.getTerm(m);
					Integer vi = var_index.get(v);
					if(vi != null) mapping[m] = vi;
					else mapping[m] = -1;
				}
				else mapping[m] = -1;
			}
			columns.add(this.store.answerAtomicQuery(a, mapping, vars.size()));
		}		
		
		LinkedList<Tuple<Column, boolean[], Integer>> queue = new LinkedList<>();
		List<AtomSet> result = new LinkedList<>();
		
//		boolean[] pre_selected_atoms = new boolean[size];
//		for(int i = 0; i < size; i++) {
//			Column column = columns.get(i);
//			if(column.isFactColumn()) pre_selected_atoms[i] = true;
//		}
//		AtomSet reduce = new AtomSet();
//		for(int i = 0; i < size; i++) {
//			if(!pre_selected_atoms[i]) reduce.add(e.getAtom(i));
//		}
//		if(reduce.isEmpty()) return null;
//		else result.add(reduce);
		
		for(int i = 0; i < size; i++) {
//			boolean[] selected_atoms = pre_selected_atoms.clone();
			boolean[] selected_atoms = new boolean[size];
			selected_atoms[i] = true;
			Column column = columns.get(i);
			if(column.size() != 0) {
				LiftedAtomSet atomset = liftAtomSet(e, var_index, selected_atoms, columns.get(i).getCopy(), columns);
				if(atomset.isEmpty()) return null;
				if(atomset.getColumn().size() != 0) result.add(atomset);
				queue.add(new Tuple<>(columns.get(i), selected_atoms, i + 1));				
			}
		}
		
		while(!queue.isEmpty()) {
			Tuple<Column, boolean[], Integer> current_t = queue.poll();
			Column cur_column = current_t.a;
			boolean[] selected_atoms = current_t.b;
			int level = current_t.c;			
			
			if(level < size) {
				Column next_column = columns.get(level);
				if(next_column.size() != 0) {
					Column join_column = Utils.innerJoinColumn(cur_column, next_column);
					
					boolean[] next_selected_atoms = selected_atoms.clone();
					next_selected_atoms[level] = true;
					if(join_column.size() != 0) {
						LiftedAtomSet atomset = liftAtomSet(e, var_index, next_selected_atoms, join_column, columns);
						if(atomset.isEmpty()) return null;
						if(atomset.getColumn().size() != 0) result.add(atomset);
						queue.add(new Tuple<>(join_column, next_selected_atoms, level + 1));
					
					}
				}
				boolean[] next_selected_atoms = selected_atoms.clone();
				next_selected_atoms[level] = false;
				queue.add(new Tuple<>(cur_column, next_selected_atoms, level + 1));	
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
