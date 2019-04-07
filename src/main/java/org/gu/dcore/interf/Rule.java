package org.gu.dcore.interf;

/**
 * This is the interface for Rule type objects
 * @author sharpen
 * @version 1.0, April 2018 
 */
public interface Rule {
	/**
	 * Check whether the rule is safe
	 * @return if the rule is safe, return true.
	 */
	public boolean isSafe();
	
//	/**
//	 * @return The head formula of the rule.
//	 */
//	public Formula getHead();
//	
//	/**
//	 * @return The body formula of the rule.
//	 */
//	public Formula getBody();
}
