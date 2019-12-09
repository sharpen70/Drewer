package org.gu.dcore.model;

import java.util.Map;

import org.gu.dcore.store.Column;

public class LiftedRule extends Rule {
	private Column column;
	
	public LiftedRule(Rule r, Column column) {
		super(r);
		
		this.column = column;
	}
}
