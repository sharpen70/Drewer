package org.gu.dcore.reasoning;

import java.util.ArrayList;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;

public class AtomSetUtils {

	public static AtomSet minus(AtomSet a, AtomSet b) {
		ArrayList<Atom> remaining = new ArrayList<>();
		
		for(Atom atom : a) {
			if(!b.contains(atom)) remaining.add(atom);
		}
		
		return new AtomSet(remaining);
	}
}
