package org.gu.dcore.examples;

import java.io.File;

import org.gu.dcore.checker.SeparableChecker;
import org.gu.dcore.model.Program;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;

public class TestFUS_SHY {
	public static void main(String[] args) throws Exception {
		String benchmarks = "/home/peng/projects/evaluations/benchmarks/existential_rules/";
		
		File dir = new File(benchmarks);
		
		for(File o : dir.listFiles()) {
			String name = o.getName();
			String dlp = benchmarks + name + "/" + name + ".dlp";
		
			Program P = new DcoreParser().parseFile(dlp);
			
			Modularizor modularizor = new Modularizor(P.getRuleSet());
			
			SeparableChecker checker = new SeparableChecker(modularizor);
			
			boolean separable = checker.check();
			boolean shy = checker.checkShy();
			
			System.out.println(name + " Shy: " + shy + " Separable: " + separable);
		}
	}
}
