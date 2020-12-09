package org.gu.dcore.examples;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.utils.Utils;

public class QueryGeneration {
	private static Map<Integer, List<Predicate>> predLevels = null;
	private static IndexedByHeadPredRuleSet ihs = null;
	
	public static void main(String[] args) throws IOException {
		genDeep();
	}
	
	private static void genDeep() throws IOException {
		String ontofile = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/deep300/";
		Program P = new DcoreParser().parseFile(ontofile + "deep300.dlp");
		List<Rule> rules = Utils.compute_single_rules(P.getRuleSet());
		ihs = new IndexedByHeadPredRuleSet(rules);
		predLevels = getPredLevels(rules);
		System.out.println(predLevels.keySet());
		String longQ = ontofile + "QueriesInLength/";
		File f = new File(longQ);
		if(!f.exists()) f.mkdir();
		
		List<ConjunctiveQuery> queries = new LinkedList<>();
		queries.addAll(genQuery(2, 4, 3));
		queries.addAll(genQuery(3, 4, 3));
		queries.addAll(genQuery(4, 4, 3));
		
		for(int i = 0; i < queries.size(); i++) {
			String qfile = longQ + "q" + (i + 1);
			PrintWriter writer = new PrintWriter(new File(qfile));
			writer.println(queries.get(i));
			writer.close();
		}
	}
	
	public static List<ConjunctiveQuery> genQuery(int len, int depth, int num) {				
		List<ConjunctiveQuery> qlist = new LinkedList<>();
		
		for(int i = 0; i < num; i++) {
			AtomSet init = new AtomSet();
		    List<Term> ansVar = new LinkedList<>();
		    Variable joinVar = null;
			Random rand = new Random();

			
			List<Predicate> preds = predLevels.get(depth);
			if(preds == null) return null;
			Predicate pred = preds.get(rand.nextInt(preds.size()));
			Set<Rule> brs = ihs.getRulesByPredicate(pred);
		    List<Rule> lrs = new LinkedList<>(brs);
		    Rule rr = lrs.get(rand.nextInt(lrs.size()));
		    int offset = 0;
		    
		    for(Atom a : rr.getHead()) {
		    	boolean found = false;
		    	for(Term t : a.getTerms()) {
		    		if(t instanceof Variable) {
		    			if(rr.getFrontierVariables().contains(t)) {
		    				joinVar = (Variable)t;
		    				ansVar.add(t);
		    				init.add(a);
		    				offset = a.getVariables().size();
		    				found = true;
		    				break;
		    			}
		    		}
		    	}
		    	if(found) break;
		    }
		    
			int l = 0;
			
			while(l < len) {
				int _depth = rand.nextInt(depth - 1) + 2;
				List<Predicate> _preds = predLevels.get(_depth);
				if(_preds == null) continue;
				Predicate _pred = _preds.get(rand.nextInt(_preds.size()));
				Set<Rule> _brs = ihs.getRulesByPredicate(_pred);
			    List<Rule> _lrs = new LinkedList<>(_brs);
			    Rule _rr = _lrs.get(rand.nextInt(_lrs.size()));
			    
			    for(Atom a : _rr.getHead()) {
			    	boolean found = false;
			    	for(Term t : a.getTerms()) {
			    		if(t instanceof Variable) {
			    			if(_rr.getFrontierVariables().contains(t)) {
			    				Atom na = replaceVar(a, (Variable)t, joinVar, offset + 1);
			    				offset += na.getVariables().size();
			    				init.add(na);
			    				found = true;
			    				break;
			    			}
			    		}
			    	}
			    	if(found) break;
			    }
			    
			    l++;
			}
			
			qlist.add(new ConjunctiveQuery(ansVar, init));			
		}
		
		System.out.println("Gen queries with length " + len + " and depth " + depth);
		return qlist;
	}
	
	private static Atom replaceVar(Atom atom, Variable vs, Variable vt, int offset) {
		ArrayList<Term> terms = new ArrayList<>();
		for(int i = 0; i < atom.getTerms().size(); i++) {
			Term t = atom.getTerm(i);
			if(t.equals(vs)) terms.add(vt);
			else {
				if(t instanceof Variable) {
					Variable nv = new Variable(((Variable)t).getValue() + offset);
					terms.add(nv);
				}
				else terms.add(t);
			}
		}
		return new Atom(atom.getPredicate(), terms);
	}
	
	public static Map<Integer, List<Predicate>> getPredLevels(List<Rule> ruleset) {
		Collection<Predicate> predicates = PredicateFactory.instance().getPredicates();
		
		IndexedByHeadPredRuleSet ihs = new IndexedByHeadPredRuleSet(ruleset);
		
		Map<Integer, List<Predicate>> predicate_levels = new HashMap<>();
		
		for(Predicate predicate : predicates) {
			int max_depth = 0;
			
			Queue<Pair<Predicate,Integer>> queue = new LinkedList<>();
			queue.add(new Pair<>(predicate, 0));
			
			Set<Rule> visited = new HashSet<>();
			
			while(!queue.isEmpty()) {
				Pair<Predicate,Integer> pair = queue.poll();
				Predicate pred = pair.a;
				int level = pair.b;
				
				int cur_level = level + 1;
				
				if(cur_level > max_depth) max_depth = cur_level;
				
				Set<Rule> rules = ihs.getRulesByPredicate(pred);
				
				if(rules != null) {
					for(Rule r : rules) {
						if(visited.add(r)) {
							for(Atom a : r.getBody()) {
								queue.add(new Pair<>(a.getPredicate(), cur_level));
							}
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
