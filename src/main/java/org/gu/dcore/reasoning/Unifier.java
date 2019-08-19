package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Unifier {
	private Partition partition;
	private Set<Atom> B;
	private Set<Atom> H;
	private AtomSet body;
	private Rule hr;
	
	private Set<Atom> stickyAtoms;
	private boolean valid;
	private boolean analyzed = false;
	
	public Unifier(Set<Atom> B, Set<Atom> H, AtomSet body, Rule hr, Partition partition) {
		this.B = B;
		this.H = H;
		this.body = body;
		this.hr = hr;
		
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
	
	public Term getImageOf(Term t, boolean withOffset) {
		int offset = withOffset ? this.partition.getOffset() : 0;
		return this.partition.getSubstitution().getImageOf(t, offset);
	}
	
	public Atom getImageOf(Atom a, boolean withOffset) {
		return null;
	}
	
	public AtomSet getImageOfPiece() {
		AtomSet up = new AtomSet();
		for(Atom a : this.B) {
			up.add(this.partition.getSubstitution().getImageOf(a, 0));
		}
		return up;
	}
	
	public AtomSet getImageOf(AtomSet atomset, boolean withOffset) {
		int offset = withOffset ? this.partition.getOffset() : 0;
		return this.partition.getSubstitution().getImageOf(atomset, offset);
	}
	
//	public Set<Term> getImageOf(Set<Variable> vars) {
//		return this.partition.getSubstitution().getImageOf(vars);
//	}
	
	public Unifier extend(Unifier u) {
		Set<Atom> newpB = new HashSet<>();
		Set<Atom> newH = new HashSet<>();
		
		newpB.addAll(this.B);
		newH.addAll(this.H);		
		newpB.addAll(u.B);
		newH.addAll(u.H);
		
		Partition newP = this.partition.join(u.partition);
		
		return new Unifier(newpB, newH, this.body, this.hr, newP);
	}
	
	public boolean isPartitionValid() {
		if(!this.analyzed) analyze();
		return this.valid;
	}
	
	public boolean isPieceUnifier() {
		if(!this.analyzed) analyze();
		return this.valid && this.stickyAtoms.isEmpty();
	}
	
	public Set<Atom> getStickyAtoms() {
		if(!this.analyzed) analyze();
		return this.stickyAtoms;
	}
	
	private void analyze() {
		this.analyzed = true;
		this.valid = true;
		this.stickyAtoms = new HashSet<>();
		
		List<Atom> minus = new LinkedList<>();
		
		for(Atom a : this.body) {
			if(!this.B.contains(a)) minus.add(a);
		}
		
		for(Set<Object> c : this.partition.categories) {
			boolean constant = false;
			boolean existential = false;
			boolean frontier = false;
			
			Set<Atom> separatingAtoms = new HashSet<>();
			
			for(Object o : c) {
				if(o instanceof Constant) {
					if(constant == true) { this.valid = false; return; }
					constant = true;
				}
				else {
					int value = (int)o;
					
					if(value >= this.partition.getOffset()) {
						if(hr.isExistentialVar(value - this.partition.getOffset())) 
							if(existential || frontier) { this.valid = false; return; }
							else existential = true;
						else
							if(existential) { this.valid = false; return; }
							else frontier = true;
					}
					else {
						for(Atom a : minus) {
							for(Variable v : a.getVariables()) {
								if(v.getValue() == value) {
									separatingAtoms.add(a);
									break;
								}
							}
						}
					}
				}
			}
			
			if(existential) stickyAtoms.addAll(separatingAtoms);
		}
	}
}
