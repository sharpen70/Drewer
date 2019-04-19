package org.gu.dcore.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
	public Map<Set<Term>, boolean[]> categories;	
		
	private Substitution substitution = null;
	
	public Partition() {
		this.categories = new HashMap<>();
	}
	
	/*
	 * @param  b: a term from the atomset
	 * 		   h: a term from the head of existential rule
	 */
	public boolean add(Term b, TermType bt, Term h, TermType ht) {		
		boolean b_in = false;
		boolean h_in = false;
		
		Entry<Set<Term>, boolean[]> first = null;
		
		if((bt == TermType.CONSTANT && (ht == TermType.CONSTANT) ||
				ht == TermType.EXISTENTIAL))
			return false;
		
		for(Entry<Set<Term>, boolean[]> entry : this.categories.entrySet()) {
			boolean this_round = false;
			
			Set<Term> category = entry.getKey();
			boolean[] status = entry.getValue();
			
	
			if(b_in && h_in) break;
			
			if(!b_in && category.contains(b)) {
				if(bt == TermType.CONSTANT && (status[0] || status[1])) 
					return false;
				
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
					return false;
				
				if(ht == TermType.EXISTENTIAL && (status[0] || status[1] || 
						status[2])) return false;
				
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
		
		return true;
	}
	
	private void changeStatus(boolean[] status, TermType type) {
		switch(type) {
			case CONSTANT : status[0] = true; break;
			case EXISTENTIAL : status[1] = true; break;
			case FRONTIER : status[2] = true; break;
			default : break;
		}
	}
	
	public boolean join(Partition p) {		
		for(Entry<Set<Term>, boolean[]> entry1 : p.categories.entrySet()) {
			Set<Term> hit = null;
			List<Set<Term>> addToHit = new LinkedList<>();
			
			for(Entry<Set<Term>, boolean[]> entry2 : this.categories.entrySet()) {
				for(Term t : entry1.getKey()) {
					if(entry2.getKey().contains(t)) {
						if(hit == null) {
							hit = entry2.getKey();
							if(!join_category(p, hit, entry1.getKey())) return false;
						}
						else addToHit.add(entry2.getKey());
					}
				}
			}
			
			if(hit == null) this.categories.put(entry1.getKey(), entry1.getValue());
			
			for(Set<Term> c : addToHit) {
				if(!join_category(p, hit, c)) return false;
				this.categories.remove(c);
			}
		}
		
		return true;
	}
	
	
	private boolean join_category(Partition p, Set<Term> thisCategory, Set<Term> thatCategory) {
		boolean[] thisStatus = this.categories.get(thisCategory);	
		boolean[] thatStatus = p.categories.get(thatCategory);
	
		if(thisStatus[0] && thatStatus[0] || 
				(thisStatus[1] && (thatStatus[0] || thatStatus[1] || thatStatus[2])) || 
				(thatStatus[1] && (thisStatus[0] || thisStatus[1] || thisStatus[2])))
			return false;
		
		thisCategory.addAll(thatCategory);
		thisStatus[0] = thisStatus[0] || thatStatus[0];
		thisStatus[1] = thisStatus[1] || thatStatus[1];
		thisStatus[1] = thisStatus[2] || thatStatus[2];
		
		return true;
	}
	
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
	}	
}
