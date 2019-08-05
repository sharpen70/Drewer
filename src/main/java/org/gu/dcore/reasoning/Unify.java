package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;

public class Unify {
	
	public static List<Unifier> getSinglePieceUnifier(AtomSet block, Rule br, Rule hr) {
		List<Unifier> singlePieceUnifiers = new LinkedList<>();
		Map<Atom, List<Unifier>> preUnifiers = new HashMap<>();
		
		for(Atom a : br.getBody()) {
			for(Atom b : hr.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition(br.getMaxVar());
					
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						partition.add(at, bt);
					}					

					Set<Atom> B = new HashSet<>(); B.add(a);
					Set<Atom> H = new HashSet<>(); H.add(b);
					
					Unifier u = new Unifier(B, H, br, hr, partition);
					
					if(u.isPartitionValid()) {
						if(u.isPieceUnifier()) singlePieceUnifiers.add(u);
						else {
							List<Unifier> unifiers = preUnifiers.get(a);
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
		
		for(Entry<Atom, List<Unifier>> entry : preUnifiers.entrySet()) {
			Iterator<Unifier> it = entry.getValue().iterator();
			while(it.hasNext()) {
				singlePieceUnifiers.addAll(extend(it.next(), preUnifiers));
				it.remove();
			}
		}
		
		return singlePieceUnifiers;	
	}
	
	public static List<Unifier> getSinglePieceUnifier(Rule br, Rule hr) {
	//	List<Unifier> preUnifiers = new LinkedList<>();
		List<Unifier> singlePieceUnifiers = new LinkedList<>();
		Map<Atom, List<Unifier>> preUnifiers = new HashMap<>();
		
		for(Atom a : br.getBody()) {
			for(Atom b : hr.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition(br.getMaxVar());
					
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						partition.add(at, bt);
					}					

					Set<Atom> B = new HashSet<>(); B.add(a);
					Set<Atom> H = new HashSet<>(); H.add(b);
					
					Unifier u = new Unifier(B, H, br, hr, partition);
					
					if(u.isPartitionValid()) {
						if(u.isPieceUnifier()) singlePieceUnifiers.add(u);
						else {
							List<Unifier> unifiers = preUnifiers.get(a);
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
		
		for(Entry<Atom, List<Unifier>> entry : preUnifiers.entrySet()) {
			Iterator<Unifier> it = entry.getValue().iterator();
			while(it.hasNext()) {
				singlePieceUnifiers.addAll(extend(it.next(), preUnifiers));
				it.remove();
			}
		}
		
		return singlePieceUnifiers;	
	}
	
	private static List<Unifier> extend(Unifier unifier, Map<Atom, List<Unifier>> preUnifiers) {
		List<Unifier> result = new LinkedList<>();
		
		Set<Atom> stickyAtoms = unifier.getStickyAtoms();
		
		List<Unifier> preq = new LinkedList<>();
		preq.add(unifier);
		
		for(Atom a : stickyAtoms) {
			List<Unifier> temp_preq = new LinkedList<>();
			
			for(Unifier pu : preq) {
				List<Unifier> us = preUnifiers.get(a);
				
				if(us == null || us.isEmpty()) continue;
				
				for(Unifier u : us) {
					Unifier extended = pu.extend(u);
					if(extended != null) temp_preq.add(extended);
				}
			}
			
			preq = temp_preq;
		}
		
		for(Unifier pu : preq) {
			if(pu.isPieceUnifier()) result.add(pu);
			else result.addAll(extend(pu, preUnifiers));
		}
		
		return result;
	}
} 
