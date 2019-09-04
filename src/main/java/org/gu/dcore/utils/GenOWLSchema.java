package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;

public class GenOWLSchema {
	public static void main(String[] args) throws Exception {
		String chasebench = "/home/sharpen/projects/evaluations/benchmarks/owl/";
		String[] owls = {"Reactome", "Uniprot"};

		File dir = new File(chasebench);
		
		for(String owl : owls) {
			PredicateFactory.reset();
			
			Program P = new DcoreParser().parseFile(dir + owl + "/" + owl + ".dlp");
			
			PrintStream t_output = new PrintStream(new File(chasebench + owl + "/schema/t-schema"));
			PrintStream s_output = new PrintStream(new File(chasebench + owl + "/schema/s-schema"));
			PrintStream b_output = new PrintStream(new File(chasebench + owl + "/schema/bridge.sch"));
			
			PrintStream s_rdfox = new PrintStream(new File(chasebench + owl + "/bridge_source.rdfox"));
			PrintStream b_rdfox = new PrintStream(new File(chasebench + owl + "/bridge.rdfox"));
			PrintStream t_rdfox = new PrintStream(new File(chasebench + owl + "/target.rdfox"));
			
			PrintStream s_dlp = new PrintStream(new File(chasebench + owl + "/bridge_source.dlp"));
			PrintStream t_dlp = new PrintStream(new File(chasebench + owl + "/t.dlp"));
			
			Map<String, List<String>> dataMap = new HashMap<>();
			
			Scanner data = new Scanner(new File(chasebench, owl + "/data.nt"));
			
			while(data.hasNextLine()) {
				String triple = data.nextLine();
				String[] ts = triple.split(" ");				
				
				if(ts[1].equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")) {
					String t = ts[2];
					String p = t.substring(0, t.length() - 1) + "_src>";
					String v = ts[0];
					
					List<String> vs = dataMap.get(p);
					if(vs == null) vs = new LinkedList<String>();
					vs.add(v);
				}
				else {
					String t = ts[1];
					String p = t.substring(0, t.length() - 1) + "_src>";
					String v = ts[0] + "," + ts[2];
					
					List<String> vs = dataMap.get(p);
					if(vs == null) vs = new LinkedList<String>();
					vs.add(v);
				}
			}
			
			data.close();
			
			Set<Predicate> src_preds = new HashSet<>();
			
			for(Rule r : P.getRuleSet()) {
				for(Atom a : r.getBody()) {
					src_preds.add(a.getPredicate());
				}
			}
			
			
		}
		
	}
}
