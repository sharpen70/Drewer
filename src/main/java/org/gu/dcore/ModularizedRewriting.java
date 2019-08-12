package org.gu.dcore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.gu.dcore.modularization.BaseMarking;
import org.gu.dcore.modularization.Block;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.modularization.RuleBasedMark;
import org.gu.dcore.reasoning.Unifier;
import org.gu.dcore.reasoning.Unify;

public class ModularizedRewriting {
	private Modularizor modularizor;
	private IndexedBlockRuleSet ibr;
	private Set<Rule> selected;
	
	private final Constant blank = TermFactory.instance().createConstant("BLANK");
	private final String pstring = "EXR";
	
	private Map<BlockRule, BlockRule> selected_exrule;
	
	public ModularizedRewriting(List<Rule> onto) {
		this.modularizor = new Modularizor(onto);
		this.modularizor.modularize();
		this.ibr = this.modularizor.getIndexedBlockOnto();
		
		this.selected = new HashSet<>();
		this.selected_exrule = new HashMap<>();
	}
	
	public List<Rule> rewrite(ConjunctiveQuery q) {
		this.selected = new HashSet<>();
		
		Predicate Q = PredicateFactory.instance().createPredicate("Q", q.getAnsVar().size());
		Atom Qhead = AtomFactory.instance().createAtom(Q, q.getAnsVar());
		Rule Qr = RuleFactory.instance().createRule(new AtomSet(Qhead), new AtomSet(q.getBody()));
		
		BaseMarking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		BlockRule bQr = marking.getBlockRule(Qr, rbm);
	
		List<Rule> result = new LinkedList<>();
		Queue<Rule> normalRuleQueue = new LinkedList<>();
		
		AtomSet body = new AtomSet();
		
		for(Block b : bQr.getBlocks()) {
			body.add(rewriteBlock(bQr, b, result, normalRuleQueue));
		}
		body.addAll(bQr.getNormalAtoms());
		result.add(RuleFactory.instance().createRule(bQr.getHead(), body));
		
		return result;
	}
	
	private Atom rewriteBlock(BlockRule br, Block b, List<Rule> result, Queue<Rule> normalRuleQueue) {
		Set<Term> variables = b.getVariables();
		ArrayList<Term> atom_t = new ArrayList<>(variables);;
		
		Predicate blockPred = PredicateFactory.instance().createPredicate(b.getBlockName(), atom_t.size());
		Atom init_head = AtomFactory.instance().createAtom(blockPred, variables);	
		
		Queue<Tuple4<ArrayList<Term>, AtomSet, AtomSet, Set<Rule>>> queue = new LinkedList<>();
		AtomSet na = new AtomSet(b.getBricks());
		queue.add(new Tuple4<>(atom_t, na, new AtomSet(), b.getSources()));
		
		List<AtomSet> rewrited = new LinkedList<>();
		rewrited.add(na);
		
		while(!queue.isEmpty()) {
			Tuple4<ArrayList<Term>, AtomSet, AtomSet, Set<Rule>> t = queue.poll();	
			
			Set<BlockRule> rs = this.ibr.getRules(t.b);
			for(BlockRule hr : rs) {
				List<Unifier> unifiers = Unify.getSinglePieceUnifier(t.b, br, hr);
				
				if(!unifiers.isEmpty()) {
					Set<Rule> current_sources = new HashSet<>(t.d);
					AtomSet current_target = new AtomSet();
					
					AtomSet tails = new AtomSet();
					
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
					tails.addAll(hr.getNormalAtoms());
					
					if(!hr.isExRule() && this.selected.add(hr)) result.add(hr);
					
					for(Unifier u : unifiers) {
						AtomSet rewriting = rewrite(t.b, current_target, u);
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
							Set<Term> eliminated = u.getImageOf(hr.getExistentials());
							rewrited.add(rewriting);
							
							ArrayList<Term> rw_t = new ArrayList<>();
							for(int i = 0; i < t.a.size(); i++) {
								Term _t = t.a.get(i);
								if(eliminated.contains(_t)) rw_t.set(i, blank);
								else rw_t.set(i, u.getImageOf(_t));
							}

							AtomSet uc = u.getImageOf(tails);
							uc.addAll(t.c);
							if(hr.isExRule()) {
								Atom newhead =  AtomFactory.instance().createAtom(blockPred, rw_t);
								Rule rw_rule = RuleFactory.instance().createRule(new AtomSet(newhead), rewriting, uc);
								result.add(rw_rule);
							}
							if(!current_target.isEmpty()) {
								queue.add(new Tuple4<>(rw_t, rewriting, uc, current_sources));
							}							
						}
						else {
							Predicate newP = PredicateFactory.instance().createPredicate(pstring, hr.getFrontierTerm().size());
							Atom ex_newhead = AtomFactory.instance().createAtom(newP, hr.getFrontierTerm());						
						}
					}
					
				}
			}

		}

		return init_head;
	}
	
	public boolean source_related(Set<Rule> s1, Collection<Rule> s2) {
		for(Rule r : s2) {
			if(s1.contains(r)) return true;
		}
		return false;
	}
	
//	private List<AtomSet> combine(List<List<Atom>> atomlists) {
//		LinkedList<AtomSet> as = new LinkedList<>();
//		
//		Iterator<List<Atom>> it = atomlists.iterator();
//		
//		if(!it.hasNext()) return as;
//		
//		for(Atom a : it.next()) {
//			as.add(new AtomSet(a));
//		}
//		
//		while(it.hasNext()) {
//			List<Atom> list = it.next();
//			AtomSet s = as.poll();
//			for(Atom a : list) {
//				AtomSet ns = new AtomSet(s);
//				ns.add(a);
//				as.add(ns);
//			}
//		}
//		
//		return as;
//	}
	
	/*
	 * f the set of atoms being replaced
	 * b the set of atoms to replace
	 * u the unifier for replacing
	 */
	private AtomSet rewrite(AtomSet f, AtomSet b, Unifier u) {
		AtomSet uf = u.getImageOf(f);
		AtomSet ub = u.getImageOf(b);
		
		AtomSet up = u.getImageOfPiece();
		
		uf = HomoUtils.minus(uf, up);
		uf = HomoUtils.simple_union(uf, ub);
		
		return uf;
	}
	
	private boolean isMoreGeneral(AtomSet f, AtomSet h) {
		if (HomoUtils.contains(f, h)) 
			return true;
		else 
			return new Homomorphism(f, h).exist();		
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
