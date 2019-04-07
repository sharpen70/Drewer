package org.gu.dcore.model;

import java.io.PrintStream;
import java.util.List;

import org.gu.dcore.indexing.Vocabulary;

/**
 * This is the class of Datalog+- program 
 * @author sharpen
 * @version 1.0, April 2018
 */
public class Program {
	private List<ExRule> rules;
	
	public Program(List<ExRule> rules) {
		this.rules = rules;
	}
	/**
	 * Print out the program using defined syntax
	 * @param out the output for printing
	 */
	public void print(PrintStream out) {
		for(ExRule r : rules) {
			r.print(out);
		}
	}
}
