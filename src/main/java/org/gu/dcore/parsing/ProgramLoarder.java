package org.gu.dcore.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.gu.dcore.antlr4.EDLGBaseVisitor;
import org.gu.dcore.antlr4.EDLGLexer;
import org.gu.dcore.antlr4.EDLGParser;
import org.gu.dcore.antlr4.EDLGParser.AtomContext;
import org.gu.dcore.antlr4.EDLGParser.BodyContext;
import org.gu.dcore.antlr4.EDLGParser.ExruleContext;
import org.gu.dcore.antlr4.EDLGParser.ProgramContext;
import org.gu.dcore.antlr4.EDLGParser.TermsContext;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.ExRule;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;


/**
 * This is the parser class for Datalog program
 * @author sharpen
 * @version 1.0, April 2018
 */
public class ProgramLoarder {
	
	public ProgramLoarder() {
		
	}
	
	/**
	 * @param program the input source for parsing
	 * @return the Program object corresponding to input
	 * @throws IOException 
	 */
	public Program load(String programFile) throws IOException {		
		//Parsing
		CharStream charStream = CharStreams.fromFileName(programFile);
		
		EDLGLexer lexer = new EDLGLexer(charStream);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		EDLGParser parser = new EDLGParser(tokens);
		
		ProgramVisitor vistor = new ProgramVisitor();
		
		Program program = vistor.visit(parser.program());
		
		return program;
	}
	
	private class ProgramVisitor extends EDLGBaseVisitor<Program> {		
		@Override
		public Program visitProgram(ProgramContext ctx) {
			ExRuleVisitor exRuleVisitor = new ExRuleVisitor();
			
			List<ExRule> exrules = ctx.exrule()
					.stream()
					.map(exrule -> exrule.accept(exRuleVisitor))
					.collect(Collectors.toList());
			
			return new Program(exrules);
		}
	}
	
	private class ExRuleVisitor extends EDLGBaseVisitor<ExRule> {
		@Override
		public ExRule visitExrule(ExruleContext ctx) {
			AtomVisitor atomVisitor = new AtomVisitor();
			
			Atom head = ctx.head().atom().accept(atomVisitor);
			
			List<Atom> body = new ArrayList<>();
			
			BodyContext _body = ctx.body();
			
			while(_body != null) {
				if(_body.atom() != null)
					body.add(_body.atom().accept(atomVisitor));
				_body = _body.body();
			}
					
			return new ExRule(head, body);
		}
	}
	
	private class AtomVisitor extends EDLGBaseVisitor<Atom> {
		@Override
		public Atom visitAtom(AtomContext ctx) {
			String iri = ctx.predicate().getText();
			Map<String, Integer> vMap = new HashMap<>();
			
			int arity = 0;
			int value = 0;
			
			List<Term> terms = new ArrayList<>();
			
			TermsContext _terms = ctx.terms();
			
			while(_terms != null) {
				arity++;
				String ts = _terms.term().getText();
				Term t;
				
				if(ts.startsWith("?")) {
					String vstring = ts.substring(1);
					Integer vv = vMap.get(vstring);
					if(vv == null) {
						vv = value++;
						vMap.put(vstring, vv);
					}
					t = TermFactory.instance().createVariable(vv);
				}
				else t = TermFactory.instance().createConstant(ts);
				
				terms.add(t);
				_terms = _terms.terms();
			}
			
			Predicate predicate = PredicateFactory.instance().createPredicate(iri, arity);
					
			return new Atom(predicate, terms);
		}
	}
}
