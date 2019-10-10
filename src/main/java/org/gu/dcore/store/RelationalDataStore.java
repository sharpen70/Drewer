package org.gu.dcore.store;

import java.util.HashMap;

import org.gu.dcore.model.Predicate;

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
}
