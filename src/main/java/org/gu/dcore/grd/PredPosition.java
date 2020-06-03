package org.gu.dcore.grd;

import java.util.HashSet;
import java.util.Set;

import org.gu.dcore.model.Predicate;

public class PredPosition {
	private Predicate predicate;
	private Set<Integer> indice;
	
	public PredPosition(Predicate p, Set<Integer> indice) {
		this.predicate = p;
		this.indice = indice;
	}
	
	public PredPosition(Predicate p, int index) {
		this.predicate = p;
		this.indice = new HashSet<>();
		this.indice.add(index);
	}
	
	public Predicate getPredicate() {
		return this.predicate;
	}
	
	public Set<Integer> getIndice() {
		return this.indice;
	}
	
	@Override
	public String toString() {
		return "" + this.predicate + this.indice;
	}
	
	@Override
	public int hashCode() {
		return this.predicate.hashCode() + this.indice.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PredPosition)) return false;
		PredPosition _obj = (PredPosition)obj;
		
		if(this.predicate.equals(_obj.predicate)) 
			return this.indice.equals(_obj.indice);
		else return false;
	}
}
