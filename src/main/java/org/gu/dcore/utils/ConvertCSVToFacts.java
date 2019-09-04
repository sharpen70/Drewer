package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertCSVToFacts {
	public static void main(String[] args) throws Exception {
		String chasebench = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/";
	
		File dir = new File(chasebench);
			
		for(File f : dir.listFiles()) {
			File data = new File(f, "data");
			File facts = new File(f, "facts");
			facts.mkdir();
			
			for(File csv : data.listFiles()) {
				String predicate_name = csv.getName().split("\\.")[0];
				Scanner scanner = new Scanner(csv);
				PrintWriter writer = new PrintWriter(new File(facts, predicate_name));
				
				while(scanner.hasNextLine()) {
					String s = scanner.nextLine();				
					String[] terms = s.split(",");
					
					String atom = predicate_name + "(";
					
					for(int i = 0; i < terms.length; i++) {
						String a;
						if(!terms[i].startsWith("\"")) a = "\"" + terms[i] + "\"";
						else a = terms[i];
						
						if(i > 0) atom += ",";
						atom += a;
					}
					
					atom += ").\n";
					writer.write(atom);
				}
				
				scanner.close();
				writer.close();
			}
		}
		
		
	}
}
