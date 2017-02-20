package hku.algo;

import hku.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * @author ximanchen
 * @date 2.18, 2015
 * (1) read the node information  
 * 1. 读节点信息
 * (2) read the node and edges (Nodes are named starting from 1, 2, 3, ...)
 * 2. 读节点和边
 */
public class ChenDataReader {
	private String graphFile = null;
	private String nodeFile = null;
	private int userNum = -1;
	private int edgeNum = -1;
	
	public ChenDataReader(String graphFile, String nodeFile){
		this.graphFile = graphFile;
		this.nodeFile = nodeFile;
		
		try{
			File test= new File(nodeFile);
			long fileLength = test.length(); 
			LineNumberReader rf = new LineNumberReader(new FileReader(test));
			if (rf != null) {
				rf.skip(fileLength);
				userNum = rf.getLineNumber();//obtain the number of nodes
			}
			rf.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.print(nodeFile);
		System.out.println(" the # of nodes in G:" + userNum);
	}
	
	//return the user's keyword information
	public String[][] readNode(){
		//NOTICE: users[i][0] is the i-th user's name
		double len = 0.0;//count the number of keywords
		String users[][] = new String[userNum + 1][];
		String line = null;
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(nodeFile));
			
			while((line = stdin.readLine()) != null){
				line = line.trim();//2015-10-21 bug
				String userIdString = line.substring(0, line.indexOf('\t'));
				int userId = Integer.parseInt(userIdString);
						
				line = line.substring(line.indexOf('\t') + 1);
				String username = line;
				if(line.indexOf('\t') >= 0){ //have keywords
					username = line.substring(0, line.indexOf('\t'));
				}
				line = line.substring(line.indexOf('\t') + 1);

				String kw[] = line.trim().split(" ");
				len += kw.length;
				
				users[userId] = new String[kw.length + 1];
				users[userId][0] = username;
				for(int i = 0;i < kw.length;i ++){
					users[userId][i + 1] = kw[i];
				}
			}
			stdin.close();
		}catch(Exception e){
			System.out.println("line:" + line);
			e.printStackTrace();
		}
		System.out.println("the avg # of keywords in each node:" + (len / userNum));
		return users;
	}

	//return the graph edge information
	public int[][] readGraph(){
		int edges = 0;
		int graph[][] = new int[userNum + 1][];
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(graphFile));
						
			String line = null;
			while((line = stdin.readLine()) != null){
				String s[] = line.split(" ");//图是空格分割的
				int userId = Integer.parseInt(s[0]);
				graph[userId] = new int[s.length - 1];//数组长度固定了？
				for(int i = 1;i < s.length;i ++){
					graph[userId][i - 1] = Integer.parseInt(s[i]);
				}
				edges += graph[userId].length;
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.print(graphFile);
		System.out.println(" the # of edges in G:" + edges);
		System.out.println("the average degree:" + (edges * 1.0 / userNum));
		
		edgeNum = edges / 2;
		
		return graph;
	}
	
	public int getUserNum() {
		return userNum;
	}
	
	public int getEdgeNum(){
		return edgeNum;
	}

	public static void main(String[] args) {
		ChenDataReader dataReader = new ChenDataReader(Config.flickrGraph, Config.flickrNode);
		String users[][] = dataReader.readNode();
		int graph[][] = dataReader.readGraph();
//		
//		int nodeId = 7786;
//		String kw[] = users[nodeId];
//		for(int i = 0;i < users[nodeId].length;i ++){
//			int neighbor = graph[nodeId][i];
//			String tmpKw[] = users[neighbor];
//			
//			String out = "";
//			for(int j = 0;j < kw.length;j ++){
//				for(int k = 0;k < tmpKw.length;k ++){
//					if(kw[j].equals(tmpKw[k])){
//						out += kw[j] + " ";
//					}
//				}
//			}
//			
//			System.out.println(users[nodeId][0] + " * " + users[neighbor][0] + ": " + out);
//		}
//		System.out.println();
//		
//		System.out.println(graph[nodeId].length);
	}

}
