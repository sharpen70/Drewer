package org.gu.dcore;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.rewriting.ModularizedRewriting;

public class Rewrite {
	public static void main(String[] args) throws Exception {
		String dlp = args[0];
		String queries = args[1];
		String output_dir = args.length < 3 ? null : args[2];
	
		Scanner scanner = new Scanner(new File(queries));
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(dlp, false);
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	System.out.println("Rewriting " + args[0]);
    	
    	while(scanner.hasNextLine()) {
    		String qs = scanner.nextLine();
        	ConjunctiveQuery query = new QueryParser().parse(qs, false);
        	
        	System.out.println("====================");

        	System.out.println("Rewriting query " + qs);  		
//        	System.out.println(Qr);
           	
        	long start = System.currentTimeMillis();
        	
        	System.out.println("---------------------");
        	List<Rule> datalog = mr.rewrite(query);
        	
        	for(Rule r : datalog) System.out.println(r.toString());
        	
        	long end = System.currentTimeMillis();
        	
        	System.out.println("---------------------");
        	
        	System.out.println("Rewritings " + datalog.size() + " Time " + (end - start));
        	
//        	if(output_dir != null) {
//        		File output = new File(output_dir);
//        		if(!output.exists()) output.mkdirs();
//        		File program_output = new File(output.getAbsolutePath() + "/rewrite.rdfox");
//        		File schema_output = new File(output.getAbsolutePath() + "/rewrite-schema");
//        		File rdfox_query = new File(output.getAbsolutePath() + "/query/");
//        		
//        		rdfox_query.mkdirs();
//        		rdfox_query = new File(rdfox_query, "query.txt"); 
//        		
//        		
//        		PrintStream program_outstream = new PrintStream(program_output);
//        		PrintStream schema_outstream = new PrintStream(schema_output);
//        		PrintStream rdfox_query_outstream =  new PrintStream(rdfox_query);
//        		
//        		for(Rule r : datalog) {
//        			program_outstream.println(r.toRDFox());
//        		}
//        		
//        		for(Predicate p : PredicateFactory.instance().getRewPredicates()) {
//        			String p_schema = p.getName();
//        			p_schema += "{\n";
//        			for(int i = 0; i < p.getArity(); i++) {
//        				if(i > 0) p_schema += ",\n";
//        				p_schema += "    v" + i + " : STRING";
//        			}
//        			p_schema += "\n}\n\n";
//        			schema_outstream.print(p_schema);
//        		}
//        		
//        		Atom queryAtom = Qr.getHead().getAtom(0);
//        		Atom ansAtom = new Atom(new Predicate("q", 0, queryAtom.getTerms().size()), queryAtom.getTerms());
//        		
//        		rdfox_query_outstream.print(ansAtom.toRDFox() + " <- " + queryAtom.toRDFox() + " .");
//        		
//        		program_outstream.close();
//        		schema_outstream.close();
//        		rdfox_query_outstream.close();
//        		
//            	System.out.println("Rewriting write output time: " + (System.currentTimeMillis() - end) + "\n");   
//        	}
     	 	
    	}
    	
    	scanner.close();
	}
}
