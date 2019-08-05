package org.gu.dcore.modularization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Block {
	private List<Rule> sources;
	private List<Rule> pass_sources;
	
	private Set<Atom> bricks;
	
	public Block(Block ... blocks) {
		this.sources = new LinkedList<>();
		this.pass_sources = new LinkedList<>();
		this.bricks = new HashSet<>();
		
		for(Block b : blocks) {
			this.sources.addAll(b.sources);
			this.pass_sources.addAll(b.pass_sources);
			this.bricks.addAll(b.bricks);
		}
	}
	
	public Block(Set<Atom> bricks, Rule source, boolean pass) {
		this.bricks = bricks;
		this.sources = new LinkedList<>();
		this.sources.add(source);
		this.pass_sources = new LinkedList<>();
		if(pass) this.pass_sources.add(source);
	}
	
	public boolean overlap(Block b) {
		for(Atom a : b.bricks) {
			if(this.bricks.contains(a)) return true;
		}
		
		return false;
	}
	
	public boolean related(Block b) {
		for(Rule r : b.sources) {
			if(this.sources.contains(r)) return true;
		}
		return false;
	}
	
	public Set<Atom> getBricks() {
		return this.bricks;
	}
	
	public boolean contains(Atom a) {
		return this.bricks.contains(a);
	}
	
	public List<Rule> getSources() {
		return this.sources;
	}
	
	public List<Rule> getPassSources() {
		return this.pass_sources;
	}
	
	public Set<Term> getVariables() {
		Set<Term> vars = new HashSet<>();
		for(Atom a : bricks) {
			for(Term t : a.getTerms()) {
				if(t instanceof Variable) {
					vars.add(t);
				}
			}
		}
		return vars;
	}
	
	@Override
	public String toString() {
		String s = "{";
		
		boolean first = true;
		
		for(Atom a : bricks) {
			if(!first) s += ", ";
			 first = false; 
			s += a.toString();
		}
		s += "}";
		
		return s;
	}
}
