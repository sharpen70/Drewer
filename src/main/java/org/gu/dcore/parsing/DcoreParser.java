package org.gu.dcore.parsing;
/*
 * Copyright (C) 2018 - 2020 Artificial Intelligence and Semantic technology, 
 * Griffith University
 * 
 * Contributors:
 * Peng Xiao (sharpen70@gmail.com)
 * Zhe wang
 * Kewen Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.gu.dcore.utils.Utils;


/**
 * This is the parser class for Datalog program
 * @author sharpen
 * @version 1.0, April 2018
 */
public class DcoreParser {
	private Map<String, Term> vMap;
	private int v = 0;
	
	boolean full = true;
	
	/* Due the input syntax of some benchmarks where prefix is not indicated,
	 * a default prefix needs to be provided 
	 * Here we use the first prefix occurring in the ontology as the default prefix
	 */
	String default_prefix = "";
	
	public DcoreParser() {
		vMap = new HashMap<>();
	}
	
	/**
	 * @param program the input source for parsing
	 * @return the Program object corresponding to input
	 * @throws IOException 
	 */
	public Program parseFile(String programFile) throws IOException {		
		return this.parse(CharStreams.fromFileName(programFile), true);
	}
	
	public Program parseFile(String programFile, boolean full_iri) throws IOException {		
		return this.parse(CharStreams.fromFileName(programFile), full_iri);
	}
	
	public Program parse(String s) {
		return this.parse(CharStreams.fromString(s), true);
	}
	
	public Program parse(CharStream charStream, boolean full_iri) {
		PredicateFactory.reset();
		TermFactory.reset();
		
		this.full = full_iri;
		
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
			String iri;
			
			if(ctx.predicate().DESCRIPTION() != null) iri = ctx.predicate().DESCRIPTION().getText();
			else {					
				String bracketed = ctx.predicate().BRACKETED().getText();
				iri = bracketed.substring(1, bracketed.length() - 1);
			}
			
			if(default_prefix == "") default_prefix = Utils.getPrefix(iri);
			
			iri = full ? iri : Utils.getShortIRI(iri);
			
			if(iri == "!") return null;
			
			ArrayList<Term> terms = new ArrayList<>();
			
			TermsContext _terms = ctx.terms();
			
			while(_terms != null) {
				String ts;
				
				if(_terms.term().DESCRIPTION() != null) ts = _terms.term().DESCRIPTION().getText();
				else if(_terms.term().STRING() != null) ts = _terms.term().STRING().getText();
				else {					
					String bracketed = _terms.term().BRACKETED().getText();
					ts = bracketed.substring(1, bracketed.length() - 1);
				}
				Term t;
				
				if(Character.isUpperCase(ts.charAt(0))) {
					t = vMap.get(ts);
					if(t == null) {
						t = TermFactory.instance().getVariable(v++);
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
	
	public String getDefaultPrefix() {
		return default_prefix;
	}
}
