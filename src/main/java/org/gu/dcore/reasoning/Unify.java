package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.ExRule;

public class Unify {
	public static List<Unifier> getSinglePieceUnifier(ExRule rule, AtomSet atomset) {
		return null;
	}
	
	private static List<Unifier> getMostGeneralPreUnifier(ExRule rule, AtomSet atomset) {
	//	List<Unifier> preUnifiers = new LinkedList<>();
		List<Unifier> singlePieceUnifiers = new LinkedList<>();
		Map<Atom, List<Unifier>> preUnifiers = new HashMap<>();
		
		for(Atom a : atomset) {
			for(Atom b : rule.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition();
					boolean valid = true;
					
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						TermType atType = at instanceof Constant ? TermType.CONSTANT : TermType.DEFAULT;
						if(!partition.add(at, atType, bt, rule.getTermType(bt))) {
							valid = false; break;
						}
					}
					
					if(valid) {
						Set<Atom> B = new HashSet<>(); B.add(a);
						Set<Atom> H = new HashSet<>(); H.add(b);
						
						Unifier u = new Unifier(B, H, partition, atomset);
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
		Set<Atom> stickyAtoms = unifier.getStickyAtoms();
		
		Queue<Partition> prePartition = new LinkedList<>();
		prePartition.add(unifier.getPartition());
		
		for(Atom a : stickyAtoms) {
			while(!prePartition.isEmpty()) {
				Partition p = prePartition.poll();
				List<Unifier> us = preUnifiers.get(a);
				
				if(us == null || us.isEmpty()) continue;
				
				for(Unifier u : us) {
					
				}
			}
		}
		
		return null;
	}
}
