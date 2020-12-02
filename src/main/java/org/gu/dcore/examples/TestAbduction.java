package org.gu.dcore.examples;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.abduction.ConcreteAbduction;
import org.gu.dcore.abduction.NormalQueryAbduction;
import org.gu.dcore.abduction.PatternAbduction;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

public class TestAbduction {
	public static void main(String[] args) throws ParsingException, IOException {
//		String onto_file = "/home/sharpen/projects/evaluations/benchmarks/owl/LUBM/LUBM.dlp";
//		String data_file = "/home/sharpen/projects/evaluations/benchmarks/owl/LUBM/data001";
//		String q_file = "/home/sharpen/projects/evaluations/benchmarks/owl/LUBM/obs_queries.dlp";
		
//		String onto_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/semintec/semintec.dlp";
//		String data_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/semintec/data";
//		String q_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/semintec/obs_queries.dlp";
		
		String onto_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/vicodi/vicodi.dlp";
		String data_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/vicodi/data";
		String q_file = "/home/sharpen/projects/evaluations/benchmarks/abdu/vicodi/obs_queries.dlp";
		
//		String onto_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/ONT-256/ONT-256.dlp";
//		String data_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/ONT-256/data";
//		String q_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/ONT-256/obs_queries.dlp";
		
//		String onto_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/STB-128/STB-128.dlp";
//		String data_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/STB-128/data";
//		String q_file = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/STB-128/obs_queries.dlp";
		
		
		DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(onto_file);
    	
    	Scanner scanner = new Scanner(new File(q_file));
    	
    	int i = 0;
    	
    	DatalogEngine engine = new DatalogEngine();
    	
//    	engine.addRules(P.getRuleSet());
    	engine.addSourceFromCSVDir(data_file);
//    	engine.load();
    	
    	while(scanner.hasNextLine()) {
    		String q = scanner.nextLine();   		
    		
    		i++;
//    		if(i != 3) continue;
    		
	    	ConjunctiveQuery query = new QueryParser().parse(q);

	 //   	ConjunctiveQuery query = new QueryParser().parse("?() :- Account(<http://www.owl-ontologies.com/unnamed.owl#po31987>).");
	    	
        	Rule qr = RuleFactory.instance().createQueryRule(query);
        	AtomSet obs = qr.getBody();
        	
//	    	ConcreteAbduction abduction = new ConcreteAbduction(P.getRuleSet(), obs, engine);
//	    	PatternAbduction abduction = new PatternAbduction(P.getRuleSet(), obs, engine);
	    	NormalQueryAbduction abduction = new NormalQueryAbduction(P.getRuleSet(), obs, engine);
	    	
	  //  	System.out.println(engine.answerAtomicQuery("A(a, ?b) ."));
	    	long start = System.currentTimeMillis();
	    	List<AtomSet> expl = abduction.getExplanations();
	    	long end = System.currentTimeMillis();
	    	
	    	if(expl == null) {
	    		System.out.println("The observation is already satisfied");
	    	}
	    	else {
	    		int full_number = 0;
	    		for(AtomSet atomset : expl) {
	    			if(atomset instanceof LiftedAtomSet) {
	    				full_number += ((LiftedAtomSet)atomset).getColumn().size();
	    			}
	    			else full_number++;
	    		}
	    		System.out.println("Number of Explanations: (Compact) " + expl.size() + " (Full) " + full_number + " cost: " + (end - start) + " ms");
//	    		for(AtomSet atomset : expl) System.out.println(atomset);
	    	
	    	}  
    	}
    	scanner.close();
	}
}
