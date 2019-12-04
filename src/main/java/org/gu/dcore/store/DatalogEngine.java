package org.gu.dcore.store;

import java.util.List;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class DatalogEngine {
	private KnowledgeBase kb = null;
	
	public DatalogEngine() {

	}
	
	public void addRules(List<Rule> rules) throws ParsingException {
		String import_str = "";
		
		for(Rule r :rules) {
			if(!r.isExRule()) {
				import_str += r.toVLog() + ". \n";
			}
		}
		
		kb = RuleParser.parse(import_str);
	}
	
	public void addSourceFromCSVDir(String fileDir) {
		
	}
	
	public void addSourceFromCSV(String pname, int arity, String filepath) throws ParsingException {
		String import_str = "@source " + pname + "(" + arity + ") : load-csv(\"" + filepath + "\") .";
		kb = RuleParser.parse(import_str);
	}
	
	public void addFacts(Atom fact) throws ParsingException {
		RuleParser.parseInto(kb, fact.toRDFox());
	}
}
