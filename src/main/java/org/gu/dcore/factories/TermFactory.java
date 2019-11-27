package org.gu.dcore.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gu.dcore.model.Constant;
import org.gu.dcore.model.RepConstant;
import org.gu.dcore.model.Variable;

public class TermFactory {
	private static TermFactory factory = null;
	
	private Map<String, Constant> cMap = null;
	private ArrayList<RepConstant> rc;
	private long id = 0;
	
	private int var_index = 0;
	
	private TermFactory() {
		this.cMap = new HashMap<>();
		this.rc = new ArrayList<>();
	}
	
	public static TermFactory instance() {
		if(factory == null) factory = new TermFactory();
		
		return factory;
	}
	
	public static void reset() {
		factory = new TermFactory();
	}
	
	public Constant createConstant(String iri) {
		Constant c = this.cMap.get(iri);
		
		if(c == null) {
			c = new Constant(iri, this.id++);
			this.cMap.put(iri, c);
		}
		
		return c;
	}
	
	public RepConstant getRepConstant(int i) {
		if(i >= rc.size()) {
			for(int t = rc.size(); t <= i; t++) {
				rc.add(new RepConstant(t));
			}
		}
		return rc.get(i);
	}
	
	public Variable createVariable() {
		return new Variable(var_index++);
	}
}
