package org.gu.dcore.grd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.gu.dcore.utils.Pair;

public class TGraph {
	private ArrayList<List<Integer>> graph;
	private int size;
	
	private List<List<Integer>> SCCs;
	private List<Integer> entryNodes;	
	private List<Integer> freeNodes;
	
	private int[] DFN, LOW;
	private int order;
	private boolean[] visit;
	private Stack<Integer> stack;
	
	public TGraph(int size) {
		this.size = size;
		this.graph = new ArrayList<>();
		
		for(int i = 0; i < size; i++) this.graph.add(new ArrayList<>()); 
	}
	
	public TGraph(int size, boolean[][] edges) {
		this.size = size;
		this.graph = new ArrayList<>();
		
		for(int i = 0; i < size; i++) {
			ArrayList<Integer> e = new ArrayList<Integer>();
			this.graph.add(e); 

			for(int j = 0; j < size; j++) {
				if(edges[i][j]) e.add(j);
			}
		}
	}
	
	public void addEdge(int i, int j) {
		List<Integer> nodes = this.graph.get(i);
		if(!nodes.contains(j))
			nodes.add(j);
	}
	
	public List<List<Integer>> getSCCs() {
		if(this.SCCs == null) computeSCCs(null);
		
		return this.SCCs;
	}
	
	public void computeSCCs(List<Integer> roots) {
		this.SCCs = new LinkedList<>();
		
		DFN = new int[size];
		LOW = new int[size];
		visit = new boolean[size];
		
		stack = new Stack<>();
		
		if(roots == null) {
			for(int i = 0; i < size; i++) 
				if(DFN[i] == 0) iterative_tarjan(i);
		}
		else {
			for(Integer i : roots)
				if(DFN[i] == 0) iterative_tarjan(i);
		}
		
		while(!stack.isEmpty()) freeNodes.add(stack.pop());
	}
	
	private void tarjan(int x) {
		DFN[x] = LOW[x] = ++order;
		stack.add(x);
		visit[x] = true;
		
		List<Integer> nextNodes = graph.get(x);
		
		for(Integer i : nextNodes) {
			if(DFN[i] == 0) {
				tarjan(i);
				LOW[x] = Integer.min(LOW[x], LOW[i]);
			}
			else if(visit[i]) {
				LOW[x] = Integer.min(LOW[x], DFN[i]);
			}
		}
		
		if(LOW[x] == DFN[x]) {
			this.entryNodes.add(x);
			List<Integer> scc = new LinkedList<>();
			while(!stack.isEmpty()) {
				int c = stack.pop();
				scc.add(c);
				if(c == x) break;
			}
			if(scc.size() == 1) {
				if(nextNodes.contains(x)) SCCs.add(scc);
			}
			else SCCs.add(scc);
		}
	}
	
	private void iterative_tarjan(int start) {
		Stack<Pair<Integer, Integer>> t = new Stack<>();
		
		t.push(new Pair<>(start, 0));
		
		while(!t.isEmpty()) {
			Pair<Integer, Integer> p = t.pop();
			int x = p.a;
			
			if(p.b == 0) {
				DFN[x] = LOW[x] = ++order;
				stack.add(x);
				visit[x] = true;
			}
			
			List<Integer> nextNodes = graph.get(x);
			
			boolean recusive = false;
			
			for(int j = p.b; j < nextNodes.size(); j++) {
				int i = nextNodes.get(j);
				if(DFN[i] == 0) {
					recusive = true;
					t.push(new Pair<>(x, j + 1));
					t.push(new Pair<>(i, 0));
					break;
				}
				else if(visit[i]) {
					LOW[x] = Integer.min(LOW[x], DFN[i]);
				}
			}
			
			if(recusive) continue;
			
			if(LOW[x] == DFN[x]) {
				this.entryNodes.add(x);
				List<Integer> scc = new LinkedList<>();
				while(!stack.isEmpty()) {
					int c = stack.pop();
					scc.add(c);
					if(c == x) break;
				}
				if(scc.size() == 1) {
					if(nextNodes.contains(x)) SCCs.add(scc);
				}
				else SCCs.add(scc);
			}
			
			if(!t.isEmpty()) {
				int v = t.peek().a;
				LOW[v] = Integer.min(LOW[v], LOW[x]);
			}
		}				
	}
	
	public List<Integer> getFreeNodes() {
		return this.freeNodes;
	}
	
	public List<Integer> getEntryNodes() {
		return this.entryNodes;
	}
}
