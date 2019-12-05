package org.gu.dcore.model;

public class Variable implements Term {
	private int value;
	
	public Variable(int _value) {
		this.value = _value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return "X" + this.value;
	}
	
	@Override
	public String toRDFox() {
		return "?" + this.value;
	}
	
	@Override
	public String toVlog() {
		return "?V" + this.value;
	}
	
	@Override
	public int hashCode() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Variable)) return false;
		Variable _obj = (Variable) obj;
		return this.value == _obj.value;
	}
	@Override
	public boolean isConstant() {
		return false;
	}
	@Override
	public boolean isVariable() {
		return true;
	}
}
