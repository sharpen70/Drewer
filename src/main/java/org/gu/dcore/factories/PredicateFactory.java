package org.gu.dcore.factories;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Predicate;

public class PredicateFactory {
	private static PredicateFactory factory = null;
	private Map<String, Predicate> pMap = null;
	private Map<String, Predicate> rew_Map = null;
	private long id = 1;
	private long rid;
	
	private PredicateFactory() {
		this.pMap = new HashMap<>();
		this.rew_Map = new HashMap<>();
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
	
	public void rewrite_reset() {
		this.rew_Map.clear();
		this.rid = this.id;
	}
	
	public Predicate createQueryPredicate(int arity) {
		String pstring = "ans";
		Predicate Q = new Predicate(pstring, 0, arity);
		this.rew_Map.put(pstring, Q);
		return Q;
	}
	
	public Predicate createBlockPredicate(String iri, int arity) {
		Predicate p = this.rew_Map.get(iri);
		
		if(p == null) {
			p = new Predicate(iri, this.rid++, arity);
			this.rew_Map.put(iri, p);
		}
		
		return p;
	}
	
	public Collection<Predicate> getPredicates() {
		return this.pMap.values();
	}
	
	public Collection<Predicate> getRewPredicates() {
		return this.rew_Map.values();
	}
	
	public List<Predicate> getPredicateSet() {
		List<Predicate> plist = new LinkedList<Predicate>();
		plist.addAll(this.pMap.values());
		plist.addAll(this.rew_Map.values());
		
		return plist;
	}
}
