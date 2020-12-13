package org.gu.dcore.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class TranslatorForSystems {
	public static String toGrind(ConjunctiveQuery query) {
		Set<Term> vars = query.getAnsVar();
		
		String s = "Q(";
		s += toRapid(new LinkedList<>(vars));
		s += ") <- ";
		
		boolean first = true;
		for(Atom a : query.getBody()) {
			if(first) first = false;
			else s += ", ";
			s += toGrind(a);
		}
		
		return s;
	}
	
	public static String toRapid(ConjunctiveQuery query) {
		Set<Term> vars = query.getAnsVar();
	
		String s = "Q(";
		s += toRapid(new LinkedList<>(vars));
		s += ") <- ";
		
		boolean first = true;
		for(Atom a : query.getBody()) {
			if(first) first = false;
			else s += ", ";
			s += toRapid(a);
		}
		
		return s;
	}
	
	public static String toRapid(Atom atom) {
		Predicate p = atom.getPredicate();
		String s = p.shortIri();
		s += "(" + toRapid(atom.getTerms()) + ")";
		
		return s;
	}
	
	public static String toGrind(Atom atom) {
		Predicate p = atom.getPredicate();
		String s = p.getName();
		s += "(" + toRapid(atom.getTerms()) + ")";
		
		return s;
	}
	
	public static String toRapid(List<Term> terms) {
		String s = "";
		
		boolean first = true;
		for(Term t : terms) {
			if(first) first = false;
			else s += " ,";
			
			if(t instanceof Variable)
				s += "?" + ((Variable)t).getValue();
			else
				s += ((Constant)t).toString();
		}
		
		return s;
	}
}
