package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AtomSet implements Iterable<Atom> {
	private ArrayList<Atom> atoms;
	
	public AtomSet() {
		atoms = new ArrayList<>();
	}
	
	public AtomSet(AtomSet as) {
		atoms = new ArrayList<>();
		for(Atom a : as.atoms) this.atoms.add(a);
	}
	
	public AtomSet(ArrayList<Atom> _atoms) {
		this.atoms = _atoms;
	}
	
	public AtomSet(Collection<Atom> atoms) {
		this.atoms = new ArrayList<>();
		for(Atom a : atoms) this.atoms.add(a);
	}
	
	public AtomSet(Atom atom) {
		this.atoms = new ArrayList<>();
		this.atoms.add(atom);
	}
	
	public void add(Atom a) {
		this.atoms.add(a);
	}
	
	public void addAll(AtomSet as) {
		addAll(as.atoms);
	}
	
	public void addAll(Collection<Atom> as) {
		for(Atom a : as) add(a);
	}
	
	public boolean isEmpty() {
		return this.atoms.isEmpty();
	}
	
	public ArrayList<Atom> getAtoms() {
		return this.atoms;
	}
	
	public Atom getAtom(int i) {
		return this.atoms.get(i);
	}
	
	public boolean contains(Atom a) {
		return this.atoms.contains(a);
	}
	
	public boolean containsAll(Collection<Atom> atoms) {
		return this.atoms.containsAll(atoms);
	}
	
	public int size() {
		return this.atoms.size();
	}
	
	@Override
	public Iterator<Atom> iterator() {
		return atoms.iterator();
	}
	
	public Set<Variable> getVariables() {
		Set<Variable> vars = new HashSet<>();
		
		for(Atom a : this.atoms) {
			vars.addAll(a.getVariables());
		}
		
		return vars;
	}
	
	public Set<Term> getTerms() {
		Set<Term> terms = new HashSet<>();
		
		for(Atom a : this.atoms) {
			terms.addAll(a.getTerms());
		}
		
		return terms;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		for(int i = 0; i < atoms.size(); i++) {
			s += atoms.get(i);
			if(i != atoms.size() - 1) s += ", ";
		}
		
		return s;
	}
}
