package org.gu.dcore;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.antlr.v4.runtime.misc.Triple;
import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.modularization.BaseMarking;
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.modularization.RuleBasedMark;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;

public class ModularizedRewriting {
	List<Rule> ruleset;
	List<BlockRule> blockRuleset;
	Modularizor modularizor;
	IndexedBlockRuleSet ibr;
	Set<Rule> selected;
	Map<Block, List<Rule>> blockMap;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.ruleset = onto;
		this.modularizor = new Modularizor(onto);
		this.modularizor.modularize();
		this.ibr = this.modularizor.getIndexedBlockOnto();
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {
		this.selected = new HashSet<>();
		
		Predicate Q = PredicateFactory.instance().createPredicate("Q", q.getAnsVar().size());
		Atom Qhead = AtomFactory.instance().createAtom(Q, q.getAnsVar());
		Rule Qr = RuleFactory.instance().createRule(new AtomSet(Qhead), new AtomSet(q.getBody()));
		
		BaseMarking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		BlockRule brs = marking.getBlockRule(Qr, rbm);
	
		
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

				}
		}
		
		
		return result;
	}
	
	private List<Atom> rewriteBlock(BlockRule br, Block b, List<Rule> result, Queue<Rule> normalRuleQueue) {
		Set<Term> variables = b.getVariables();
		Predicate init_predicate = PredicateFactory.instance().createBlockPredicate(variables.size());
		Atom init_head = AtomFactory.instance().createAtom(init_predicate, variables);
		Rule init_rule = RuleFactory.instance().createRule(new AtomSet(init_head), new AtomSet(b.getBricks()));
		
		List<Atom> result_head = new LinkedList<>();
		
		result_head.add(init_head);
		
		Queue<Triple<Atom, AtomSet, AtomSet>> queue = new LinkedList<>();
		queue.add(new Triple<>(init_head, new AtomSet(b.getBricks()), new AtomSet()));
		
		while(!queue.isEmpty()) {
			Triple<Atom, AtomSet, AtomSet> t = queue.poll();
			Set<BlockRule> rs = this.ibr.getRules(t.b);
			for(BlockRule hr : rs) {
				List<Unifier> unifiers = Unify.getSinglePieceUnifier(t.b, br, hr);
				
				if(!unifiers.isEmpty()) {
					List<List<Atom>> tails = new LinkedList<>();
					for(Block hb : hr.getBlocks()) {
						if(!b.related(hb)) {
							tails.add(rewriteBlock(hr, hb, result, normalRuleQueue));
						}
					}
					for(Atom a : hr.getNormalAtoms()) {
						for(BlockRule nr : this.ibr.getNormalRules(a.getPredicate())) {
							if(this.selected.add(nr)) {
								result.add(nr);
								normalRuleQueue.add(nr);
							}
						}
					}
					List<AtomSet> ct = combine(tails);
					for(AtomSet c : ct) c.addAll(hr.getNormalAtoms());
					
					if(ct.isEmpty()) ct.add(new AtomSet());
					
					if(hr.isExRule()) {
						
					}
					else {
						              
					}
				}
			}

		}

		
		
		
		return result_head;
	}
	
	private List<AtomSet> combine(List<List<Atom>> atomlists) {
		LinkedList<AtomSet> as = new LinkedList<>();
		
		Iterator<List<Atom>> it = atomlists.iterator();
		
		if(!it.hasNext()) return as;
		
		for(Atom a : it.next()) {
			as.add(new AtomSet(a));
		}
		
		while(it.hasNext()) {
			List<Atom> list = it.next();
			AtomSet s = as.poll();
			for(Atom a : list) {
				AtomSet ns = new AtomSet(s);
				ns.add(a);
				as.add(ns);
			}
		}
		
		return as;
	}
	
}
