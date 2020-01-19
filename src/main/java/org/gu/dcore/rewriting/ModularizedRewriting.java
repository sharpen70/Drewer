package org.gu.dcore.rewriting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.gu.dcore.factories.AtomFactory;
import org.gu.dcore.factories.PredicateFactory;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.factories.TermFactory;
import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.modularization.BaseMarking;
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.modularization.RuleBasedMark;
import org.gu.dcore.reasoning.SinglePieceUnifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.tuple.Tuple;
import org.gu.dcore.tuple.Tuple5;
import org.gu.dcore.utils.Utils;

public class ModularizedRewriting {
	private Modularizor modularizor;
	private IndexedBlockRuleSet ibr;
	
	private final Constant blank = TermFactory.instance().createConstant("BLANK");
	private Set<Integer> selected;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.modularizor = new Modularizor(onto);
		this.modularizor.modularize();
		this.ibr = this.modularizor.getIndexedBlockOnto();		
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {
		this.selected = new HashSet<>();
		PredicateFactory.instance().rewrite_reset();
		
		Rule Qr = RuleFactory.instance().createQueryRule(q);
		
		BaseMarking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		BlockRule bQr = marking.getBlockRule(Qr, rbm);
	
		List<Rule> result = new LinkedList<>();
		Queue<BlockRule> rewQueue = new LinkedList<>();
		rewQueue.add(bQr);
		
		boolean first = true;
		
		while(!rewQueue.isEmpty()) {
			BlockRule r = rewQueue.poll();
			
			if(!selected.add(r.getRuleIndex())) continue;
		
			AtomSet body = new AtomSet();
			
			for(Block b : r.getBlocks()) {
				body.add(createBlockAtom(b));
				rewriteBlock(r, b, true, result, rewQueue);
			}
			for(Atom a : r.getNormalAtoms()) {
				if(a.getPredicate().getName().equals("ANS")) continue;
				
				Set<BlockRule> brs = this.ibr.getRules(a.getPredicate());
				for(BlockRule nr : brs) {
					if(!nr.isExRule()) rewQueue.add(nr);
				}
				body.add(a);
			}
			
			if(!r.isNormalRule() || first)
				result.add(RuleFactory.instance().createRule(r.getHead(), body));
			else 
				result.add(r);
			
			first = false;
		}
		
		return result;
	}
	
