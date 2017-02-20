package hku.algo;

import hku.Config;
import hku.algo.index.AdvancedIndex;
import hku.algo.query1.IncS;

/**
 * @author Ximan Chen
 * @date  2.20, 2017
 */
public class ChenTestMain {

	public static void main(String[] args) {
		
		String graphfile = "E:/ACQ/Datasets/toy-graph";
		String nodefile = "E:/ACQ/Datasets/toy-node";//邻接矩阵的格式
		DataReader dataReader = new DataReader(graphfile, nodefile); 
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);//索引选择advanced索引
		TNode root = index.build(); //建立索引
		int core[] = index.getCore();//获得所有节点的coreness
		
		Config.k = 1;//设置要求的k
		
		int queryId = 1;//查询节点id
		long time1 = System.currentTimeMillis();
		IncS query1 = new IncS(graph, nodes, root, core, null);//采用的索引
		int size1 = query1.query(queryId);
		long time2 = System.currentTimeMillis();
		System.out.println("time cost:" + (time2 - time1));
		System.out.println("size:"+size1);
	}

}
