package org.gu.dcore.reasoning;

import java.util.LinkedList;
import java.util.List;

public class AggregateUnifier {
	private List<SinglePieceUnifier> spus;
	
	public AggregateUnifier() {
		this.spus = new LinkedList<>();
	}
	
	public boolean aggregate(SinglePieceUnifier spu) {
		this.spus.add(spu);
		
		return true;
	}
	
	public boolean isCompatible(SinglePieceUnifier spu) {
		return true;
	}
}
