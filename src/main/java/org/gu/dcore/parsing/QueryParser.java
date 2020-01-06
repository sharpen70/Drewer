package org.gu.dcore.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.gu.dcore.antlr4.QUERYBaseVisitor;
import org.gu.dcore.antlr4.QUERYLexer;
import org.gu.dcore.antlr4.QUERYParser;
import org.gu.dcore.antlr4.QUERYParser.AtomContext;
import org.gu.dcore.antlr4.QUERYParser.AtomsetContext;
import org.gu.dcore.antlr4.QUERYParser.QueryContext;
import org.gu.dcore.antlr4.QUERYParser.TermsContext;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.utils.Utils;

public class QueryParser {
	private Map<String, Term> vMap;
	private int v = 0;
	
	private boolean full = true;
	private String default_prefix = "";
	
	public QueryParser() {
		vMap = new HashMap<>();
	}
	
	public QueryParser(String prefix) {
		vMap = new HashMap<>();
		default_prefix = prefix;
	}
	
	public ConjunctiveQuery parseFile(String programFile) throws IOException {		
		return this.parse(CharStreams.fromFileName(programFile), true);
	}
	
	public ConjunctiveQuery parse(String s) {
		return this.parse(CharStreams.fromString(s), true);
	}
	
	public ConjunctiveQuery parse(String s, boolean full_iri) {
		return this.parse(CharStreams.fromString(s), full_iri);
	}
	
	public ConjunctiveQuery parse(CharStream charStream, boolean full_iri) {
		full = full_iri;
		
		QUERYLexer lexer = new QUERYLexer(charStream);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		QUERYParser parser = new QUERYParser(tokens);
		
		QueryVisitor vistor = new QueryVisitor();
		
		vMap.clear();
		v = 0;
		
		ConjunctiveQuery query = vistor.visit(parser.query());
		
		return query;
	}
	
	private class QueryVisitor extends QUERYBaseVisitor<ConjunctiveQuery> {
		@Override
		public ConjunctiveQuery visitQuery(QueryContext ctx) {
			List<Term> terms = new ArrayList<>();
			if(ctx.ansVar().terms() != null) {
				terms = ctx.ansVar().terms().accept(new TermsVisitor());
			}
			AtomSet body = ctx.atomset().accept(new AtomSetVisitor());
			
			ConjunctiveQuery query = new ConjunctiveQuery(terms, body);
			
			return query;
		}
	}
	
	private class AtomSetVisitor extends QUERYBaseVisitor<AtomSet> {
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
	
	private class AtomVisitor extends QUERYBaseVisitor<Atom> {
		@Override
		public Atom visitAtom(AtomContext ctx) {
			String iri;
			
			if(ctx.predicate().DESCRIPTION() != null) iri = ctx.predicate().DESCRIPTION().getText();
			else {					
				String bracketed = ctx.predicate().BRACKETED().getText();
				iri = bracketed.substring(1, bracketed.length() - 1);
			}
			
			if(full) {
				if(iri.indexOf("#") == -1) iri = default_prefix + "#" + iri;
			}
			else iri = Utils.getShortIRI(iri);
			
			TermsContext _terms = ctx.terms();
			
			ArrayList<Term> terms = _terms.accept(new TermsVisitor());
			
			Predicate predicate = PredicateFactory.instance().createPredicate(iri, terms.size());
					
			return new Atom(predicate, terms);
		}
	}
	
	private class TermsVisitor extends QUERYBaseVisitor<ArrayList<Term>> {
		@Override
		public ArrayList<Term> visitTerms(TermsContext ctx) {
			ArrayList<Term> terms = new ArrayList<>();
			
			while(ctx != null) {
				String ts;
				
				if(ctx.term().DESCRIPTION() != null) ts = ctx.term().DESCRIPTION().getText();
				else if(ctx.term().STRING() != null) ts = ctx.term().STRING().getText();
				else {					
					String bracketed = ctx.term().BRACKETED().getText();
					ts = bracketed.substring(1, bracketed.length() - 1);
				}
				
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
				ctx = ctx.terms();
			}
			
			return terms;
		}
	}
}
