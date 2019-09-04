package org.gu.dcore;

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
import org.gu.dcore.homomorphism.Homomorphism;
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
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;
import org.gu.dcore.utils.Pair;

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
//			if(!first && r.isNormalRule()) {
//				if(selected.add(r)) {
//					result.add(r);
//				}
//				else continue;
//			}
//			
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
			
			Queue<Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, Set<Integer>>> queue = new LinkedList<>();
			queue.add(new Tuple5<>(blockAtom.getTerms(), br.getBody(), na, new AtomSet(), new HashSet<>()));
//			Queue<Tuple4<ArrayList<Term>, AtomSet, AtomSet, AtomSet>> queue = new LinkedList<>();
//			queue.add(new Tuple4<>(blockAtom.getTerms(), br.getBody(), na, new AtomSet()));

			
			List<AtomSet> rewrited = new LinkedList<>();
			rewrited.add(br.getBody());
			
			while(!queue.isEmpty()) {
				Tuple5<ArrayList<Term>, AtomSet, AtomSet, AtomSet, Set<Integer>> t = queue.poll();	
//				Tuple4<ArrayList<Term>, AtomSet, AtomSet, AtomSet> t = queue.poll();	
				
				AtomSet rewrite_target = t.c;
				Set<BlockRule> rs = this.ibr.getRules(rewrite_target);
				
				for(BlockRule hr : rs) {
					Set<Integer> processed = new HashSet<>(t.e);
					Set<Variable> restricted_var = br_b.c ? blockRule.getFrontierVariables() : new HashSet<>();
					List<Unifier> unifiers = Unify.getSinglePieceUnifier(t.c, t.b, hr, restricted_var);
					List<Pair<Unifier, AtomSet>> available_unifiers = new LinkedList<>();
						
					for(Unifier u : unifiers) {
						AtomSet rewriting = rewrite(t.b, hr.getBody(), u);
//						System.out.println(rewriting);
//						AtomSet rewriting = new AtomSet();

						if(hr.isExRule())  {
							boolean subsumed = false;
							//Remove redundant rewritings
							Iterator<AtomSet> it = rewrited.iterator();
							while(it.hasNext()) {
								AtomSet rew = it.next();
								
								if(isMoreGeneral(rew, rewriting)) {
									subsumed = true; break;
								}
								if(isMoreGeneral(rewriting, rew)) {
									it.remove();
								}
							}
							if(!subsumed) {
								available_unifiers.add(new Pair<>(u, rewriting));
								rewrited.add(rewriting);
							}
						}
						else available_unifiers.add(new Pair<>(u, rewriting));
					}	
			
					if(!available_unifiers.isEmpty()) {
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
						
						if(!hr.isExRule() ) {
							if(processed.add(hr.getRuleIndex())) {
								if(selected.add(hr.getRuleIndex())) {
									if(hr.getMblocks().isEmpty()) result.add(hr);
									else {
										Rule n_rule = RuleFactory.instance().
											createRule(hr.getHead(), current_target, tails);
										result.add(n_rule);
									}
								}
							}
							else continue;
						}
						
						for(Pair<Unifier, AtomSet> p : available_unifiers) {
							Unifier u = p.a;
							AtomSet rewriting = rewrite(t.c, current_target, u);
						
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
							
							if(hr.isExRule()) {
								Atom newhead =  AtomFactory.instance().createAtom(blockAtom.getPredicate(), rw_t);
								Rule rw_rule = RuleFactory.instance().createRule(new AtomSet(newhead), rewriting, uc);
								result.add(rw_rule);
							}
								
							if(!current_target.isEmpty()) {
								queue.add(new Tuple5<>(rw_t, p.b, rewriting, uc, processed));
							}	
						}	
					}
				}
			}
		}
	}
	
	public boolean source_related(Set<Rule> s1, Collection<Rule> s2) {
		for(Rule r : s2) {
			if(s1.contains(r)) return true;
		}
		return false;
	}
	
	/*
	 * f the set of atoms being replaced
	 * b the set of atoms to replace
	 * u the unifier for replacing
	 */
	private AtomSet rewrite(AtomSet f, AtomSet b, Unifier u) {
		AtomSet uf = u.getImageOf(f, false);
		AtomSet ub = u.getImageOf(b, true);
		
		AtomSet up = u.getImageOfPiece();
		
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);
		
		return uf;
	}
	
	private Atom createBlockAtom(Block b) {
		Set<Term> variables = b.getVariables();
		ArrayList<Term> atom_t = new ArrayList<>(variables);;
		
		Predicate blockPred = PredicateFactory.instance().createBlockPredicate(b.getBlockName(), atom_t.size());
		Atom blockAtom = AtomFactory.instance().createAtom(blockPred, variables);	
		
		return blockAtom;
	}
	
	private boolean isMoreGeneral(AtomSet f, AtomSet h) {
		if (HomoUtils.contains(f, h)) 
			return true;
		else 
			return new Homomorphism(f, h).exist();		
	}
	
	private final class Tuple<T1, T2, T3> {
		public T1 a;
		public T2 b;
		public T3 c;
		
		Tuple(T1 a, T2 b, T3 c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}
	
//	private final class Tuple4<T1, T2, T3, T4> {
//		public T1 a;
//		public T2 b;
//		public T3 c;
//		public T4 d;
//		
//		Tuple4(T1 a, T2 b, T3 c, T4 d) {
//			this.a = a;
//			this.b = b;
//			this.c = c;
//			this.d = d;
//		}
//	}
	
	private final class Tuple5<T1, T2, T3, T4, T5> {
		public T1 a;
		public T2 b;
		public T3 c;
		public T4 d;
		public T5 e;
		
		Tuple5(T1 a, T2 b, T3 c, T4 d, T5 e) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
		}
	}
	
}
