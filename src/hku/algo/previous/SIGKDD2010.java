package hku.algo.previous;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.FindCKCore;
import hku.algo.FindKCore;
import hku.algo.KCore;
import java.util.*;

/**
 * @author fangyixiang
 * @date Oct 27, 2015
 * We implement the greedy algorithm proposed in the SIGKDD2010 paper
 */
public class SIGKDD2010 {
	private int graph[][] = null;
	private int queryId = -1;
	
	public SIGKDD2010(int graph[][]){
		this.graph = graph;
	}
	
	public Set<Integer> query(int queryId){
		this.queryId = queryId;
		if(graph[queryId].length < Config.k)   return null;

		//step 1: find k-core first
		FindKCore fkc = new FindKCore(graph, Config.k);
		int subCore[] = fkc.decompose();
		
		//step 2: find ck-core
		FindCKCore finder = new FindCKCore();
		int rsArray[] = finder.findCKCore(graph, subCore, queryId);
		
		if(rsArray.length > 1){
//			System.out.println("SIGKDD: we find a community with size = " + rsArray.length);
			Set<Integer> set = new HashSet<Integer>();
			for(int id:rsArray)   set.add(id);
			return set;
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpNode);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		SIGKDD2010 sigkdd = new SIGKDD2010(graph);
		sigkdd.query(246688);
	}

}
