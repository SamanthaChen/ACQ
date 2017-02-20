package hku.algo.previous;

import hku.Config;
import hku.algo.*;

import java.util.*;
/**
 * @author fangyixiang
 * @date Oct 27, 2015
 * We implement the greedy algorithm proposed in the SIGKDD2010 paper
 */
public class SIGMOD2014 {
	private int graph[][] = null;
	private int core[] = null;
	private int queryId = -1;
	
	public SIGMOD2014(int graph[][], int core[]){
		this.graph = graph;
		this.core = core;
	}
	
	public Set<Integer> query(int queryId){
		this.queryId = queryId;
//		if(graph[queryId].length < Config.k)   return 0;
		long startT = System.nanoTime();
		if(core[queryId] < Config.k)   return null;
		
		Map<Integer, Integer> degMap = new HashMap<Integer, Integer>();//selectedVertex -> degree
        Set<Integer> CSet = new HashSet<Integer>();//result set C in SIGMOD2014 paper
		Map<Integer, Set<Integer>> incidMap = new HashMap<Integer, Set<Integer>>();//incidence -> vertex set
		Map<Integer, Integer> lookMap = new HashMap<Integer, Integer>();//vertext -> incidence
		Set<Integer> BSet = new HashSet<Integer>();//simulate the visited set in SIGMOD2014 paper
		Set<Integer> tmpSet = new HashSet<Integer>();tmpSet.add(queryId);
		incidMap.put(0, tmpSet);
		BSet.add(queryId);

		while(incidMap.size() > 0){
			//step 1: select the vertex with the largest incidence
			int maxIncidence = -1;
			for(int incidence:incidMap.keySet()){
				if(incidence > maxIncidence){
					maxIncidence = incidence;
				}
			}
			Set<Integer> maxIncidSet = incidMap.get(maxIncidence);
			Iterator<Integer> iter = maxIncidSet.iterator();
			int maxVertex = iter.next();
			maxIncidSet.remove(maxVertex);
			if(maxIncidSet.size() == 0)   incidMap.remove(maxIncidence);
			CSet.add(maxVertex);
			lookMap.remove(maxVertex);
			
			//step 2: update the incidence, and degrees due to the selected vertex
			int edgeCount = 0;
			for(int i = 0;i < graph[maxVertex].length;i ++){
				int neighborId = graph[maxVertex][i];
				if(CSet.contains(neighborId)){//degree in the subgraph induced by C
					edgeCount += 1;
					if(degMap.containsKey(neighborId)){
						if(degMap.get(neighborId) < Config.k - 1){//only consider vertices with degree less than Config.k
							degMap.put(neighborId, degMap.get(neighborId) + 1);
						}else{
							degMap.remove(neighborId);
						}
					}
				}else if(lookMap.containsKey(neighborId)){//update incidence in lookMap
					int curIncidence = lookMap.get(neighborId);
					lookMap.put(neighborId, curIncidence + 1);//update lookMap
					
					incidMap.get(curIncidence).remove(neighborId);
					if(incidMap.get(curIncidence).size() == 0)   incidMap.remove(curIncidence);
					if(incidMap.get(curIncidence + 1) == null){
						Set<Integer> tSet = new HashSet<Integer>(); tSet.add(neighborId);
						incidMap.put(curIncidence + 1, tSet);
					}else{
						incidMap.get(curIncidence + 1).add(neighborId);
					}
				}
			}
			if(edgeCount < Config.k){//only consider vertices with degree less than Config.k
				degMap.put(maxVertex, edgeCount);
			}
			
			//step 3: compute the minimum degree
			if(degMap.size() == 0){
				return CSet;
			}
			
			//step 4: explore the neighbors as candidates
			for(int i = 0;i < graph[maxVertex].length;i ++){
				int neighborId = graph[maxVertex][i];
				if(!BSet.contains(neighborId) && core[neighborId] >= Config.k){
					BSet.add(neighborId);//label it as visisted
					
					int incidence = 0;
					for(int id:graph[neighborId])   if(CSet.contains(id))   incidence += 1;
					if(incidMap.containsKey(incidence)){
						incidMap.get(incidence).add(neighborId);
					}else{
						Set<Integer> tSet = new HashSet<Integer>();tSet.add(neighborId);
						incidMap.put(incidence, tSet);
					}
					lookMap.put(neighborId, incidence);
				}
			}
		}
		
		//global search
		return globalQuery();
	}
	
	public Set<Integer> globalQuery(){
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
		
		KCore kcore = new KCore(graph);
		int core[] = kcore.decompose();
		
		SIGMOD2014 sigmod = new SIGMOD2014(graph, core);
		sigmod.query(15238);
//		sigmod.query(246688);
	}

}
