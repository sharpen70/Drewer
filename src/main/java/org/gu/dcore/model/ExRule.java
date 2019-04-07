package org.gu.dcore.model;

import java.io.PrintStream;
import java.util.List;

import org.gu.dcore.interf.Rule;

public class ExRule implements Rule {
	public boolean isSafe;
	public Atom head;
	public List<Atom> body;
	
	public ExRule(Atom head, List<Atom> body) {
		this.head = head;
		this.body = body;
	}
	
	public boolean isSafe() {
		return false;
	}
	
	/**
	 * Print out the rule using defined syntax
	 * @param out output stream
	 */
	public void print(PrintStream out) {
		head.print(out);
		
		out.print(" <- ");
		
		for(int i = 0; i < body.size(); i++) {
			Atom b = body.get(i);
			b.print(out);
			if(i != body.size() - 1) out.print(", ");
		}
		
		out.print(";\n");
	}
}
