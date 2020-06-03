package org.gu.dcore.reasoning;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;

public class AggregateUnifier {
	private List<SinglePieceUnifier> spus;
	private Partition partition;
	private Set<Atom> piece;
	private Rule hr;
	
	public AggregateUnifier(SinglePieceUnifier u) {
		this.spus = new LinkedList<>();
		this.spus.add(u);
		this.partition = u.getPartition();
		this.piece = u.getB();
		this.hr = u.getRule();
	}
	
	public AggregateUnifier(List<SinglePieceUnifier> lu, Partition partition, Set<Atom> piece, Rule hr) {
		this.spus = lu;
		this.partition = partition;
		this.piece = piece;
		this.hr = hr;
	}
	
	public AggregateUnifier aggregate(SinglePieceUnifier spu) {
		Set<Atom> agg_piece = new HashSet<>(this.piece);
		for(Atom a : spu.getB()) if(!agg_piece.add(a)) return null;
		
		Partition agg_partition = this.partition.join(spu.getPartition(), true);
		if(!agg_partition.isAdmissible()) return null;
		
		List<SinglePieceUnifier> agg_spus = new LinkedList<>(spus);
		agg_spus.add(spu);
		
		return new AggregateUnifier(agg_spus, agg_partition, agg_piece, this.hr);
	}
	
	public List<SinglePieceUnifier> getSPUs() {
		return this.spus;
	}	
	
	public Set<Term> getImageOfExistential(Term t) {		
		Set<Term> terms = new HashSet<>();
		
		for(SinglePieceUnifier spu : this.spus) {
			Term st = spu.getImageOf(t, 1);
			terms.add(this.partition.getSubstitution().getImageOf(st, 0, 0, 0));
		}
		
		return terms;
	}
	
	public AtomSet getImageOfLeftAtomSet(AtomSet atomset) {		
		return this.partition.getSubstitution().getImageOf(atomset, 0, 0, 0);
	}
	
	public AtomSet getImageOfRightAtomSet(AtomSet atomset) {
		AtomSet result = new AtomSet();
		
		int agg = 1;
		
		for(SinglePieceUnifier u : this.spus) {
			AtomSet sp = u.getImageOf(atomset, agg++);
			sp = this.partition.getSubstitution().getImageOf(sp, 0, 0, 0);
			
			result.addAll(sp);
		}
		
		return result;		
	}	
	
	public Term getImageOf(Term t) {		
		return this.partition.getSubstitution().getImageOf(t, 0, 0, 0);
	}
	
	public AtomSet getImageOfPiece() {
		AtomSet result = new AtomSet();
		
		for(Atom a : this.piece) {
			result.add(this.partition.getSubstitution().getImageOf(a, 0, 0, 0));
		}
		
		return result;
	}
	
	public Set<Atom> getB() {
		Set<Atom> B = new HashSet<>();
		
		for(SinglePieceUnifier spu : this.spus) {
			B.addAll(spu.getB());
		}
		
		return B;
	}
}
