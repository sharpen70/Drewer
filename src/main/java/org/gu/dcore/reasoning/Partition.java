package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import org.gu.dcore.interf.Term;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Variable;

public class Partition {
	/*
	 * The values of the map are arrays of boolean with size 3
	 * 1st of array denoting the category contain constant
	 * 2st of array denoting the category contain existential var
	 * 3st of array denoting the category contain frontier var
	 */
	private Map<Set<Term>, boolean[]> categories;	
	private Map<Set<Term>, Set<Term>> separating;
	private Set<Term> sticky;
	
	private boolean valid = true;
	
	private Substitution substitution = null;
	
	public Partition() {
		this.categories = new HashMap<>();
		this.separating = new HashMap<>();
		this.sticky = new HashSet<>();
	}
	
	public boolean isValid() {
		return this.valid;
	}
	
	public boolean isStickyEmpty() {
		return this.sticky.isEmpty();
	}
	
	/*
	 * @param  b: a term from the atomset
	 * 		   h: a term from the head of existential rule
	 */
	public void add(Term b, TermType bt, Term h, TermType ht) {
		Set<Term> sticky_var = new HashSet<>();
		
		boolean b_in = false;
		boolean h_in = false;
		
		Entry<Set<Term>, boolean[]> first = null;
		
		if((bt == TermType.CONSTANT && ht == TermType.CONSTANT) ||
				(bt == TermType.CONSTANT && ht == TermType.EXISTENTIAL))
		{	this.valid = false; return;  } 
		
		for(Entry<Set<Term>, boolean[]> entry : this.categories.entrySet()) {
			boolean this_round = false;
			
			Set<Term> category = entry.getKey();
			boolean[] status = entry.getValue();
			
			Set<Term> sep = this.separating.getOrDefault(category, new HashSet<>());
			
			if(b_in && h_in) break;
			
			if(!b_in && category.contains(b)) {
				if(bt == TermType.CONSTANT && (status[0] || status[1])) 
				{	this.valid = false; return;  } 
				
				if(bt == TermType.SEPARATING) {
					if(status[1]) this.sticky.add(b);
					this.separating.get(category).add(b);
				}
				
				if(first == null) {
					first = entry;
					b_in = true;
					this_round = true;
					this.changeStatus(status, bt);
				}
				else {
					first.getKey().addAll(category);
					this.changeStatus(first.getValue(), bt);
					this.categories.remove(category);
				}
			}
			
			if(!h_in && category.contains(h)) {
				if(ht == TermType.CONSTANT && (status[0] || status[1])) 
				{	this.valid = false; return;  } 
				
				if(ht == TermType.EXISTENTIAL && (status[0] || status[1] || 
						status[2])) {	this.valid = false; return;  } 
				
				if(ht == TermType.EXISTENTIAL) {
					Set<Term> _sep = this.separating.get(category);
					if(!sep.isEmpty()) this.sticky.addAll(sep);
				}
				
				if(first == null) {
					first = entry;
					h_in = true;
					this.changeStatus(status, ht);
				}
				else {
					first.getKey().addAll(category);
					this.changeStatus(first.getValue(), ht);
					if(!this_round) this.categories.remove(category);
				}
			}
		}
		
		if(b_in && !h_in) { first.getKey().add(h); this.changeStatus(first.getValue(), ht); }
		if(!b_in && h_in) { first.getKey().add(b); this.changeStatus(first.getValue(), bt); }
		if(!b_in && !h_in) this.addCategory(b, bt, h, ht);
	}
	
	private void changeStatus(boolean[] status, TermType type) {
		switch(type) {
			case CONSTANT : status[0] = true; break;
			case EXISTENTIAL : status[1] = true; break;
			case FRONTIER : status[2] = true; break;
			default : break;
		}
	}
	
	public Partition join(Partition p) {
		
		
		return null;
	}
	
	private void join()
	public Substitution getSubstitution() {
		if(this.substitution == null) {
			this.substitution = new Substitution();
			for(Set<Term> category : this.categories.keySet()) {
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
	
	private void addCategory(Term b, TermType bt, Term h, TermType ht) {
		Set<Term> category = new HashSet<>();
		boolean[] status = new boolean[3];
		
		category.add(b);
		category.add(h);
		
		this.changeStatus(status, bt);
		this.changeStatus(status, ht);
		
		this.categories.put(category, status);
		
		if(bt == TermType.SEPARATING) {
			Set<Term> _sep = new HashSet<>();
			_sep.add(b);
			this.separating.put(category, _sep);

			if(ht == TermType.EXISTENTIAL)	this.sticky.add(b);
		}
	}	
}
