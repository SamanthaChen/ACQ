package hku.variant.variant2;

import hku.Config;
import hku.algo.FindCCS;
import hku.algo.TNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Nov 4, 2015
 */
public class SWT {
	private String nodes[][] = null;
	private int graph[][] = null;
	private TNode root;// the built index
	private int core[] = null;
	private int queryId = -1;
	private int threshold = 0;
	
	public SWT(int graph[][], String nodes[][], TNode root, int core[]){
		this.graph = graph;
		this.nodes = nodes;
		this.root = root;
		this.core = core;
	}
	
	public void query(int queryId, String kws[], double thresholdDouble){
		this.queryId = queryId;
		if(core[queryId] < Config.k)   return ;
		
		double tmp = kws.length * thresholdDouble;
		if(tmp - (int)(tmp) >= 0.5){
			this.threshold = (int)(tmp) + 1;
		}else{
			this.threshold = (int)(tmp);
		}
		
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
			Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
			int countOut = 0;
			for(int i = 0;i < kws.length;i ++){
				if(kwMap.containsKey(kws[i])){
					int ids[] = kwMap.get(kws[i]);
					for(int id:ids){
						if(countMap.containsKey(id)){
							countMap.put(id, countMap.get(id) + 1);
						}else{
							countMap.put(id, 1);
						}
					}
				}else{//this keyword is not contained. Skip all the nodes!!!
					countOut += 1;
					if(threshold + countOut > kws.length)   break;
				}
			}
			for(Map.Entry<Integer, Integer> entry:countMap.entrySet()){//collect all the candidate nodes
				if(entry.getValue() >= threshold){
					curList.add(entry.getKey());
				}
			}
			for(TNode tnode:curNode.getChildList())   queue.add(tnode);
		}
		
		if(curList.size() > 1){
			FindCCS finder = new FindCCS(graph, curList, queryId);
			Set<Integer> ccsSet = finder.findRobustCCS();
//			if(ccsSet.size() > 1)   System.out.println("SWT finds a LAC with size = " + ccsSet.size());
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
