package org.gu.dcore.model;

/**
 * Predicate class
 * @author sharpen
 * @version 1.0, April 2018.
 */
public class Predicate {
	private long id;
	private String name;
	
	public Predicate(String name, long id, int arity) {
		this.name = name;
		this.id = id;
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
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
