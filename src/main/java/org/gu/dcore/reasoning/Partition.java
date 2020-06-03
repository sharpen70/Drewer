package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;

public class Partition {
	public List<Set<Term>> categories;	
		
	private NormalSubstitution substitution = null;
	private int var_offset = 0;
	private int rc_offset = 0;
	
	public Partition(int var_offset) {
		this.categories = new LinkedList<>();
		this.var_offset = var_offset;
	}
	
	public Partition(int var_offset, int rc_offset) {
		this(var_offset);
		this.rc_offset = rc_offset;
	}
	
	public Partition getCopy() {
		Partition p = new Partition(this.var_offset);
		
		for(Set<Term> c : this.categories) {
			Set<Term> nc = new HashSet<>(c);
			p.categories.add(nc);
		}
		
		return p;
	}
	
	public int getVOffset() {
		return this.var_offset;
	}
	
	public int getRCOffset() {
		return this.rc_offset;
	}
	/*
	 * @param  b: a term from the atomset
	 * 		   h: a term from the head of existential rule
	 */
	public void add(Term b, Term h) {		
		boolean b_in = false;
		boolean h_in = false;
		
		Set<Term> first = null;
		
		if(h instanceof Variable) {
			int v = ((Variable) h).getValue() + this.var_offset;
			h = TermFactory.instance().getVariable(v);
		}
		
		if(h instanceof RepConstant) {
			int v = ((RepConstant) h).getValue() + this.rc_offset;
			h = TermFactory.instance().getRepConstant(v);
		}
		
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
		if(!b_in && !h_in) {
			Set<Term> category = new HashSet<>();			
			category.add(b); category.add(h);			
			this.categories.add(category);
		}
	}
	
	/*
	 * @param   p: the partition to be joined,
	 * 			left_rule_variable_only: Only join the variables in the rule 
	 * 				whose body is unified, i.e., the left hand side rule
	 */
	public Partition join(Partition p, boolean left_rule_variable_only) {
		Partition re = this.getCopy();
		
		Iterator<Set<Term>> it = p.categories.iterator();
		
		while(it.hasNext()) {
			Set<Term> pc = it.next();
			
			Set<Term> hit = null;			
			
			Iterator<Set<Term>> rit = re.categories.iterator();
			while(rit.hasNext()) {
				Set<Term> tc = rit.next();
				for(Term t : pc) {
					if(left_rule_variable_only && t instanceof Variable &&
							((Variable)t).getValue() >= this.var_offset)
						continue;
					
					if(tc.contains(t)) {
						if(hit == null) {
							hit = tc;
							tc.addAll(pc);
//							for(Term tt : pc) {
//								if(((Variable)tt).getValue() < this.var_offset)
//									tc.add(tt);
//							}
						}
						else {
							hit.addAll(tc);
							rit.remove();
						}
						break;
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
	
	public boolean isAdmissible() {
		for(Set<Term> category : this.categories) {
			boolean c = false;
			for(Term t : category) {
				if(t instanceof Constant) { 
					if(c) return false;
					else c = true;
				}
			}
		}
		return true;
	}
	
	public NormalSubstitution getSubstitution() {
		if(this.substitution == null) {
			this.substitution = new NormalSubstitution();
			for(Set<Term> category : this.categories) {
				Term mapto = null;

				for(Term t : category) {
					if(t instanceof Constant) {
						mapto = t;
						break;
					}
					else if(t instanceof RepConstant) {
						if(mapto == null || mapto instanceof Variable) {
							mapto = t;
						}
						else if(mapto instanceof RepConstant && 
								((RepConstant)mapto).getValue() >= this.rc_offset) {
							mapto = t;
						}
					}
					else {
						if(mapto == null) {
							if(((Variable)t).getValue() < this.var_offset) {
								mapto = t;
							}
						}
					}
				}	
				
				for(Term t : category) {
					if(!t.equals(mapto)) {
						this.substitution.add(t, mapto, false);
					}
				}
			}
		}
		return this.substitution;
	}
}
