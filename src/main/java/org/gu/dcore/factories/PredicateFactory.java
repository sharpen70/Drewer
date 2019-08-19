package org.gu.dcore.factories;

import java.util.HashMap;
import java.util.Map;

import org.gu.dcore.model.Predicate;

public class PredicateFactory {
	private static PredicateFactory factory = null;
	private Map<String, Predicate> pMap = null;
	private long id = 0;
	
	private PredicateFactory() {
		pMap = new HashMap<>();
	}
	
	public static PredicateFactory instance() {
		if(factory == null) factory = new PredicateFactory();
		
		return factory;
	}
	
	public static void reset() {
		factory = new PredicateFactory();
	}
	
	public Predicate createPredicate(String iri, int arity) {
		Predicate p = this.pMap.get(iri);
		
		if(p == null) {
			p = new Predicate(iri, this.id++, arity);
			this.pMap.put(iri, p);
		}
		
		return p;
	}
}
