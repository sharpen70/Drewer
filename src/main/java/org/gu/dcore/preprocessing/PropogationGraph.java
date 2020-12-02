package org.gu.dcore.preprocessing;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.grd.NPredPosition;
import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.homomorphism.Homomorphism;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.tuple.Pair;


public class PropogationGraph {
	private IndexedByBodyPredRuleSet indexed_ruleset;

	public PropogationGraph(List<Rule> onto) {
		this.indexed_ruleset = new IndexedByBodyPredRuleSet(onto);
	}
	
	public Map<Predicate, List<Set<Integer>>> computeAtomCoverage(Atom a) {
		Map<Predicate, List<Set<Integer>>> coveredPreds = new HashMap<>();
		Set<Rule> visited = new HashSet<>();
		
		Set<Rule> rules = this.indexed_ruleset.get(a.getPredicate());
		
		Queue<Pair<Rule,PredPosition>> queue = new LinkedList<>();
		
		for(Rule r : rules) {
			if(r.isLinear()) {
				if(new Homomorphism(r.getBody(), new AtomSet(a)).exist()) {
					visited.add(r);				
					Set<Integer> root = new HashSet<>();
					for(int i = 0; i < a.getPredicate().getArity(); i++)
						root.add(i);
					PredPosition rootPred = new PredPosition(a.getPredicate(), root);
					queue.add(new Pair<>(r,rootPred));
				}
			}
		}
		
		while(!queue.isEmpty()) {
			Pair<Rule,PredPosition> pair = queue.poll();
			Rule r = pair.a;
			PredPosition root = pair.b;
			
			Atom body = r.getBody().getAtom(0);
			Set<Integer> indice = root.getIndice();
			Set<Term> terms = new HashSet<>();
			
			for(Integer i : indice) {
				Term t = body.getTerm(i);
				terms.add(t);
			}		
			
			for(Atom h : r.getHead()) {
				Set<Integer> affectedPo = new HashSet<>();
				for(int i = 0; i < h.getTerms().size(); i++) {
					Term t = h.getTerm(i);
					if(terms.contains(t)) {
						affectedPo.add(i);
					}						
				}
				if(affectedPo.size() != 0) {
					List<Set<Integer>> obtained = coveredPreds.get(h.getPredicate());
					if(obtained == null) {
						obtained = new LinkedList<>();
						coveredPreds.put(h.getPredicate(), obtained);
					}
					obtained.add(affectedPo);
					Set<Rule> nextRules = this.indexed_ruleset.get(h.getPredicate());
					if(nextRules != null) {
						for(Rule _r : nextRules) {
							if(_r.isLinear() && visited.add(_r)) {
								if(new Homomorphism(_r.getBody(), r.getHead()).exist()) {
									queue.add(new Pair<>(_r, new PredPosition(h.getPredicate(), affectedPo)));
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
