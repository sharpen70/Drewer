package org.gu.dcore.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.parsing.DcoreParser;

public class ConvertSourceTGD {
	public static void main(String[] args) throws Exception {
		String chasebench = "/home/sharpen/projects/chaseBench/scenarios/";

		File dir = new File(chasebench);
		
		for(File f : dir.listFiles()) {
			String tgds = chasebench + f.getName() + "/st.dlp";
			DcoreParser parser = new DcoreParser();
			Program P = parser.parseFile(tgds, true);
			
			Map<Predicate, Predicate> sourcePredicates = new HashMap<>();
			
			PrintStream outputstream1 = new PrintStream(new File(chasebench + f.getName() + "/bridge_source.rdfox"));	
			PrintStream outputstream4 = new PrintStream(new File(chasebench + f.getName() + "/bridge_source.dlp"));
			
			for(Rule r : P.getRuleSet()) {
				for(Atom a : r.getBody()) {
					Predicate sp = a.getPredicate();
					Predicate np = sourcePredicates.get(sp);
					if(np == null) {
						np = new Predicate("bridge_" + sp.getName(), 0, sp.getArity());
						sourcePredicates.put(sp, np);
					}
					a.setPredicate(np);
				}
				outputstream1.println(r.toRDFox());
				outputstream4.println(r);
			}
			outputstream1.close();
			outputstream4.close();
			
			PrintStream outputstream2 = new PrintStream(new File(chasebench + f.getName() + "/bridge.rdfox"));	
			PrintStream outputstream5 = new PrintStream(new File(chasebench + f.getName() + "/bridge.dlp"));
			PrintStream outputstream3 = new PrintStream(new File(chasebench + f.getName() + "/schema/bridge.sch"));	
			
			for(Entry<Predicate, Predicate> entry : sourcePredicates.entrySet()) {
				Predicate sp = entry.getKey();
				Predicate np = entry.getValue();
				
				ArrayList<Term> terms = new ArrayList<>();
				
				for(int i = 0; i < sp.getArity(); i++) {
					terms.add(new Variable(i));
				}
				
				Atom spa = new Atom(sp, terms);
				Atom npa = new Atom(np, terms);
				
				Rule r = new Rule(new AtomSet(npa), new AtomSet(spa), 0);
				outputstream2.println(r.toRDFox());
				outputstream5.println(r);
				
    			String p_schema = np.getName();
    			p_schema += "{\n";
    			for(int i = 0; i < np.getArity(); i++) {
    				if(i > 0) p_schema += ",\n";
    				p_schema += "    v" + i + " : SYMBOL";
    			}
    			p_schema += "\n}\n\n";
    			outputstream3.print(p_schema);
			}	
			outputstream2.close();
			outputstream3.close();
			outputstream5.close();
		}
	}
}
