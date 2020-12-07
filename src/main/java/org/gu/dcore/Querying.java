package org.gu.dcore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.preprocessing.QueryElimination;
import org.gu.dcore.rewriting.ModularizedRewriting;
import org.gu.dcore.rewriting.ModularizedRewriting2;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class Querying {
	public static void main(String[] args) throws ParsingException, IOException {
		String ontologyfile = null;		
		String queriesfile = null;
		String datafile = null;		
		
		boolean verbose = false;
		
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
				if(flag.equals("v")) verbose = true;
			}
		}
		
		if(ontologyfile == null || queriesfile == null) {
			System.out.println("Missing input !");
			return;
		}
		
		long start, end, tstart, tend;
		tstart = System.currentTimeMillis();
		
		start = System.currentTimeMillis();
		DcoreParser parser = new DcoreParser();    	
    	Program P = parser.parseFile(ontologyfile);
    	
    	if(verbose) System.out.println("Finish Parsing Files ...");  	
    	List<Rule> ruleset = Utils.compute_single_rules(P.getRuleSet());
    	end = System.currentTimeMillis();
    	
    	long p_time = end - start;
    	if(verbose) System.out.println("Preprocessing taking " + p_time + " ms");
    	
    	Scanner scn = new Scanner(new File(queriesfile));
    	if(scn.hasNextLine()) {   		
    		String line = scn.nextLine();
    		
    	   	ConjunctiveQuery query = new QueryParser().parse(line);       	
    	   	if(verbose) System.out.println("Querying on: " + query.toString());      	   	
        	
    	   	start = System.currentTimeMillis();
    	   	QueryElimination qe = new QueryElimination(ruleset);
    	   	qe.eliminate(query);
    	   	end = System.currentTimeMillis();  	   	
    	   	long eliminate_time = end - start;
    	   	if(verbose) System.out.println("Finish query optimizaiton, taking " + eliminate_time + " ms");
    	   	
    	   	start = System.currentTimeMillis();
    		Modularizor modularizor = new Modularizor(ruleset);
    		modularizor.modularize();
    		end = System.currentTimeMillis();
    		long mod_time = end - start;
    		if(verbose) System.out.println("Finish modularization, taking " + mod_time + " ms");
    		
    		start = System.currentTimeMillis();
        	ModularizedRewriting2 mr = new ModularizedRewriting2(modularizor, query);
        	List<Rule> datalog = mr.rewrite();               	
        	end = System.currentTimeMillis();
        	
        	long rew_time = eliminate_time + end - start;      	     
        	
        	long query_time = 0;
        	
        	if(datafile != null) {   
        		if(verbose) System.out.println("Finish rewriting, datalog program size " + datalog.size() + " cost " + rew_time + " ms"); 
            	   
	        	start = System.currentTimeMillis();
	        	DatalogEngine engine = new DatalogEngine();
	        	engine.addSourceFromCSVDir(datafile);
	        	engine.addRules(datalog);
	        	engine.load();
	        	end = System.currentTimeMillis();
	        	if(verbose) System.out.println("Finish Loading data, cost " + (end - start) + " ms");
	        	
	        	start = System.currentTimeMillis();
	        	engine.materialize();
	        	end = System.currentTimeMillis();
	        	
	        	long mat_time = end - start;
	        	
	        	if(verbose) System.out.println("Finish Vlog materialization, cost " + mat_time + " ms");
	        		
	        	Column answers = engine.answerAtomicQuery(getQueryAtom(query.getAnsVar().size()));
	        	end = System.currentTimeMillis();
	        	
	        	query_time = end -start;
	        	
	        	if(verbose) System.out.println("Finish answering queries, answer size " + answers.getTuples().size() + " cost " + (query_time + rew_time) + " ms");
        	}
        	else {
//            	for(Rule r : P.getRuleSet()) {
//	        		System.out.println(r);
//	        	}
        		if(verbose) System.out.println("Finish rewriting, datalog program size " + datalog.size() + " cost " + (end - start) + " ms"); 
        	}
        	tend = System.currentTimeMillis();
        	
        	if(verbose) System.out.println("Total time cost " + (tend - tstart) + " ms");
        	else  {
        		if(datafile == null)
        			System.out.println(datalog.size() + "," + rew_time);
        		else 
        			System.out.println(datalog.size() + "," + (query_time + rew_time) + "," + (tend - tstart));
        	}
        }
 
    	scn.close();		
	}
	
	public static String getQueryAtom(int ansVarNumb) {
		String s = "ans(";
		for(int i = 0; i < ansVarNumb; i++) {
			if(i != 0) s += ", ";
			s += "?V" + i;
		}
		s += ")";
		return s;
	}
}
