package org.gu.dcore.model;

/* Representative Constant */
public class RepConstant implements Term {
	private int name;
	
	public RepConstant(int name) {
		this.name = name;
	}
	
	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public String toRDFox() {
		return null;
	}

}
