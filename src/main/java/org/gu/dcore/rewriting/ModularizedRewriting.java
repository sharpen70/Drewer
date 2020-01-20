package org.gu.dcore.rewriting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.gu.dcore.reasoning.AggregateUnifier;
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
		List<Rule> single_piece_rules = compute_single_rules(onto);
		this.modularizor = new Modularizor(single_piece_rules);
//		this.modularizor = new Modularizor(onto);
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
//					List<AggregateUnifier> unifiers = Unify.getAggregatedPieceUnifier(t.c, t.b, hr, restricted_var);
					
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
						    AtomSet rew_bbody = u.getImageOf(t.c, false);
						    
						    AtomSet rew_hbody = u.getImageOf(hr.getBody(), true);
							AtomSet rew_current_target = u.getImageOf(current_target, true);
				
							AtomSet up = u.getImageOfPiece();
						
							rew_bbody = HomoUtils.minus(rew_bbody, up);
							AtomSet a_rewriting = HomoUtils.simple_union(rew_bbody, rew_hbody);
							AtomSet rewriting = HomoUtils.simple_union(rew_bbody, rew_current_target);
							
							AtomSet o_rewriting = Utils.rewrite(t.b, hr.getBody(), u);
							
							if(ex_rew)  {
								boolean subsumed = false;
								//Remove redundant rewritings
								Iterator<AtomSet> it = rewrited.iterator();
								while(it.hasNext()) {
									AtomSet rew = it.next();
									
									if(Utils.isMoreGeneral(rew, a_rewriting, true)) {
										subsumed = true; break;
									}
									if(Utils.isMoreGeneral(a_rewriting, rew, true)) {
										it.remove();
									}
								}
								if(!subsumed) {
									rewrited.add(a_rewriting);
								}
								else continue;
							}							
							
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
								System.out.println(rw_rule);
								result.add(rw_rule);
							}
								
							if(!current_target.isEmpty()) {
								AtomSet rewrited_target;
								if(ex_rew) rewrited_target = new AtomSet();
								else {
									rewrited_target = new AtomSet(t.e);
									rewrited_target.addAll(rew_current_target);
								}
								System.out.println(rewriting);
								queue.add(new Tuple5<>(rw_t, o_rewriting, rewriting, uc, rewrited_target));
							}	
						}	
					}
				}
			}
		}
	}
	
