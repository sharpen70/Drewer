package org.gu.dcore.utils;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Variable;

public class Utils {
	public static String getPrefix(String iri) {
		int sp = iri.lastIndexOf("#");
		
		if(sp != -1) return iri.substring(0, sp);
		else {
			int slash_p = iri.lastIndexOf("/");
			if(slash_p != -1) return iri.substring(0, slash_p);
		}
		
		return iri;
	}
	
	public static String getShortIRI(String iri) {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(iri);
		
		if(m.find()) {
			return m.group(1);
		}
		
		int sp = iri.indexOf("#");
		
		if(sp != -1) return iri.substring(sp + 1);
		else {
			int slash_p = iri.lastIndexOf("/");
			if(slash_p != -1) return iri.substring(slash_p + 1);
		}
		
		return iri;
	}
	
	public static boolean isMoreGeneral(AtomSet f, AtomSet h, boolean normal) {
		if (HomoUtils.contains(f, h)) 
			return true;
		else 
			return new Homomorphism(f, h, normal).exist();		
	}
	
	public static void removeSubsumed(List<AtomSet> s1, List<AtomSet> s2, boolean normal) {
		Iterator<AtomSet> it1 = s1.iterator();
		while(it1.hasNext()) {
			AtomSet atomset = it1.next();
			Iterator<AtomSet> it2 = s2.iterator();
			while(it2.hasNext()) {
				AtomSet atomset2 = it2.next();
				if(Utils.isMoreGeneral(atomset2, atomset, normal)) {
					it1.remove();
					break;
				}
			}
		}
	}
	
	public List<Long[]> full_join(List<Long[]> a, List<Long[]> b, int[] a_key, int[] b_key) {
		List<Long[]> result = new LinkedList<>();
		return result;
	}	
	
	public static Set<Variable> join_variables(Atom a, AtomSet atomset) {
		Set<Variable> result = new HashSet<>();
		
		for(Variable v : a.getVariables()) {
			if(is_join_variable(a, v, atomset))
				result.add(v);
		}

		return result;
	}
	
	public static boolean is_join_variable(Atom a, Variable v, AtomSet atomset) {
		for(Atom atom : atomset) {
			if(!a.equals(atom)) {
					if(atom.contains(v)) return true;
			}
		}
		return false; 
	}
}
