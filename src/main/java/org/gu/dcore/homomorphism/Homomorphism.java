package org.gu.dcore.homomorphism;

import java.util.ArrayList;
import java.util.List;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.NormalSubstitution;
import org.gu.dcore.reasoning.Substitution;

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
		this.subsCurrentPath = new NormalSubstitution[this.level_size];
		this.directions = new int[this.level_size];
		
		for(int i = 0; i < this.level_size; i++) {
			this.directions[i] = -1;
		}
		
		this.subsCurrentPath[0] = new NormalSubstitution();
	}
	
	public boolean exist() {
		if(!getSubsitutionsForSourceAtoms()) return false;
		
		return backtrack();
	}
	
	public boolean backtrack() {
		int level = 0;
		
		while(level >= 0 && level < level_size) {
			int dir = this.directions[level];
			NormalSubstitution cur_sub = this.subsCurrentPath[level];
			ArrayList<NormalSubstitution> subs = this.substitutionsPerLv.get(level);
			
			int i = dir + 1;
			for(; i < subs.size(); i++) {
				NormalSubstitution lv_sub = subs.get(i);
				cur_sub = cur_sub.add(lv_sub);
				if(cur_sub != null) break;
			}
			if(i >= subs.size()) {
				this.directions[level] = 0;
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
	
	private Substitution homo(Atom source, Atom target) {
		if(!source.getPredicate().equals(target.getPredicate())) return null;
		
		NormalSubstitution sub = new NormalSubstitution();
		
		for(int i = 0; i < source.getTerms().size(); i++) {
			Term st = source.getTerm(i);
			Term tt = target.getTerm(i);
			
			if(st instanceof Constant) {
				if(!st.equals(tt)) return null;
			}
			else {
				if(!sub.add((Variable)st, tt)) return null;
			}
		}
		
		return sub;
	}
	
	private boolean getSubsitutionsForSourceAtoms() {
		for(int i = 0; i < source.size(); i++) {
			ArrayList<Substitution> subs = new ArrayList<>();
			for(int j = 0; j < target.size(); j++) {
				Atom sa = source.getAtom(i);
				Atom sb = target.getAtom(j);
				Substitution sub = homo(sa, sb);
				if(sub != null) subs.add(sub);
			}
			if(subs.isEmpty()) return false;
			else this.substitutionsPerLv.set(i, subs);
		}
		return true;
	}
}
