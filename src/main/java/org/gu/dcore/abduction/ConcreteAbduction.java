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

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.Partition;
import org.gu.dcore.rewriting.CompactComparator;
import org.gu.dcore.rewriting.Comparator;
import org.gu.dcore.rewriting.ConcreteComparator;
import org.gu.dcore.rewriting.RepComparator;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class ConcreteAbduction extends AbstactQueryAbduction {
	public ConcreteAbduction(List<Rule> onto, AtomSet q, DatalogEngine D) {
		super(onto, q, D);
	}
	
	public ConcreteAbduction(List<Rule> onto, AtomSet q, DatalogEngine D, Set<Predicate> abdu) {
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
			List<AtomSet> reduce_result = atomset_reduce(rewriting);
			if(reduce_result == null) return null;
			if(reduce_result.size() == 0) compact_explanationn_set.add(rewriting);
			else compact_explanationn_set.addAll(reduce_result);
		}
		compact_reduce(compact_explanationn_set, new RepComparator());
		compact_reduce(compact_explanationn_set, new ConcreteComparator());
		
		return compact_explanationn_set;
	}
	
	private void compact_reduce(List<AtomSet> rewritings, Comparator cmp) {
		Iterator<AtomSet> it1 = rewritings.iterator();
		
		while(it1.hasNext()) {
			AtomSet rew1 = it1.next();			
			Iterator<AtomSet> it2 = rewritings.iterator();
			while(it2.hasNext()) {
				AtomSet rew2 = it2.next();
				if(!rew1.equals(rew2)) {
					if(!(rew2 instanceof LiftedAtomSet)) {
						if(Utils.isMoreGeneral(rew2, rew1)) it1.remove();
					}
					else if (rew1 instanceof LiftedAtomSet) {
						CompactComparator cc = new CompactComparator(rew1, rew2);
						List<Partition> parts = cc.getCompactUnifiers(cmp);
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
	
	protected List<AtomSet> atomset_reduce(AtomSet e) throws IOException, ParsingException {
		List<AtomSet> result = new LinkedList<>();
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
		
		LinkedList<Tuple<Integer, boolean[], Column>> queue = new LinkedList<>();
		LinkedList<Pair<boolean[], Column>> preMatchedComponents = new LinkedList<>();
		
		Tuple<Integer, boolean[], Column> init = new Tuple<>(0, new boolean[size], new Column(vars.size()));
		queue.add(init);
		
		while(!queue.isEmpty()) {
			Tuple<Integer, boolean[], Column> p = queue.pop();
			int level = p.a;
			boolean[] selected_atoms = p.b;
			Column current_column = p.c;
			
			if(level >= e.size()) {
				if(current_column.size() != 0) {
					preMatchedComponents.add(new Pair<>(selected_atoms, current_column));
				}
				continue;
			}
			
			boolean[] next_t = selected_atoms.clone();
			next_t[level] = true;
			boolean[] next_f = selected_atoms.clone();
			next_f[level] = false;
			
			Column next_column = columns.get(level);
			level++;	
			
			if(next_column.getTuples().isEmpty()) {
				queue.add(new Tuple<>(level, next_f, current_column));
			}
			else if(current_column.getTuples().isEmpty()) {				
				queue.add(new Tuple<>(level, next_t, next_column));
				queue.add(new Tuple<>(level, next_f, current_column));
			}					
			else {
				Column join_column = current_column.full_join(next_column);
				
				queue.add(new Tuple<>(level, next_t, join_column));
				queue.add(new Tuple<>(level, next_f, current_column));
			}
		}
		
		Iterator<Pair<boolean[], Column>> it1 = preMatchedComponents.iterator();		
		
		while(it1.hasNext()) {
			Pair<boolean[], Column> ct1 = it1.next();
			Iterator<Pair<boolean[], Column>> it2 = preMatchedComponents.iterator();
			while(it2.hasNext()) {
				Pair<boolean[], Column> ct2 = it2.next();
				
				if(ct1 == ct2) continue;
				
				boolean[] selected_atoms1 = ct1.a;
				boolean[] selected_atoms2 = ct2.a;
				Column column1 = ct1.b;
				Column column2 = ct2.b;
				
				boolean contain = true;
				for(int i = 0; i < selected_atoms1.length; i++) {
					if(selected_atoms2[i] && !selected_atoms1[i]) {
						contain = false;
						break;
					}
				}
				if(contain) {
					int[] jk = new int[column1.getArity()];
					int jk_length = 0;
					Set<Variable> piv = new HashSet<>();
					
					for(int i = 0; i < selected_atoms1.length; i++) {
						if(!selected_atoms1[i]) {
							Atom a = e.getAtom(i);
							Set<Variable> rvars = a.getVariables();
							boolean[] blank = column2.getPosition_blank();
							for(Variable v : rvars) {
								int p = var_index.get(v);
								if(blank[p] && piv.add(v)) jk[jk_length++] = p;
							}
						}
					}
					column2.outerJoin(column1, jk, jk_length);
				}
			}
		}
		
		for(Pair<boolean[], Column> pair : preMatchedComponents) {
			if(pair.b.size() != 0) { 
				LiftedAtomSet le = liftAtomSet(e, var_index, pair.a, pair.b);
				if(le.isEmpty()) return null;
				else result.add(le);
			}
		}
		
		return result;
	}
}
