package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Unify {
	
	public static List<SinglePieceUnifier> getSinglePieceUnifiers(AtomSet body, Rule hr) {
		return getSinglePieceUnifiers(body, body, hr, new HashSet<>());
	}
	
	public static List<SinglePieceUnifier> getSinglePieceUnifiers(AtomSet block, AtomSet _rbody, Rule hr, Set<Variable> restricted_var) {
		List<SinglePieceUnifier> singlePieceUnifiers = new LinkedList<>();
		Map<Atom, List<SinglePieceUnifier>> preUnifiers = new HashMap<>();
		
		AtomSet rbody = new AtomSet(_rbody);
		
		if(!restricted_var.isEmpty()) {
			Predicate restricted_predicate = PredicateFactory.instance().createPredicate("Restricted", restricted_var.size());
			Atom restricted_atom = new Atom(restricted_predicate, restricted_var);
			rbody.add(restricted_atom);
		}
		
		for(Atom a : block) {
			for(Atom b : hr.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition(rbody.getMaxVarValue() + 1);
					
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						partition.add(at, bt);
					}					

					Set<Atom> B = new HashSet<>(); B.add(a);
					Set<Atom> H = new HashSet<>(); H.add(b);
					
					SinglePieceUnifier u = new SinglePieceUnifier(B, H, rbody, hr, partition);
					
					if(u.isPartitionValid()) {
						if(u.isPieceUnifier()) singlePieceUnifiers.add(u);
						else {
							List<SinglePieceUnifier> unifiers = preUnifiers.get(a);
							if(unifiers == null) {
								unifiers = new LinkedList<>();
								unifiers.add(u);
								preUnifiers.put(a, unifiers);
							}
							else unifiers.add(u);
						}
					}
				}
			}
		}
		
		for(Entry<Atom, List<SinglePieceUnifier>> entry : preUnifiers.entrySet()) {
			Iterator<SinglePieceUnifier> it = entry.getValue().iterator();
			while(it.hasNext()) {
				singlePieceUnifiers.addAll(extend(it.next(), preUnifiers));
				it.remove();
			}
		}
		
		return singlePieceUnifiers;	
	}
	
	public static List<AggregateUnifier> getAggregatedPieceUnifier(AtomSet block, AtomSet rbody, Rule hr, Set<Variable> restricted_var) {
		List<SinglePieceUnifier> singlePieceUnifiers = getSinglePieceUnifiers(block, rbody, hr, restricted_var);
		
		LinkedList<AggregateUnifier> unifAggregated = new LinkedList<AggregateUnifier>();
		
		if (!singlePieceUnifiers.isEmpty()) {
			LinkedList<SinglePieceUnifier> restOfUnifToAggregate = new LinkedList<>(singlePieceUnifiers);
			
			while(!restOfUnifToAggregate.isEmpty()) {
				SinglePieceUnifier u = restOfUnifToAggregate.poll();
				
				for(AggregateUnifier _u : aggregate(u, restOfUnifToAggregate)) {
					unifAggregated.add(_u);
				}
			}
		}
		
		return unifAggregated;			
	}
	
	private static LinkedList<AggregateUnifier> aggregate(SinglePieceUnifier u,
			LinkedList<SinglePieceUnifier> l) {
		AggregateUnifier aggregateUnifier = new AggregateUnifier(u);
		
		return aggregate(aggregateUnifier, l);
	}
	
	private static LinkedList<AggregateUnifier> aggregate(AggregateUnifier u,
			LinkedList<SinglePieceUnifier> l) {
		LinkedList<SinglePieceUnifier> lu = new LinkedList<>(l);
		// if there is no more unifier to aggregate
		if (lu.isEmpty()) {
			LinkedList<AggregateUnifier> res = new LinkedList<>();
			res.add(u);
			return res;
		} else { 
			SinglePieceUnifier first = lu.poll(); 
			LinkedList<AggregateUnifier> res = aggregate(u, lu);
			AggregateUnifier aggu = u.aggregate(first);
			
			if (aggu != null) {
				res.addAll(aggregate(aggu, lu));
			}
			
			return res;
		}
	}
	
	private static List<SinglePieceUnifier> extend(SinglePieceUnifier unifier, Map<Atom, List<SinglePieceUnifier>> preUnifiers) {
		List<SinglePieceUnifier> result = new LinkedList<>();
		
		Set<Atom> stickyAtoms = unifier.getStickyAtoms();
		
		List<SinglePieceUnifier> preq = new LinkedList<>();
		preq.add(unifier);
			
		for(Atom a : stickyAtoms) {
			List<SinglePieceUnifier> temp_preq = new LinkedList<>();
			
			for(SinglePieceUnifier pu : preq) {
				List<SinglePieceUnifier> us = preUnifiers.get(a);
				
				if(us == null || us.isEmpty()) continue;
				
				for(SinglePieceUnifier u : us) {
					SinglePieceUnifier extended = pu.extend(u);
					if(extended != null) temp_preq.add(extended);
				}
			}
			
			preq = temp_preq;
		}
		
		for(SinglePieceUnifier pu : preq) {
			if(pu.isPieceUnifier()) result.add(pu);
			else if (pu.isPartitionValid()) result.addAll(extend(pu, preUnifiers));
		}
		
		return result;
	}
} 
