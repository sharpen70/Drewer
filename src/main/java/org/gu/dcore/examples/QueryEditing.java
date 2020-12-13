package org.gu.dcore.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.utils.TranslatorForSystems;

public class QueryEditing {
	public static void main(String[] args) throws IOException {
//		transRapid();
//		transGrind();
		splitQueryFile();
//		getQueryMeta();
	}
	
	private static void transRapid() throws FileNotFoundException {
		String ontoDir = "/home/sharpen/projects/evaluations/benchmarks/owl/OG/";
		String dlpqueries = ontoDir + "QueriesInLength/";
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
	
	private static void transGrind() throws FileNotFoundException {
		String ontoDir = "/home/sharpen/projects/evaluations/benchmarks/owl/OG/";
		String dlpqueries = ontoDir + "QueriesInLength/";
		String rapidqueries = ontoDir + "grd_queries/";
				
		File dir = new File(rapidqueries);
		if(!dir.exists()) dir.mkdir();
		
		for(File qf : new File(dlpqueries).listFiles()) {
			Scanner scn = new Scanner(qf);

			while(scn.hasNextLine()) {
				String line = scn.nextLine();
				ConjunctiveQuery query = new QueryParser().parse(line);
				
				System.out.println(qf + " length " + query.getBody().size());
				PrintWriter writer = new PrintWriter(new File(rapidqueries + qf.getName()));
				writer.print(TranslatorForSystems.toGrind(query));
				writer.close();				
			}
			scn.close();
		}
	}
	
	private static void getQueryMeta() throws IOException {
		String ontoDir = "/home/peng/projects/evaluations/benchmarks/existential_rules/ONT-256/";
		String qDir = ontoDir + "dlp_queries/";
		String onto = ontoDir + "ONT-256.dlp";
		
		Program P = new DcoreParser().parseFile(onto);
		IndexedByHeadPredRuleSet ihs = new IndexedByHeadPredRuleSet(P.getRuleSet());
		
		for(int i = 1; i <= new File(qDir).listFiles().length; i++) {
			ConjunctiveQuery q = new QueryParser().parseFile(qDir + "q" + i);
			
			int max_depth = 0;
			
			for(Atom atom : q.getBody()) {
				Predicate predicate = atom.getPredicate();
				int t_max_depth = 0;
				
				Queue<Pair<Predicate,Integer>> queue = new LinkedList<>();
				queue.add(new Pair<>(predicate, 0));
				
				Set<Rule> visited = new HashSet<>();
				
				while(!queue.isEmpty()) {
					Pair<Predicate,Integer> pair = queue.poll();
					Predicate pred = pair.a;
					int level = pair.b;
					
					int cur_level = level + 1;
					
					if(cur_level > t_max_depth) t_max_depth = cur_level;
					
					Set<Rule> rules = ihs.getRulesByPredicate(pred);
					
					if(rules != null) {
						for(Rule r : rules) {
							if(visited.add(r)) {
								for(Atom a : r.getBody()) {
									queue.add(new Pair<>(a.getPredicate(), cur_level));
								}
							}
						}
					}
				}	
				if(t_max_depth > max_depth) max_depth = t_max_depth;
			}	
			System.out.println("q" + i + " " + q.getBody().size() + " " + max_depth);
		}
	}
	
	private static void splitQueryFile() throws FileNotFoundException {
		String ontoDir = "/home/sharpen/projects/evaluations/benchmarks/existential_rules/";
		String qfile = ontoDir + "qa_queries.dlp";
		String sdir = ontoDir + "qa_dlp_queries";
		
		File dir = new File(sdir);
		if(!dir.exists()) dir.mkdir();
		
		int i = 0;
		
		Scanner scn = new Scanner(new File(qfile));
		
		while(scn.hasNextLine()) {
			String line = scn.nextLine();
			ConjunctiveQuery q = new QueryParser().parse(line);
			i++;
			System.out.println("q" + i + ", " + q.getBody().size());
			PrintWriter writer = new PrintWriter(new File(sdir + "/q" + i));
			
			writer.println(line);
			writer.close();
		}
		scn.close();		
	}
}
