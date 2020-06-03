package org.gu.dcore.homomorphism;

import java.util.ArrayList;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.NormalSubstitution;

public class Homomorphism {
	private AtomSet source;
	private AtomSet target;	
	
	private int level_size;
	private ArrayList<ArrayList<NormalSubstitution>> substitutionsPerLv;
	private NormalSubstitution[] subsCurrentPath;
	private int[] directions;
			
	public Homomorphism(AtomSet source, AtomSet target) {
		this.source = source;
		this.target = target;
		
		this.level_size = source.size();
		this.substitutionsPerLv = new ArrayList<>(this.level_size);
		this.subsCurrentPath = new NormalSubstitution[this.level_size + 1];
		this.directions = new int[this.level_size];
		
		for(int i = 0; i < this.level_size; i++) {
			this.directions[i] = -1;
			this.substitutionsPerLv.add(null);
		}
		
		this.subsCurrentPath[0] = new NormalSubstitution();
	}
	
	public boolean exist() {
		if(!getSubsitutionsForSourceAtoms()) return false;
		
		return backtrack();
	}
	
	private boolean backtrack() {
		int level = 0;
		
		while(level >= 0 && level < level_size) {
			int dir = this.directions[level];
			NormalSubstitution cur_sub = this.subsCurrentPath[level];
			ArrayList<NormalSubstitution> subs = this.substitutionsPerLv.get(level);
			
			int i = dir + 1;
			for(; i < subs.size(); i++) {
				NormalSubstitution lv_sub = subs.get(i);
				NormalSubstitution merged_sub = cur_sub.add(lv_sub, false);
				if(merged_sub != null) {
					cur_sub = merged_sub;
					break;
				}
			}
			if(i >= subs.size()) {
				this.directions[level] = -1;
				level--;
			}
			else {
				this.directions[level] = i;
				level++;
				this.subsCurrentPath[level] = cur_sub;
			}
		}
		
		return level > 0;
	}
	
	/* normal:  normal homomorphism or homomorphism with extended renaming */
	private NormalSubstitution homo(Atom source, Atom target) {
		if(!source.getPredicate().equals(target.getPredicate())) return null;
		
		NormalSubstitution sub = new NormalSubstitution();
		
		for(int i = 0; i < source.getTerms().size(); i++) {
			Term st = source.getTerm(i);
			Term tt = target.getTerm(i);
			
			if(st instanceof Constant) {
				if(!st.equals(tt)) return null;
			}
			else {
				if(!sub.add(st, tt, false)) return null;
			}
		}
		
		return sub;
	}
	
	private boolean getSubsitutionsForSourceAtoms() {
		for(int i = 0; i < source.size(); i++) {
			ArrayList<NormalSubstitution> subs = new ArrayList<>();
			for(int j = 0; j < target.size(); j++) {
				Atom sa = source.getAtom(i);
				Atom sb = target.getAtom(j);
				NormalSubstitution sub = homo(sa, sb);
				if(sub != null) subs.add(sub);
			}
			if(subs.isEmpty()) return false;
			else this.substitutionsPerLv.set(i, subs);
		}
		return true;
	}
}
