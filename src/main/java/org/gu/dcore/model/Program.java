package org.gu.dcore.model;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 * This is the class of Datalog+- program 
 * @author sharpen
 * @version 1.0, April 2018
 */
public class Program implements Iterable<Rule> {
	private List<Rule> rules;
	
	public Program(List<Rule> rules) {
		this.rules = rules;
	}

	@Override
	public Iterator<Rule> iterator() {
		return this.rules.iterator();
	}
	
	public int size() {
		return this.rules.size();
	}
	
	public Rule getRule(int i) {
		return this.rules.get(i);
	}
	
	@Override
	public String toString() {
		String s = "";
		for(Rule r : rules) {
			s += r.toString();
			s += "\n";
		}
		
		return s;
	}
}
