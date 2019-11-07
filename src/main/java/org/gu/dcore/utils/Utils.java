package org.gu.dcore.utils;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.reasoning.Unifier;

public class Utils {
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
	
	/*
	 * f the set of atoms being replaced
	 * b the set of atoms to replace
	 * u the unifier for replacing
	 */
	public static AtomSet rewrite(AtomSet f, AtomSet b, Unifier u) {
		AtomSet uf = u.getImageOf(f, false);
		AtomSet ub = u.getImageOf(b, true);
		
		AtomSet up = u.getImageOfPiece();
		
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);
		
		return uf;
	}
	
	public static boolean isMoreGeneral(AtomSet f, AtomSet h) {
		if (HomoUtils.contains(f, h)) 
			return true;
		else 
			return new Homomorphism(f, h).exist();		
	}
	
	public static void addAndKeepMinimal(List<AtomSet> atomsets, List<AtomSet> toAdd) {
		for(AtomSet tocheck : toAdd) {
			addAndKeepMinimal(atomsets, tocheck);
		}
	}
	
	public static void addAndKeepMinimal(List<AtomSet> atomsets, AtomSet toAdd) {
		boolean subsumed = false;
		Iterator<AtomSet> it = atomsets.iterator();
		while(it.hasNext()) {
			AtomSet rew = it.next();
			
			if(Utils.isMoreGeneral(rew, toAdd)) {
				subsumed = true; break;
			}
			if(Utils.isMoreGeneral(toAdd, rew)) {
				it.remove();
			}
		}
		if(!subsumed) atomsets.add(toAdd);
	}
}
