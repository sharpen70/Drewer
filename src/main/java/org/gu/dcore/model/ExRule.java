package org.gu.dcore.model;

public class ExRule {
	private AtomSet head;
	private AtomSet body;
	
	public ExRule(AtomSet head, AtomSet body) {
		this.head = head;
		this.body = body;
	}

	public AtomSet getHead() {
		return this.head;
	}
	
	public AtomSet getBody() {
		return this.body;
	}
	
	@Override
	public String toString() {
		String s = head.toString();
		s += " <- ";
		s += body.toString();
		s += ".";
		
		return s;
	}
}
