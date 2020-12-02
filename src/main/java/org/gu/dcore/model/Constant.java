package org.gu.dcore.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Constant class
 * @author sharpen
 * @version 1.0, April 2018 
 */
public class Constant implements Term {
	private String name;
	private long id;
	
	
	public Constant(String _name, long _id) {
		this.name = _name;
		this.id = _id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public String toRDFox() {
		String str = this.name.startsWith("\"") ? this.name : "\"" + this.name + "\"";
		return str;
	}
	
	@Override
	public String toVLog() {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(this.name);
		
		if(m.find()) {
			return this.name;
		}
		else {
			return "\"" + this.name + "\"";
		}
	}
	
	@Override
	public String toDLV() {
		return "\"" + this.name + "\"";
	}
	
	@Override
	public boolean isConstant() {
		return true;
	}
	@Override
	public boolean isVariable() {
		return false;
	}
}
