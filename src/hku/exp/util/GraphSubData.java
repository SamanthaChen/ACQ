package hku.exp.util;

import hku.Config;
import hku.algo.DataReader;

import java.io.*;
import java.util.*;

/**
 * @author fangyixiang
 * @date Oct 12, 2015
 */
public class GraphSubData {
	private String graphFile = null;
	private String nodeFile = null;
	private int graph[][];//graph structure
	private String nodes[][];//the keywords of each node
	private Set<Integer> id20Set = null, id40Set = null, id60Set = null, id80Set = null; 
	
	public GraphSubData(String graphFile, String nodeFile){
		this.graphFile = graphFile;
		this.nodeFile = nodeFile;
		
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		this.graph = dataReader.readGraph();
		this.nodes = dataReader.readNode();
		
		this.id80Set = new HashSet<Integer>();
		this.id60Set = new HashSet<Integer>();
		this.id40Set = new HashSet<Integer>();
		this.id20Set = new HashSet<Integer>();
		
		int userNum = graph.length;
		
		int n80 = (int)(userNum * 0.8);
		int n60 = (int)(userNum * 0.6);
		int n40 = (int)(userNum * 0.4);
		int n20 = (int)(userNum * 0.2);
		System.out.println("n80:" + n80 + "\tn60:" + n60 + "\tn40:" + n40 + "\tn20:" + n20);
		
		List<Integer> id100List = new ArrayList<Integer>();
		List<Integer> id80List = new ArrayList<Integer>();
		List<Integer> id60List = new ArrayList<Integer>();
		List<Integer> id40List = new ArrayList<Integer>();
		
		//80%
		for(int i = 1;i < graph.length;i ++)   id100List.add(i);
		while(true){
			Random rand = new Random();
			int index = rand.nextInt(id100List.size());
			int id = id100List.get(index);
			if(!id80Set.contains(id))   id80Set.add(id);
			if(id80Set.size() >= n80)   break;
		}
		id80List.addAll(id80Set);
		
		//60%
		while(true){
			Random rand = new Random();
			int index = rand.nextInt(id80List.size());
			int id = id80List.get(index);
			if(!id60Set.contains(id))   id60Set.add(id);
			if(id60Set.size() >= n60)   break;
		}
		id60List.addAll(id60Set);
		
		//40%
		while(true){
			Random rand = new Random();
			int index = rand.nextInt(id60List.size());
			int id = id60List.get(index);
			if(!id40Set.contains(id))   id40Set.add(id);
			if(id40Set.size() >= n40)   break;
		}
		id40List.addAll(id40Set);
		
		//20%
		while(true){
			Random rand = new Random();
			int index = rand.nextInt(id40List.size());
			int id = id40List.get(index);
			if(!id20Set.contains(id))   id20Set.add(id);
			if(id20Set.size() >= n20)   break;
		}
	}
	
	public void createAll(){
		create(id20Set, 20);
		create(id40Set, 40);
		create(id60Set, 60);
		create(id80Set, 80);
	}
	
	private void create(Set<Integer> set, int percentage){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();//oldId -> newId
		int index = 1;
		for(int id:set){
			map.put(id, index);
			index += 1;
		}
		
		//create node file
		try{
			BufferedWriter stdout = new BufferedWriter(new FileWriter(nodeFile + "-" + percentage));
			for(int i = 1;i < nodes.length;i ++){
				if(map.containsKey(i)){
					int newId = map.get(i);
					stdout.write(newId + "\t" + nodes[i][0] + "\t");
					for(int j = 1;j < nodes[i].length;j ++){
						stdout.write(nodes[i][j] + " ");
					}
					stdout.newLine();
				}
			}
			stdout.flush();
			stdout.close();
		}catch(Exception e){e.printStackTrace();}
		
		//create graph file
		try{
			BufferedWriter stdout = new BufferedWriter(new FileWriter(graphFile + "-" + percentage));
			for(int i = 1;i < graph.length;i ++){
				if(map.containsKey(i)){
					int newId = map.get(i);
					stdout.write(newId + "");
					for(int j = 0;j < graph[i].length;j ++){
						int neighbor = graph[i][j];
						if(map.containsKey(neighbor)){
							int newNeighbor = map.get(neighbor);
							stdout.write(" " + newNeighbor);
						}
					}
					stdout.newLine();
				}
			}
			stdout.flush();
			stdout.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		GraphSubData data = new GraphSubData(Config.flickrGraph, Config.flickrNode);
//		GraphSubData data = new GraphSubData(Config.dblpGraph, Config.dblpNode, never);
//		GraphSubData data = new GraphSubData(Config.twitterGraph, Config.twitterNode, never);
//		GraphSubData data = new GraphSubData(Config.tencentGraph, Config.tencentNode, never);
//		GraphSubData data = new GraphSubData(Config.dbpediaGraph, Config.dbpediaNode, never);
		data.createAll();
	}

}
