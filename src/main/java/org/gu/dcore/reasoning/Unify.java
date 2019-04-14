package org.gu.dcore.reasoning;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ExRule;
import org.gu.dcore.model.Variable;

public class Unify {
	public static List<Unifier> getSinglePieceUnifier(ExRule rule, AtomSet atomset) {
		return null;
	}
	
	private static List<Unifier> getMostGeneralPreUnifier(ExRule rule, AtomSet atomset) {
		List<Unifier> preUnifiers = new LinkedList<>();
		
		for(Atom a : atomset) {
			for(Atom b : rule.getHead()) {
				if(a.getPredicate().equals(b.getPredicate())) {
					Partition partition = new Partition();
					boolean valid = true;
					for(int i = 0; i < a.getPredicate().getArity(); i++) {
						Term at = a.getTerm(i);
						Term bt = b.getTerm(i);
						
						valid = partition.add(a.getTerm(i), b.getTerm(i));
						if(!valid) break;
					}
					if(valid) preUnifiers.add(new Unifier(new AtomSet(b), new AtomSet(a), 
							partition));
				}
			}
		}
		
		return preUnifiers;	
	}
}
