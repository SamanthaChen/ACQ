package hku.algo.previous;

import hku.Config;
import hku.algo.DataReader;

import java.io.*;
import java.util.*;

/**
 * @author fangyixiang
 * @date Feb 18, 2016
 * implement the algorithm CODICIL in WWW'2013
 */
class Pair{
	public int id;
	public double dist;
	public Pair(int id, double dist){   this.id = id;   this.dist = dist;}
}
public class Codicil {
	private String graphFile = null;
	private int graph[][] = null;
	private String nodes[][] = null;
	private int n = -1;
	private int contK = -1;
	
	public Codicil(int graph[][], String nodes[][]){
		this.graph = graph;
		this.nodes = nodes;
		this.graphFile = Config.dblpGraph + "-toy";
		this.n = graph.length - 1;
		this.contK = 5;
	}
	
	public Codicil(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		graph = dataReader.readGraph();
		nodes = dataReader.readNode();
		this.graphFile = graphFile;
		this.n = graph.length - 1;//the total number of vertices
		this.contK = dataReader.getEdgeNum() / n;//setting the number of content vertices for each vertices
		System.out.println("contK:" + contK);
	}
	
	public void handle(){
		long startTime = System.currentTimeMillis();
		Comparator<Pair> OrderIsdn =  new Comparator<Pair>(){  
            public int compare(Pair o1, Pair o2) {  
                if(o1.dist <= o2.dist)   return 1;
            	else   return -1;
            }              
        };
		
		//step 1: compute word document frequency
		Map<String, Double> dfMap = new HashMap<String, Double>();
		for(int i = 1;i <= n;i ++){
			for(int j = 1;j < nodes[i].length;j ++){
				String word = nodes[i][j];
				if(dfMap.containsKey(word))   dfMap.put(word, dfMap.get(word) + 1.0);
				else                          dfMap.put(word, 1.0);
			}
		}
		System.out.println("step 1 finished: " + (new Date()).toLocaleString());
		
		
		//step 2: create content edges
		List<Set<Integer>> uList = new ArrayList<Set<Integer>>();
		uList.add(null);//for consuming space
		for(int i = 1;i <= n;i ++){
			Set<Integer> set = new HashSet<Integer>();
			for(int id:graph[i])   set.add(id);
			uList.add(set);
		}
		double x[] = new double[n + 1];//compute ||x||
		for(int i = 1;i <= n;i ++){
			double tmp = 0.0;
			for(int j = 1;j < nodes[i].length;j ++){
				String word = nodes[i][j];
				double term = Math.log(1 + n / dfMap.get(word));// / Math.log(2.0);
				tmp += term * term;
			}
			x[i] = Math.sqrt(tmp);
		}
		System.out.println("step 2.1 finished: " + (new Date()).toLocaleString());
		for(int i = 1;i <= n;i ++){
			//obtain v_i's keyword set
			Set<String> kwSet = new HashSet<String>();
			for(int j = 1;j < nodes[i].length;j ++)   kwSet.add(nodes[i][j]);
			
			//obtain v_i's neighbors and neighbors' neighbors
			Set<Integer> hop2Set = new HashSet<Integer>();
			for(int id1:graph[i]){
				//hop2Set.add(id1);
				for(int id2:graph[id1])   if(id2 != i)   hop2Set.add(id2);
			}
			
			Queue<Pair> queue =  new PriorityQueue<Pair>(contK, OrderIsdn);
			for(int nbId:hop2Set){
				//compute xy
				double xy = 0.0;
				for(int j = 1;j < nodes[nbId].length;j ++){
					String word = nodes[nbId][j];
					if(kwSet.contains(word)){
						double term = Math.log(1 + n / dfMap.get(word));// / Math.log(2.0);
						xy += term * term;
					}
				}
				
				//compute the similarity between x and y
				double sim = xy  / (x[i] * x[nbId]); //consine similarity
				queue.add(new Pair(nbId, sim));
			}
			
			//select top-k
			int count = 0;
			while(queue.size() > 0){
				int nodeId = queue.poll().id;
				uList.get(i).add(nodeId);
				uList.get(nodeId).add(i);
				
				count ++;
				if(count >= contK)   break;
			}
			
			if(i % 10000 == 0)   System.out.println("c-edge i=" + i + " " + (new Date()).toLocaleString());
		}
		System.out.println("step 2.2 finished: " + (new Date()).toLocaleString());
		
		//step 3: generate sampled graph
		List<Set<Integer>> smpList = new ArrayList<Set<Integer>>();
		for(int i = 0;i <= n;i ++)   smpList.add(new HashSet<Integer>());
		for(int i = 1;i <= n;i ++){
			Set<Integer> linkSet = new HashSet<Integer>();//v_i's neighbors in original graph
			Set<String> contSet = new HashSet<String>();//v_i's content in original graph
			for(int nbId:graph[i])   linkSet.add(nbId);
			for(int j = 1;j < nodes[i].length;j ++)   contSet.add(nodes[i][j]);
			
			Set<Integer> unbSet = uList.get(i);
			Map<Integer, Double> linkMap = new HashMap<Integer, Double>();
			Map<Integer, Double> contMap = new HashMap<Integer, Double>();
			
			//link similarity
			double max = 0.0;
			double min = Double.MAX_VALUE;
			for(int unbId:unbSet){
				int same = 0;
				int all = linkSet.size();
				for(int nbId:graph[unbId]){
					if(linkSet.contains(nbId))   same += 1;
					else                         all += 1;
				}
				double sim = same * 1.0 / all;
				linkMap.put(unbId, sim);
				if(sim > max)   max = sim;
				if(sim < min)   min = sim;
//				System.out.println("link i:" + i + "  unbId:" + unbId + " sim:" + sim + " same:" + same + " all:" + all);
			}
			if(max > min){//re-scale
				for(int unbId:linkMap.keySet()){
					double sim = (linkMap.get(unbId) - min) / (max - min);
					linkMap.put(unbId, sim);
//					System.out.println("nmlk i:" + i + " unbId:" + unbId + " sim:" + sim);
				}
			}
			
			//content similarity
			max = 0.0;
			min = Double.MAX_VALUE;
			for(int unbId:unbSet){
				//compute ||y||
				double xy = 0.0;
				for(int j = 1;j < nodes[unbId].length;j ++){
					String word = nodes[unbId][j];
					if(contSet.contains(word)){
						double term = Math.log(1 + n / dfMap.get(word));// / Math.log(2.0);
						xy += term * term;
					}
				}
								
				//compute the similarity between x and y
				double sim = xy  / (x[i] * x[unbId]);
				if(sim > max)   max = sim;
				if(sim < min)   min = sim;
				contMap.put(unbId, sim);
//				System.out.println("cont i:" + i + " unbId:" + unbId + " sim:" + sim);
			}
			if(max > min){//re-scale
				for(int unbId:contMap.keySet()){
					double sim = (contMap.get(unbId) - min) / (max - min);
					contMap.put(unbId, sim);
//					System.out.println("nmct i:" + i + " unbId:" + unbId + " sim:" + sim);
				}
			}
			
			//sample
			Queue<Pair> queue =  new PriorityQueue<Pair>(contK, OrderIsdn);
			for(Map.Entry<Integer, Double> entry:linkMap.entrySet()){
				int id = entry.getKey();
				double sim = 0.5 * entry.getValue() + 0.5 * contMap.get(id);
				queue.add(new Pair(id, sim));
//				System.out.println("queue i:" + i + " id:" + id + " sim:" + sim);
			}
			int T = (int)(Math.sqrt(unbSet.size()));
			for(int t = 0;t < T;t ++){
				int id = queue.poll().id;
				smpList.get(i).add(id);
				smpList.get(id).add(i);
//				System.out.println("queue select i:" + i + " id:" + id);
			}
		}
		System.out.println("step 3 finished: " + (new Date()).toLocaleString());
		
		//step 4: save the graph
		try{
			BufferedWriter stdout = new BufferedWriter(new FileWriter(graphFile + "-smp"));
			int m = 0;
			for(int i = 1;i < smpList.size();i ++)   m += smpList.get(i).size();
			stdout.write(n + " " + (m / 2));
			stdout.newLine();
			for(int i = 1;i < smpList.size();i ++){
				Set<Integer> set = smpList.get(i);
				List<Integer> ls = new ArrayList<Integer>(set);
				if(ls.size() > 0){
					stdout.write(ls.get(0) + "");
					for(int j = 1;j < ls.size();j ++)   stdout.write(" " + ls.get(j));
				}
				stdout.newLine();
			}
			stdout.flush();
			stdout.close();
		}catch(Exception e){e.printStackTrace();}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total time cost:" + (endTime - startTime) / 1000
				+ "   Graph:" + graphFile);
	}
	
	public static void main(String[] args) {
		Codicil codicil = new Codicil(Config.flickrGraph, Config.flickrNode);
		codicil.handle();//1900s
		
//		codicil = new Codicil(Config.dblpGraph, Config.dblpNode);
//		codicil.handle();//109s
//		
//		codicil = new Codicil(Config.tencentGraph, Config.tencentNode);
//		codicil.handle();
//		
//		codicil = new Codicil(Config.dbpediaGraph, Config.dbpediaNode);
//		codicil.handle();
	}
}
