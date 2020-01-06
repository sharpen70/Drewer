package org.gu.dcore.examples;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gu.dcore.abduction.NormalQueryAbduction;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.store.DatalogEngine;
import org.semanticweb.vlog4j.parser.ParsingException;

public class TestAbduction {
	public static void main(String[] args) throws ParsingException, IOException {
		String onto_file = "/home/peng/projects/tractable-abd/tboxfiles_dlp/semintec-tbox.dlp";
		String data_file = "/home/peng/projects/abdu_eval/data/SEMINTEC";
		
		DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(onto_file);
    	
 //   	ConjunctiveQuery query = new QueryParser().parse("?(Y) :- D(X), A(X,Y).");
    	ConjunctiveQuery query = new QueryParser().parse("?() :- Account(<http://www.owl-ontologies.com/unnamed.owl#po31987>).");
    	
    	DatalogEngine engine = new DatalogEngine();
    	
//    	engine.addRules(P.getRuleSet());
    	engine.addSourceFromCSVDir(data_file);
    	
    	Set<Predicate> abdu = new HashSet<>();
    	
 //   	SupportedAbduction abduction = new SupportedAbduction(P.getRuleSet(), query, engine, abdu);
    	NormalQueryAbduction abduction = new NormalQueryAbduction(P.getRuleSet(), query, engine, abdu);
    	
  //  	System.out.println(engine.answerAtomicQuery("A(a, ?b) ."));
    	List<AtomSet> expl = abduction.getExplanations();
    	
    	for(AtomSet atomset : expl) System.out.println(atomset);
	}
}
