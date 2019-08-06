package org.gu.dcore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
		
		Queue<Tuple4<Atom, AtomSet, AtomSet, Set<Rule>>> queue = new LinkedList<>();
		AtomSet na = new AtomSet(b.getBricks());
		queue.add(new Tuple4<>(init_head, na, new AtomSet(), b.getSources()));
		
		List<AtomSet> rewrited = new LinkedList<>();
		rewrited.add(na);
		
		while(!queue.isEmpty()) {
			Tuple4<Atom, AtomSet, AtomSet, Set<Rule>> t = queue.poll();	
			
			Set<BlockRule> rs = this.ibr.getRules(t.b);
			for(BlockRule hr : rs) {
				List<Unifier> unifiers = Unify.getSinglePieceUnifier(t.b, br, hr);
				
				if(!unifiers.isEmpty()) {
					Set<Rule> current_sources = new HashSet<>(t.d);
					AtomSet current_target = new AtomSet();
					
					List<List<Atom>> tails = new LinkedList<>();
					
					for(Block hb : hr.getBlocks()) {
						if(!source_related(t.d, hb.getSources())) {
							tails.add(rewriteBlock(hr, hb, result, normalRuleQueue));
						}
						else {
							current_sources.addAll(hb.getSources());
							current_target.addAll(hb.getBricks());
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
					if(ct.isEmpty()) ct.add(new AtomSet());
					for(AtomSet c : ct) c.addAll(hr.getNormalAtoms());
					
					if(!hr.isExRule() && this.selected.add(hr)) result.add(hr);
					
					for(Unifier u : unifiers) {
						AtomSet rewriting = rewrite(t.b, current_target, u);
						boolean subsumed = false;
						for(AtomSet rw : rewrited) {
							if(isMoreGeneral(rw, rewriting)) {
								subsumed = true; break;
							}
						}
						if(!subsumed) {
							rewrited.add(rewriting);
							
							for(AtomSet c : ct) {
								if(hr.isExRule()) {								
									Rule rw_rule = RuleFactory.instance().createRule(new AtomSet(rw_head), rewriting, c);
								}
								if(!current_target.isEmpty()) {
									queue.add(new Tuple4(rw_head, rewriting, c, current_sources));
								}
							}
						}
					}
					
				}
			}

		}

		return result_head;
	}
	
	public boolean source_related(Set<Rule> s1, Collection<Rule> s2) {
		for(Rule r : s2) {
			if(s1.contains(r)) return true;
		}
		return false;
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
	
	private final class Tuple4<T1, T2, T3, T4> {
		public T1 a;
		public T2 b;
		public T3 c;
		public T4 d;
		
		Tuple4(T1 a, T2 b, T3 c, T4 d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}
	
}
