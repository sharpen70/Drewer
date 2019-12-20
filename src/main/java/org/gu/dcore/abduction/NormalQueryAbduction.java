package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.gu.dcore.tuple.Pair;
import org.gu.dcore.utils.Utils;
import org.semanticweb.vlog4j.parser.ParsingException;

public class NormalQueryAbduction extends QueryAbduction {

	public NormalQueryAbduction(List<Rule> onto, ConjunctiveQuery q, DatalogEngine D, Set<Predicate> abdu) {
		super(onto, q, D, abdu);
	}
	
	public List<AtomSet> getExplanations() throws IOException, ParsingException {
		List<AtomSet> result = new LinkedList<>();		
		List<AtomSet> final_set = new LinkedList<>();
		LinkedList<AtomSet> exploration_set = new LinkedList<>();
		
		exploration_set.add(this.query.getBody());
		
		while(!exploration_set.isEmpty()) {
			AtomSet current = exploration_set.poll();
			
			List<Pair<LiftedAtomSet, Map<Term, Term>>> cleaned = atomset_reduce(current);
			List<AtomSet> rewritings = new LinkedList<>();
			
			if(cleaned.isEmpty()) {
				if(allAbducibles(current))
					final_set.add(current);
				rewritings.addAll(rewrite(current));
			}
			else {
				for(Pair<LiftedAtomSet, Map<Term, Term>> la : cleaned) {
					AtomSet as = la.a;
					if(allAbducibles(as))
						final_set.add(as);
					rewritings.addAll(rewrite(as));
				}
			}
			
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
			boolean[] ansVar = getAnswerVariables(a, atomset);
			Column answers = this.store.answerAtomicQuery(a, ansVar);

		}
		
		return result;
	}
	
	private boolean[] getAnswerVariables(Atom a, AtomSet atomset) {
		boolean[] result = new boolean[a.getTerms().size()];
		
		for(int i = 0; i < a.getTerms().size(); i++) {
			Term t = a.getTerm(i);
			if(t instanceof Variable) {
				if(Utils.is_join_variable(a, (Variable)t, atomset))
					result[i] = true;
			}
		}
		
		return result;
	}
	
	private AtomSet rewrite_with_facts(AtomSet atomset, Atom a, Column facts) {
		if(atomset instanceof LiftedAtomSet) {
			Column c = ((LiftedAtomSet) atomset).getColumn();
			int max_jk_size = atomset.getRepConstants().size();
			
			int[] jk1 = new int[max_jk_size];
			int[] jk2 = new int[max_jk_size];
			int m = 0;
			
			for(int i = 0; i < a.getTerms().size(); i++) {
				Term t = a.getTerm(i);
				if(t instanceof RepConstant) {
					jk1[m] = ((RepConstant) t).getValue();
					jk2[m] = i;
					m++;
				}
			}
			
		}
		AtomSet ur = HomoUtils.minus(atomset, new AtomSet(a));
		
		return ur;
	}
}
