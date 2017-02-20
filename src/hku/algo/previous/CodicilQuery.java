package hku.algo.previous;

import hku.Config;
import hku.algo.DataReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Feb 19, 2016
 * online community search
 */
public class CodicilQuery {
	private int graph[][] = null;
	private String nodes[][] = null;
	private int vertexCluster[] = null;//vertexId -> clusterId
	public List<Set<Integer>> list = null;//the clustering result
	
	public CodicilQuery(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		graph = dataReader.readGraph();
		nodes = dataReader.readNode();
		
		vertexCluster = new int[graph.length];
		list = new ArrayList<Set<Integer>>();
		for(int i = 0;i < Config.clusterK;i ++)   list.add(new HashSet<Integer>());
		
		String clusterPath = graphFile + "-smp" + ".part." + Config.clusterK;
		System.out.println("clusterPath:" + clusterPath);
		
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(clusterPath));
			
			int vertexId = 1;
			String line = null;
			while((line = stdin.readLine()) != null){
				int clusterId = Integer.parseInt(line);
				
				vertexCluster[vertexId] = clusterId;
				list.get(clusterId).add(vertexId);
				vertexId += 1;
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Set<Integer> query(int queryId){
		int clusterId = vertexCluster[queryId];
		return list.get(clusterId);
	}

	public static void main(String args[]){
		CodicilQuery cq = new CodicilQuery(Config.dblpGraph, Config.dblpNode);
		Set<Integer> lac = cq.query(963045);
		System.out.println("lac.size:" + lac.size());
	}
}