	/*
	 * restricted, whether to consider the variable in the rule head
	 */
	private void rewriteBlock(BlockRule blockRule, Block block, boolean restricted, List<Rule> result, Queue<BlockRule> rewQueue) {
		Queue<Tuple<BlockRule, Block, Boolean>> bqueue = new LinkedList<>();
		
		bqueue.add(new Tuple<>(blockRule, block, restricted));
		
		while(!bqueue.isEmpty()) {
			Tuple<BlockRule, Block, Boolean> br_b = bqueue.poll();
			BlockRule br = br_b.a;
			Block b = br_b.b;
			
			Atom blockAtom = createBlockAtom(b);
			AtomSet na = new AtomSet(b.getBricks());
			
			Rule init_rule = RuleFactory.instance().createRule(new AtomSet(blockAtom), na);
			result.add(init_rule);
			
			Queue<Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, AtomSet>> queue = new LinkedList<>();
			queue.add(new Tuple5<>(blockAtom.getTerms(), br.getBody(), na, new AtomSet(), new AtomSet()));
			
			List<AtomSet> rewrited = new LinkedList<>();
			
			while(!queue.isEmpty()) {
				Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, AtomSet> t = queue.poll();	
				
				AtomSet rewrite_target = t.c;
				Set<BlockRule> rs = this.ibr.getRules(rewrite_target);
				
				for(BlockRule hr : rs) {
					Set<Variable> restricted_var = br_b.c ? blockRule.getFrontierVariables() : new HashSet<>();
					List<SinglePieceUnifier> unifiers = Unify.getSinglePieceUnifiers(t.c, t.b, hr, restricted_var);
			
					if(!unifiers.isEmpty()) {
						boolean ex_rew = hr.isExRule();
						
						AtomSet current_target = new AtomSet();
						
						AtomSet tails = new AtomSet();
						
						for(Block hb : hr.getPassBlocks()) {
							current_target.addAll(hb.getBricks());
						}
						
						for(Block hb : hr.getMblocks()) {
							tails.add(createBlockAtom(hb));
							bqueue.add(new Tuple<>(hr, hb, false));
						}
						
						for(Atom a : hr.getNormalAtoms()) {
							Set<BlockRule> brs = this.ibr.getRules(a.getPredicate());
							if(brs != null) rewQueue.addAll(brs);
	
						}
						tails.addAll(hr.getNormalAtoms());					

						if(!ex_rew && selected.add(hr.getRuleIndex())) {
							if(hr.getMblocks().isEmpty()) result.add(hr);
							else {
								Rule n_rule = RuleFactory.instance().
									createRule(hr.getHead(), current_target, tails);
								result.add(n_rule);
							}
						}
						
						for(SinglePieceUnifier u : unifiers) {
						    AtomSet rewriting = u.getImageOf(t.c, false);		
							AtomSet rew_body = u.getImageOf(current_target, true);	
							AtomSet up = u.getImageOfPiece();
						
							rewriting = HomoUtils.minus(rewriting, up);
							rewriting = HomoUtils.simple_union(rewriting, rew_body);
							
							if(ex_rew)  {
								boolean subsumed = false;
								//Remove redundant rewritings
								Iterator<AtomSet> it = rewrited.iterator();
								while(it.hasNext()) {
									AtomSet rew = it.next();
									
									if(Utils.isMoreGeneral(rew, rewriting, true)) {
										subsumed = true; break;
									}
									if(Utils.isMoreGeneral(rewriting, rew, true)) {
										it.remove();
									}
								}
								if(!subsumed) {
									rewrited.add(rewriting);
								}
								else continue;
							}
							
							AtomSet o_rewriting = Utils.rewrite(t.b, hr.getBody(), u);
							
							Set<Term> eliminated = new HashSet<>();
							for(Variable v : hr.getExistentials()) {
								eliminated.add(u.getImageOf(v, true));
							}
							
							ArrayList<Term> rw_t = new ArrayList<>();
							for(int i = 0; i < t.a.size(); i++) {
								Term _t = t.a.get(i);
								if(eliminated.contains(_t)) rw_t.add(blank);
								else rw_t.add(u.getImageOf(_t, false));
							}
	
							AtomSet uc = u.getImageOf(tails, true);
							
							uc.addAll(t.d);
							
							if(ex_rew) {
								if(!t.e.isEmpty()) {
									boolean all_hit = true;
									for(Atom a : t.e) {
										if(!u.getB().contains(a)) {
											all_hit = false;
											break;
										}
									}
									if(!all_hit) continue;
								}
								Atom newhead =  AtomFactory.instance().createAtom(blockAtom.getPredicate(), rw_t);
								Rule rw_rule = RuleFactory.instance().createRule(new AtomSet(newhead), rewriting, uc);
								result.add(rw_rule);
							}
								
							if(!current_target.isEmpty()) {
								AtomSet rewrited_target;
								if(ex_rew) rewrited_target = new AtomSet();
								else {
									rewrited_target = new AtomSet(t.e);
									rewrited_target.addAll(rew_body);
								}
								queue.add(new Tuple5<>(rw_t, o_rewriting, rewriting, uc, rewrited_target));
							}	
						}	
					}
				}
			}
		}
	}
	
	private Atom createBlockAtom(Block b) {
		Set<Term> variables = b.getVariables();
		ArrayList<Term> atom_t = new ArrayList<>(variables);;
		
		Predicate blockPred = PredicateFactory.instance().createBlockPredicate(b.getBlockName(), atom_t.size());
		Atom blockAtom = AtomFactory.instance().createAtom(blockPred, variables);	
		
		return blockAtom;
	}
}
