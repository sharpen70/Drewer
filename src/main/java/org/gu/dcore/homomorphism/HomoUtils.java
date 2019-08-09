package org.gu.dcore.homomorphism;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;

public class HomoUtils {
	/*
	 * If b is contained in a, not based on object equality
	 */
	public static boolean contains(AtomSet a, AtomSet b) {
		for(Atom bb : b) {
			boolean in = false;
			for(Atom aa : a) {
				if(bb.getPredicate().equals(aa.getPredicate())
						&& bb.getTerms().equals(aa.getTerms()))
				{
					in = true; break;
				}
			}
			if(!in) return false;
		}
		
		return true;
	}
	
	/*
	 * Return a / b
	 */
	public static AtomSet minus(AtomSet a, AtomSet b) {
		AtomSet m = new AtomSet();
		
		for(Atom aa : a) {
			if(!b.contains(aa)) m.add(aa);
		}
		
		return m;
	}
	
	/*
	 * Return a union b (without duplication check) 
	 */
	public static AtomSet simple_union(AtomSet a, AtomSet b) {
		AtomSet m = new AtomSet(a);
		m.addAll(b);
		return m;
	}
	
	/*
	 * Return a union b 
	 */
	public static AtomSet union(AtomSet a, AtomSet b) {
		AtomSet m = new AtomSet(a);
		for(Atom bb : b) {
			if(!m.contains(bb)) m.add(bb);
		}
		return m;
	}
}
