package org.gu.dcore.rewriting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.NormalSubstitution;
import org.gu.dcore.reasoning.Partition;

public class CompactComparator {
	private AtomSet source;
	private AtomSet target;	
	
	private Map<Atom, List<Partition>> preParts = null;
	
	public CompactComparator(AtomSet source, AtomSet target) {
		this.source = source;
		this.target = target;
	}
	
	public List<Partition> getCompactUnifiers(Comparator comparator) {
		List<Partition> result = new LinkedList<>();
		
		if(!getPartialPartitions()) return result;
		
		List<Partition> prePartitions = getPrePartitions();
		
		for(Partition part : prePartitions) {
			int rc_offset = part.getRCOffset();
			AtomSet m_source = part.getSubstitution().getImageOf(source, 0, rc_offset, 1);
			AtomSet m_target = part.getSubstitution().getImageOf(target);
			
			if(comparator.compare(m_source, m_target)) result.add(part);
		}
		return result;
	}

	private List<Partition> getPrePartitions() {
		LinkedList<Partition> queue = new LinkedList<>();
		
		for(Entry<Atom, List<Partition>> entry : preParts.entrySet()) {
			if(queue.isEmpty()) {
				for(Partition part : entry.getValue()) {
					queue.add(part);
				}
			}
			else {
				LinkedList<Partition> tqueue = new LinkedList<>();
				while(!queue.isEmpty()) {
					Partition cur = queue.pop();
					for(Partition part : entry.getValue()) {						
						tqueue.add(cur.join(part, false));
					}
				}
				queue = tqueue;
			}
		}
		
		return queue;
	}
	
	private boolean getPartialPartitions() {
		preParts = new HashMap<>();
		
		for(int i = 0; i < this.source.size(); i++) {
			Atom source_atom = this.source.getAtom(i);
			List<Partition> parts = new LinkedList<>();
			for(int j = 0; j < this.target.size(); j++) {
				Atom target_atom = this.target.getAtom(j);
				if(source_atom.getPredicate().equals(target_atom.getPredicate())) {
					Partition part = new Partition(0, source.getMaxRCValue() + 1);
					NormalSubstitution sub = new NormalSubstitution();
					boolean valid = true;
					for(int p = 0; p < source_atom.getTerms().size(); p++) {
						Term source_term = source_atom.getTerm(p);
						Term target_term = target_atom.getTerm(p);
						
						if(source_term instanceof RepConstant && target_term instanceof RepConstant) {
							part.add(source_term, target_term);
						}
						else if(source_term instanceof Variable && target_term instanceof Variable) {
							if(!sub.add(source_term, target_term, false)) {
								valid = false;
								break;
							}
						}
						else {
							valid = false;
							break;
						};
					}
					if(valid) {
						parts.add(part);
					}
				}
			}
			if(parts.isEmpty()) return false;
			else preParts.put(source_atom, parts);
		}
		return true;
	}
}
