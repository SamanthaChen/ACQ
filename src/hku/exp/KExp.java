package hku.exp;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.query1.IncS;
import hku.algo.query1.IncT;
import hku.algo.query2.*;
import hku.exp.util.QueryIdReader;
import hku.algo.online.BasicG;
import hku.algo.online.BasicW;

import java.util.*;

import hku.util.*;
/**
 * @author fangyixiang
 * @date Oct 13, 2015
 * test the variance of k
 */
public class KExp {

	public void expIndex(String graphFile, String nodeFile){
		Config.k = 4;
		singleIndex(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 5;
		singleIndex(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 6;
		singleIndex(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 7;
		singleIndex(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 8;
		singleIndex(graphFile, nodeFile, graphFile + "-query=100");
	}
	
	private void singleIndex(String graphFile, String nodeFile, String queryFile){
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
		}
	}
	
	
	public void expBasic(String graphFile, String nodeFile){
		Config.k = 4;
		singleBasic(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 5;
		singleBasic(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 6;
		singleBasic(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 7;
		singleBasic(graphFile, nodeFile, graphFile + "-query=100");
		
		Config.k = 8;
		singleBasic(graphFile, nodeFile, graphFile + "-query=100");
	}
	
	public void singleBasic(String graphFile, String nodeFile, String queryFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		QueryIdReader qReader = new QueryIdReader();
		List<Integer> queryIdList = qReader.read(queryFile);
		
		double q4 = 0, q5 = 0, q6 = 0;
		int count = 0;
		for(int i = 0;i < queryIdList.size();i ++){
			int queryId = queryIdList.get(i);
			if(graphFile.contains("dblp") && i < 200)  continue;
			
			count += 1;
			System.out.println("count:" + count + " queryId:" + queryId + " k:" + Config.k); 
			
			long time1 = System.nanoTime();
			BasicG base1 = new BasicG(graph, nodes);
			base1.query(queryId);
			long time2 = System.nanoTime();
			q4 += time2 - time1;
			
			long time3 = System.nanoTime();
			BasicW base2 = new BasicW(graph, nodes);
			base2.query(queryId);
			long time4 = System.nanoTime();
			q5 += time4 - time3;
		}
		
		Log.log(graphFile + " count:" + count + " Config.k:" + Config.k 
						+ " basic-g:" + q4 / 1000000 / count
						+ " basic-w:" + q5 / 1000000 / count);
		Log.log("\n");
	}
	
	public static void main(String[] args) {
//		KExp exp = new KExp();
//		exp.expIndex(Config.dblpGraph, Config.dblpNode);
//		exp.expIndex(Config.twitterGraph, Config.twitterNode);
//		exp.expIndex(Config.tencentGraph, Config.tencentNode);
//		exp.expIndex(Config.dbpediaGraph, Config.dbpediaNode);
		
		KExp exp = new KExp();
//		exp.expBasic(Config.dblpGraph, Config.dblpNode);
//		exp.expBasic(Config.twitterGraph, Config.twitterNode);
//		exp.expBasic(Config.tencentGraph, Config.tencentNode);
		exp.expBasic(Config.dbpediaGraph, Config.dbpediaNode);
	}

}
