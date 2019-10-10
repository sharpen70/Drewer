package org.gu.dcore.abduction;

import java.util.List;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;

public class PatternExplanation {
	private AtomSet E;
	private List<Rule> pattern;
	
	public PatternExplanation(AtomSet E, List<Rule> pattern) {
		this.E = E;
		this.pattern = pattern;
	}
	
	@Override
	public String toString() {
		return "(" + this.E.toString() + "{" + this.pattern.toString() + "})";
	}
}
