package org.gu.dcore.model;

import org.gu.dcore.interf.Term;

/**
 * Constant class
 * @author sharpen
 * @version 1.0, April 2018 
 */
public class Constant implements Term {
	private String name;
	private long id;
	
	
	public Constant(String _name, long _id) {
		name = _name;
		id = _id;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
