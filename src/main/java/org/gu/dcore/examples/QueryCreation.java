package org.gu.dcore.examples;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.tuple.Pair;

public class QueryCreation {
	public static void main(String[] args) throws IOException {
		String ontofile = "/home/peng/projects/evaluations/benchmarks/existential_rules/deep200/deep200.dlp";
		
		Program P = new DcoreParser().parseFile(ontofile);	
	
		Map<Integer, List<Predicate>> predLevels = getPredLevels(P.getRuleSet());
		
		int[] select = {2, 3, 4, 5};
		
		Random rand = new Random();
		
		for(int i = 0; i < select.length; i++) {
			List<Predicate> preds = predLevels.get(select[i]);
			int index = rand.nextInt(preds.size());
			System.out.println("level " + select[i] + ": " + preds.get(index));
		}
	}
	
	public static Map<Integer, List<Predicate>> getPredLevels(List<Rule> ruleset) {
		Collection<Predicate> predicates = PredicateFactory.instance().getPredicates();
		
		IndexedByHeadPredRuleSet ihs = new IndexedByHeadPredRuleSet(ruleset);
		
		Map<Integer, List<Predicate>> predicate_levels = new HashMap<>();
		
		for(Predicate predicate : predicates) {
			int max_depth = 0;
			
			Queue<Pair<Predicate,Integer>> queue = new LinkedList<>();
			queue.add(new Pair<>(predicate, 0));
			
			while(!queue.isEmpty()) {
				Pair<Predicate,Integer> pair = queue.poll();
				Predicate pred = pair.a;
				int level = pair.b;
				
				int cur_level = level + 1;
				
				if(cur_level > max_depth) max_depth = cur_level;
				
				Set<Rule> rules = ihs.getRulesByPredicate(pred);
				
				if(rules != null) {
					for(Rule r : rules) {
						for(Atom a : r.getBody()) {
							queue.add(new Pair<>(a.getPredicate(), cur_level));
						}
					}
				}
			}
			
			List<Predicate> preds = predicate_levels.get(max_depth);
			if(preds == null) {
				preds = new LinkedList<Predicate>();
				predicate_levels.put(max_depth, preds);
			}
			preds.add(predicate);
		}
		
		return predicate_levels;
	}
}
