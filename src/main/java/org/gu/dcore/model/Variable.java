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
		return "" + this.value;
	}
}
