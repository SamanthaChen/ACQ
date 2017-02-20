package hku.exp;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.query1.IncS;
import hku.algo.query1.IncT;
import hku.algo.query2.*;
import hku.exp.util.QueryIdReader;

import java.util.*;

import hku.util.*;
/**
 * @author fangyixiang
 * @date Oct 13, 2015
 * test the variance of graphs
 */
public class SubGraphQueryExp {

	public void exp(String graphFile, String nodeFile){
		Config.k = 6;
		
		singleExp(graphFile + "-20", nodeFile + "-20", graphFile + "-query=20");
		singleExp(graphFile + "-40", nodeFile + "-40", graphFile + "-query=40");
		singleExp(graphFile + "-60", nodeFile + "-60", graphFile + "-query=60");
		singleExp(graphFile + "-80", nodeFile + "-80", graphFile + "-query=80");
//		singleExp(graphFile, nodeFile, graphFile + "-query=100");
	}
	
	private void singleExp(String graphFile, String nodeFile, String queryFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		QueryIdReader qReader = new QueryIdReader();
		List<Integer> queryIdList = qReader.read(queryFile);
		
		double q1 = 0, q2 = 0, q3 = 0;
		int count = 0;
		for(int queryId:queryIdList){
			long time1 = System.nanoTime();
			IncS query1 = new IncS(graph, nodes, root, core, null);
			int size1 = query1.query(queryId);
			long time2 = System.nanoTime();
			q1 += time2 - time1;
			
			long time3 = System.nanoTime();
			IncT query2 = new IncT(graph, nodes, root, core, null);
			int size2 = query2.query(queryId);
			long time4 = System.nanoTime();
			q2 += time4 - time3;
			
			long time5 = System.nanoTime();
			Dec query3 = new Dec(graph, nodes, root, core, null);
			query3.query(queryId);
			long time6 = System.nanoTime();
			q3 += time6 - time5;
			
			count += 1;
			if(count == 1){
				Log.log(graphFile + " Config.k=" + Config.k);
			}else if(count % 100 == 0){
				Log.log("count:" + count
						+ " Inc-S:" + q1 / 1000000 / count
						+ " Inc-T:" + q2 / 1000000 / count
						+ " Dec:" + q3 / 1000000 /count);
				if(count == queryIdList.size())   Log.log("\n");
			}
			
//			Log.log("count:" + count 
//					+ " queryName:" + nodes[queryId][0] 
//					+ " Inc-S:" + (time2 - time1) / 1000000
//					+ " Inc-T:" + (time4 - time3) / 1000000 
//					+ " Dec:" + (time6 - time5) / 1000000);
		}
	}
	
	public static void main(String[] args) {
		SubGraphQueryExp exp = new SubGraphQueryExp();
		exp.exp(Config.dblpGraph, Config.dblpNode);
//		exp.exp(Config.twitterGraph, Config.twitterNode);
//		exp.exp(Config.tencentGraph, Config.tencentNode);
//		exp.exp(Config.dbpediaGraph, Config.dbpediaNode);
	}

}
