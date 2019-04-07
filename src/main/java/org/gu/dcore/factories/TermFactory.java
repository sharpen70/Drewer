package org.gu.dcore.factories;

import java.util.HashMap;
import java.util.Map;

import org.gu.dcore.model.Constant;
import org.gu.dcore.model.Variable;

public class TermFactory {
	private static TermFactory factory = null;
	private Map<String, Constant> cMap = null;
	private long id = 0;
	
	private Variable[] vars;
	private static final int max_var_size = 100;
	
	private TermFactory() {
		this.cMap = new HashMap<>();
		this.vars = new Variable[max_var_size];
	}
	
	public static TermFactory instance() {
		if(factory == null) factory = new TermFactory();
		
		return factory;
	}
	
	public Constant createConstant(String iri) {
		Constant c = this.cMap.get(iri);
		
		if(c == null) {
			c = new Constant(iri, this.id++);
			this.cMap.put(iri, c);
		}
		
		return c;
	}
	
	public Variable createVariable(int value) {
		Variable v = this.vars[value];
		
		if(v == null) {
			v = new Variable(value);
			this.vars[value] = v;
		}
		
		return v;
	}
}
