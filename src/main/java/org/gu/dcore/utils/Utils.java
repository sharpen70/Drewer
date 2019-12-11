package org.gu.dcore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Term;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.store.Column;

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
		adjust_column_with_unifier(f, u, false);
		adjust_column_with_unifier(b, u, true);
		
		AtomSet uf = u.getImageOf(f, false);
		
		AtomSet ub = u.getImageOf(b, true);
		
		AtomSet up = u.getImageOfPiece();
		
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);
		
		
		return uf;
	}
	
	/*
	 * rhs denote whether atomset belongs to the rule for unification
	 */
	private static void adjust_column_with_unifier(AtomSet atomset, Unifier u, boolean rhs) {
		if(!(atomset instanceof LiftedAtomSet)) return;
		LiftedAtomSet liftedAtomset = (LiftedAtomSet)atomset;
		Column column = liftedAtomset.getColumn();
		Map<Integer, Object> eqs = new HashMap<>();
		
		for(RepConstant rc : atomset.getRepConstants()) {
			Term t = u.getImageOf(rc, rhs);
			if(t instanceof Constant) {
				String name = ((Constant) t).getName();
				eqs.put(rc.getValue(), name);
			}
			if(!rhs && (t instanceof RepConstant)) {
				int value = ((RepConstant) t).getValue();
				eqs.put(rc.getValue(), value);
			}
		}
		column.filter(eqs);
	}
	
	public static boolean isMoreGeneral(AtomSet f, AtomSet h) {
		if (HomoUtils.contains(f, h)) 
			return true;
		else 
			return new Homomorphism(f, h).exist();		
	}
	
	public static void removeSubsumed(List<AtomSet> s1, List<AtomSet> s2) {
		Iterator<AtomSet> it1 = s1.iterator();
		while(it1.hasNext()) {
			AtomSet atomset = it1.next();
			Iterator<AtomSet> it2 = s2.iterator();
			while(it2.hasNext()) {
				AtomSet atomset2 = it2.next();
				if(Utils.isMoreGeneral(atomset2, atomset)) {
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
	
	public static Atom substitute(Atom a, Map<Term, Term> submap) {
		ArrayList<Term> terms = new ArrayList<>();
		
		for(int i = 0; i < a.getTerms().size(); i++) {
			Term t = a.getTerm(i);
			Term mt = submap.get(t);
			
			if(mt != null) terms.add(mt);
			else terms.add(t);
		}
		
		return AtomFactory.instance().createAtom(a.getPredicate(), terms);
	}
	
	public static AtomSet substitute(AtomSet as, Map<Term, Term> submap) {
		AtomSet result = new AtomSet();
		for(Atom a : as) result.add(substitute(a, submap));
		return result;
	}	
}
