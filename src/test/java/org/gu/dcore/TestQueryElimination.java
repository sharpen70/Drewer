package org.gu.dcore;
/*
 * Copyright (C) 2018 - 2020 Artificial Intelligence and Semantic Technology, 
 * Griffith University
 * 
 * Contributors:
 * Peng Xiao (sharpen70@gmail.com)
 * Zhe wang
 * Kewen Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.gu.dcore.model.ConjunctiveQuery;
import org.gu.dcore.model.Program;
import org.gu.dcore.model.Rule;
import org.gu.dcore.parsing.DcoreParser;
import org.gu.dcore.parsing.QueryParser;
import org.gu.dcore.preprocessing.QueryElimination;
import org.gu.dcore.rewriting.ModularizedRewriting;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestQueryElimination extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TestQueryElimination( String testName )
	{
	    super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
	    return new TestSuite( TestQueryElimination.class );
	}
	
	public void test1() {
    	DcoreParser parser = new DcoreParser();
    	
    	Program P = parser.parse("A(X, Y) :- D(Y, X). B(X) :- A(X, Y).  C(X,Z):-D(X,Y). B(X):-C(X,X).\n");
    	
    	ConjunctiveQuery query = new QueryParser().parse("?(X) :- D(X, Y), B(X).");
    	
    	System.out.println("============");
    	System.out.println(P);
    	System.out.println(query);
    	
    	QueryElimination qe = new QueryElimination(P.getRuleSet());
    	
    	qe.eliminate(query);
    	
    	System.out.println(query);
    	
	    assertTrue( true );		
	}
}
