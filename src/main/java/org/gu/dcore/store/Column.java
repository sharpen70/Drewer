package org.gu.dcore.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		this.tuples = new LinkedList<>();
		this.position_blank = new boolean[arity];
	}
	
	public Column(int arity, int[] columnMap) {
		this(arity);
		
		this.columnMap = columnMap;		
		for(int i = 0; i < columnMap.length; i++) this.position_blank[i] = true;
	}
	
	public int size() {
		return this.tuples.size();
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
	
	public void filter(Map<Integer, Object> eqs) {
		Iterator<String[]> it = this.tuples.iterator();
		while(it.hasNext()) {
			String[] tuple = it.next();
			for(Entry<Integer, Object> entry : eqs.entrySet()) {
				Object o = entry.getValue();
				Integer i = entry.getKey();
				if(o instanceof Integer) {
					if(tuple[i] != tuple[(int)o]) {
						it.remove();
						break;
					}
				}
				if(o instanceof String) {
					if(tuple[i] != o) {
						it.remove();
						break;
					}
				}
			}
		}
	}
	
	public Column join(Column b, int[] jka, int[] jkb) {
		int aty = this.arity + b.arity - jka.length;
		Column result = new Column(aty);
		
		int jk_length = jka.length;
		
		if(jk_length == 0) {
			
		}
		
		List<String[]> left, right;
		int[] left_key, right_key;
		
		boolean this_left = true;
		if(this.tuples.size() < b.tuples.size()) {
			left = this.tuples; left_key = jka;
			right = b.tuples; right_key = jkb;
		}
		else {
			left = b.tuples; left_key = jkb;
			right = this.tuples; right_key = jka;
			this_left = false;
		}
		
		/* build index map */
		Map<String[], List<String[]>> index = new HashMap<>();
		
		for(String[] t : left) {
			String[] jk = new String[jk_length];
			for(int i = 0; i < jk_length; i++) {
				jk[i] = t[left_key[i]];
			}
			List<String[]> ts = index.get(jk);
			if(ts == null) {
				ts = new LinkedList<>();
				index.put(jk, ts);
			}
			ts.add(t);
		}
		
		/* Probe */
		for(String[] t : right) {
			String[] jk = new String[jk_length];
			for(int i = 0; i < jk_length; i++) {
				jk[i] = t[right_key[i]];
			}
			List<String[]> mts = index.get(jk);
			if(mts != null) {
				for(String[] mt : mts) {
					String[] nt = new String[aty];
					String[] merge, tomerge;
					int[] tomerge_jk;
					
					if(this_left) {
						merge = mt;
						tomerge = t;
						tomerge_jk = right_key;
					}
					else {
						merge = t;
						tomerge = mt;
						tomerge_jk = left_key;
					}
					
					int m;
					for(m = 0; m < merge.length; m++) {
						nt[m] = merge[m];
					}
					boolean[] removed = new boolean[tomerge.length];
					for(int i = 0; i < tomerge_jk.length; i++) removed[tomerge_jk[i]] = true;
					
					for(int i = 0; i < tomerge.length; i++) {
						if(!removed[i]) nt[m++] = tomerge[i];
					}
					result.add(nt);
				}
			}
		}
		
		return result;
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
		Map<String[], List<String[]>> index = new HashMap<>();
		Set<String[]> hit_key = new HashSet<>();
		
		Column left, right;
		if(this.tuples.size() < b.tuples.size()) { left = this; right = b; }
		else { left = b; right = this;}
		
		/* build hash index */
		for(String[] tuple : left.tuples) {
			String[] keys = new String[join_key.size()];
			for(int i = 0; i < keys.length; i++) keys[i] = tuple[join_key.get(i)];
			List<String[]> rows = index.get(keys);
			if(rows == null) {
				rows = new LinkedList<>();
				index.put(keys, rows);
			}
			rows.add(tuple);
		}
		
		/* Probe */
		Iterator<String[]> it = right.tuples.iterator();
		while(it.hasNext()) {
			String[] b_tuple = it.next();
			String[] keys = new String[join_key.size()];
			for(int i = 0; i < keys.length; i++) keys[i] = b_tuple[join_key.get(i)];
			
			List<String[]> rows = index.get(keys);
			if(rows != null) {
				hit_key.add(keys);
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
			if(hit_key.contains(keys)) it.remove();
		}
		
		return result;
	}
}
