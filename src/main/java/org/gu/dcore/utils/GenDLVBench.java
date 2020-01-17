package org.gu.dcore.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class GenDLVBench {
	public static void main(String[] args) throws IOException {
//		String chasebench = "/home/peng/projects/evaluations/benchmarks/owl/";
//		String[] owls = {"Reactome", "Uniprot"};
		
		String chasebench = "/home/peng/projects/evaluations/benchmarks/existential_rules/";
		String[] owls = {"deep200", "deep300", "LUBM", "ONT-256", "STB-128"};
		
		for(String owl : owls) {
//			Scanner data = new Scanner(new File(chasebench, owl + "/data.nt"));
//			PrintWriter writer = new PrintWriter(new File(chasebench, owl + "/data.v"));
//			
//			Map<Predicate, Predicate> pmap = new HashMap<>();
//			
//			while(data.hasNextLine()) {
//				String triple = data.nextLine();
//				String[] ts = triple.split("&&");				
//				
//				String t, p, v;
//				int arity;
//				
//				if(ts.length == 2) {
//					arity = 1;
//					p = Utils.getShortIRI(ts[1]);
//					v = "\"" + Utils.getShortIRI(ts[0]) +  "\"";
//					writer.println(p + "(" + v + ").");
//				}
//				else {
//					arity = 2;
//					p = Utils.getShortIRI(ts[1]);
//					v = "\"" + Utils.getShortIRI(ts[0]) +  "\"" + "," + "\"" +Utils.getShortIRI(ts[2]) +  "\"";
//					writer.println(p + "(" + v + ").");
//				}			
//			}
//			
//			writer.close();
//			data.close();
			
			String ontologyfile = chasebench + owl + "/" + owl + ".dlp";
			DcoreParser parser = new DcoreParser();    	
	    	Program P = parser.parseFile(ontologyfile);
	    	PrintWriter writer = new PrintWriter(new File(chasebench + owl + "/" + owl + ".v"));
	    	for(Rule r : P.getRuleSet()) {
	    		writer.println(r.toDLV());
	    	}
	    	writer.close();
	    	String queriesfile = chasebench + owl + "/queries.dlp";
	    	Scanner scn = new Scanner(new File(queriesfile));
	    	File qdir = new File(chasebench + owl + "/dlv_queries");
	    	qdir.mkdir();
	    	int i = 0;
	    	while(scn.hasNextLine()) {
	    		String line = scn.nextLine();
	    		ConjunctiveQuery query = new QueryParser().parse(line);
	    		PrintWriter qwriter = new PrintWriter(new File(qdir, "q" + (i++)));
	    		Set<Variable> exvar = new HashSet<>(query.getBody().getVariables());
	    		exvar.removeAll(query.getAnsVar());
				String s = "";
				if(!exvar.isEmpty()) {
					s += "#exists{";
					boolean first = true;
					for(Variable v : exvar) {
						if(!first) s += ",";
						else first = false;
						
						s += v.toString();
					}
					s += "} ";
				}
	    		qwriter.print(s + query.getBody().toShort() + "?");
	    		qwriter.close();
	    	}
	    	scn.close();
		}
	}
}