package org.gu.dcore.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.core.reasoner.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.implementation.VLogReasoner;
import org.semanticweb.vlog4j.parser.ParsingException;
import org.semanticweb.vlog4j.parser.RuleParser;

public class DatalogEngine {
	private KnowledgeBase kb = null;
	private Reasoner reasoner = null;
	
	public DatalogEngine() throws ParsingException {
		kb = RuleParser.parse("");
	}
	
	public void addRules(List<Rule> rules) throws ParsingException {
		String import_str = "";
		
		for(Rule r :rules) {
			if(!r.isExRule()) {
				import_str += r.toVLog() + ". \n";
			}
		}
		
		RuleParser.parseInto(kb, import_str);
	}
	
	public Column answerAtomicQuery(Atom atom, int[] mapping, int arity) throws IOException, ParsingException {
		PositiveLiteral query = RuleParser.parsePositiveLiteral(atom.toVlog());
		
		Column result = new Column(arity, mapping);
		
		if(reasoner == null) reasoner = new VLogReasoner(kb);
		
		reasoner.reason();
		
		final QueryResultIterator answers = reasoner.answerQuery(query, false);
		
		answers.forEachRemaining(answer -> result.add(answer));
		
		return result;
	}
	
	public void addSourceFromCSVDir(String fileDir) throws FileNotFoundException, ParsingException {
		File dir = new File(fileDir);
		for(File csv : dir.listFiles()) addSourceFromCSV(csv);
	}
	
	public void addSourceFromCSV(File csv) throws ParsingException, FileNotFoundException {
		String fname = csv.getName();
		String pname = fname.substring(0, fname.indexOf("."));
		Scanner scanner = new Scanner(csv);
		
		String line;
		int arity = 0;
		
		if(scanner.hasNextLine()) {
			line = scanner.nextLine();
			arity = line.split(",").length;
			scanner.close();
		}
		
		String import_str = "@source <" + pname + ">(" + arity + ") : load-csv(\"" + csv.getAbsolutePath() + "\") .";
		RuleParser.parseInto(kb, import_str);
	}
	
	public void addFacts(Atom fact) throws ParsingException {
		RuleParser.parseInto(kb, fact.toRDFox());
	}
}
