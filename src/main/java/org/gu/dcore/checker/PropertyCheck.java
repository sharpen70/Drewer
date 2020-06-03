package org.gu.dcore.checker;

import java.io.File;

import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.model.Program;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;

public class PropertyCheck {
	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.out.println("Missing input!");
			return;
		}
		Program P = new DcoreParser().parseFile(args[0]);
		
		
//		String ofile = "/home/sharpen/projects/evaluations/benchmarks/owl/Uniprot/Uniprot.dlp";
//		Program P = new DcoreParser().parseFile(ofile);
//		Program P = new DcoreParser().parse("R(X,Y) :- P(Z, X). P(X,Z) :- R(X,Z), R(Z,X).");
		Modularizor modularizor = new Modularizor(P.getRuleSet());
		
		SeparableChecker checker = new SeparableChecker(modularizor);
		WAChecker wachecker = new WAChecker(P.getRuleSet(), PredicateFactory.instance().getPredicates());
		
		boolean separable = checker.check();
		boolean wa = wachecker.check();
		
		System.out.println("FUS-SHY: " + separable);
		System.out.println("Weakly Acyclic: " + wa);
	}
}
