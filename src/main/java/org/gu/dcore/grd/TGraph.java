package org.gu.dcore.grd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.gu.dcore.utils.Pair;

public class TGraph {
	private ArrayList<List<Integer>> graph;
	private int size;
	
	private List<List<Integer>> loops;
	
	private int[] DFN, LOW;
	private int order;
	private boolean[] visit;
	private Stack<Integer> stack;
	
	public TGraph(int size) {
		this.size = size;
		this.graph = new ArrayList<>();
		
		for(int i = 0; i < size; i++) this.graph.add(new LinkedList<>()); 
	}
	
	public void addEdge(int i, int j) {
		List<Integer> nodes = this.graph.get(i);
		nodes.add(j);
	}
	
	public List<List<Integer>> getLoops() {
		if(this.loops == null) computeLoops();
		
		return this.loops;
	}
	
	private void computeLoops() {
		this.loops = new LinkedList<>();
		
		DFN = new int[size];
		LOW = new int[size];
		visit = new boolean[size];
		
		stack = new Stack<>();
		
		for(int i = 0; i < size; i++) {
			if(DFN[i] == 0) iterative_tarjan(i);
		}
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
			List<Integer> scc = new LinkedList<>();
			while(!stack.isEmpty()) {
				int c = stack.pop();
				scc.add(c);
				if(c == x) break;
			}
			if(scc.size() == 1) {
				if(nextNodes.contains(x)) loops.add(scc);
			}
			else loops.add(scc);
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
			
			for(Integer i : nextNodes) {
				if(DFN[i] == 0) {
					recusive = true;
					t.push(new Pair<>(x, i));
					t.push(new Pair<>(i, 0));
					break;
				}
				else if(visit[i]) {
					LOW[x] = Integer.min(LOW[x], DFN[i]);
				}
			}
			
			if(recusive) continue;
			
			if(LOW[x] == DFN[x]) {
				List<Integer> scc = new LinkedList<>();
				while(!stack.isEmpty()) {
					int c = stack.pop();
					scc.add(c);
					if(c == x) break;
				}
				if(scc.size() == 1) {
					if(nextNodes.contains(x)) loops.add(scc);
				}
				else loops.add(scc);
			}
			
			if(!t.isEmpty()) {
				int v = t.peek().a;
				LOW[v] = Integer.min(LOW[v], LOW[x]);
			}
		}				
	}
}
