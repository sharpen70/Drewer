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
import java.util.LinkedList;
import java.util.List;

import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPairwiseVoidVisitor;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class DcoreOWL2Parser {
	private List<Rule> rules;
	
	public DcoreOWL2Parser() {
	}
	
	public Program parseFile(String ontoFile) throws OWLOntologyCreationException {
		rules = new LinkedList<>();
		
		OWLOntologyManager manager = OWLManager.createConcurrentOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ontoFile));
		
		ontology.axioms().filter(new DcoreOWL2Filter()).forEach(a -> a.accept(new tboxAxiomVistor()));
		
		return new Program(rules);
	}
	
	/*
	 * At this stage, we consider only SubClassOf and SubPropertyOf axioms
	 */
	private class tboxAxiomVistor implements OWLAxiomVisitor {
		@Override
		public void visit(OWLSubClassOfAxiom axiom) {
			String sub_iri = axiom.getSubClass().accept(getExpIRI);
			String super_iri = axiom.getSuperClass().accept(getExpIRI);
		};
		@Override
		public void visit(OWLEquivalentClassesAxiom axiom) {
			OWLPairwiseVoidVisitor<OWLClassExpression> _vistor = new OWLPairwiseVoidVisitor<OWLClassExpression>() {
				@Override
				public void visit(OWLClassExpression a, OWLClassExpression b) {
					String a_iri = a.accept(getExpIRI);
					String b_iri = b.accept(getExpIRI);
				}
			};
			axiom.forEach(_vistor);
		};
		@Override
		public void visit(OWLSubObjectPropertyOfAxiom axiom) {
			OWLObjectPropertyExpression subp = axiom.getSubProperty();
			OWLObjectPropertyExpression superp = axiom.getSuperProperty();
			
			String subp_iri = getPropertyIRI(subp);
			String superp_iri = getPropertyIRI(superp);
			String i_subp_iri = getPropertyIRI(subp.getInverseProperty());
			String i_superp_iri = getPropertyIRI(superp.getInverseProperty());
			
			String rt_subp_iri = getPRClassIRI(subp);
			String rt_superp_iri = getPRClassIRI(superp);
			String rt_i_subp_iri = getPRClassIRI(subp.getInverseProperty());
			String rt_i_superp_iri = getPRClassIRI(superp.getInverseProperty());
			

		};
	}
	
	private class DGLPClassVisitor implements OWLClassExpressionVisitorEx<AtomSet> {
		
	}
	
	private static String getPropertyIRI(OWLObjectPropertyExpression exp) {
		String piri = exp.getNamedProperty().toStringID();
		if(exp instanceof OWLObjectInverseOf) return StDatabaseBuilder.getInverseStringiri(piri);
		else return piri;
	}
	
	private static String getPRClassIRI(OWLObjectPropertyExpression exp) {
		String piri = exp.getNamedProperty().toStringID();
		if(exp instanceof OWLObjectInverseOf) return StDatabaseBuilder.getPRStringiri(StDatabaseBuilder.getInverseStringiri(piri));
		else return StDatabaseBuilder.getPRStringiri(piri);	
	}
	
	private OWLClassExpressionVisitorEx<String> getExpIRI = new OWLClassExpressionVisitorEx<String>() {
		public String visit(OWLClass ce) {
			return ce.toStringID();
		};
		
		public String visit(OWLObjectSomeValuesFrom svf) {
			OWLObjectPropertyExpression exp = svf.getProperty();
			
			if(exp instanceof OWLObjectProperty) return StDatabaseBuilder.getPRStringiri(exp.getNamedProperty().toStringID());
			else return StDatabaseBuilder.getPRStringiri(StDatabaseBuilder.getInverseStringiri(exp.getNamedProperty().toStringID()));
		}
	};	
}
