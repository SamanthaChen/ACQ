package hku.exp.util;

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
 * test the variance of keywords
 */
public class SubKwQueryTest {

	public void exp(String graphFile, String nodeFile){
		Config.k = 6;
		singleExp(graphFile, nodeFile + "-only-20", graphFile + "-query");
		singleExp(graphFile, nodeFile, graphFile + "-query");
	}
	
	private void singleExp(String graphFile, String nodeFile, String queryFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		
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
			System.out.print("count:" + count);
			System.out.print(" queryId:" + queryId);
			System.out.print(" Inc-S:" + (time2 - time1));
			System.out.print(" Inc-T:" + (time4 - time3)); 
		    System.out.println(" NGH:" + (time6 - time5) + "\n\n");
		    
		    if(count > 10)   break;
		}
	}
	
	public static void main(String[] args) {
		SubKwQueryTest exp = new SubKwQueryTest();
//		exp.exp(Config.dblpGraph, Config.dblpNode);
//		exp.exp(Config.twitterGraph, Config.twitterNode);
		exp.exp(Config.tencentGraph, Config.tencentNode);
//		exp.exp(Config.dbpediaGraph, Config.dbpediaNode);
	}

}
