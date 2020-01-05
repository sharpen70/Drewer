package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gu.dcore.grd.IndexedByHeadPredRuleSet;
import org.gu.dcore.homomorphism.HomoUtils;
import org.gu.dcore.model.Atom;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.LiftedAtomSet;
import org.gu.dcore.model.Predicate;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Rule;
import org.gu.dcore.model.Term;
import org.gu.dcore.model.Variable;
import org.gu.dcore.reasoning.FreshIndividualSubstitution;
import org.gu.dcore.store.Column;
import org.gu.dcore.store.DatalogEngine;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class NormalQueryAbduction extends QueryAbduction {

	public NormalQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
		this.irs = new IndexedByHeadPredRuleSet(onto);
	}
	
	public List<AtomSet> getExplanations() throws IOException, ParsingException {
		List<AtomSet> result = new LinkedList<>();		
		List<AtomSet> final_set = new LinkedList<>();
		LinkedList<AtomSet> exploration_set = new LinkedList<>();
		
		exploration_set.add(this.query.getBody());
		
		while(!exploration_set.isEmpty()) {
			AtomSet current = exploration_set.poll();
			
			if(allAbducibles(current)) final_set.add(current);
		
			List<AtomSet> rewritings = new LinkedList<>();
			
			rewritings.addAll(rewrite_with_data(current));
			rewritings.addAll(rewrite(current));
			
			Utils.removeSubsumed(rewritings, final_set, true);
			Utils.removeSubsumed(rewritings, exploration_set, true);
			Utils.removeSubsumed(exploration_set, rewritings, true);
			Utils.removeSubsumed(final_set, rewritings, true);
			
			exploration_set.addAll(rewritings);
		}
		
		for(AtomSet atomset : final_set) {
			FreshIndividualSubstitution sub = new FreshIndividualSubstitution();
			result.add(sub.imageOf(atomset));
		}
		
		return result;
	}
	
	private List<AtomSet> rewrite_with_data(AtomSet atomset) throws ParsingException, IOException {
		List<AtomSet> result = new LinkedList<>();
		
		for(Atom a : atomset) {
			AtomSet r = rewrite_with_facts(atomset, a);
			if(r != null) result.add(r);
		}
		
		return result;
	}
	
	private AtomSet rewrite_with_facts(AtomSet atomset, Atom a) throws ParsingException, IOException {
		AtomSet ur = HomoUtils.minus(atomset, new AtomSet(a));
		
		Map<Term, Term> vr_map = new HashMap<>();
		int m = atomset.getRepConstants().size();
		int n = 0;
		ArrayList<Integer> mask = new ArrayList<>();
		Set<Term> reserve = new HashSet<>();
		
		for(int i = 0; i < a.getTerms().size(); i++) {
			Term t = a.getTerm(i);
			if(t instanceof Variable) {
				if(reserve.add(t)) {
					if(Utils.is_join_variable(a, (Variable)t, atomset)) {
						mask.add(n++);
						vr_map.put(t, new RepConstant(m++));
					}
					else n++;
				}				
			}
			else if(t instanceof RepConstant) {
				if(reserve.add(t)) mask.add(n++);
			}
			else n++;
		}
		
		Column answers = this.store.answerAtomicQuery(a, mask);
		
		if(answers.size() == 0) return null;
		
		if(atomset instanceof LiftedAtomSet) {
			Column c = ((LiftedAtomSet) atomset).getColumn();
			int max_jk_size = atomset.getRepConstants().size();
			
			int[] jk1 = new int[max_jk_size];
			int[] jk2 = new int[max_jk_size];
			int l = 0;
			
			for(int i = 0; i < a.getTerms().size(); i++) {
				Term t = a.getTerm(i);
				if(t instanceof RepConstant) {
					jk1[l] = ((RepConstant) t).getValue();
					jk2[l] = i;
					l++;
				}
			}
			Column nc = c.join(answers, jk1, jk2, l).a;
			ur = Utils.substitute(ur, vr_map);
			ur = new LiftedAtomSet(ur, nc);
		}
		else {
			ur = Utils.substitute(ur, vr_map);
			ur = new LiftedAtomSet(ur, answers);
		}
			
		return ur;
	}
}
