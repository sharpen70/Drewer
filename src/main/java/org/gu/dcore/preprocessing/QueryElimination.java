package org.gu.dcore.preprocessing;
/*
 * Copyright (C) 2018 - 2020 Artificial Intelligence and Semantic Technology, 
 * Griffith University
 * 
 * Contributors:
 * Peng Xiao (sharpen70@gmail.com)
 * Zhe wang
 * Kewen Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.tuple.Tuple;

public class QueryElimination {
	private IndexedByBodyPredRuleSet indexed_ruleset;
	
	public QueryElimination(List<Rule> onto) {
		this.indexed_ruleset = new IndexedByBodyPredRuleSet(onto);
	}
	
	public void eliminate(ConjunctiveQuery q) {		
		AtomSet qbody = q.getBody();
		
		boolean[] remain = new boolean[qbody.size()];
		Arrays.fill(remain, true);
		
		for(int i = 0; i < qbody.size(); i++) {
			if(remain[i]) {
				Atom cur = qbody.getAtom(i);
				computeAtomCoverage(cur, q, remain);
			}
		}
		
		AtomSet newbody = new AtomSet();
		for(int i = 0; i < qbody.size(); i++) {
			if(remain[i]) newbody.add(qbody.getAtom(i));
		}
		
		q.setBody(newbody);
	}
	
	private void computeAtomCoverage(Atom a, ConjunctiveQuery q, boolean[] remain) {
		Map<Predicate, List<Term[]>> pMap = computeAtomCoverage(a);
		Set<Term> ansVar = q.getAnsVar();		
		AtomSet qbody = q.getBody();
		
		for(int i = 0; i < qbody.size(); i++) {
			Atom b = qbody.getAtom(i);
			if(a.equals(b)) continue;
			
			List<Term[]> positions = pMap.get(b.getPredicate());
			
			if(positions == null || positions.size() == 0) continue;
			
			int len = b.getTerms().size();
			boolean[] T = new boolean[len];
			
			for(int ti = 0; ti < len; ti++) {
				Term t = b.getTerm(ti);
				if(t instanceof Constant || ansVar.contains(t) 
						|| joinVar(b, qbody, (Variable)t)) {
					T[ti] = true;
				}
			}
			
			boolean cover = true;
			for(Term[] pv : positions) {
				cover = true;
				for(int ti = 0; ti < len; ti++) {
					if(T[ti]) {
						if(pv[ti] == null || !pv[ti].equals(b.getTerm(ti)))
							cover = false;
					}
				}
				if(cover) break;
			}
			if(cover) remain[i] = false;
		}			
	}
	
	private boolean joinVar(Atom a, AtomSet as, Variable v) {
		for(Atom _a : as) {
			if(a.equals(_a)) continue;
			if(_a.contains(v)) return true;
		}
		return false;
	}
	
	private Map<Predicate, List<Term[]>> computeAtomCoverage(Atom a) {
		Map<Predicate, List<Term[]>> coveredPreds = new HashMap<>();
		Set<Rule> visited = new HashSet<>();
		
		Set<Rule> rules = this.indexed_ruleset.get(a.getPredicate());
		
		Queue<Tuple<Rule,Predicate,Term[]>> queue = new LinkedList<>();
		
		if(rules == null) return coveredPreds;
		
		for(Rule r : rules) {
			if(r.isLinear()) {
				if(new Homomorphism(r.getBody(), new AtomSet(a)).exist()) {
					visited.add(r);				
					int arity = a.getPredicate().getArity();

					Term[] terms = new Term[arity];
					for(int i = 0; i < arity; i++)
						terms[i] = a.getTerm(i);
					
					queue.add(new Tuple<>(r, a.getPredicate(), terms));
				}
			}
		}
		
		while(!queue.isEmpty()) {
			Tuple<Rule, Predicate, Term[]> tuple = queue.poll();
			Rule r = tuple.a;
			Predicate p = tuple.b;
			Term[] rootTerm = tuple.c;
			
			Atom body = r.getBody().getAtom(0);
			Map<Term, Term> tmap = new HashMap<>();
			
			for(int i = 0; i < rootTerm.length; i++) {
				if(rootTerm[i] != null) {
					Term t = body.getTerm(i);
					tmap.put(t, rootTerm[i]);
				}
			}		
			
			for(Atom h : r.getHead()) {
				int hs = h.getTerms().size();
				Term[] nt = new Term[hs];
				boolean related = false;
				for(int i = 0; i < hs; i++) {
					Term t = h.getTerm(i);
					Term rt = tmap.get(t);
					if(rt != null) {
						nt[i] = rt;
						related = true;
					}						
				}
				if(related) {
					List<Term[]> obtained = coveredPreds.get(h.getPredicate());
					if(obtained == null) {
						obtained = new LinkedList<>();
						coveredPreds.put(h.getPredicate(), obtained);
					}
					obtained.add(nt);
					Set<Rule> nextRules = this.indexed_ruleset.get(h.getPredicate());
					if(nextRules != null) {
						for(Rule _r : nextRules) {
							if(_r.isLinear() && visited.add(_r)) {
								if(new Homomorphism(_r.getBody(), r.getHead()).exist()) {
									queue.add(new Tuple<>(_r, h.getPredicate(), nt));
								}
							}
						}
					}
				}
			}
		}
		
		return coveredPreds;
	}
}
