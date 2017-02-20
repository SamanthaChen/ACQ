package hku.variant.variant3;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.TNode;
import hku.algo.index.AdvancedIndex;

/**
 * @author fangyixiang
 * @date Nov 16, 2015
 */
public class Test {

	public static void main(String[] args) {
		DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpNode);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);
		TNode root = index.build();
		int core[] = index.getCore();
		System.out.println("index construction finished !");
		
		int queryId = 15238;
		String inKws[] = {"data", "mine", "analysis", "network"};
		
		BasicGV3 bg3 = new BasicGV3(graph, nodes);
		bg3.query(queryId, inKws);
		
		BasicWV3 bw3 = new BasicWV3(graph, nodes);
		bw3.query(queryId, inKws);
		
		DecV3 dv3 = new DecV3(graph, nodes, root, core);
		dv3.query(queryId, inKws);
	}

}
