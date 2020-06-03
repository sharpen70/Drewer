package org.gu.dcore.rewriting;

import java.util.ArrayList;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.reasoning.NormalSubstitution;

public class AbstactComparator implements Comparator {
	private boolean extended = false;
	
	public AbstactComparator(boolean extended) {
		this.extended = extended;
	}
	
	@Override
	public boolean compare(AtomSet source, AtomSet target) {
		return true;
	}
	
	protected NormalSubstitution homo(Atom source, Atom target) {
		if(!source.getPredicate().equals(target.getPredicate())) return null;
		
		NormalSubstitution sub = new NormalSubstitution();
		
		for(int i = 0; i < source.getTerms().size(); i++) {
			Term st = source.getTerm(i);
			Term tt = target.getTerm(i);
			
			if(st instanceof Constant && (this.extended || st instanceof Constant)) {
				if(!st.equals(tt)) return null;
			}
			else {
				if(!sub.add(st, tt, false)) return null;
			}
		}
		
		return sub;
	}
	
	protected ArrayList<ArrayList<NormalSubstitution>> getSubsitutionsForSourceAtoms(AtomSet source, AtomSet target) {
		int size = source.size();
		ArrayList<ArrayList<NormalSubstitution>> substitutionsPerLv = new ArrayList<>(size);
		
		for(int i = 0; i < size; i++) {
			substitutionsPerLv.add(null);
		}
		
		for(int i = 0; i < source.size(); i++) {
			ArrayList<NormalSubstitution> subs = new ArrayList<>();
			for(int j = 0; j < target.size(); j++) {
				Atom sa = source.getAtom(i);
				Atom sb = target.getAtom(j);
				NormalSubstitution sub = homo(sa, sb);
				if(sub != null) subs.add(sub);
			}
			if(subs.isEmpty()) return null;
			else substitutionsPerLv.set(i, subs);
		}
		return substitutionsPerLv;
	}
}
