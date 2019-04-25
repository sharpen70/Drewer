package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Unifier {
	private Partition partition;
	private Set<Atom> B;
	private Set<Atom> H;
	
	public Unifier(Set<Atom> pB, Set<Atom> H, Partition partition) {
		this.B = B;
		this.H = H;
		this.partition = partition;
	}
	
	public Partition getPartition() {
		return this.partition;
	}
	
	public Set<Atom> getB() {
		return this.B;
	}
	
	public Set<Atom> getH() {
		return this.H;
	}
	
	public Atom getImageOf(Atom a) {
		return null;
	}
	
	public AtomSet getImageOf(AtomSet atomset) {
		return null;
	}
	
	public Unifier extend(Unifier u) {
		Set<Atom> newpB = new HashSet<>();
		Set<Atom> newH = new HashSet<>();
		
		newpB.addAll(this.B);
		newH.addAll(this.H);		
		newpB.addAll(u.B);
		newH.addAll(u.H);
		
		Partition newP = this.partition.join(u.partition);
		
		return new Unifier(newpB, newH, newP);
	}
	
	public boolean isPieceUnifier() {
		if(sticky == null) this.computeSticky();
		return this.sticky.isEmpty();
	}
	
	public Set<Atom> getStickyAtoms() {
		if(this.sticky == null) this.computeSticky();
		
		Set<Atom> re = new HashSet<>();
		
		for(List<Atom> atoms : this.sticky.values()) 
			re.addAll(atoms);
		
		return re;
	}
	
	private void computeSticky() {
		this.sticky = new HashMap<>();
		List<Atom> minus = new LinkedList<>();
		
		for(Atom a : this.B) {
			if(!this.pB.contains(a)) minus.add(a);
		}
		
		Set<Variable> pBvar = new HashSet<>();
		for(Atom a : pB) pBvar.addAll(a.getVariable());
		
		for(Atom a : minus) {
			for(Variable v : pBvar) {
				if(a.getVariable().contains(v)) {
					List<Atom> sticky_atoms = this.sticky.get(v);
					if(sticky_atoms == null) {
						sticky_atoms = new LinkedList<>();
						sticky_atoms.add(a);
						this.sticky.put(v, sticky_atoms);
					}
					else sticky_atoms.add(a);
				}
			}
		}
		
		for(Entry<Set<Term>, boolean[]> category : this.partition.categories.entrySet()) {
			if(category.getValue()[1]) {
				for(Term t : this.sticky.keySet()) 
					if(!category.getKey().contains(t)) this.sticky.remove(t);
			}
		}
	}
}
