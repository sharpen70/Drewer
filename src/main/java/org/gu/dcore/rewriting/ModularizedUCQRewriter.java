package org.gu.dcore.rewriting;

import java.util.LinkedList;
import java.util.List;

import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.BaseMarking;
import org.gu.dcore.modularization.BlockRule;
import org.gu.dcore.modularization.Modularizor;
import org.gu.dcore.modularization.RuleBasedMark;

public class ModularizedUCQRewriter {
	Modularizor modularizor;
	private IndexedBlockRuleSet ibr;
	
	public ModularizedUCQRewriter(List<Rule> onto) {
		List<Rule> single_piece_rules = RewriteUtils.compute_single_rules(onto);
		
		this.modularizor = new Modularizor(single_piece_rules);
		this.modularizor.modularize();
		this.ibr = this.modularizor.getIndexedBlockOnto();	
	}
	
	public List<AtomSet> getUCQRewriting(ConjunctiveQuery q) {
		Rule Qr = RuleFactory.instance().createQueryRule(q);
		
		BaseMarking marking = this.modularizor.getMarking();
		RuleBasedMark rbm = marking.markQueryRule(Qr);
		
		BlockRule bQr = marking.getBlockRule(Qr, rbm);
		
		List<AtomSet> rewritings = new LinkedList<>();
		List<AtomSet> exploration = new LinkedList<>();
		
//		exploration.add(arg0)
		return null;
	}
}
