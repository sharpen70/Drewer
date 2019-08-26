package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertCSVToFacts {
	public static void main(String[] args) throws Exception {
		File input_dir;
		PrintWriter writer;
		
		if(args.length < 2) {
			input_dir = new File("/home/sharpen/projects/chaseBench/scenarios/deep/data/");
			writer = new PrintWriter(System.out);
		}
		else {
			input_dir = new File(args[0]);
			writer = new PrintWriter(new File(args[1]));
		}
		
		for(File csv : input_dir.listFiles()) {
			String predicate_name = csv.getName().split("\\.")[0];
			Scanner scanner = new Scanner(csv);
			
			while(scanner.hasNextLine()) {
				String s = scanner.nextLine();				
				Pattern pattern = Pattern.compile("\"(.*?)\"");
				Matcher matcher = pattern.matcher(s);
				
				String atom = predicate_name + "(";
				
				boolean first = true;
				
				while(matcher.find()) {
					if(!first) atom += ", ";
					atom += matcher.group(1);
					first = false;
				}
		
				atom += ").\n";
				writer.write(atom);
			}
			
			scanner.close();
		}
		
		writer.close();
	}
}
