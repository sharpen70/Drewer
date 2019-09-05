package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class GenOWLSchema {
	public static void main(String[] args) throws Exception {
		String chasebench = "/home/sharpen/projects/evaluations/benchmarks/owl/";
		String[] owls = {"Reactome", "Uniprot"};
		
		for(String owl : owls) {			
			int fact_num = 0;
			
			Program P = new DcoreParser().parseFile(chasebench + owl + "/" + owl + ".dlp", false);
			
			File sch_dir = new File(chasebench + owl + "/schema");
			sch_dir.mkdir();
			
			PrintStream t_output = new PrintStream(new File(sch_dir + "/t-schema"));
			PrintStream s_output = new PrintStream(new File(sch_dir + "/s-schema"));
			PrintStream b_output = new PrintStream(new File(sch_dir + "/bridge.sch"));
			
			PrintStream s_rdfox = new PrintStream(new File(chasebench + owl + "/bridge_source.rdfox"));
			PrintStream b_rdfox = new PrintStream(new File(chasebench + owl + "/bridge.rdfox"));
			PrintStream t_rdfox = new PrintStream(new File(chasebench + owl + "/target.rdfox"));
			
			PrintStream s_dlp = new PrintStream(new File(chasebench + owl + "/bridge_source.dlp"));
			PrintStream t_dlp = new PrintStream(new File(chasebench + owl + "/t.dlp"));
			PrintStream b_dlp = new PrintStream(new File(chasebench + owl + "/bridge.dlp"));
			
			Map<String, List<String>> dataMap = new HashMap<>();
			
			Scanner data = new Scanner(new File(chasebench, owl + "/data.nt"));
			
			Map<Predicate, Predicate> pmap = new HashMap<>();
			
			while(data.hasNextLine()) {
				fact_num++;
				String triple = data.nextLine();
				String[] ts = triple.split("&&");				
				
				String t, p, v;
				int arity;
				
				if(ts.length == 2) {
					arity = 1;
					 p = Utils.getShortIRI(ts[1]);
					 v = "\"" + Utils.getShortIRI(ts[0]) +  "\"";				
				}
				else {
					arity = 2;
					p = Utils.getShortIRI(ts[1]);
					v = "\"" + Utils.getShortIRI(ts[0]) +  "\"" + "," + "\"" +Utils.getShortIRI(ts[2]) +  "\"";					
				}
				
				List<String> vs = dataMap.get(p);
				if(vs == null) {
					vs = new LinkedList<String>();
					dataMap.put(p, vs);
					
					Predicate p1 = PredicateFactory.instance().createPredicate(p, arity);
					Predicate p2 = PredicateFactory.instance().createPredicate(p + "_target", arity);
					
					pmap.put(p1, p2);
				}
				vs.add(v);
			}
			
			data.close();
			
			File dataDir = new File(chasebench + owl + "/data");
			dataDir.mkdir();
			
			for(Entry<String, List<String>> entry : dataMap.entrySet()) {
				PrintStream csv = new PrintStream(new File(dataDir, entry.getKey() + ".csv"));
				for(String s : entry.getValue()) {
					csv.println(s);
				}
				csv.close();
			}
			
			for(Rule r : P.getRuleSet()) {
				for(Atom a : r.getHead()) {
					Predicate sp = a.getPredicate();
					Predicate np = pmap.get(sp);
					if(np != null) {
						a.setPredicate(np);
					}					
				}
				
				for(Atom a : r.getBody()) {
					Predicate sp = a.getPredicate();
					Predicate np = pmap.get(sp);
					if(np != null) {
						a.setPredicate(np);
					}					
				}
				t_rdfox.println(r.toRDFox());
				t_dlp.println(r);
			}
			t_rdfox.close();
			t_dlp.close();
			
			for(Entry<Predicate, Predicate> entry : pmap.entrySet()) {
				Predicate sp = entry.getKey();
				Predicate np = entry.getValue();
				
				ArrayList<Term> terms = new ArrayList<>();
				
				for(int i = 0; i < sp.getArity(); i++) {
					terms.add(new Variable(i));
				}
				
				Atom spa = new Atom(sp, terms);
				Atom npa = new Atom(np, terms);
				
				Rule r = new Rule(new AtomSet(npa), new AtomSet(spa), 0);
				b_rdfox.println(r.toRDFox());
				b_dlp.println(r);
				
    			String p_schema = np.getName();
    			String sp_schema = sp.getName();
    			
    			String schema = " {\n";
    			for(int i = 0; i < sp.getArity(); i++) {
    				if(i > 0) schema += ",\n";
    				schema += "    v" + i + " : STRING";
    			}
    			schema += "\n}\n\n";		
 
    	//		b_output.print(p_schema + schema);
    			s_output.print(sp_schema + schema);
			}	
			
			Set<Predicate> cp = pmap.keySet();
			
			for(Predicate p : PredicateFactory.instance().getPredicates()) {
				if(!cp.contains(p)) {
					String t_schema = p.getName();
	    			
	    			String schema = " {\n";
	    			for(int i = 0; i < p.getArity(); i++) {
	    				if(i > 0) schema += ",\n";
	    				schema += "    v" + i + " : STRING";
	    			}
	    			schema += "\n}\n\n";		
	 
	    			t_output.print(t_schema + schema);
				}
			}
			
			b_rdfox.close();
			b_dlp.close();
			b_output.close();
			s_output.close();
			t_output.close();
			
			Scanner scanner = new Scanner(new File(chasebench + owl + "/queries.dlp"));
			PrintStream cqs = new PrintStream(new File(chasebench + owl + "/queries_s.dlp"));
			File q_dir = new File(chasebench + owl + "/queries");
			q_dir.mkdir();
			
			int i = 0;
	    	while(scanner.hasNextLine()) {
	    		String qs = scanner.nextLine();
	        	ConjunctiveQuery query = new QueryParser().parse(qs, false);
	        	
	        	for(Atom a : query.getBody()) {
	        		Predicate replace = pmap.get(a.getPredicate());
	        		if(replace != null)
	        			a.setPredicate(replace);
	        	}
	        	
	        	cqs.println(query);
	        	
	    		PrintStream txt = new PrintStream(new File(q_dir, "q" + (i++) + ".txt"));
	    		txt.println(query.toRDFox());
	    		txt.close();
	    	}
	    	cqs.close();
	    	scanner.close();
	    	
			System.out.println(owl + " facts " + fact_num + ", axioms " + P.size());
		}						
	}
}
