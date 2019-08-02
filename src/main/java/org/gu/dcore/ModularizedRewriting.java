package org.gu.dcore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BaseMarking;
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.modularization.RuleBasedMark;

public class ModularizedRewriting {
	List<Rule> ruleset;
	List<BlockRule> blockRuleset;
	Modularizor modularizor;

	Set<Rule> selected;
	Map<Block, List<Rule>> blockMap;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.ruleset = onto;
		this.modularizor = new Modularizor(onto);
		this.modularizor.modularize();
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {
		this.selected = new HashSet<>();
		
		Predicate Q = PredicateFactory.instance().createPredicate("Q", q.getAnsVar().size());
		Atom Qhead = AtomFactory.instance().createAtom(Q, q.getAnsVar());
		Rule Qr = RuleFactory.instance().createRule(new AtomSet(Qhead), new AtomSet(q.getBody()));
		
		BaseMarking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		BlockRule brs = marking.getBlockRule(Qr, rbm);
		IndexedBlockRuleSet ibr = this.modularizor.getIndexedBlockOnto();
	
		
		return null;
	}
	
	private List<Rule> backwardChaining(BlockRule qr, List<BlockRule> rs) {
		List<Rule> result = new LinkedList<>();
		IndexedBlockRuleSet ibr = this.modularizor.getIndexedBlockOnto();
		
		Queue<BlockRule> rqueue = new LinkedList<>();
		
		rqueue.add(qr);
		
		while(!rqueue.isEmpty()) {
			BlockRule br = rqueue.poll();
			
		
			for(Atom a : br.getNormalAtoms()) {
				for(BlockRule r : ibr.getNormalRules(a.getPredicate())) {
					if(this.selected.add(r)) {
						result.add(r);
						rqueue.add(r);
					}
				}
			}
			
			if(br.getBlocks().isEmpty()) result.add(br);
			else 
				for(Block b : br.getBlocks()) {
					for(BlockRule r : ibr.getBlockRules(b)) {
						
					}
				}
		}
		
		
		return result;
	}
	
	private List<Rule> rewriteBlock(Block b) {
		if(this.blockMap == null) this.blockMap = new HashMap<>();
		List<Rule> computed = this.blockMap.get(b);
		if(computed != null) return computed;
		
		List<Rule> result = new LinkedList<>();
		
		
		
		this.blockMap.put(b, result);
		return result;
	}
	
	
}
