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

import java.io.IOException;
import java.util.List;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.preprocessing.QueryElimination;
import org.gu.dcore.rewriting.ModularizedRewriting;
import org.gu.dcore.rewriting.ModularizedRewriting2;

public class TestLUBM {

	public static void main(String[] args) throws IOException
	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/U/U_m.dlp";
		String O = "/home/sharpen/projects/evaluations/benchmarks/owl/LUBM/LUBM.dlp";
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(O);
    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
//    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#affiliatedOrganizationOf>(B, C).");
    	
    	ConjunctiveQuery query1 = new QueryParser().parse("?(A,B) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person>(A),"
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf>(A,B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course>(B).");
    	
    	ConjunctiveQuery query2 = new QueryParser().parse("?(A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#affiliatedOrganizationOf>(B, C).");
 
    	ConjunctiveQuery query3 = new QueryParser().parse("?(A,B,C) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Student>(A), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#advisor>(A, B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#FacultyStaff>(B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#takesCourse>(A, C), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf>(B, C), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course>(C).");
    	
    	ConjunctiveQuery query4 = new QueryParser().parse("?(A,B) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person>(A), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Organization>(B).");
    	
    	ConjunctiveQuery query5 = new QueryParser().parse("?(A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person>(A), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#University>(B), "
    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#hasAlumnus>(B, A).");
    	
    	System.out.println("============");
    	
    	System.out.println(query5);
    	
    	QueryElimination qe = new QueryElimination(P.getRuleSet());
    	
    	qe.eliminate(query5);
    	
    	System.out.println(query5);
    	
//		Rule Qr = RuleFactory.instance().createQueryRule(query);
//		
//    	System.out.println(Qr);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:" + datalog.size() + "\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
	}
	
//	public static void main(String[] args) throws IOException
//	{
////		String O = "/home/peng/projects/evaluations/benchmarks/owl/LUBM/LUBM.dlp";
////		String O = "/home/sharpen/projects/benchmarktool/benchmark/owl/U.dlp";
//		String O = "/home/sharpen/projects/evaluations/benchmarks/new/chembl/chembl.dlp";
//		
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
////    	ConjunctiveQuery query = new QueryParser().parse("? (A) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor>(A, B), "
////    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#affiliatedOrganizationOf>(B, C).");
//    	
////    	ConjunctiveQuery query = new QueryParser().parse("?(A,B) :- <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Person>(A), <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf>(A, B), "
////    			+ "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course>(B).");
////    	
////    	ConjunctiveQuery query = new QueryParser().parse("?(X0) :- <file:///h"
////    			+ "ome/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#Device>(X0), "
////    			+ "<http://ksg.meraka.co.za/adolena.owl#assistsWith>(X0, X1), "
////    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#PhysicalAbility>(X1), "
////    			+ "<http://ksg.meraka.co.za/adolena.owl#affects>(X2, X1), "
////    			+ "<file:///home/aurona/0AlleWerk/Navorsing/Ontologies/NAP/NAP#Quadriplegia>(X2).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("?(VAR_activity,VAR_assay,VAR_target,VAR_uniprot) :- <http://rdf.ebi.ac.uk/terms/chembl#Activity>(VAR_activity), <http://rdf.ebi.ac.uk/terms/chembl#hasMolecule>(VAR_activity, <http://rdf.ebi.ac.uk/resource/chembl/molecule/CHEMBL941>), <http://rdf.ebi.ac.uk/terms/chembl#hasAssay>(VAR_activity, VAR_assay), <http://rdf.ebi.ac.uk/terms/chembl#hasTarget>(VAR_assay, VAR_target), <http://rdf.ebi.ac.uk/terms/chembl#hasTargetComponent>(VAR_target, VAR_targetcmpt), <http://rdf.ebi.ac.uk/terms/chembl#targetCmptXref>(VAR_targetcmpt, VAR_uniprot), <http://rdf.ebi.ac.uk/terms/chembl#UniprotRef>(VAR_uniprot).");
//    	System.out.println("============");   	
//
//    	
//    	ModularizedRewriting2 mr = new ModularizedRewriting2(P.getRuleSet(), query);
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite();
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:" + datalog.size() + "\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	}
}
