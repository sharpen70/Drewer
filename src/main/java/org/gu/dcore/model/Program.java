package org.gu.dcore.model;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 * This is the class of Datalog+- program 
 * @author sharpen
 * @version 1.0, April 2018
 */
public class Program implements Iterable<ExRule> {
	private List<ExRule> rules;
	
	public Program(List<ExRule> rules) {
		this.rules = rules;
	}

	@Override
	public Iterator<ExRule> iterator() {
		return this.rules.iterator();
	}
}
