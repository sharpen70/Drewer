package org.gu.dcore.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.utils.TranslatorForSystems;

public class QueryEditing {
	public static void main(String[] args) throws FileNotFoundException {
		String ontoDir = "/home/peng/projects/evaluations/benchmarks/owl/LUBM/";
		String dlpqueries = ontoDir + "dlp_queries/";
		String rapidqueries = ontoDir + "rpd_queries/";
				
		File dir = new File(rapidqueries);
		if(!dir.exists()) dir.mkdir();
		
		for(File qf : new File(dlpqueries).listFiles()) {
			Scanner scn = new Scanner(qf);

			while(scn.hasNextLine()) {
				String line = scn.nextLine();
				ConjunctiveQuery query = new QueryParser().parse(line);
				
				System.out.println(qf + " length " + query.getBody().size());
				PrintWriter writer = new PrintWriter(new File(rapidqueries + qf.getName()));
				writer.print(TranslatorForSystems.toRapid(query));
				writer.close();				
			}
			scn.close();
		}

	}
}
