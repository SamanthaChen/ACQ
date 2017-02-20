package hku.exp.effect;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.previous.CodicilQuery;
import hku.algo.query2.Dec;
import hku.exp.sim.AMFreq;
import hku.exp.sim.APJSim;
import hku.exp.sim.LinkAvgFreq;
import hku.exp.sim.LinkMinFreq;
import hku.exp.sim.LinkPent;
import hku.exp.util.QueryIdReader;
import hku.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Feb 20, 2016
 */
public class CodicilEffect {
	public void exp(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		String queryFile = graphFile + "-query=100";
		QueryIdReader qReader = new QueryIdReader();
		List<Integer> queryIdList = qReader.read(queryFile);
		
		Log.log(graphFile + " effectiveness test");

//		int arr[] = {2500, 5000, 7500, 10000};
//		int arr[] = {100, 1000, 10000, 100000};
		int arr[] = {1000, 5000, 10000, 50000, 100000};
		for(int clusterNum:arr){
			Config.clusterK = clusterNum;
			CodicilQuery query = new CodicilQuery(graphFile, nodeFile);
			expCluster(graph, nodes, queryIdList, query, clusterNum);
		}
	}
	
	private void expCluster(int graph[][], String nodes[][], List<Integer> queryIdList, 
			CodicilQuery query, int clusterNum){
		APJSim apj = new APJSim(nodes);
		AMFreq adf = new AMFreq(nodes);
		LinkAvgFreq laf = new LinkAvgFreq(graph);
		LinkPent lp = new LinkPent(graph);
		double apjSum = 0.0, amfSum = 0.0, lafSum = 0.0, pentSum = 0.0;
//		double link[] = new double[10000];
		
//		double linkDec[] = new double[10000];
		double pentDecSum = 0.0;
		double lafDecSum = 0.0;
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		double allNum = 0.0;
		int count = 0;
		for(int i = 0;i < queryIdList.size();i ++){
			int queryId = queryIdList.get(i);
			
			Dec query3 = new Dec(graph, nodes, root, core, null);
			List<Set<Integer>> ccsList = query3.query(queryId);
			
			if(ccsList != null && ccsList.size() > 0){
				count += 1;
				
				Set<Integer> set = query.query(queryId);
				
				System.out.println("i:" + i + " queryId:" + queryId + " size:" + set.size()
				   + " " + (new Date()).toLocaleString());
				
				allNum += set.size();
				apjSum += apj.singleSim(set);
				amfSum += adf.singleSim(set, queryId);
				pentSum += lp.singleLinkFreq(set);
				lafSum += laf.singleLinkFreq(set);
				
				pentDecSum += lp.freq(ccsList);
				lafDecSum += laf.freq(ccsList);
				
//				double arr[] = lf.singleLinkFreq(set);
//				for(int j = 0;j < arr.length;j ++)   link[j] += arr[j];
//				
//				arr = lf.freq(ccsList);
//				for(int j = 0;j < arr.length;j ++)   linkDec[j] += arr[j];
			}
		}
//		for(int i = 0;i < link.length;i ++)   link[i] = link[i] / count;
//		for(int i = 0;i < linkDec.length;i ++)   linkDec[i] = linkDec[i] / count;
		
		Log.log("ClusterNum:" + clusterNum  + "   count:" + count);
		Log.log("Average size:" + (allNum / count));
		Log.log("CPJSim:" + apjSum / count);
		Log.log("CMFreq:" + amfSum / count);
		Log.log("pent:" + pentSum / count);
		Log.log("pentLAC:" + pentDecSum / count);
		Log.log("linkAvg:" + lafSum / count);
		Log.log("linkAvgLAC:" + lafDecSum / count);
		
//		String linkStr = link[0] + "";
//		for(int i = 1;i <= 30;i ++)   linkStr += " " + link[i];
//		Log.log("Codicil degree distribution:" + linkStr);
//		
//		String linkDecStr = linkDec[0] + "";
//		for(int i = 1;i <= 30;i ++)   linkDecStr += " " + linkDec[i];
//		Log.log("Dec degree distribution:" + linkDecStr);
		
		Log.log("\n");
	}
	
	
	

	public static void main(String[] args) {
		CodicilEffect ce = new CodicilEffect();
		ce.exp(Config.flickrGraph, Config.flickrNode);
		ce.exp(Config.dblpGraph, Config.dblpNode);
//		ce.exp(Config.tencentGraph, Config.tencentNode);
//		ce.exp(Config.dbpediaGraph, Config.dbpediaNode);
	}

}
