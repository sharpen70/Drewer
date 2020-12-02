package org.gu.dcore.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreOWL2Parser;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class TestOWL2Parser {
	public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException {
		String owl = "/home/sharpen/projects/evaluations/benchmarks/new/UOBM/UOBM.owl";
//		String owl = "/home/sharpen/projects/evaluations/benchmarks/owl/LUBM/LUBM.owl";
		DcoreOWL2Parser parser = new DcoreOWL2Parser();
		
		Program P = parser.parseFile(owl);
		
		PrintWriter writer = new PrintWriter(new File("/home/sharpen/projects/evaluations/benchmarks/new/UOBM/UOBM.dlp"));
		
		System.out.println(P.getRuleSet().size());
		
		for(Rule r : P.getRuleSet()) {
			writer.println(r.toString());
		}
		
		writer.close();
	}
}
