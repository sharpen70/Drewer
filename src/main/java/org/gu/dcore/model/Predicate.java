package org.gu.dcore.model;

/**
 * Predicate class
 * @author sharpen
 * @version 1.0, April 2018.
 */
public class Predicate {
	private long id;
	private String name;
	private int arity;
	
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
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	public int getArity() {
		return this.arity;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
