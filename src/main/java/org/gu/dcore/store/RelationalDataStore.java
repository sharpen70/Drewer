package org.gu.dcore.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Term;

public class RelationalDataStore implements DataStore {
	private HashMap<Predicate, Relation> relational_data;
	
	public RelationalDataStore() {
		this.relational_data = new HashMap<>();
	}
	
	public void loadData(String datafile) {
		
	}
	
	public void loadCSVData(String dataDir) {
		
	}
	
	public Relation getRelation(Predicate predicate) {
		return this.relational_data.get(predicate);
	}
	
	public List<Long[]> getMatchedTuples(Atom a, int[] index_mapping) {
		Relation relation = this.relational_data.get(a.getPredicate());
		return relation.getMatchTuples(a.getTerms());
	}
}
