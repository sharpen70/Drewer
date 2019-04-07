package org.gu.dcore.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.interf.Formula;

/**
 * A conjunction of atoms with quantifiers
 * @author sharpen
 * @version 1.0, April 2018. 
 */
public class ExConjFML implements Formula {
	public List<Atom> atoms;
	public Set<Variable> vars;
	public Map<Variable, Boolean> quantified; 
}
