package hku.variant.variant1;

import java.util.*;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.FindCCS;
import hku.algo.FindCKCore;
import hku.algo.KCore;

/**
 * @author fangyixiang
 * @date Nov 3, 2015
 * basic-w for variant1
 */
public class BasicWV1 {
	private String nodes[][] = null;
	private int graph[][] = null;
	private int core[] = null;
	private int queryId = -1;
	
	public BasicWV1(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		nodes = dataReader.readNode();
		graph = dataReader.readGraph();
	}
	
	public BasicWV1(int graph[][], String nodes[][]){
		this.graph = graph;
		this.nodes = nodes;
		
		//compute k-core
		KCore kcore = new KCore(graph);
		core = kcore.decompose();
	}
	
	public void query(int queryId, String kws[]) {
		this.queryId = queryId;
		if(core[queryId] < Config.k)   return ;
		
		//step 1: do keyword filtering
		Set<String> set = new HashSet<String>();
		for(String kw:kws)   set.add(kw);
		List<Integer> curList = new ArrayList<Integer>();//this list serves as a map (newID -> original ID)
		curList.add(-1);//for consuming space purpose
		for(int nodeId = 1; nodeId < core.length; nodeId ++){
			int count = 0;
			for(int i = 1;i < nodes[nodeId].length;i ++){
				if(set.contains(nodes[nodeId][i])){
					count += 1;
				}
			}
			
			//if this node's keywords are contained, then we choose it
			if(count == set.size())   curList.add(nodeId);
		}
		
		//step 2: find CCS
		if(curList.size() > 1){
			FindCCS finder = new FindCCS(graph, curList, queryId);
			Set<Integer> ccsSet = finder.findRobustCCS();
//			if(ccsSet.size() > 1)   System.out.println("BasicWV1 finds a LAC with size = " + ccsSet.size());
		}
	}
}
