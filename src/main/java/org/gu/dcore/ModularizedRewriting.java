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
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Marking;
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
		
		Marking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		List<Block> blocks = rbm.getBlocks();
		List<BlockRule> brs = this.modularizor.getBlockRules(Qr, blocks);
		IndexedBlockRuleSet ibr = this.modularizor.getIndexedBlockOnto();
	
		
		return null;
	}
	
	private List<Rule> backwardChaining(List<BlockRule> qr, List<BlockRule> rs) {
		List<Rule> result = new LinkedList<>();
		IndexedBlockRuleSet ibr = this.modularizor.getIndexedBlockOnto();
		
		Queue<BlockRule> rqueue = new LinkedList<>();
		
		rqueue.addAll(qr);
		
		while(!rqueue.isEmpty()) {
			BlockRule br = rqueue.poll();
			
			for(Block b : br.getBlocks()) {
				for(BlockRule r : ibr.getBlockRule(b)) {
					
				}
			}
			
			for(Atom a : br.getNormalAtoms()) {
				for(BlockRule r : ibr.getNormalRule(a.getPredicate())) {
					if(this.selected.add(r)) {
						result.add(r);
						rqueue.add(r);
					}
				}
			}
		}
		
		
		return null;
	}
	
	private List<Rule> rewriteBlock(Block b) {
		if(this.blockMap == null) this.blockMap = new HashMap<>();
		List<Rule> computed = this.blockMap.get(b);
		if(computed != null) return computed;
		
		List<Rule> result = new LinkedList<>();
		
		while()
		
		this.blockMap.put(b, result);
		return result;
	}
	
	
}
