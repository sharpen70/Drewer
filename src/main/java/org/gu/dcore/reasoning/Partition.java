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
	public Set<Term> pBVar;
	public Set<Term> HVar;
	public List<Set<Term>> categories;	
		
	private Substitution substitution = null;
	
	public Partition() {
		this.categories = new LinkedList<>();
		this.pBVar = new HashSet<>();
		this.HVar = new HashSet<>();
	}
	
	public Partition getCopy() {
		Partition p = new Partition();
		
		for(Set<Term> c : this.categories) {
			Set<Term> nc = new HashSet<>();
			nc.addAll(c);
			p.categories.add(nc);
		}
		
		p.pBVar.addAll(this.pBVar);
		p.HVar.addAll(this.HVar);
		
		return p;
	}
	
	/*
	 * @param  b: a term from the atomset
	 * 		   h: a term from the head of existential rule
	 */
	public void add(Term b, Term h) {
		this.pBVar.add(b);
		this.HVar.add(h);
		
		boolean b_in = false;
		boolean h_in = false;
		
		Set<Term> first = null;
		
		for(Set<Term> category : this.categories) {
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
		if(!b_in && !h_in) this.addCategory(b, h);
	}
	
	public Partition join(Partition p) {
		Partition re = this.getCopy();
		
		re.pBVar.addAll(p.pBVar);
		re.HVar.addAll(p.HVar);
		
		Iterator<Set<Term>> it = p.categories.iterator();
		
		while(it.hasNext()) {
			Set<Term> pc = it.next();
			
			Set<Term> hit = null;			

			for(Set<Term> tc : re.categories) {
				for(Term t : pc) {
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
				Set<Term> c = new HashSet<>();
				c.addAll(pc);
				re.categories.add(c);
			}
		}
		
		return re;
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
	
	private void addCategory(Term b, Term h) {
		Set<Term> category = new HashSet<>();
		
		category.add(b);
		category.add(h);
		
		this.categories.add(category);
	}	
}
