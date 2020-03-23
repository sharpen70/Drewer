package org.gu.dcore.rewriting;

import java.util.List;

import org.gu.dcore.grd.IndexedBlockRuleSet;
import org.gu.dcore.model.AtomSet;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Rule;
import org.gu.dcore.modularization.Modularizor;

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
		return null;
	}
}
