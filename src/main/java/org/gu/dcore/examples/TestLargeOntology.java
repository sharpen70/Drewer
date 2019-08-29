package org.gu.dcore.examples;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.ModularizedRewriting;
import org.gu.dcore.factories.RuleFactory;
import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;

public class TestLargeOntology 
{	
//	public void testApp() throws IOException
//	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#has_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#CHEBI_23367>(Y).");
//    	
//    	System.out.println("============");
//  //  	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:\n");
//    	for(Rule r : datalog) {
//    		System.out.println(r);
//    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	    assertTrue( true );
//	}
	
//	public void testApp1() throws IOException
//	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		
//    	DcoreParser parser = new DcoreParser();
//    	
//    	Program P = parser.parseFile(O);
//    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
//    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#lacks_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#SO_0000418>(Y).");
//    	
//    	System.out.println("============");
//  //  	System.out.println(P);
//    	System.out.println(query);
//    	
//    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
//    	
//    	long start = System.currentTimeMillis();
//    	
//    	List<Rule> datalog = mr.rewrite(query);
//    	
//    	long end = System.currentTimeMillis();
//    	
//    	System.out.println("\nRewritings:" + datalog.size() + "\n");
////    	for(Rule r : datalog) {
////    		System.out.println(r);
////    	}
//    	System.out.println("\nTime cost:" + (end - start) + "ms");
//    	
//	    assertTrue( true );
//	}
	
	public static void main(String[] args) throws IOException
	{
//		String O = "/home/sharpen/projects/dwfe/AGOSUV-bench/O/O_m.dlp";
//		String O = "/home/sharpen/projects/evaluations/Benchmarks/OBO/OBO.dlp";
//		String O = "/home/sharpen/projects/evaluations/Benchmarks/OG/OG.dlp";
//		String O = "/home/sharpen/projects/evaluations/Benchmarks/ONT-256/ONT-256.dlp";
		String O = "/home/sharpen/projects/evaluations/Benchmarks/Reactome/Reactome.dlp";
		
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parseFile(O);
    //	Program P = parser.parse("<http://purl.obolibrary.org/obo/pr#PR_000001765>(X0) :- <http://purl.obolibrary.org/obo/pr#PR_000001767>(X0).");
    	
//    	ConjunctiveQuery query = new QueryParser().parse("? (X) :- <http://purl.obolibrary.org/obo/pr#lacks_part>(X, Y), "
//    			+ "<http://purl.obolibrary.org/obo/pr#SO_0000418>(Y),"
//    			+ "<http://purl.obolibrary.org/obo/pr#has_part>(X, Z)," + 
//    			"<http://purl.obolibrary.org/obo/pr#CHEBI_23367>(Z).");
//      	
 
//    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- <http://purl.obolibrary.org/obo/pr#PR_000000001>(X).");
 //   	ConjunctiveQuery query = new QueryParser().parse("?(X) :- <http://purl.obolibrary.org/obo/pr#derives_from>(X, Y), <http://purl.obolibrary.org/obo/pr#PR_000000001>(Y).");
 //   	ConjunctiveQuery query = new QueryParser().parse("?(X0) :- <file:///c:/tmp/OpenGALEN2_FULL_WithPropertyChains.owl#isConsequenceOf>(X0, X1), "
 //   			+ "<file:///c:/tmp/OpenGALEN2_FULL_WithPropertyChains.owl#Hypertension>(X1).");
 //   	ConjunctiveQuery query = new QueryParser().parse("?(Language_vnm_21_nl0_ce0_blow_vnm_21_nl0_ae0ke0,Language_vnm_21_nl0_ce0_person_vnm_21_nl0_ae1,Language_vnm_21_nl0_ce0_leaf_vnm_21_nl0_ae00) :- language_vnm_21_nl0_ce0(Language_vnm_21_nl0_ce0_blow_vnm_21_nl0_ae0ke0,Language_vnm_21_nl0_ce0_person_vnm_21_nl0_ae1,Language_vnm_21_nl0_ce0_leaf_vnm_21_nl0_ae00), substance_ad_26_nl0_ce0(Language_vnm_21_nl0_ce0_person_vnm_21_nl0_ae1,Substance_ad_26_nl0_ce0_market_ad_26_nl0_ae1,Substance_ad_26_nl0_ce0_healthy_ad_26_nl0_ae2,Substance_ad_26_nl0_ce0_design_ad_26_nl0_ae3,Substance_ad_26_nl0_ce0_start_ad_26_nl0_ae4), set_ad_13_nl0_ce0(Set_ad_13_nl0_ce0_use_ad_13_nl0_ae0ke0,Language_vnm_21_nl0_ce0_blow_vnm_21_nl0_ae0ke0,Set_ad_13_nl0_ce0_touch_ad_13_nl0_ae2,Set_ad_13_nl0_ce0_hard_ad_13_nl0_ae3,Set_ad_13_nl0_ce0_remain_ad_13_nl0_ae4), prove_ad_43_nl0_ce0(Prove_ad_43_nl0_ce0_use_ad_13_nl0_ae0ke0,Prove_ad_43_nl0_ce0_hard_ad_13_nl0_ae1,Prove_ad_43_nl0_ce0_touch_ad_13_nl0_ae2,Set_ad_13_nl0_ce0_hard_ad_13_nl0_ae3,Prove_ad_43_nl0_ce0_admit_ad_43_nl0_ae4), jewel_vi_15_nl0_ce0(Jewel_vi_15_nl0_ce0_nerve_vi_15_nl0_ae0ke0JoinAttr,Language_vnm_21_nl0_ce0_blow_vnm_21_nl0_ae0ke0), boot_vnm_5_nl0_ce0(Language_vnm_21_nl0_ce0_blow_vnm_21_nl0_ae0ke0,Boot_vnm_5_nl0_ce0_head_vnm_5_nl0_ae1,Boot_vnm_5_nl0_ce0_judge_vnm_5_nl0_ae2,Boot_vnm_5_nl0_ce0_flat_vnm_5_nl0_ae00) .");	
    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- <http://www.biopax.org/release/biopax-level3.owl#Gene>(X).");

    	System.out.println("============");
    	
		Rule Qr = RuleFactory.instance().createQueryRule(query);
		
    	System.out.println(Qr);
    	
    	ModularizedRewriting mr = new ModularizedRewriting(P.getRuleSet());
    	
    	System.out.println("Finish modularize");
    	
    	long start = System.currentTimeMillis();
    	
    	List<Rule> datalog = mr.rewrite(query);
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nRewritings:" + datalog.size() + "\n");
    	System.out.println("\nTime cost:" + (end - start) + "ms");
 
	}
}