package hku.exp.effect;

import hku.Config;
import hku.algo.previous.CodicilQuery;
import hku.exp.sim.LinkAvgFreq;
import hku.exp.sim.LinkMinFreq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Jul 22, 2015
 * (1) read the node information
 * (2) read the node and edges (Nodes are named starting from 1, 2, 3, ...)
 */
public class SmpDataReader {
	private String graphFile = null;
	private String nodeFile = null;
	private int userNum = -1;
	private int edgeNum = -1;
	
	public SmpDataReader(String graphFile){
		this.graphFile = graphFile;
	}

	//return the graph edge information
	public int[][] readGraph(){
		int graph[][] = null;
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(graphFile));
			
			String line = stdin.readLine();
			String s[] = line.trim().split(" ");
			int userNum = Integer.parseInt(s[0]);
			
			graph = new int[userNum + 1][];
			while((line = stdin.readLine()) != null){
				if(line.length() > 0){
					s = line.split(" ");
					int userId = Integer.parseInt(s[0]);
					graph[userId] = new int[s.length - 1];
					for(int i = 1;i < s.length;i ++){
						graph[userId][i - 1] = Integer.parseInt(s[i]);
					}
				}
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return graph;
	}
	
	public static void main(String args[]){
		SmpDataReader reader = new SmpDataReader(Config.dblpGraph + "-smp");
		int graph[][] = reader.readGraph();
		
		Config.clusterK = 1000;
		CodicilQuery cq = new CodicilQuery(Config.dblpGraph, Config.dblpNode);
		List<Set<Integer>> list = cq.list;
		
		LinkAvgFreq laf = new LinkAvgFreq(graph);
		LinkMinFreq lmf = new LinkMinFreq(graph);
		
		for(int i = 0;i < list.size();i ++){
			Set<Integer> set = list.get(i);
			
			int size = set.size();
			double v1 = laf.singleLinkFreq(set);
			double v2 = lmf.singleLinkFreq(set);
			
			System.out.println("i:" + i + " size:" + size + " v1:" + v1 + " v2:" + v2);
		}
	}
}
