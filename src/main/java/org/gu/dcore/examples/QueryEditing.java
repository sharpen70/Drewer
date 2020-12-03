package org.gu.dcore.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.parsing.QueryParser;

public class QueryEditing {
	public static void main(String[] args) throws FileNotFoundException {
		String queriesfile = "/home/peng/projects/evaluations/benchmarks/existential_rules/deep200/queries.dlp";
		String queriesdir = "/home/peng/projects/evaluations/benchmarks/existential_rules/deep200/dlp_queries";
		
		Scanner scn = new Scanner(new File(queriesfile));
		
		File dir = new File(queriesdir);
		if(!dir.exists()) dir.mkdir();
		
		int i = 0;
		while(scn.hasNextLine()) {
			String line = scn.nextLine();
			PrintWriter writer = new PrintWriter(new File(queriesdir + "/q" + (++i)));
			
			ConjunctiveQuery query = new QueryParser().parse(line);
			
			System.out.println("q " + i + " length " + query.getBody().size());
			writer.print(line);
			writer.close();
			
		}
		scn.close();
	}
}
