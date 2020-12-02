package org.gu.dcore.parsing;
/*
 * Copyright (C) 2018 - 2020 Artificial Intelligence and Semantic Technology, 
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
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPairwiseVoidVisitor;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class DcoreOWL2Parser {
	private List<Rule> rules;
	
	int variable_counter = 0;
	
	public DcoreOWL2Parser() {
	}
	
	public Program parseFile(String ontoFile) throws OWLOntologyCreationException {
		rules = new LinkedList<>();
		
		OWLOntologyManager manager = OWLManager.createConcurrentOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ontoFile));
		Set<OWLOntology> ontologies = manager.getImportsClosure(ontology);
		
		for(OWLOntology o : ontologies)
			o.axioms().filter(new DcoreOWL2Filter()).forEach(a -> a.accept(new tboxAxiomVistor()));
		
		return new Program(rules);
	}
	
	/*
	 * At this stage, we consider only SubClassOf and SubPropertyOf axioms
	 */
	private class tboxAxiomVistor implements OWLAxiomVisitor {
		@Override
		public void visit(OWLSubClassOfAxiom axiom) {
			DGLPClassVisitor visitor = new DGLPClassVisitor(0);
			
			variable_counter = 0;
			AtomSet body = axiom.getSubClass().accept(visitor);
			AtomSet head = axiom.getSuperClass().accept(visitor);
			
			if(body != null && head != null)
				rules.add(RuleFactory.instance().createRule(head, body));
			
		};
		@Override
		public void visit(OWLEquivalentClassesAxiom axiom) {
			OWLPairwiseVoidVisitor<OWLClassExpression> _vistor = new OWLPairwiseVoidVisitor<OWLClassExpression>() {
				@Override
				public void visit(OWLClassExpression a, OWLClassExpression b) {
					DGLPClassVisitor visitor = new DGLPClassVisitor(0);
					
					variable_counter = 0;
					AtomSet as = a.accept(visitor);
					AtomSet bs = b.accept(visitor);
					
					if(as != null && bs != null) {
						rules.add(RuleFactory.instance().createRule(as, bs));
						rules.add(RuleFactory.instance().createRule(bs, as));
					}
				}
			};
			axiom.forEach(_vistor);
		};
		@Override
		public void visit(OWLSubObjectPropertyOfAxiom axiom) {
			OWLObjectPropertyExpression subp = axiom.getSubProperty();
			OWLObjectPropertyExpression superp = axiom.getSuperProperty();
			
			DGLPPropertyVisitor visitor = new DGLPPropertyVisitor(0);
			
			variable_counter = 0;
			AtomSet body = subp.accept(visitor);
			variable_counter = 0;
			AtomSet head = superp.accept(visitor);
			
			if(body != null && head != null)
				rules.add(RuleFactory.instance().createRule(head, body));
		};		
		@Override
		public void visit(OWLObjectPropertyDomainAxiom axiom) {	
			DGLPPropertyVisitor pty_visitor = new DGLPPropertyVisitor(0);
			DGLPClassVisitor class_visitor = new DGLPClassVisitor(0);
			
			variable_counter = 0;
			AtomSet body = axiom.getProperty().accept(pty_visitor);
			AtomSet head = axiom.getDomain().accept(class_visitor);
			
			if(body != null && head != null)
				rules.add(RuleFactory.instance().createRule(head, body));
		};
		@Override
		public void visit(OWLObjectPropertyRangeAxiom axiom) {
			DGLPPropertyVisitor pty_visitor = new DGLPPropertyVisitor(0);
						
			variable_counter = 0;
			AtomSet body = axiom.getProperty().accept(pty_visitor);
			
			DGLPClassVisitor class_visitor = new DGLPClassVisitor(variable_counter);
			
			AtomSet head = axiom.getRange().accept(class_visitor);
			
			if(body != null && head != null)
				rules.add(RuleFactory.instance().createRule(head, body));
		}
	}
	
	private class DGLPClassVisitor implements OWLClassExpressionVisitorEx<AtomSet> {
		/* the main variable for the class */
		private int base = 0;
		
		public DGLPClassVisitor(int cur_base) {
			this.base = cur_base;
		}
		
		@Override
		public AtomSet visit(OWLClass ce) {
			if(ce.isOWLThing()) return null;
			
			String iri = ce.toStringID();
			Predicate p = PredicateFactory.instance().createPredicate(iri, 1);
			
			Variable v = TermFactory.instance().getVariable(this.base);
			List<Term> terms = new ArrayList<>();
			terms.add(v);
			Atom a = AtomFactory.instance().createAtom(p, terms);
			
			return new AtomSet(a);
		};
		
		@Override
		public AtomSet visit(OWLObjectSomeValuesFrom svf) {
			OWLObjectPropertyExpression property = svf.getProperty();
			OWLClassExpression filler = svf.getFiller();
			
			DGLPPropertyVisitor visitor = new DGLPPropertyVisitor(this.base);			
			AtomSet p = property.accept(visitor);	
			
			DGLPClassVisitor nested_visitor = new DGLPClassVisitor(variable_counter);
			AtomSet fp = filler.accept(nested_visitor);
			
			if(fp == null) return null;
			
			p.addAll(fp);
			
			return p;			
		}
		
		@Override
		public AtomSet visit(OWLObjectIntersectionOf ce) {
			List<OWLClassExpression> expressions = ce.getOperandsAsList();
			AtomSet atomset = new AtomSet();
			
			for(OWLClassExpression exp : expressions) {
				AtomSet as = exp.accept(this);
				if(as != null)
					atomset.addAll(as);
				else return null;
			}
			
			return atomset;
		}
		
		@Override
		public AtomSet visit(OWLObjectHasSelf ce) {
			OWLObjectPropertyExpression exp = ce.getProperty();			
			String iri = exp.getNamedProperty().toStringID();
			
			Predicate p = PredicateFactory.instance().createPredicate(iri, 2);
			Variable v = TermFactory.instance().getVariable(this.base);
			List<Term> terms = new ArrayList<>();
			terms.add(v);
			terms.add(v);
			
			Atom a = AtomFactory.instance().createAtom(p, terms);

			return new AtomSet(a);
		}
	}
	
	private class DGLPPropertyVisitor implements OWLPropertyExpressionVisitorEx<AtomSet> {
		private int base;
		
		public DGLPPropertyVisitor(int cur_base) {
			this.base = cur_base;
		}
		@Override
		public AtomSet visit(OWLObjectInverseOf property) {
			OWLObjectPropertyExpression exp = property.getNamedProperty();			
			String iri = exp.getNamedProperty().toStringID();
			
			Predicate p = PredicateFactory.instance().createPredicate(iri, 2);
			Variable v = TermFactory.instance().getVariable(this.base);
			Variable v_ = TermFactory.instance().getVariable(++variable_counter);
			
			List<Term> terms = new ArrayList<>();
			terms.add(v_);
			terms.add(v);
			
			Atom a = AtomFactory.instance().createAtom(p, terms);

			return new AtomSet(a);
		}
		
		@Override
		public AtomSet visit(OWLObjectProperty property) {
			OWLObjectPropertyExpression exp = property.getNamedProperty();			
			String iri = exp.getNamedProperty().toStringID();
			
			Predicate p = PredicateFactory.instance().createPredicate(iri, 2);
			Variable v = TermFactory.instance().getVariable(this.base);
			Variable v_ = TermFactory.instance().getVariable(++variable_counter);
			
			List<Term> terms = new ArrayList<>();
			terms.add(v);
			terms.add(v_);
			
			Atom a = AtomFactory.instance().createAtom(p, terms);

			return new AtomSet(a);
		}
	}
}
