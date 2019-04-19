package org.gu.dcore.reasoning;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
		List<Unifier> preUnifiers = new LinkedList<>();
		List<Unifier> singlePieceUnifier = new LinkedList<>();
		
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
						Unifier u = new Unifier(new AtomSet(b), new AtomSet(a), partition, atomset, rule);
						if(u.isPieceUnifier()) singlePieceUnifier.add(u);
						else preUnifiers.add(u);
					}
				}
			}
		}
		
		Queue<Unifier> toExtend = new LinkedList<>();
		toExtend.addAll(preUnifiers);
		
		while(!toExtend.isEmpty()) {
			Unifier unifier = toExtend.poll();
			
			List<Unifier> extended = extend(unifier, atomset, preUnifiers);
			
			for(Unifier u : extended) {
				if(u.isPieceUnifier()) singlePieceUnifier.add(u);
				else toExtend.add(u);
			}
		}
		
		return singlePieceUnifier;	
	}
	
	private static List<Unifier> extend(Unifier unifier, AtomSet atomset, List<Unifier> preUnifiers) {
		
		
		return null;
	}
}
