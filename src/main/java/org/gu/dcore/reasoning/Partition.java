package org.gu.dcore.reasoning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Partition<E> {
	private List<Set<E>> categories;
	
	public Partition() {
		this.categories = new LinkedList<>();
	}
	
	public void add(E e1, E e2) {
		boolean e1_in = false;
		boolean e2_in = false;
		
		Iterator<Set<E>> e1_it = null;
		Iterator<Set<E>> e2_it = null;
		
		Iterator<Set<E>> it = this.categories.iterator();
		
		while(it.hasNext()) {
			Set<E> cur = it.next();
			
			if(cur.contains(e1)) {
				if()
				e1_it = it;
			}
			if(cur.contains(e2)) {
				e2_it = it;
			}
			
		}
		
		
	}
	
	public void addCategory(E e1, E e2) {
		Set<E> category = new HashSet<>();
		
		category.add(e1);
		category.add(e2);
		
		this.categories.add(category);
	}	
}
