package org.gu.dcore.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.NormalSubstitution;
import org.gu.dcore.reasoning.Partition;
import org.gu.dcore.store.Column;

public class Utils {
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
	
	public static boolean isMoreGeneral(AtomSet f, AtomSet h) {
		if (HomoUtils.contains(h, f)) 
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
	
	public static void computeCoverSet(List<AtomSet> rewritings) {
		Iterator<AtomSet> it1 = rewritings.iterator();
		while(it1.hasNext()) {
			AtomSet rewriting1 = it1.next();
			Iterator<AtomSet> it2 = rewritings.iterator();
			while(it2.hasNext()) {				
				AtomSet rewriting2 = it2.next();
				if(!rewriting1.equals(rewriting2))
					if(Utils.isMoreGeneral(rewriting2, rewriting1)) {
						it1.remove();
						break;
					}
			}
		}
	}
	
	public static Column innerJoinColumn(Column a, Column b) {
		int arity = a.getArity();
		
		if(arity != b.getArity()) return null;
		
		Column result = new Column(arity);		
		
		ArrayList<Integer> join_key = new ArrayList<>();
		
		for(int i = 0; i < arity; i++) {
			if(a.getPosition_blank()[i] && b.getPosition_blank()[i])
				join_key.add(i);
		}
		
		if(join_key.size() == 0) {
			for(String[] tuple_a : a.getTuples()) {
				for(String[] tuple_b : b.getTuples()) {
					String[] tuple = new String[arity];
					for(int i = 0; i < arity; i++) {
						if(tuple_a[i] != null) tuple[i] = tuple_a[i];
						if(tuple_b[i] != null) tuple[i] = tuple_b[i];
					}
					result.add(tuple);
				}
			}			
			return result;
		}
		
		Map<ArrayList<String>, List<String[]>> index = new HashMap<>();
		
		Column left, right;
		if(a.size() < b.size()) { left = a; right = b; }
		else { left = b; right = a;}
		
		/* build hash index */
		for(String[] tuple : left.getTuples()) {
			ArrayList<String> keys = new ArrayList<>(join_key.size());
			for(int i = 0; i < join_key.size(); i++) keys.add(tuple[join_key.get(i)]);
			List<String[]> rows = index.get(keys);
			if(rows == null) {
				rows = new LinkedList<>();
				index.put(keys, rows);
			}
			rows.add(tuple);
		}
		
		/* Probe */
		Iterator<String[]> it = right.getTuples().iterator();
		while(it.hasNext()) {
			String[] right_tuple = it.next();
			ArrayList<String> keys = new ArrayList<>(join_key.size());
			for(int i = 0; i < join_key.size(); i++) keys.add(right_tuple[join_key.get(i)]);
			
			List<String[]> rows = index.get(keys);
			if(rows != null) {
				for(String[] row : rows) {
					String[] merge = new String[arity];
					for(int i = 0; i < arity; i++) {
						if(row[i] != null) merge[i] = row[i];
						else if(right_tuple[i] != null) merge[i] = right_tuple[i];
					}
					result.add(merge);
				}
			}
		}		
		
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
	
	public static void refineCompactExplanation(LiftedAtomSet target, LiftedAtomSet source, List<Partition> refine_keys) {
		Set<RepConstant> source_reps = source.getRepConstants();
		Set<RepConstant> target_reps = target.getRepConstants();
		
		int rc_size = source_reps.size();
		int[] jk_source, jk_target;
		jk_source = new int[rc_size]; jk_target = new int[rc_size];
		
		for(Partition refine_key : refine_keys) {
			int offset = refine_key.getRCOffset();
			NormalSubstitution substitution = refine_key.getSubstitution();
			
			int jk_length = 0;
			for(RepConstant rc : source_reps) {
				Term t = substitution.getImageOf(rc, 0, offset, 1);
				if(t != null) {
					jk_target[jk_length] = ((RepConstant)t).getValue();
					jk_source[jk_length] = rc.getValue();
					jk_length++;
				}
			}
			
			Map<Integer, Integer> eqs_target = new HashMap<>();
			for(RepConstant rc : target_reps) {
				Term t = substitution.getImageOf(rc, 0, 0, 0);
				if(t != null) {
					eqs_target.put(rc.getValue(), ((RepConstant)t).getValue());
				}
			}
			
			Column source_column = source.getColumn();
			Column target_column = target.getColumn();
			ArrayList<Integer> source_repMap = source_column.getRepMap();
			ArrayList<Integer> target_repMap = target_column.getRepMap();
			
			/* build index map */
			Set<String[]> index = new HashSet<>();
			
			for(String[] t : source_column.getTuples()) {
				String[] jk = new String[jk_length];
				for(int i = 0; i < jk_length; i++) {
					jk[i] = t[source_repMap.get(jk_source[i])];
				}
				index.add(jk);
			}
			
			/* Probe */
			Iterator<String[]> it = target_column.getTuples().iterator();
			
			while(it.hasNext()) {
				String[] t = it.next();
				String[] jk = new String[jk_length];
				for(int i = 0; i < jk_length; i++) {
					jk[i] = t[target_repMap.get(jk_target[i])];
				}
				if(index.contains(jk)) {
					boolean t_eq = true;
					for(Entry<Integer, Integer> entry : eqs_target.entrySet()) {
						if(t[entry.getKey()] != t[entry.getValue()]) {
							t_eq = false;
							break;
						}
							
					}
					if(t_eq) it.remove();
				}
			}
		}
	}
}
