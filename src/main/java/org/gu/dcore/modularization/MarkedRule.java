package org.gu.dcore.modularization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;

public class MarkedRule extends Rule {
	private Map<Rule, Map<Atom, Set<Integer>>> markedMap;
	
	public MarkedRule(Rule r) {
		super(r);
		markedMap = new HashMap<>();
	}
	
	public List<PredPosition> mark(Rule r, PredPosition predPosition) {	
		Map<Atom, Set<Integer>> markedPositions = this.markedMap.get(r);
		
		if(markedPositions == null) {
			markedPositions = new HashMap<>();
			this.markedMap.put(r, markedPositions);
		}
		
		boolean changed = false;
		
		for(Atom a : this.body) {
			if(a.getPredicate().equals(predPosition.getPredicate())) {
				Set<Integer> mp = markedPositions.get(a);
				
				if(mp == null) {
					mp = new HashSet<>();
					markedPositions.put(a, mp);
				}
				
				changed = mp.addAll(predPosition.getIndice());
			}
		}
		
		List<PredPosition> fhead = new LinkedList<>();
		
		//mark head
		if(!changed) return fhead;
		
		Set<Term> markedv = new HashSet<>();
		
		for(Entry<Atom, Set<Integer>> entry : markedPositions.entrySet()) {
			Set<Integer> idx = entry.getValue();
//				System.out.println(entry.getKey() + " " + idx);
			for(Integer j : idx) {
				Term t = entry.getKey().getTerm(j);
				if(t.isVariable()) markedv.add(t);
			}
		}
		
//			System.out.println(this.getLabel() + " " + markedv);
		
		boolean through = true;
		
		for(Atom a : this.body) {
			List<Term> lt = a.getTerms();
			
			for(int ti = 0; ti < lt.size(); ti++) {
				Term t = lt.get(ti);
				if(markedv.contains(t)) {
					Set<Integer> indice = markedPositions.get(a);
					if(indice == null || !indice.contains(ti)) {
						through = false; break;
					}
				}
			}
			
			if(!through) break;
		}
		
		if(through) {		
			List<PredPosition> _fhead = new LinkedList<>();
			
			for(Atom a : this.head) {
				Set<Integer> hid = new HashSet<>();
				
				List<Term> terms = a.getTerms();
				for(int ai = 0; ai < terms.size(); ai++) {
					if(markedv.contains(terms.get(ai))) hid.add(ai);
				}
				
				if(hid.isEmpty()) continue;
				
				Set<Integer> mhid = markedPositions.get(a);
				if(mhid == null) {
					mhid = new HashSet<>();
					markedPositions.put(a, mhid);
				}
				if(mhid.addAll(hid)) {
					_fhead.add(new PredPosition(a.getPredicate(), hid));
				}
			}
			
			fhead.addAll(_fhead);
		}
		
		return fhead;
	}
	
	public void printMarked() {
		for(Entry<Rule, Map<Atom, Set<Integer>>> entry: this.markedMap.entrySet()) {
			System.out.print("(" + entry.getKey().getRuleIndex() + ") ");
			 Map<Atom, Set<Integer>> mm = entry.getValue();
			 
			for(Atom a : this.head) {
				Set<Integer> indice = mm.get(a);
				if(indice != null) {
					System.out.print(a.getPredicate().getName() + indice + " ");
				}
			}
			
			System.out.print(": ");
			
			for(Atom a : this.body) {
				Set<Integer> indice = mm.get(a);
				if(indice != null) {
					System.out.print(a.getPredicate().getName() + indice + " ");
				}
			}
		}
	}
}