//	public List<Rule> rewrite(ConjunctiveQuery q) {
//		this.selected = new HashSet<>();
//		PredicateFactory.instance().rewrite_reset();
//		
//		Rule Qr = RuleFactory.instance().createQueryRule(q);
//		
//		BaseMarking marking = this.modularizor.getMarking();
//		RuleBasedMark rbm = marking.markQueryRule(Qr);
//		
//		BlockRule bQr = marking.getBlockRule(Qr, rbm);
//	
//		List<Rule> result = new LinkedList<>();
//		Queue<BlockRule> rewQueue = new LinkedList<>();
//		rewQueue.add(bQr);
//		
//		boolean first = true;
//		
//		while(!rewQueue.isEmpty()) {
//			BlockRule r = rewQueue.poll();
//			
//			if(!selected.add(r.getRuleIndex())) continue;
//		
//			AtomSet body = new AtomSet();
//			
//			for(Block b : r.getBlocks()) {
//				body.add(createBlockAtom(b));
//				rewriteBlock(r, b, true, result, rewQueue);
//			}
//			for(Atom a : r.getNormalAtoms()) {
//				if(a.getPredicate().getName().equals("ANS")) continue;
//				
//				Set<BlockRule> brs = this.ibr.getRules(a.getPredicate());
//				for(BlockRule nr : brs) {
//					if(!nr.isExRule()) rewQueue.add(nr);
//				}
//				body.add(a);
//			}
//			
//			if(!r.isNormalRule() || first)
//				result.add(RuleFactory.instance().createRule(r.getHead(), body));
//			else 
//				result.add(r);
//			
//			first = false;
//		}
//		
//		return result;
//	}
//	
//	/*
//	 * restricted, whether to consider the variable in the rule head
//	 */
//	private void rewriteBlock(BlockRule blockRule, Block block, boolean restricted, List<Rule> result, Queue<BlockRule> rewQueue) {
//		Queue<Tuple<BlockRule, Block, Boolean>> bqueue = new LinkedList<>();
//		
//		bqueue.add(new Tuple<>(blockRule, block, restricted));
//		
//		while(!bqueue.isEmpty()) {
//			Tuple<BlockRule, Block, Boolean> br_b = bqueue.poll();
//			BlockRule br = br_b.a;
//			Block b = br_b.b;
//			
//			Atom blockAtom = createBlockAtom(b);
//			AtomSet na = new AtomSet(b.getBricks());
//			
//			Rule init_rule = RuleFactory.instance().createRule(new AtomSet(blockAtom), na);
//			result.add(init_rule);
//			
//			Queue<Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, Set<Integer>>> queue = new LinkedList<>();
//			queue.add(new Tuple5<>(blockAtom.getTerms(), br.getBody(), na, new AtomSet(), new HashSet<>()));
////			Queue<Tuple4<ArrayList<Term>, AtomSet, AtomSet, AtomSet>> queue = new LinkedList<>();
////			queue.add(new Tuple4<>(blockAtom.getTerms(), br.getBody(), na, new AtomSet()));
//
//			
//			List<AtomSet> rewrited = new LinkedList<>();
//			rewrited.add(br.getBody());
//			
//			while(!queue.isEmpty()) {
//				Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, Set<Integer>> t = queue.poll();	
////				Tuple4<ArrayList<Term>, AtomSet, AtomSet, AtomSet> t = queue.poll();	
//				
//				AtomSet rewrite_target = t.c;
//				Set<BlockRule> rs = this.ibr.getRules(rewrite_target);
//				
//				for(BlockRule hr : rs) {
//					Set<Integer> processed = new HashSet<>(t.e);
//					Set<Variable> restricted_var = br_b.c ? blockRule.getFrontierVariables() : new HashSet<>();
//					List<SinglePieceUnifier> unifiers = Unify.getSinglePieceUnifiers(t.c, t.b, hr, restricted_var);
//					List<Pair<SinglePieceUnifier, AtomSet>> available_unifiers = new LinkedList<>();
//						
//					for(SinglePieceUnifier u : unifiers) {
//						AtomSet rewriting = Utils.rewrite(t.b, hr.getBody(), u);
////						System.out.println(rewriting);
////						AtomSet rewriting = new AtomSet();
//
//						if(hr.isExRule())  {
//							boolean subsumed = false;
//							//Remove redundant rewritings
//							Iterator<AtomSet> it = rewrited.iterator();
//							while(it.hasNext()) {
//								AtomSet rew = it.next();
//								
//								if(Utils.isMoreGeneral(rew, rewriting, true)) {
//									subsumed = true; break;
//								}
//								if(Utils.isMoreGeneral(rewriting, rew, true)) {
//									it.remove();
//								}
//							}
//							if(!subsumed) {
//								available_unifiers.add(new Pair<>(u, rewriting));
//								rewrited.add(rewriting);
//							}
//						}
//						else available_unifiers.add(new Pair<>(u, rewriting));
//					}	
//			
//					if(!available_unifiers.isEmpty()) {
//						AtomSet current_target = new AtomSet();
//						
//						AtomSet tails = new AtomSet();
//						
//						for(Block hb : hr.getPassBlocks()) {
//							current_target.addAll(hb.getBricks());
//						}
//						
//						for(Block hb : hr.getMblocks()) {
//							tails.add(createBlockAtom(hb));
//							bqueue.add(new Tuple<>(hr, hb, false));
//						}
//						
//						for(Atom a : hr.getNormalAtoms()) {
//							Set<BlockRule> brs = this.ibr.getRules(a.getPredicate());
//							if(brs != null) rewQueue.addAll(brs);
//	
//						}
//						tails.addAll(hr.getNormalAtoms());		
//						
//						if(!hr.isExRule() ) {
//							if(processed.add(hr.getRuleIndex())) {
//								if(selected.add(hr.getRuleIndex())) {
//									if(hr.getMblocks().isEmpty()) result.add(hr);
//									else {
//										Rule n_rule = RuleFactory.instance().
//											createRule(hr.getHead(), current_target, tails);
//										result.add(n_rule);
//									}
//								}
//							}
//							else continue;
//						}
//						
//						for(Pair<SinglePieceUnifier, AtomSet> p : available_unifiers) {
//							SinglePieceUnifier u = p.a;
//							AtomSet rewriting = Utils.rewrite(t.c, current_target, u);
//						
//							Set<Term> eliminated = new HashSet<>();
//							for(Variable v : hr.getExistentials()) {
//								eliminated.add(u.getImageOf(v, true));
//							}
//							
//							ArrayList<Term> rw_t = new ArrayList<>();
//							for(int i = 0; i < t.a.size(); i++) {
//								Term _t = t.a.get(i);
//								if(eliminated.contains(_t)) rw_t.add(blank);
//								else rw_t.add(u.getImageOf(_t, false));
//							}
//	
//							AtomSet uc = u.getImageOf(tails, true);
//							
//							uc.addAll(t.d);
//							
//							if(hr.isExRule()) {
//								Atom newhead =  AtomFactory.instance().createAtom(blockAtom.getPredicate(), rw_t);
//								Rule rw_rule = RuleFactory.instance().createRule(new AtomSet(newhead), rewriting, uc);
//								result.add(rw_rule);
//							}
//								
//							if(!current_target.isEmpty()) {
//								queue.add(new Tuple5<>(rw_t, p.b, rewriting, uc, processed));
//							}	
//						}	
//					}
//				}
//			}
//		}
//	}
//	
	private List<Rule> compute_single_rules(List<Rule> rs) {
		List<Rule> result = new LinkedList<>();
		
		for(Rule r : rs) {
			Set<Variable> ex_vars = r.getExistentials();
			
			if(r.getHead().size() == 1) {
				result.add(r);
				continue;
			}
			
			LinkedList<Atom> eatom = new LinkedList<>();
			for(Atom a : r.getHead()) eatom.add(a);
			
			while(!eatom.isEmpty()) {
				Set<Term> join_ev = new HashSet<>();
				Atom p = eatom.poll();
				AtomSet piece = new AtomSet(p);
				
				for(Term t : p.getTerms()) {
					if(ex_vars.contains(t)) {
						join_ev.add(t);
					}
				}
				
				if(join_ev.isEmpty()) {
					result.add(RuleFactory.instance().createRule(piece, r.getBody()));
					continue;
				}
				
				Iterator<Atom> it = eatom.iterator();
				while(it.hasNext()) {
					Atom cp = it.next();
					Set<Term> ev = new HashSet<>();
					for(Term t : cp.getTerms()) {
						if(ex_vars.contains(t)) {
							ev.add(t);
						}
					}
					if(!Collections.disjoint(join_ev, ev)) {
						join_ev.addAll(ev);
						piece.add(cp);
						it.remove();
					}
				}
				
				if(piece.size() == r.getHead().size()) result.add(r);
				else {
					result.add(RuleFactory.instance().createRule(piece, r.getBody()));
				}
			}			
		}
		return result;
	}
	
	private Atom createBlockAtom(Block b) {
		Set<Term> variables = b.getVariables();
		ArrayList<Term> atom_t = new ArrayList<>(variables);;
		
		Predicate blockPred = PredicateFactory.instance().createBlockPredicate(b.getBlockName(), atom_t.size());
		Atom blockAtom = AtomFactory.instance().createAtom(blockPred, variables);	
		
		return blockAtom;
	}
}
