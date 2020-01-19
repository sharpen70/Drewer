package org.gu.dcore.reasoning;

import java.util.LinkedList;
import java.util.List;

public class AggregateUnifier {
	private List<SinglePieceUnifier> spus;
	
	public AggregateUnifier() {
		this.spus = new LinkedList<>();
	}
	
	public void add(SinglePieceUnifier spu) {
		this.spus.add(spu);
	}
}
