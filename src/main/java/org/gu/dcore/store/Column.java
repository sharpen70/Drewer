package org.gu.dcore.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Term;


public class Column {
	List<String[]> tuples;
	int[] columnMap;
	boolean[] position_blank;
	int arity = 0;
	
	public Column(int arity) {
		this.arity = arity;
		this.position_blank = new boolean[arity];
	}
	
	public Column(int arity, int[] columnMap) {
		this(arity);
		
		this.columnMap = columnMap;		
		for(int i = 0; i < columnMap.length; i++) this.position_blank[i] = true;
	}
	
	public boolean[] getPosition_blank() {
		return this.position_blank;
	}
	
	public void add(String[] t) {
		if(t.length == this.arity) this.tuples.add(t);
	}
	
	public void add(QueryResult answer) {
		String[] tuple = new String[this.arity];
		int i = 0;
		for(Term t : answer.getTerms()) {
			tuple[columnMap[i++]] = t.toString();
		}
		this.tuples.add(tuple);
	}
	
	public void remap(ArrayList<Integer> map) {
		List<String[]> nts = new LinkedList<>();
		for(String[] t : this.tuples) {
			String[] nt = new String[map.size()];
			for(int i = 0; i < map.size(); i++) 
				nt[i] = t[map.get(i)];
			nts.add(nt);
		}
		this.tuples = nts;
	}
	
	public List<String[]> getTuples() {
		return this.tuples;
	}
	
	/* Column b is assumed to having the same arity as this
	 * Tuples in a, b are changed after join, 
	 *   a = a/(a\cap b), b = b/(a\cap b) 
	 */ 
	public Column full_join(Column b) {
		if(this.arity != b.arity) return null;
		
		ArrayList<Integer> join_key = new ArrayList<>();
		
		for(int i = 0; i < this.arity; i++) {
			if(this.position_blank[i] && b.position_blank[i])
				join_key.add(i);
		}
		
		if(join_key.size() == 0) return b;
		
		Column result = new Column(this.arity, this.columnMap);
		Map<Join_key, List<String[]>> index = new HashMap<>();
		Set<Join_key> hit_key = new HashSet<>();
		
		Column left, right;
		if(this.tuples.size() < b.tuples.size()) { left = this; right = b; }
		else { left = b; right = this;}
		
		/* build hash index */
		for(String[] tuple : left.tuples) {
			String[] keys = new String[join_key.size()];
			for(int i = 0; i < keys.length; i++) keys[i] = tuple[join_key.get(i)];
			Join_key jk = new Join_key(keys);
			List<String[]> rows = index.get(jk);
			if(rows == null) {
				rows = new LinkedList<>();
				index.put(jk, rows);
			}
			rows.add(tuple);
		}
		
		/* Probe */
		Iterator<String[]> it = right.tuples.iterator();
		while(it.hasNext()) {
			String[] b_tuple = it.next();
			String[] keys = new String[join_key.size()];
			for(int i = 0; i < keys.length; i++) keys[i] = b_tuple[join_key.get(i)];
			Join_key jk = new Join_key(keys);
			
			List<String[]> rows = index.get(jk);
			if(rows != null) {
				hit_key.add(jk);
				for(String[] row : rows) {
					String[] merge = new String[this.arity];
					for(int i = 0; i < this.arity; i++) {
						if(row[i] != null) merge[i] = row[i];
						else if(b_tuple[i] != null) merge[i] = b_tuple[i];
					}
					result.add(merge);
				}
			}
			it.remove();
		}
		
		it = left.tuples.iterator();
		
		while(it.hasNext()) {
			String[] tuple = it.next();
			String[] keys = new String[join_key.size()];
			for(int i = 0; i < keys.length; i++) keys[i] = tuple[join_key.get(i)];
			Join_key jk = new Join_key(keys);
			if(hit_key.contains(jk)) it.remove();
		}
		
		return result;
	}
	
//	public Column inner_join(Column b, int[] jki, int[] bjki) {
//		Column result = new Column(this.arity + b.arity - jki.length);
//		Map<Join_key, List<String[]>> index = new HashMap<>();
//		
//		Column left, right;
//		if(this.tuples.size() < b.tuples.size()) { left = this; right = b; }
//		else { left = b; right = this;}
//		
//		return result;
//	}
	
	private class Join_key {
		String[] keys;
		
		Join_key(String[] keys) {
			this.keys = keys;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Join_key)) return false;
			
			Join_key that = (Join_key)obj;
			
			if(this.keys.length != that.keys.length) return false;
			for(int i = 0; i < this.keys.length; i++) {
				if(this.keys[i] != that.keys[i]) return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			int code = 7;
			
			for(int i = 0; i < this.keys.length; i++) {
				code = 31 * code + this.keys[i].hashCode();
			}
			
			return code;
		}
	}
}
