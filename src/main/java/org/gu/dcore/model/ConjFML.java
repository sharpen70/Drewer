package org.gu.dcore.model;

import java.util.List;
import java.util.Set;

import org.gu.dcore.interf.Formula;

/**
 * A conjunction of atoms, without quantifiers
 * @author sharpen
 * @version 1.0, April 2018. 
 */
public class ConjFML implements Formula {
	public List<Atom> atoms;
	public Set<Variable> vars;
}
