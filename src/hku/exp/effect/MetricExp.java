package hku.exp.effect;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.previous.SIGKDD2010;
import hku.algo.previous.SIGMOD2014;
import hku.algo.query2.Dec;
import hku.exp.sim.AMFreq;
import hku.exp.sim.APJSim;
import hku.exp.sim.LinkAvgFreq;
import hku.exp.sim.LinkMinFreq;
import hku.exp.util.QueryIdReader;
import hku.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Oct 29, 2015
 */
public class MetricExp {
	
	public void exp(String graphFile, String nodeFile){
		Config.k = 6;
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		String queryFile = graphFile + "-query=100";
		QueryIdReader qReader = new QueryIdReader();
		List<Integer> queryIdList = qReader.read(queryFile);
		
		Log.log(graphFile + " effectiveness test");
		
		APJSim apj = new APJSim(nodes);
		AMFreq amf = new AMFreq(nodes);
		LinkAvgFreq laf = new LinkAvgFreq(graph);
		LinkMinFreq lmf = new LinkMinFreq(graph);
		double aqjSum1 = 0.0, aqjSum2 = 0.0, aqjSum3 = 0.0,
			   apjSum1 = 0.0, apjSum2 = 0.0, apjSum3 = 1.0,
			   awfSum1 = 0.0, awfSum2 = 0.0, awfSum3 = 0.0,
			   amfSum1 = 0.0, amfSum2 = 0.0, amfSum3 = 0.0,
			   lafSum1 = 0.0, lafSum2 = 0.0, lafSum3 = 0.0,
			   lmfSum1 = 0.0, lmfSum2 = 0.0, lmfSum3 = 0.0;
		
		int count = 0;
		for(int i = 0;i < queryIdList.size();i ++){
			int queryId = queryIdList.get(i);
			System.out.println("i:" + i + " queryId:" + queryId + " " + (new Date()).toLocaleString());
			
			Dec query3 = new Dec(graph, nodes, root, core, null);
			List<Set<Integer>> ccsList = query3.query(queryId);
			
			if(ccsList != null && ccsList.size() > 0){
				count += 1;
				
				SIGKDD2010 sigkdd = new SIGKDD2010(graph);
				Set<Integer> sigkddSet = sigkdd.query(queryId);
				
				SIGMOD2014 sigmod = new SIGMOD2014(graph, core);
				Set<Integer> sigmodSet = sigmod.query(queryId);
				
				apjSum1 += apj.singleSim(sigkddSet);
				apjSum2 += apj.singleSim(sigmodSet);
				apjSum3 += apj.sim(ccsList);
				
				amfSum1 += amf.singleSim(sigkddSet, queryId);
				amfSum2 += amf.singleSim(sigmodSet, queryId);
				amfSum3 += amf.sim(ccsList, queryId);
				
				lafSum1 += laf.singleLinkFreq(sigkddSet);
				lafSum2 += laf.singleLinkFreq(sigmodSet);
				lafSum3 += laf.freq(ccsList);
				
				lmfSum1 += lmf.singleLinkFreq(sigkddSet);
				lmfSum2 += lmf.singleLinkFreq(sigmodSet);
				lmfSum3 += lmf.freq(ccsList);
			}
		}
		Log.log("count:" + count);
		Log.log("APJSim:" + apjSum1 / count + " " + apjSum2 / count + " " + apjSum3 / count);
		Log.log("AMFreq:" + amfSum1 / count + " " + amfSum2 / count + " " + amfSum3 / count);
		Log.log("LAFreq:" + lafSum1 / count + " " + lafSum2 / count + " " + lafSum3 / count);
		Log.log("LMFFreq:" + lmfSum1 / count + " " + lmfSum2 / count + " " + lmfSum3 / count);
		Log.log("\n");
	}
	
	public static void main(String args[]){
		MetricExp exp = new MetricExp();
		exp.exp(Config.flickrGraph, Config.flickrNode);
		exp.exp(Config.dblpGraph, Config.dblpNode);
		exp.exp(Config.tencentGraph, Config.tencentNode);
		exp.exp(Config.dbpediaGraph, Config.dbpediaNode);
	}
}
