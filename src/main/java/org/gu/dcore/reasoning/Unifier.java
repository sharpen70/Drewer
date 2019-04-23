package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Variable;

public class Unifier {
	private Partition partition;
	private Set<Atom> pB;
	private Set<Atom> H;
	
	private AtomSet B;
	private Map<Term, List<Atom>> sticky;
	
	public Unifier(Set<Atom> _pB, Set<Atom> _H, Partition _partition, AtomSet _B) {
		this.pB = _pB;
		this.H = _H;
		this.partition = _partition;
		this.B = _B;
	
		sticky = new HashMap<>();
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
		
		Partition newP = this.partition.getCopy();
		
		if(!newP.join(u.partition)) return null;
		
		newpB.addAll(u.pB);
		newH.addAll(u.H);
		
		return new Unifier(newpB, newH, newP, this.B);
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
	
	public Partition getPartition() {
		return this.partition;
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
