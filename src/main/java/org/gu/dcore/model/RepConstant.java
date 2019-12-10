package org.gu.dcore.model;

/* Representative Constant */
public class RepConstant implements Term {
	private int value;
	
	public RepConstant(int value) {
		this.value = value;
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
	
	@Override
	public String toVlog() {
		return null;
	}
	
	public int getValue() {
		return this.value;
	}
}
