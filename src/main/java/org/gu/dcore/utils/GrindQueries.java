package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class GrindQueries {
	public static void main(String[] args) throws Exception {
		String querypath = "/home/sharpen/projects/evaluations/benchmarks/owl";
		
		File dir = new File(querypath);
		
		for(File f : dir.listFiles()) {
			String queries = querypath + "/" + f.getName() + "/queries.dlp";
			String output = querypath + "/" + f.getName() + "/grind2_queries";
		Scanner scanner = new Scanner(new File(queries));
		PrintStream outstream = new PrintStream(new File(output));
    	
    	
    	while(scanner.hasNextLine()) {
    		String qs = scanner.nextLine();
        	ConjunctiveQuery query = new QueryParser().parse(qs);        	       	
        	outstream.println(query.toString());
    	}
    	
    	scanner.close();
    	outstream.close();
		}
	}
}
