package org.gu.dcore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gu.dcore.utils.Utils;

public class AtomSet implements Iterable<Atom> {
	private ArrayList<Atom> atoms;
	private Set<Variable> vars;
	private Set<RepConstant> rcs;
	
	private int maxVarValue = -1;
	private int maxRCValue = -1;
	
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
	
	public void setMaxVarValue(int v) {
		this.maxVarValue = v;
	}
	
	public void setMaxVarRCValue(int v) {
		this.maxRCValue = v;
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
		if(this.vars != null) return this.vars;
		
		this.vars = new HashSet<>();
		
		for(Atom a : this.atoms) {
			vars.addAll(a.getVariables());
		}
		
		return this.vars;
	}
	
	public Set<Variable> getJoinVariables() {
		Set<Variable> joinVars = new HashSet<>();
		for(int i = 0; i < this.size(); i++) {
			Set<Variable> vars = this.getAtom(i).getVariables();
			for(Variable var : vars) {
				if(!joinVars.contains(var)) {
					for(int j = i + 1; j < this.size(); j++) {
						if(this.getAtom(j).getVariables().contains(var))
							joinVars.add(var);
					}
				}
			}
		}
		return joinVars;
	}
	
	public Set<RepConstant> getRepConstants() {
		if(this.rcs != null) return this.rcs;
		
		this.rcs = new HashSet<>();
		
		for(Atom a : this.atoms) {
			rcs.addAll(a.getRepConstants());
		}
		
		return this.rcs;
	}
	
	/* sort the atoms such that a_j is connected with a_i (i < j) if possible*/
	public void resort() {
		Set<Variable> vs = new HashSet<>();
		vs.addAll(this.atoms.get(0).getVariables());
		
		for(int i = 1; i < this.size(); i++) {
			Atom ai = this.atoms.get(i);
			Set<Variable> ivs = ai.getVariables();
			if(Collections.disjoint(vs, ivs)) {
				for(int j = i + 1; j < this.size(); j++) {
					Atom aj = this.atoms.get(j);
					Set<Variable> jvs = aj.getVariables();
					if(!Collections.disjoint(vs, jvs)) {
						this.atoms.set(i, aj);
						this.atoms.set(j, ai);
						ivs = jvs;
					}
				}
			}
			vs.addAll(ivs);
		}
	}
	
	public int getMaxVarValue() {
		if(this.maxVarValue == -1) {
			for(Variable v : this.getVariables()) {
				if(v.getValue() > this.maxVarValue)
					this.maxVarValue = v.getValue();
			}
		}
		return this.maxVarValue;
	}
	
	public int getMaxRCValue() {
		if(this.maxRCValue == -1) {
			for(RepConstant rc : this.getRepConstants()) {
				if(rc.getValue() > this.maxRCValue)
					this.maxRCValue = rc.getValue();
			}
		}
		return this.maxRCValue;
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
	
	public String toShort() {
		String s = "";
		
		for(int i = 0; i < atoms.size(); i++) {
			s += atoms.get(i).toShort();
			if(i != atoms.size() - 1) s += ", ";
		}
		
		return s;
	}
	
	public String toRDFox() {
		String s = "";
		
		for(int i = 0; i < atoms.size(); i++) {
			s += atoms.get(i).toRDFox();
			if(i != atoms.size() - 1) s += ", ";
		}
		
		return s;
	}
	
	public String toVLog(Set<Variable> ex) {
		String s = "";
		
		for(int i = 0; i < atoms.size(); i++) {
			s += atoms.get(i).toVLog(ex);
			if(i != atoms.size() - 1) s += ", ";
		}
		
		return s;
	}
	
	public String toVLog() {
		String s = "";
		
		for(int i = 0; i < atoms.size(); i++) {
			s += atoms.get(i).toVLog();
			if(i != atoms.size() - 1) s += ", ";
		}
		
		return s;
	}
}
