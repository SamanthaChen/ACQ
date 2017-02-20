package hku.exp.util;

import hku.Config;
import hku.algo.DataReader;

import java.io.*;
import java.util.*;

/**
 * @author fangyixiang
 * @date Oct 12, 2015
 */
public class KeywordSubData {
	private String graphFile = null;
	private String nodeFile = null;
	private int graph[][];//graph structure
	private String nodes[][];//the keywords of each node
	private Set<Integer> id20Set = null, id40Set = null, id60Set = null, id80Set = null; 
	
	public KeywordSubData(String graphFile, String nodeFile){
		this.graphFile = graphFile;
		this.nodeFile = nodeFile;
		
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		this.nodes = dataReader.readNode();
	}
	
	public void createAll(){
		create(20);
		create(40);
		create(60);
		create(80);
	}
	
	private void create(int percentage){
		//create node file
		try{
			BufferedWriter stdout = new BufferedWriter(new FileWriter(nodeFile + "-only-" + percentage));
			for(int i = 1;i < nodes.length;i ++){
				String kws[] = nodes[i];
				int kwLen = kws.length - 1;
				stdout.write(i + "\t" + kws[0] + "\t");
				
				int topLen = (int)(kwLen * percentage * 0.01);
				for(int j = 1;j <= topLen;j ++){
					stdout.write(nodes[i][j] + " ");
				}
				stdout.newLine();
			}
			stdout.flush();
			stdout.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		KeywordSubData data = new KeywordSubData(Config.flickrGraph, Config.flickrNode);
//		KeywordSubData data = new KeywordSubData(Config.dblpGraph, Config.dblpNode, never);
//		KeywordSubData data = new KeywordSubData(Config.twitterGraph, Config.twitterNode, never);
//		KeywordSubData data = new KeywordSubData(Config.tencentGraph, Config.tencentNode, never);
//		KeywordSubData data = new KeywordSubData(Config.dbpediaGraph, Config.dbpediaNode, never);
		data.createAll();
	}

}
