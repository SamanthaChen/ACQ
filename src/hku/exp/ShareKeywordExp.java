package hku.exp;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;
import hku.algo.query1.IncS;
import hku.algo.query1.IncT;
import hku.algo.query2.Dec;
import hku.exp.util.QueryIdReader;
import hku.exp.util.ShareKeywords;
import hku.util.Log;

import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Oct 20, 2015
 */
public class ShareKeywordExp {

	public void exp(String graphFile, String nodeFile){
		Config.k = 6;
		
		singleExp(graphFile + "-20", nodeFile + "-20", graphFile + "-query=20");
		singleExp(graphFile + "-40", nodeFile + "-40", graphFile + "-query=40");
		singleExp(graphFile + "-60", nodeFile + "-60", graphFile + "-query=60");
		singleExp(graphFile + "-80", nodeFile + "-80", graphFile + "-query=80");
		singleExp(graphFile, nodeFile, graphFile + "-query=100");
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
		int sum[] = new int[1000];
		int count = 0;
		for(int queryId:queryIdList){
			ShareKeywords sk = new ShareKeywords(graph, nodes, root, core, null);
			int rs[] = sk.query(queryId);
			if(rs != null && rs.length >= 0){
				for(int i = 0;i < rs.length;i ++){
					sum[i] += rs[i];
				}
				count += 1;
			}
		}
		
		Log.log(graphFile + " # of shared keywords are as follows:");
		for(int i = 1;i < sum.length;i ++){
			if(sum[i] > 0){
				Log.log("shareLen:" + i + " vertexNum:" + (sum[i] * 1.0 / count));			}
		}
		Log.log("\n\n");
	}

	
	public static void main(String[] args) {
		ShareKeywordExp exp = new ShareKeywordExp();
//		exp.exp(Config.dblpGraph, Config.dblpNode);
		exp.exp(Config.twitterGraph, Config.twitterNode);
		exp.exp(Config.tencentGraph, Config.tencentNode);
		exp.exp(Config.dbpediaGraph, Config.dbpediaNode);
	}

}
