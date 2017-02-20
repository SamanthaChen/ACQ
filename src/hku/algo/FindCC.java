package hku.algo;

import java.util.*;

/**
 * @author fangyixiang
 * @date Oct 9, 2015
 * Find a connected component within a given set of nodes
 * 给定一个节点集合，找到节点集合内的连通分量
 */
public class FindCC {
	private int graph[][] = null;//input graph
	private Set<Integer> nodeSet = null;//a given set of nodes
	private int queryId = -1;
	private int edge = 0;
	private Set<Integer> rsSet = null;//the target connected component
	
	/**
	 *  给定一个节点集合，找到包含节点集合的连通分量
	 * */
	public FindCC(int graph[][], Set<Integer> nodeSet, int queryId){
		this.graph = graph;
		this.nodeSet = nodeSet;
		this.queryId = queryId;
		
		this.edge = 0;
		this.rsSet = new HashSet<Integer>();//已经访问过的节点集合
	}
	/**
	 * 找到包含查询节点，和指定的节点集合的的连通分量
	 * */
	public Set<Integer> findCC(){
		Queue<Integer> queue = new LinkedList<Integer>(); 
		queue.add(queryId);
		rsSet.add(queryId);
		
		while(queue.size() > 0){
			int curId = queue.poll();
			for(int i = 0;i < graph[curId].length;i ++){
				int neighbor = graph[curId][i];
				if(nodeSet.contains(neighbor)){
					edge += 1;
				    if(!rsSet.contains(neighbor)){
						queue.add(neighbor);
						rsSet.add(neighbor);
					}
				}
			}
		}
		
		return rsSet;
	}
	
	public int getEdge() {
		return edge;
	}
}
