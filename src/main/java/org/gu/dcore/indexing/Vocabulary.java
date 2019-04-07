package org.gu.dcore.indexing;

import java.util.HashMap;
import java.util.Map;

/**
 * A vocabulary is for recording and indexing all symbols in a program
 * @author sharpen
 * @version 1.0, April 2018
 */
public class Vocabulary {
	private static Vocabulary vocabulary = null;
	
	private long startPid;
	private long startCid;
	
	//map of symbols, predicates,constants, variables
	private Map<Long, String> pSet;
	private Map<Long, String> cSet;
	
	//maps for indexing
	private Map<String, Long> pIndex;
	private Map<String, Long> cIndex;
	
	private Vocabulary() {
		startPid = 0;
		startCid = 0;
	
		pSet = new HashMap<>();
		cSet = new HashMap<>();
		pIndex = new HashMap<>();
		cIndex = new HashMap<>();
	}
	
	public static Vocabulary instance() {
		if(vocabulary == null) vocabulary = new Vocabulary();
		return vocabulary;
	}
	
	public long getPid(String pname) {
		if(!pIndex.containsKey(pname)) {
			Long pid = startPid++;
			
			pSet.put(pid, pname);
			pIndex.put(pname, pid);
			
			return pid;
		}
		else {
			return pIndex.get(pname);
		}
	}
	
	public long getCid(String cname) {
		if(!cIndex.containsKey(cname)) {
			Long cid = startCid++;
			
			cSet.put(cid, cname);
			cIndex.put(cname, cid);
			
			return cid;
		}
		else {
			return cIndex.get(cname);
		}
	}
	
	//labeled null variables
	public long getVid() {
		return 0;
	}
}
