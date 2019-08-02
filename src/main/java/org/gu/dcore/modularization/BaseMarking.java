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

public class BaseMarking implements Marking {
	private Map<Rule, RuleBasedMark> marking; 
	private IndexedByBodyPredRuleSet onto;
	private IndexedByHeadPredRuleSet ihs;
	
	public BaseMarking(List<Rule> ruleset) {
		this.marking = new HashMap<>();
		this.onto = new IndexedByBodyPredRuleSet(ruleset);
		this.ihs = new IndexedByHeadPredRuleSet(ruleset);
	}
	
	public void mark(Rule source) {
		for(Variable v : source.getExistentials()) {
			for(PredPosition pp : source.getHeadPositions(v)) {
				mark(source, pp);
			}
		}
	}
	
	public void mark(Rule source, PredPosition pp) {
		List<Rule> affected = this.onto.get(pp.getPredicate());
		
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
			List<Rule> rs = this.ihs.get(a.getPredicate());
			for(Rule r : rs) {	
				RuleBasedMark rrbm = this.marking.get(r);
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
		List<Rule> passSources = new LinkedList<>();
		
		for(Entry<Rule, Set<Variable>> entry : 
			rbm.markedHeadVars.entrySet()) {
			if(!entry.getValue().isEmpty()) passSources.add(entry.getKey());
		}
		
		List<Block> blocks = new LinkedList<>();
		
		for(Block b : rbm.getBaseBlocks()) {
			Block merge = null;
			Iterator<Block> it = blocks.iterator();
			while(it.hasNext()) {
				Block mb = it.next();
				if(mb.overlap(b)) {
					if(merge == null) merge = new Block(mb, b);
					else {
						merge = new Block(merge, mb);
						it.remove();
					}
				}				
			}
			if(merge == null) blocks.add(b);
		}
		
		return new BlockRule(r, blocks, passSources);
	}
	
	public BlockRule getBlockRule(Rule r) {
		RuleBasedMark rbm = this.marking.get(r);
		return getBlockRule(r, rbm);
	}
	
	@Override
	public void printMarked() {
		for(Entry<Rule, RuleBasedMark> entry : this.marking.entrySet()) {
			System.out.println(entry.getKey());
			entry.getValue().printMarked();
		}
	}
	

}
