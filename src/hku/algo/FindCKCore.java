package hku.algo;

import hku.Config;

import java.util.*;

/**
 * @author fangyixiang
 * @date Aug 17, 2015
 * 给定节点q，和corenumber k，快速定位包含q的k-core
 */
public class FindCKCore {
	
	public int[] findCKCore(int graph[][], int core[], int queryId) {
		//当query节点比设定的k还要小，直接返回query
		if(core[queryId] < Config.k){
			int rsNode[] = {queryId};
			return rsNode;
		}

		Set<Integer> visitSet = new HashSet<Integer>();//访问集合
		Queue<Integer> queue = new LinkedList<Integer>(); 
		
		//step 1: initialize
		//步骤1：初始化
		queue.add(queryId);//需要访问的节点队列
		visitSet.add(queryId);//已经访问过的节点集合
		
		//step 2: search
		while(queue.size() > 0){
			int current = queue.poll();
			for(int i = 0;i < graph[current].length;i ++){ //把current点的邻居都访问一遍,BFS
				int neighbor = graph[current][i];
				if(visitSet.contains(neighbor) == false && core[neighbor] >= Config.k){
					queue.add(neighbor);
					visitSet.add(neighbor);
				}
			}
		}
		
		//count the number of nodes in the k-core
		//计算k-core中节点数
		int count = visitSet.size();
		
		//put all the nodes in an array
		//将所有的节点放在一个array里面
		int index = 0;
		int rsNode[] = new int[count];
		Iterator<Integer> iter = visitSet.iterator();
		for(int id:visitSet){
			rsNode[index] = id;
			index += 1;
		}
		
		return rsNode;
	}
}
