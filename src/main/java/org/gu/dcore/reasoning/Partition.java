package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Variable;

public class Partition {
	private List<Set<Term>> categories;
	private Substitution substitution = null;
	
	public Partition() {
		this.categories = new LinkedList<>();
	}
	
	public boolean add(Term e1, Term e2) {
		boolean e1_in = false;
		boolean e2_in = false;
		
		Set<Term> first = null;
		
		Iterator<Set<Term>> it = this.categories.iterator();

		while(it.hasNext()) {
			Set<Term> cur = it.next();
			boolean this_turn = false; 
			
			if(e1_in && e2_in) break;
			
			if(!e1_in && cur.contains(e1)) {
				if(first == null) {
					first = cur;
					e1_in = true;
					this_turn = true;
				}
				else {
					first.addAll(cur);
					it.remove();
				}
			}
			if(!e2_in && cur.contains(e2)) {
				if(first == null) {
					first = cur;
					e2_in = true;
				}
				else {
					first.addAll(cur);
					if(!this_turn) it.remove();
				}
			}
		}
		
		if(e1_in && !e2_in) first.add(e2);
		if(!e1_in && e2_in) first.add(e1);
		if(!e1_in && !e2_in) this.addCategory(e1, e2);
		
		return true;
	}
	
	/*
	 * return 1: valid partition without separating mappings, 
	 * 			i.e., a partition for single piece unifier 
	 * 
	 *		  0: valid partition with separating mappings
	 *
	 *	     -1: invalid partition
	 */
	public int add(Term e1, TermType t1, Term e2, TermType t2) {
		boolean e1_in = false;
		boolean e2_in = false;
		
		Set<Term> first = null;
		
		Iterator<Set<Term>> it = this.categories.iterator();

		while(it.hasNext()) {
			Set<Term> cur = it.next();
			boolean this_turn = false; 
			
			if(e1_in && e2_in) break;
			
			if(!e1_in && cur.contains(e1)) {
				if(first == null) {
					first = cur;
					e1_in = true;
					this_turn = true;
				}
				else {
					first.addAll(cur);
					it.remove();
				}
			}
			if(!e2_in && cur.contains(e2)) {
				if(first == null) {
					first = cur;
					e2_in = true;
				}
				else {
					first.addAll(cur);
					if(!this_turn) it.remove();
				}
			}
		}
		
		if(e1_in && !e2_in) first.add(e2);
		if(!e1_in && e2_in) first.add(e1);
		if(!e1_in && !e2_in) this.addCategory(e1, e2);
		
		return true;
	}
	
	public Substitution getSubstitution() {
		if(this.substitution == null) {
			this.substitution = new Substitution();
			for(Set<Term> category : this.categories) {
				Term mapto = null;
				
				Term last = null;
				
				for(Term t : category) {
					last = t;
					if(t instanceof Constant) {
						mapto = t;
						break;
					}
				}
				
				if(mapto == null) mapto = last;
				
				for(Term t : category) {
					if(!t.equals(mapto)) this.substitution.add((Variable)t, mapto);
				}
			}
		}
		return this.substitution;
	}
	
	public void addCategory(Term e1, Term e2) {
		Set<Term> category = new HashSet<>();
		
		category.add(e1);
		category.add(e2);
		
		this.categories.add(category);
	}	
	
	public List<Set<Term>> getCategories() {
		return this.categories;
	}
}
