package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Partition {
	public List<Set<Object>> categories;	
		
	private NormalSubstitution substitution = null;
	private int var_offset;
	
	public Partition(int var_offset) {
		this.categories = new LinkedList<>();
		this.var_offset = var_offset;
	}
	
	public Partition getCopy() {
		Partition p = new Partition(this.var_offset);
		
		for(Set<Object> c : this.categories) {
			Set<Object> nc = new HashSet<>();
			nc.addAll(c);
			p.categories.add(nc);
		}
		
		return p;
	}
	
	public int getOffset() {
		return this.var_offset;
	}
	/*
	 * @param  b: a term from the atomset
	 * 		   h: a term from the head of existential rule
	 * 		   eV: whether h is an existential variable
	 */
	public void add(Object b, Object h) {		
		boolean b_in = false;
		boolean h_in = false;
		
		Set<Object> first = null;
		
		if(b instanceof Variable) b = ((Variable) b).getValue();
		if(h instanceof Variable) h = ((Variable) h).getValue() + this.var_offset;
		
		for(Set<Object> category : this.categories) {
			boolean this_round = false;			
	
			if(b_in && h_in) break;
			
			if(!b_in && category.contains(b)) {				
				if(first == null) {
					first = category;
					b_in = true;
					this_round = true;
				}
				else {
					first.addAll(category);
					this.categories.remove(category);
				}
			}
			
			if(!h_in && category.contains(h)) {				
				if(first == null) {
					first = category;
					h_in = true;
				}
				else {
					first.addAll(category);
					if(!this_round) this.categories.remove(category);
				}
			}
		}
		
		if(b_in && !h_in) first.add(h); 
		if(!b_in && h_in) first.add(b);
		if(!b_in && !h_in) {
			Set<Object> category = new HashSet<>();			
			category.add(b); category.add(h);			
			this.categories.add(category);
		}
	}
	
	public Partition join(Partition p) {
		Partition re = this.getCopy();
		
		Iterator<Set<Object>> it = p.categories.iterator();
		
		while(it.hasNext()) {
			Set<Object> pc = it.next();
			
			Set<Object> hit = null;			

			for(Set<Object> tc : re.categories) {
				for(Object t : pc) {
					if(tc.contains(t)) {
						if(hit == null) {
							hit = tc;
							tc.addAll(pc);
						}
						else {
							hit.addAll(pc);
							it.remove();
						}
					}
				}
			}
			
			if(hit == null) {
				Set<Object> c = new HashSet<>();
				c.addAll(pc);
				re.categories.add(c);
			}
		}
		
		return re;
	}
	
	public NormalSubstitution getSubstitution() {
		if(this.substitution == null) {
			this.substitution = new NormalSubstitution();
			for(Set<Object> category : this.categories) {
				Object mapto = null;
				
				Object last = null;
				
				for(Object t : category) {
					last = t;
					if(t instanceof Constant) {
						mapto = t;
						break;
					}
				}
				
				if(mapto == null) mapto = last;
				
				for(Object t : category) {
//					if(!t.equals(mapto)) this.substitution.add((Variable)t, mapto);
				}
			}
		}
		return this.substitution;
	}
}
