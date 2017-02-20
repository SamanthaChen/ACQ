package hku.exp;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.previous.SIGKDD2010;
import hku.algo.previous.SIGMOD2014;
import hku.algo.query1.IncS;
import hku.algo.query1.IncT;
import hku.algo.query2.Dec;
import hku.exp.util.QueryIdReader;
import hku.util.Log;

import java.util.*;

/**
 * @author fangyixiang
 * @date Oct 27, 2015
 * Comparison between three methods: SIGKDD2010, SIGMOD2014, Dec
 */
public class CompareExp {
	
	public void exp(String graphFile, String nodeFile){
		Config.k = 4;
		singleExp(graphFile, nodeFile);
		
		Config.k = 5;
		singleExp(graphFile, nodeFile);
		
		Config.k = 6;
		singleExp(graphFile, nodeFile);
		
		Config.k = 7;
		singleExp(graphFile, nodeFile);
		
		Config.k = 8;
		singleExp(graphFile, nodeFile);
	}

	private void singleExp(String graphFile, String nodeFile){
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
		
		double q1 = 0, q2 = 0, q3 = 0;
		int count = 0;
		for(int queryId:queryIdList){
			count += 1;
			System.out.println("count:" + count + " queryId:" + queryId + " k:" + Config.k); 
			
			long time1 = System.nanoTime();
			SIGKDD2010 sigkdd = new SIGKDD2010(graph);
			sigkdd.query(queryId);
			long time2 = System.nanoTime();
			q1 += time2 - time1;
//			System.out.println("SIGKDD2010:" + (time2 - time1) / 1000000);
			
			long time3 = System.nanoTime();
			SIGMOD2014 sigmod = new SIGMOD2014(graph, core);
			sigmod.query(queryId);
			long time4 = System.nanoTime();
			q2 += time4 - time3;
//			System.out.println("SIGMOD2014:" + (time4 - time3) / 1000000);
			
			long time5 = System.nanoTime();
			Dec query3 = new Dec(graph, nodes, root, core, null);
			query3.query(queryId);
			long time6 = System.nanoTime();
			q3 += time6 - time5;
//			System.out.println("Dec:" + (time6 - time5) / 1000000);
			
			if(count == 1){
				Log.log(graphFile + " comparison Config.k=" + Config.k);
			}else if(count % 100 == 0){
				Log.log(graphFile + " count:" + count
						+ " SIGKDD2010:" + q1 / 1000000 / count
						+ " SIGMOD2014:" + q2 / 1000000 / count 
						+ " CAC:" + q3 / 1000000 / count);
				if(count == queryIdList.size())   Log.log("\n");
			}
		}
	}
	
	public static void main(String[] args) {
		CompareExp exp = new CompareExp();
//		exp.exp(Config.dblpGraph, Config.dblpNode);
		exp.exp(Config.flickrGraph, Config.flickrNode);
		exp.exp(Config.tencentGraph, Config.tencentNode);
	}

}
