package org.gu.dcore;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

public class VLog {
	public static void main(String[] args) throws IOException, ParsingException {
		String ontologyfile = null;		
		String queriesfile = null;
		String datafile = null;		
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				String flag = args[i].substring(1);
				if(flag.equals("o")) {
					ontologyfile = args[++i];
				}
				if(flag.equals("d")) datafile = args[++i];
				if(flag.equals("q")) {
					queriesfile = args[++i];
				}
			}
		}
		
		if(ontologyfile == null || datafile == null || queriesfile == null) {
			System.out.println("Missing input !");
			return;
		}
		
		long start, end, tstart, tend;
		
		tstart = System.currentTimeMillis();
		DcoreParser parser = new DcoreParser();    	
    	Program P = parser.parseFile(ontologyfile);
    	
    	System.out.println("Finish Parsing Files ...");   	   	
    	
    	Scanner scn = new Scanner(new File(queriesfile));
    	
    	while(scn.hasNextLine()) {   		
    		String line = scn.nextLine();
    		
    	   	ConjunctiveQuery query = new QueryParser().parse(line);
        	
        	String qatom = getQueryAtom(query.getAnsVar());
        	String qr = qatom + " :- " + query.getBody().toVLog() + ".";
        	
    	   	System.out.println("Querying: " + qr);      	   	        	
        	
    	   	start = System.currentTimeMillis();
        	DatalogEngine engine = new DatalogEngine();
        	engine.setSkolemAlgorithm();
        	engine.addSourceFromCSVDir(datafile);
        	engine.addRules(getQueryRelatedRuleSet(P.getRuleSet(), query));
        	engine.addRules(qr);
        	engine.load();
        	end = System.currentTimeMillis();
        	System.out.println("Finish Loading data, cost " + (end - start) + " ms");
        	
        	start = System.currentTimeMillis();
        	engine.materialize();
        	end = System.currentTimeMillis();
        	
        	System.out.println("Finish Vlog materialization, cost " + (end - start) + " ms");       	
        	
        	Column answers = engine.answerAtomicQuery(qatom);
        	end = System.currentTimeMillis();
        	tend = System.currentTimeMillis();
        	System.out.println("Finish answering queries, answer size " + answers.getTuples().size() + ", cost " + (end - start) + " ms");
        	System.out.println("Total time cost " + (tend - tstart) + "ms");
    	}
    	
    	scn.close();		
	}
	
	private static List<Rule> getQueryRelatedRuleSet(List<Rule> rules, ConjunctiveQuery q) {
		IndexedByHeadPredRuleSet ihrs = new IndexedByHeadPredRuleSet(rules);
		List<Rule> related_rules = new LinkedList<Rule>();
		Set<Rule> selected = new HashSet<>();
		Queue<Predicate> queue = new LinkedList<>();
		for(Atom a : q.getBody()) {
			queue.add(a.getPredicate());
		}
		while(!queue.isEmpty()) {
			Predicate p = queue.poll();
			Set<Rule> rs = ihrs.getRulesByPredicate(p);
			if(rs != null) {
				for(Rule r : rs) {
					if(selected.add(r)) {
						related_rules.add(r);
						for(Atom a : r.getBody()) {
							queue.add(a.getPredicate());
						}
					}
				}
			}
		}
		
		return related_rules;
	}
	
	private static String getQueryAtom(Set<Term> ansVar) {
		String s = "ans(";
		boolean first = true;
		for(Term v : ansVar) {
			if(first) first = false;
			else s += " ,";
			s += v.toVLog();
		}
		s += ")";
		return s;
	}		
}
