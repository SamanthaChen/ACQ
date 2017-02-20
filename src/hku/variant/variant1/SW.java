package hku.variant.variant1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.FindCCS;
import hku.algo.KCore;
import hku.algo.TNode;

/**
 * @author fangyixiang
 * @date Nov 3, 2015
 */
public class SW {
	private String nodes[][] = null;
	private int graph[][] = null;
	private TNode root;// the built index
	private int core[] = null;
	private int queryId = -1;
	
	public SW(int graph[][], String nodes[][], TNode root, int core[]){
		this.graph = graph;
		this.nodes = nodes;
		this.root = root;
		this.core = core;
	}
	
	public void query(int queryId, String kws[]){
		this.queryId = queryId;
		if(core[queryId] < Config.k)   return ;
		
		Set<String> set = new HashSet<String>();
		for(String kw:kws)   set.add(kw);
		
		List<Integer> curList = new ArrayList<Integer>();//this list serves as a map (newID -> original ID)
		curList.add(-1);//for consuming space purpose
		
		TNode subRoot = locateAllCK(root);
		Queue<TNode> queue = new LinkedList<TNode>(); 
		queue.add(subRoot);
		while(queue.size() > 0){
			TNode curNode = queue.poll();
			
			//intersection on the inverted list
			Map<String, int[]> kwMap = curNode.getKwMap();
			Set<Integer> intersecSet = new HashSet<Integer>();
			boolean isFirst = true;
			for(int i = 0;i < kws.length;i ++){
				if(kwMap.containsKey(kws[i])){
					int invert[] = kwMap.get(kws[i]);
					if(isFirst){
						isFirst = false;
						for(int j = 0;j < invert.length;j ++)   intersecSet.add(invert[j]);
					}else{
						Set<Integer> tmpSet = new HashSet<Integer>();
						for(int j = 0;j < invert.length;j ++){
							if(intersecSet.contains(invert[j])){
								tmpSet.add(invert[j]);
							}
						}
						intersecSet = tmpSet;
					}
				}else{//this keyword is not contained. Skip all the nodes!!!
					intersecSet = null;
					break;
				}
			}
			if(intersecSet != null)   curList.addAll(intersecSet);//collect all the candidate nodes
			for(TNode tnode:curNode.getChildList())   queue.add(tnode);
		}
		
		if(curList.size() > 1){
			FindCCS finder = new FindCCS(graph, curList, queryId);
			Set<Integer> ccsSet = finder.findRobustCCS();
			if(ccsSet.size() > 1){
				System.out.println("SW finds a LAC with size = " + ccsSet.size());
			}else{
				System.out.println("No community");
			}
		}
	}
	
	// locate a list of tnodes, each of which has (1)coreness>=Config.k and (2) contains queryId
	private TNode locateAllCK(TNode root) {
		// step 1: find nodes with coreNumber=Config.k using BFS
		List<TNode> candRootList = new ArrayList<TNode>();
		Queue<TNode> queue = new LinkedList<TNode>();
		queue.add(root);

		while (queue.size() > 0) {
			TNode curNode = queue.poll();
			for (TNode tnode : curNode.getChildList()) {
				if (tnode.getCore() < Config.k) {
					queue.add(tnode);
				} else {// the candidate root node must has coreness at least Config.k
					candRootList.add(tnode);
				}
			}
		}
//		System.out.println("candRootList.size:" + candRootList.size());

		// step 2: locate a list of ck-cores
		for (TNode tnode : candRootList) {
			if (findCK(tnode)) {
				return tnode;
			}
		}

		return null;
	}
	// check whether a subtree rooted at "root" contains queryId or not
	private boolean findCK(TNode root) {
		if (root.getCore() <= core[queryId]) {
			boolean rs = false;
			if (root.getNodeSet().contains(queryId)) {
				rs = true;
			} else {
				for (TNode tnode : root.getChildList()) {
					if (findCK(tnode)) {
						rs = true;
						break;
					}
				}
			}
			return rs;
		} else {
			return false;
		}
	}
}
