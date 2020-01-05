package org.gu.dcore.model;

/**
 * Predicate class
 * @author sharpen
 * @version 1.0, April 2018.
 */
public class Predicate {
	private long id;
	private String name;
	private final int arity;
	
	public Predicate(String _name, long _id, int _arity) {
		this.name = _name;
		this.id = _id;
		this.arity = _arity;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	public int getArity() {
		return this.arity;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String shortIri() {
		int index = this.name.indexOf("#");
		if(index != -1)
			return this.name.substring(index + 1);
		else 
			return this.name;
	}
}
