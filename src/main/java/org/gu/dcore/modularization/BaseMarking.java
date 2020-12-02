package org.gu.dcore.modularization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gu.dcore.grd.IndexedByBodyPredRuleSet;
import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.grd.PredPosition;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Variable;
import org.gu.dcore.tuple.Tuple;

public class BaseMarking {
	private Map<Rule, RuleBasedMark> marking; 
	private IndexedByBodyPredRuleSet onto;
	private IndexedByHeadPredRuleSet ihs;
	
	public BaseMarking(List<Rule> ruleset) {
		this.marking = new HashMap<>();
		this.onto = new IndexedByBodyPredRuleSet(ruleset);
		this.ihs = new IndexedByHeadPredRuleSet(ruleset);
	}
	
	public void mark(Rule source) {		
		if(!source.getExistentials().isEmpty()) {
			RuleBasedMark rbm = this.marking.get(source);
			if(rbm == null) {
				rbm = new RuleBasedMark(source);
				this.marking.put(source, rbm);
			}
			rbm.markedHeadVars.put(source, source.getExistentials());
		}
		
		for(Variable v : source.getExistentials()) {
			LinkedList<Tuple<Rule, Rule, PredPosition>> queue = new LinkedList<>();
			
			for(PredPosition pp : source.getHeadPositions(v)) {
				Set<Rule> affected = this.onto.get(pp.getPredicate());
				
				if(affected != null) {
					for(Rule r : affected) {
						queue.add(new Tuple<>(r, source, pp));
					}
				}
			}
			
			while(!queue.isEmpty()) {
				Tuple<Rule, Rule, PredPosition> markpair = queue.poll();
				Rule r = markpair.a;
				Rule _source = markpair.b;
				PredPosition pp = markpair.c;
				
				RuleBasedMark rbm = this.marking.get(r);
				
				if(rbm == null) {
					rbm = new RuleBasedMark(r);
					this.marking.put(r, rbm);
				}
				
				for(PredPosition npp : rbm.add(_source, pp)) {
					Set<Rule> affected = this.onto.get(pp.getPredicate());
					if(affected != null) {
						for(Rule ar : affected) {
							queue.add(new Tuple<>(ar, _source, npp));
						}
					}
				}				
			}
		}
	}
	
	/*
	 * Recursive implementation of marking, make incur stack overflow
	 */
	public void _mark(Rule source) {		
		if(!source.getExistentials().isEmpty()) {
			RuleBasedMark rbm = this.marking.get(source);
			if(rbm == null) {
				rbm = new RuleBasedMark(source);
				this.marking.put(source, rbm);
			}
			rbm.markedHeadVars.put(source, source.getExistentials());
		}
		
		for(Variable v : source.getExistentials()) {
			for(PredPosition pp : source.getHeadPositions(v)) {
				mark(source, pp);
			}
		}
	}
	
	public void mark(Rule source, PredPosition pp) {
		Set<Rule> affected = this.onto.get(pp.getPredicate());
		
		if(affected == null) return;
		
		for(Rule r : affected) {
			mark(r, source, pp);
		}
	}
	
	public void mark(Rule r, Rule source, PredPosition pp) {
		RuleBasedMark rbm = this.marking.get(r);
		
		if(rbm == null) {
			rbm = new RuleBasedMark(r);
			this.marking.put(r, rbm);
		}
		
		for(PredPosition npp : rbm.add(source, pp)) {
			mark(source, npp);
		}
	}
	
	public RuleBasedMark markQueryRule(Rule qr) {
		RuleBasedMark rbm = new RuleBasedMark(qr);
		
		for(Atom a : qr.getBody()) {
			for(Rule r : this.ihs.getRulesByPredicate(a.getPredicate())) {	
				RuleBasedMark rrbm = this.marking.get(r);
				if(rrbm == null) continue;
				
				for(Entry<Rule, Set<Variable>> entry : rrbm.markedHeadVars.entrySet()) {
					for(Variable v : entry.getValue()) {
						for(PredPosition pp : r.getHeadPositions(v)) 
							rbm.add(entry.getKey(), pp);
					}
				}
			}
		}
		
		return rbm;
	}
	
	public BlockRule getBlockRule(Rule r, RuleBasedMark rbm) {
		List<Block> blocks = new LinkedList<>();
		
		if(rbm == null) return new BlockRule(r, blocks);
		
		List<Block> conblocks = rbm.getConBlocks();
		for(Block b : conblocks) {
			Block merge = null;
			Iterator<Block> it = blocks.iterator();
			while(it.hasNext()) {
				Block mb = it.next();
				if(mb.overlap(b)) {
					if(merge == null) {
						merge = mb;
						mb.merge(b);
					}
					else {
						merge.merge(mb);;
						it.remove();
					}
				}				
			}
			if(merge == null) blocks.add(b);
		}
		
		return new BlockRule(r, blocks);
	}
	
	public BlockRule getBlockRule(Rule r) {
		RuleBasedMark rbm = this.marking.get(r);
		return getBlockRule(r, rbm);
	}
	
	public void printMarked() {
		for(Entry<Rule, RuleBasedMark> entry : this.marking.entrySet()) {
			System.out.println(entry.getKey());
			entry.getValue().printMarked();
		}
	}
	

}
