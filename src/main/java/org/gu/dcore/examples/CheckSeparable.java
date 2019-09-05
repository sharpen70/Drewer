package org.gu.dcore.examples;

import java.io.File;

import org.gu.dcore.grd.SeparableChecker;
import org.gu.dcore.model.Program;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.parsing.DcoreParser;

public class CheckSeparable {
	public static void main(String[] args) throws Exception {
		String benchmarks = "/home/sharpen/projects/evaluations/benchmarks/owl/";
		
		File dir = new File(benchmarks);
		
		for(File o : dir.listFiles()) {
			String name = o.getName();
			String dlp = benchmarks + name + "/" + name + ".dlp";
		
			Program P = new DcoreParser().parseFile(dlp);
			
			Modularizor modularizor = new Modularizor(P.getRuleSet());
			
			SeparableChecker checker = new SeparableChecker(modularizor);
			
			if(checker.check()) System.out.println(name + " is separable.");
			else System.out.println(name + " is not separable");
		}
	}
}
