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
		String nodefile = "E:/ACQ/Datasets/toy-node";//�ڽӾ���ĸ�ʽ
		DataReader dataReader = new DataReader(graphfile, nodefile); 
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		AdvancedIndex index = new AdvancedIndex(graph, nodes);//����ѡ��advanced����
		TNode root = index.build(); //��������
		int core[] = index.getCore();//������нڵ��coreness
		
		Config.k = 1;//����Ҫ���k
		
		int queryId = 1;//��ѯ�ڵ�id
		long time1 = System.currentTimeMillis();
		IncS query1 = new IncS(graph, nodes, root, core, null);//���õ�����
		int size1 = query1.query(queryId);
		long time2 = System.currentTimeMillis();
		System.out.println("time cost:" + (time2 - time1));
		System.out.println("size:"+size1);
	}

}
