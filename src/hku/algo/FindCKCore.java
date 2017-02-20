package hku.algo;

import hku.Config;

import java.util.*;

/**
 * @author fangyixiang
 * @date Aug 17, 2015
 * �����ڵ�q����corenumber k�����ٶ�λ����q��k-core
 */
public class FindCKCore {
	
	public int[] findCKCore(int graph[][], int core[], int queryId) {
		//��query�ڵ���趨��k��ҪС��ֱ�ӷ���query
		if(core[queryId] < Config.k){
			int rsNode[] = {queryId};
			return rsNode;
		}

		Set<Integer> visitSet = new HashSet<Integer>();//���ʼ���
		Queue<Integer> queue = new LinkedList<Integer>(); 
		
		//step 1: initialize
		//����1����ʼ��
		queue.add(queryId);//��Ҫ���ʵĽڵ����
		visitSet.add(queryId);//�Ѿ����ʹ��Ľڵ㼯��
		
		//step 2: search
		while(queue.size() > 0){
			int current = queue.poll();
			for(int i = 0;i < graph[current].length;i ++){ //��current����ھӶ�����һ��,BFS
				int neighbor = graph[current][i];
				if(visitSet.contains(neighbor) == false && core[neighbor] >= Config.k){
					queue.add(neighbor);
					visitSet.add(neighbor);
				}
			}
		}
		
		//count the number of nodes in the k-core
		//����k-core�нڵ���
		int count = visitSet.size();
		
		//put all the nodes in an array
		//�����еĽڵ����һ��array����
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
