package hku.exp.util;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.KCore;

import java.util.*;
import java.io.*;
/**
 * @author fangyixiang
 * @date Oct 13, 2015
 * Given a dataset, we target to generate a set of node names, that are shared by all its sub-datasets
 * This will ensure that, for a given queryId, its running time on a smaller dataset should be less than that of large dataset
 */
public class GenerateKQueryId {
	private String graphFile = null;
	private String nodeFile = null;
	
	public void generate(String graphFile, String nodeFile, int smpNum){
		this.graphFile = graphFile;
		this.nodeFile = nodeFile;
		
		DataReader dataReader = new DataReader(graphFile + "-20", nodeFile + "-20");
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		System.out.println(graphFile + "-20 has nodes:" + graph.length);
		
		KCore kcore = new KCore(graph);
		int core[] = kcore.decompose();
		
		int allLen = 0;
		Set<String>  nameSet = new HashSet<String>();
		Set<Integer> idSet = new HashSet<Integer>();
		while(true){
			Random rand = new Random();
			int id = rand.nextInt(graph.length - 1) + 1;
			if(!idSet.contains(id)){
				if(core[id] >= Config.k){
					if(graphFile.contains("dbpedia") && nodes[id][0].length() <= 21){
						nameSet.add(nodes[id][0]);
						idSet.add(id);
						allLen += nodes[id].length - 1;
					}else{
						nameSet.add(nodes[id][0]);
						idSet.add(id);
						allLen += nodes[id].length - 1;
					}
				}
			}
			if(idSet.size() >= smpNum)   break;
		}
		System.out.println("The average length is " + allLen * 1.0 / idSet.size());
		
		List<String> id20List = generate(nameSet, 20);
		List<String> id40List = generate(nameSet, 40);
		List<String> id60List = generate(nameSet, 60);
		List<String> id80List = generate(nameSet, 80);
		List<String> id100List = generate(nameSet, 100);
		
		
		save(id20List, graphFile + "-query=20");
		save(id40List, graphFile + "-query=40");
		save(id60List, graphFile + "-query=60");
		save(id80List, graphFile + "-query=80");
		save(id100List, graphFile + "-query=100");
	}
	
	private List<String> generate(Set<String> nameSet, int percentage){
		List<String> idXXList = new ArrayList<String>();
		
		DataReader dataReader = null;
		if(percentage == 100){
			dataReader = new DataReader(graphFile, nodeFile);
		}else{
			dataReader = new DataReader(graphFile + "-" + percentage, nodeFile + "-" + percentage);
		}
		String nodes[][] = dataReader.readNode();
		
		for(int i = 1;i < nodes.length;i ++){
			if(nameSet.contains(nodes[i][0])){
				idXXList.add(i + " " + nodes[i][0]);
			}
		}
		
		System.out.println("percentage:" + percentage + " size:" + idXXList.size());
		
		return idXXList;
	}
	
	private void save(List<String> idXXList, String file){
		try{
			BufferedWriter stdout = new BufferedWriter(new FileWriter(file));
			for(int i = 0;i < idXXList.size();i ++){
				String idStr = idXXList.get(i);
				stdout.write(idStr);
				stdout.newLine();
			}
			stdout.flush();
			stdout.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		GenerateKQueryId generator = new GenerateKQueryId();
		
//		generator.generate(Config.flickrGraph, Config.flickrNode, Config.qIdNum, never);
//		generator.generate(Config.dblpGraph, Config.dblpNode, Config.qIdNum, never);
//		generator.generate(Config.twitterGraph, Config.twitterNode, never);
//		generator.generate(Config.tencentGraph, Config.tencentNode, Config.qIdNum, never);
		generator.generate(Config.dbpediaGraph, Config.dbpediaNode, 100);
	}

}
