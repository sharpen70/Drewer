package org.gu.dcore.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String getShortIRI(String iri) {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(iri);
		
		if(m.find()) {
			return m.group(1);
		}
		
		int sp = iri.indexOf("#");
		
		if(sp != -1) return iri.substring(sp + 1);
		else {
			int slash_p = iri.lastIndexOf("/");
			if(slash_p != -1) return iri.substring(slash_p + 1);
		}
		
		return iri;
	}
}
