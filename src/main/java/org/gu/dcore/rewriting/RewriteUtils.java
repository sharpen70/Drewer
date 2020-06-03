package org.gu.dcore.rewriting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.AggregateUnifier;
import org.gu.dcore.reasoning.SinglePieceUnifier;
import org.gu.dcore.store.Column;
import org.gu.dcore.tuple.Pair;

public class RewriteUtils {
	/*
	 * f the set of atoms being replaced
	 * b the set of atoms to replace
	 * u the unifier for replacing
	 */
	public static AtomSet rewrite(AtomSet f, AtomSet b, SinglePieceUnifier u) {	
//		Column c = null;
//		
//		if(f instanceof LiftedAtomSet || b instanceof LiftedAtomSet) {
//			adjust_column_with_unifier(f, u, false);
//			adjust_column_with_unifier(b, u, true);
//			
//			if(f instanceof LiftedAtomSet && b instanceof LiftedAtomSet) {
//				Column fc = ((LiftedAtomSet)f).getColumn();
//				Column bc = ((LiftedAtomSet)b).getColumn();
//				
//				int rc_size = b.getRepConstants().size();
//				int[] jka, jkb;
//				jka = new int[rc_size]; jkb = new int[rc_size];
//				int ji = 0;
//				for(RepConstant rc : b.getRepConstants()) {
//					Term t = u.getImageOf(rc, 1);
//					if(t != null && t instanceof RepConstant) {
//						jka[ji] = ((RepConstant)t).getValue();
//						jkb[ji] = rc.getValue();
//						ji++;
//					}
//				}
//				Pair<Column, Map<Term, Term>> result = fc.join(bc, jka, jkb, rc_size);
//				c = result.a;
//				b = substitute(b, result.b);
//			}
//			else if(f instanceof LiftedAtomSet) c = ((LiftedAtomSet)f).getColumn();
//			else c = ((LiftedAtomSet)b).getColumn();
//		}
		
		AtomSet uf = u.getImageOf(f, 0);		
		AtomSet ub = u.getImageOf(b, 1);	
		AtomSet up = u.getImageOfPiece();
	
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);		
		
//		if(c != null) uf = new LiftedAtomSet(uf, c);
		return uf;
	}
	
	public static AtomSet aggreRewrite(AtomSet f, AtomSet b, AggregateUnifier u) {
		AtomSet uf = u.getImageOfLeftAtomSet(f);
		AtomSet up = u.getImageOfPiece();
		AtomSet ub = u.getImageOfRightAtomSet(b);
		
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);
		
		return uf;
	}
	
	/*
	 * rhs denote whether atomset belongs to the rule for unification
	 */
	private static void adjust_column_with_unifier(AtomSet atomset, SinglePieceUnifier u, boolean rhs) {
		if(!(atomset instanceof LiftedAtomSet)) return;
		LiftedAtomSet liftedAtomset = (LiftedAtomSet)atomset;
		Column column = liftedAtomset.getColumn();
		Map<Integer, Object> eqs = new HashMap<>();
		
		for(RepConstant rc : atomset.getRepConstants()) {
			int offset_num = rhs ? 1 : 0;
			Term t = u.getImageOf(rc, offset_num);
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
	
	public static List<Rule> compute_single_rules(List<Rule> rs) {
		List<Rule> result = new LinkedList<>();
		
		for(Rule r : rs) {
			Set<Variable> ex_vars = r.getExistentials();
			
			if(r.getHead().size() == 1) {
				result.add(r);
				continue;
			}
			
			LinkedList<Atom> eatom = new LinkedList<>();
			for(Atom a : r.getHead()) eatom.add(a);
			
			while(!eatom.isEmpty()) {
				Set<Term> join_ev = new HashSet<>();
				Atom p = eatom.poll();
				AtomSet piece = new AtomSet(p);
				
				for(Term t : p.getTerms()) {
					if(ex_vars.contains(t)) {
						join_ev.add(t);
					}
				}
				
				if(join_ev.isEmpty()) {
					result.add(RuleFactory.instance().createRule(piece, r.getBody()));
					continue;
				}
				
				Iterator<Atom> it = eatom.iterator();
				while(it.hasNext()) {
					Atom cp = it.next();
					Set<Term> ev = new HashSet<>();
					for(Term t : cp.getTerms()) {
						if(ex_vars.contains(t)) {
							ev.add(t);
						}
					}
					if(!Collections.disjoint(join_ev, ev)) {
						join_ev.addAll(ev);
						piece.add(cp);
						it.remove();
					}
				}
				
				if(piece.size() == r.getHead().size()) result.add(r);
				else {
					result.add(RuleFactory.instance().createRule(piece, r.getBody()));
				}
			}			
		}
		return result;
	}
}
