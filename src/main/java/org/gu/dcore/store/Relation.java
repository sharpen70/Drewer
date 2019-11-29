package org.gu.dcore.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.model.Term;

public class Relation {
	private List<Long[]> tuples;
	private int arity;
	
	public Relation(int arity) {		
		this.arity = arity;
	}
	
	public void add(Long[] t) {
		if(t.length == this.arity) this.tuples.add(t);
	}
	
	public Map<Term, List<Long>> getMatchTerms(ArrayList<Term> map_terms) {
		return null;
	}
	
	/*   Tuples in a, b are changed after join, 
	 *   a = a/(a\cap b), b = b/(a\cap b) 
	 */ 
	public Relation full_join(Relation b, int[] join_key) {
		Relation result = new Relation(this.arity);
		Map<Join_key, List<Long[]>> index = new HashMap<>();
		Set<Join_key> hit_key = new HashSet<>();
		
		Relation left, right;
		if(this.tuples.size() < b.tuples.size()) { left = this; right = b; }
		else { left = b; right = this;}
		
		/* build hash index */
		for(Long[] tuple : left.tuples) {
			Long[] keys = new Long[join_key.length];
			for(int i = 0; i < keys.length; i++) keys[i] = tuple[join_key[i]];
			Join_key jk = new Join_key(keys);
			List<Long[]> rows = index.get(jk);
			if(rows == null) {
				rows = new LinkedList<>();
				index.put(jk, rows);
			}
			rows.add(tuple);
		}
		
		/* Probe */
		Iterator<Long[]> it = right.tuples.iterator();
		while(it.hasNext()) {
			Long[] b_tuple = it.next();
			Long[] keys = new Long[join_key.length];
			for(int i = 0; i < keys.length; i++) keys[i] = b_tuple[join_key[i]];
			Join_key jk = new Join_key(keys);
			
			List<Long[]> rows = index.get(jk);
			if(rows != null) {
				hit_key.add(jk);
				for(Long[] row : rows) {
					Long[] merge = new Long[this.arity];
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
			Long[] tuple = it.next();
			Long[] keys = new Long[join_key.length];
			for(int i = 0; i < keys.length; i++) keys[i] = tuple[join_key[i]];
			Join_key jk = new Join_key(keys);
			if(hit_key.contains(jk)) it.remove();
		}
		
		return result;
	}
	
	public Relation inner_join(Relation b, int[] jki, int[] bjki) {
		Relation result = new Relation(this.arity + b.arity - jki.length);
		Map<Join_key, List<Long[]>> index = new HashMap<>();
		
		Relation left, right;
		if(this.tuples.size() < b.tuples.size()) { left = this; right = b; }
		else { left = b; right = this;}
		
		return result;
	}
	
	private class Join_key {
		Long[] keys;
		
		Join_key(Long[] keys) {
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
