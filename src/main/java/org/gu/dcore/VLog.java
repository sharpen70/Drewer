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
		
		DcoreParser parser = new DcoreParser();    	
    	Program P = parser.parseFile(ontologyfile);
    	
    	System.out.println("Finish Parsing Files ...");
    	
		long start, end;
		
	   	start = System.currentTimeMillis();
    	DatalogEngine engine = new DatalogEngine();
    	engine.addSourceFromCSVDir(datafile);
    	end = System.currentTimeMillis();
    	System.out.println("Finish Loading data, cost " + (end - start) + " ms");
    	
    	Scanner scn = new Scanner(new File(queriesfile));
    	
    	if(scn.hasNextLine()) {   		
    		String line = scn.nextLine();
    		
    	   	ConjunctiveQuery query = new QueryParser().parse(line);
        	
    	   	System.out.println("Querying on: " + query.toString());      	   	        	
        	
        	start = System.currentTimeMillis();
        	engine.addRules(P.getRuleSet());
        	engine.materialize();
        	end = System.currentTimeMillis();
        	
        	System.out.println("Finish Vlog materialization, cost " + (end - start) + " ms");
        	
        	start = System.currentTimeMillis();
        	Column answers = engine.answerAtomicQuery(getQueryAtom(query.getAnsVar().size()));
        	end = System.currentTimeMillis();
        	
        	System.out.println("Finish answering queries, answer size " + answers.getTuples().size() + " cost " + (end - start) + " ms");
    	}
    	
    	scn.close();		
	}
	
	public static String getQueryAtom(int ansVarNumb) {
		String s = "ANS(";
		for(int i = 0; i < ansVarNumb; i++) {
			if(i != 0) s += ", ";
			s += "?a" + i;
		}
		s += ")";
		return s;
	}		
}
