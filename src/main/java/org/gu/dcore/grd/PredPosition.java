package org.gu.dcore.grd;

import java.util.List;

import org.gu.dcore.model.Predicate;

public class PredPosition {
	private Predicate predicate;
	private List<Integer> indice;
	
	public PredPosition(Predicate p, List<Integer> indice) {
		this.predicate = p;
		this.indice = indice;
	}
	
	
}
