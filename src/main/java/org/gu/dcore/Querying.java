package org.gu.dcore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.rewriting.ModularizedRewriting;
import org.gu.dcore.rewriting.ModularizedRewriting2;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

public class Querying {
	public static void main(String[] args) throws ParsingException, IOException {
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
		
		if(ontologyfile == null || queriesfile == null) {
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
        	
    	   	System.out.println("Querying on: " + query.toString());      	   	
        	
    	   	long estart = System.currentTimeMillis();
        	ModularizedRewriting2 mr = new ModularizedRewriting2(P.getRuleSet());
        	
        	start = System.currentTimeMillis();
        	List<Rule> datalog = mr.rewrite(query);               	
        	end = System.currentTimeMillis();
        	
        	long rew_time = end - estart;
      	     	
        	if(datafile != null) {      
            	System.out.println("Finish rewriting, datalog program size " + datalog.size() + " cost " + (end - start) + " ms"); 
            	
	        	start = System.currentTimeMillis();
	        	DatalogEngine engine = new DatalogEngine();
	        	engine.addSourceFromCSVDir(datafile);
	        	engine.addRules(datalog);
	        	engine.load();
	        	end = System.currentTimeMillis();
	        	System.out.println("Finish Loading data, cost " + (end - start) + " ms");
	        	
	        	start = System.currentTimeMillis();
	        	engine.materialize();
	        	end = System.currentTimeMillis();
	        	
	        	System.out.println("Finish Vlog materialization, cost " + (end - start) + " ms");
	        	
	        	
	        	Column answers = engine.answerAtomicQuery(getQueryAtom(query.getAnsVar().size()));
	        	end = System.currentTimeMillis();
	        	
	        	System.out.println("Finish answering queries, answer size " + answers.getTuples().size() + " cost " + (end - start + rew_time) + " ms");
        	}
        	else {
            	for(Rule r : P.getRuleSet()) {
	        		System.out.println(r);
	        	}
            	System.out.println("Finish rewriting, datalog program size " + datalog.size() + " cost " + (end - start) + " ms"); 
        	}
        }
    	
    	tend = System.currentTimeMillis();
    	System.out.println("Total time cost " + (tend - tstart) + " ms");
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
