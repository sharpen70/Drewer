package org.gu.dcore.examples;

import java.io.FileNotFoundException;

import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

public class TestVlog {
	public static void main(String[] args) throws ParsingException, FileNotFoundException {
		DatalogEngine engine = new DatalogEngine();
		
//		String dir = "/home/peng/projects/chasebench/scenarios/deep/100/data";
		String dir = "/home/sharpen/projects/chasebench-master/scenarios/STB-128/data";
		engine.addSourceFromCSVDir(dir);
	}
}
