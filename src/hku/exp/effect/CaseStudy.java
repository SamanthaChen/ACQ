package hku.exp.effect;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.previous.SIGKDD2010;
import hku.algo.previous.SIGMOD2014;
import hku.algo.query1.IncS;
import hku.algo.query2.Dec;
import hku.exp.util.QueryIdReader;
import hku.variant.variant1.SW;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CaseStudy {

	//show the communities
	public void show(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		Config.k = 4;
		int queryId = 152532;//jim gray
//		int queryId = 15238;//jiawei han
		IncS query = new IncS(graph, nodes, root, core, Config.dblpCCS);
		query.query(queryId);
	}
	
	//measure the 4 metrics
	public void measure(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		Config.k = 4;
		int queryId = 152532;//jim gray
//		int queryId = 15238;//jiawei han
		
		SIGKDD2010 sigkdd = new SIGKDD2010(graph);
		Set<Integer> sigkddSet = sigkdd.query(queryId);
		
		SIGMOD2014 sigmod = new SIGMOD2014(graph, core);
		Set<Integer> sigmodSet = sigmod.query(queryId);
		
		Dec query3 = new Dec(graph, nodes, root, core, null);
		List<Set<Integer>> ccsList = query3.query(queryId);
		
		System.out.println("ADFreq");
		AMFreqCaseStudy adcs = new AMFreqCaseStudy(nodes);
		List<String> sortWordList1 = adcs.sim(ccsList, queryId);
		adcs.singleSim(sigmodSet, queryId, sortWordList1);
		adcs.singleSim(sigkddSet, queryId, sortWordList1);
		
//		System.out.println("AWFreq");
//		AWFreqCaseStudy awcs = new AWFreqCaseStudy(nodes);
//		List<String> sortWordList2 = awcs.sim(ccsList, queryId);
//		awcs.singleSim(sigmodSet, queryId, sortWordList2);
//		awcs.singleSim(sigkddSet, queryId, sortWordList2);
	}
	
	public void reynold(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		Config.k = 4;
//		int queryId = 152532;//jim gray
		int queryId = 15238;//jiawei han
		
		SIGKDD2010 sigkdd = new SIGKDD2010(graph);
		Set<Integer> sigkddSet = sigkdd.query(queryId);
		
		SIGMOD2014 sigmod = new SIGMOD2014(graph, core);
		Set<Integer> sigmodSet = sigmod.query(queryId);
		
		Dec query3 = new Dec(graph, nodes, root, core, null);
		List<Set<Integer>> ccsList = query3.query(queryId);
		
		//step 1: frequency
		AMAllFreqCaseStudy adcs = new AMAllFreqCaseStudy(nodes);
		List<String> sortWordList1 = adcs.sim(ccsList, queryId);
		adcs.singleSim(sigmodSet, queryId, sortWordList1);
		adcs.singleSim(sigkddSet, queryId, sortWordList1);
		
		//step 2: size
		double tmp = 0;
		for(Set<Integer> set:ccsList)   tmp += set.size();
		tmp = tmp / ccsList.size();
		System.out.println("Size Global:" + sigkddSet.size() + " Local:" + sigmodSet.size() + " Dec:" + tmp);
		
		//step 3: distinct words
		Set<String> sigkddWordSet = new HashSet<String>();
		for(int id:sigkddSet){
			for(int i = 1;i < nodes[id].length;i ++){
				sigkddWordSet.add(nodes[id][i]);
			}
		}
		Set<String> sigmodWordSet = new HashSet<String>();
		for(int id:sigmodSet){
			for(int i = 1;i < nodes[id].length;i ++){
				sigmodWordSet.add(nodes[id][i]);
			}
		}
		double sum = 0;
		for(Set<Integer> set:ccsList){
			Set<String> tmpSet = new HashSet<String>();
			for(int id:set){
				for(int i = 1;i < nodes[id].length;i ++){
					tmpSet.add(nodes[id][i]);
				}
			}
			sum += tmpSet.size();
		}
		double avg = sum / ccsList.size();
		System.out.println("Word Global:" + sigkddWordSet.size() + " Local:" + sigmodWordSet.size() + " Dec:" + avg);
		
		//step 4: links
		double link1 = 0.0;
		double maxLink1 = 0.0;
		for(int id:sigkddSet){
			int count = 0;
			for(int nghId:graph[id]){
				if(sigkddSet.contains(nghId)){
					count += 1;
				}
			}
			if(count > maxLink1)   maxLink1 = count;
			link1 += count;
		}
		link1 = link1 / sigkddSet.size();
		double link2 = 0.0;
		double maxLink2 = 0.0;
		for(int id:sigmodSet){
			int count = 0;
			for(int nghId:graph[id]){
				if(sigmodSet.contains(nghId)){
					count += 1;
				}
			}
			if(count > maxLink2)   maxLink2 = count;
			link2 += count;
		}
		link2 = link2 / sigmodSet.size();
		double link3 = 0.0;
		double maxLink3 = 0.0;
		for(Set<Integer> set:ccsList){
			double link = 0.0;
			double maxLink = 0;
			for(int id:set){
				int count = 0;
				for(int nghId:graph[id]){
					if(set.contains(nghId)){
						count += 1;
					}
				}
				if(count > maxLink)   maxLink = count;
				link += count;
			}
			
			System.out.println("link:" + link + " nodes:" + set.size());
			maxLink3 += maxLink;
			link3 += link / set.size();
		}
		link3 = link3 / ccsList.size();
		maxLink3 = maxLink3 / ccsList.size();
		System.out.println("avg degree Global:" + link1 + " Local:" + link2 + " Dec:" + link3);
		System.out.println("max degree Global:" + maxLink1 + " Local:" + maxLink2 + " Dec:" + maxLink3);
	}
	
	public void variant1(){
		DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpNode);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		Config.k = 4;
		
//		int queryId = 152532;//jim gray
		int queryId = 15238;//jiawei han
		
		String kws[] = {"data", "cube"};
		SW sw = new SW(graph, nodes, root, core);
		sw.query(queryId, kws);
	}
	
	public static void main(String[] args) {
		CaseStudy cs = new CaseStudy();
//		cs.show(Config.dblpGraph, Config.dblpNode);
//		cs.measure(Config.dblpGraph, Config.dblpNode);
//		cs.reynold(Config.dblpGraph, Config.dblpNode);
		cs.variant1();
	}

}


/*
information
stream
multidimensional
data
object
analysis
frequent
database
network
graph
pattern
classification
approach
cluster
mine
cube
recursion
discovery
efficient
heterogeneous
*/