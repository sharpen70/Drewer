package org.gu.dcore.grd;

import java.util.Set;

import org.gu.dcore.model.Predicate;

public class PredPosition {
	private Predicate predicate;
	private Set<Integer> indice;
	
	public PredPosition(Predicate p, Set<Integer> indice) {
		this.predicate = p;
		this.indice = indice;
	}
	
	public Predicate getPredicate() {
		return this.predicate;
	}
	
	public Set<Integer> getIndice() {
		return this.indice;
	}
}