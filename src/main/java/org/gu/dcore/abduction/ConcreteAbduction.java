package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.Partition;
import org.gu.dcore.rewriting.CompactComparator;
import org.gu.dcore.rewriting.ConcreteComparator;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class ConcreteAbduction extends AbstactQueryAbduction {
	
	public ConcreteAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
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
						List<Partition> parts = cc.getCompactUnifiers(new ConcreteComparator());
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
		List<LiftedAtomSet> result = new LinkedList<>();
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
		
		Iterator<Pair<boolean[], Column>> it1 = preMatchedComponents.iterator();		
		
		while(it1.hasNext()) {
			Pair<boolean[], Column> ct1 = it1.next();
			Iterator<Pair<boolean[], Column>> it2 = preMatchedComponents.iterator();
			while(it2.hasNext()) {
				Pair<boolean[], Column> ct2 = it2.next();
				
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
					for(int i = 0; i < selected_atoms1.length; i++) {
						if(!selected_atoms1[i]) {
							Atom a = e.getAtom(i);
							Set<Variable> rvars = a.getVariables();
							boolean[] blank = column2.getPosition_blank();
							for(Variable v : rvars) {
								int p = var_index.get(v);
								if(blank[p]) jk[jk_length++] = p;
							}
						}
					}
					column2.outerJoin(column1, jk, jk_length);
				}
			}
		}
		
		for(Pair<boolean[], Column> pair : preMatchedComponents) {
			result.add(liftAtomSet(e, var_index, pair.a, pair.b));
		}
		
		return result;
	}
}
