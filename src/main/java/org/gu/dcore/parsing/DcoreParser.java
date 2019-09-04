package org.gu.dcore.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.gu.dcore.antlr4.EDLGParser.AtomsetContext;
import org.gu.dcore.antlr4.EDLGParser.ExruleContext;
import org.gu.dcore.antlr4.EDLGParser.ProgramContext;
import org.gu.dcore.antlr4.EDLGParser.PruleContext;
import org.gu.dcore.antlr4.EDLGParser.TermsContext;
import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;


/**
 * This is the parser class for Datalog program
 * @author sharpen
 * @version 1.0, April 2018
 */
public class DcoreParser {
	private Map<String, Term> vMap;
	private int v = 0;
	
	public DcoreParser() {
		vMap = new HashMap<>();
	}
	
	/**
	 * @param program the input source for parsing
	 * @return the Program object corresponding to input
	 * @throws IOException 
	 */
	public Program parseFile(String programFile) throws IOException {		
		return this.parse(CharStreams.fromFileName(programFile));
	}
	
	public Program parse(String s) {
		return this.parse(CharStreams.fromString(s));
	}
	
	public Program parse(CharStream charStream) {
		PredicateFactory.reset();
		TermFactory.reset();
		
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
			List<Rule> exrules = new LinkedList<>();
			
			for(PruleContext c : ctx.prule()) {
				ExruleContext exc = c.exrule();
				if(exc != null) {
					exrules.add(exc.accept(exRuleVisitor));
				}
			}
			
//			List<Rule> exrules = ctx.prule()
//					.stream()
//					.map(prule -> prule.accept(exRuleVisitor))
//					.collect(Collectors.toList());
			
			return new Program(exrules);
		}
	}
	
	private class ExRuleVisitor extends EDLGBaseVisitor<Rule> {
		@Override
		public Rule visitExrule(ExruleContext ctx) {
			AtomSetVisitor atomSetVisitor = new AtomSetVisitor();
			
			vMap.clear();
			v = 0;
			
			AtomSet head = ctx.atomset(0).accept(atomSetVisitor);
			AtomSet body = ctx.atomset(1).accept(atomSetVisitor);
			
			if(head == null) return null;
			
			return RuleFactory.instance().createRule(head, body);
		}
	}
	
	private class AtomSetVisitor extends EDLGBaseVisitor<AtomSet> {
		@Override
		public AtomSet visitAtomset(AtomsetContext ctx) {
			AtomVisitor atomVisitor = new AtomVisitor();
			
			ArrayList<Atom> atomset = new ArrayList<>();
			
			while(ctx != null) {
				Atom a = ctx.atom().accept(atomVisitor);
				if(a == null) return null;
				atomset.add(a);
				ctx = ctx.atomset();
			}
			
			return new AtomSet(atomset);
		}
	}
	
	private class AtomVisitor extends EDLGBaseVisitor<Atom> {
		@Override
		public Atom visitAtom(AtomContext ctx) {
			String iri = ctx.predicate().DESCRIPTION().getText();
			
			if(iri == "!") return null;
			
			ArrayList<Term> terms = new ArrayList<>();
			
			TermsContext _terms = ctx.terms();
			
			while(_terms != null) {
				String ts = _terms.term().DESCRIPTION().getText();
				Term t;
				
				if(Character.isUpperCase(ts.charAt(0))) {
					t = vMap.get(ts);
					if(t == null) {
						t = new Variable(v++);
						vMap.put(ts, t);
					}
				}
				else 
					t = TermFactory.instance().createConstant(ts);
				
				terms.add(t);
				_terms = _terms.terms();
			}
			
			Predicate predicate = PredicateFactory.instance().createPredicate(iri, terms.size());
					
			return new Atom(predicate, terms);
		}
	}
}
