package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AtomSet implements Iterable<Atom> {
	private ArrayList<Atom> atoms;
	
	public AtomSet(ArrayList<Atom> _atoms) {
		this.atoms = _atoms;
	}
	
	public AtomSet(Atom atom) {
		this.atoms = new ArrayList<>();
		this.atoms.add(atom);
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
			vars.addAll(a.getVariable());
		}
		
		return vars;
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
