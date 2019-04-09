package org.gu.dcore.model;

import org.gu.dcore.interf.Term;

/**
 * Class for variables
 * @author sharpen
 * @version 1.0, April 2018
 */
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
		return "?" + this.value;
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
}
