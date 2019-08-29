package org.gu.dcore;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class Rewrite {
	public static void main(String[] args) throws Exception {
		String dlp = args[0];
		String queries = args[1];
		
		PrintStream outstream = args.length < 3 ? System.out : new PrintStream(new File(args[2]));
		
		Scanner scanner = new Scanner(new File(queries));
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(dlp);
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	while(scanner.hasNextLine()) {
    		String qs = scanner.nextLine();
        	ConjunctiveQuery query = new QueryParser().parse(qs);
        	
        	System.out.println("============");
        	System.out.println("Rewriting " + args[0]);
        	
    		Rule Qr = RuleFactory.instance().createQueryRule(query);
    		
        	System.out.println(Qr);
           	
        	long start = System.currentTimeMillis();
        	
        	List<Rule> datalog = mr.rewrite(query);
        	
        	long end = System.currentTimeMillis();
        	
        	outstream.println("Rewritings " + datalog.size() + " Time " + (end - start));
        	
        	System.out.println("\nRewriting Finish.");
    	}
    	
    	scanner.close();
    	outstream.close();
	}
}
