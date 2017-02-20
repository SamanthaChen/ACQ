package hku.algo.previous;

import hku.Config;
import hku.algo.DataReader;
import hku.exp.sim.LinkAvgFreq;
import hku.exp.sim.LinkMinFreq;

import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Feb 19, 2016
 * analyze the communities
 */
public class CodicilEva {

	public static void main(String[] args) {
		Config.clusterK = 1000;
		
		CodicilQuery cq = new CodicilQuery(Config.dblpGraph, Config.dblpNode);
		List<Set<Integer>> list = cq.list;
		
		DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpNode);
		int graph[][] = dataReader.readGraph();
		
		
		LinkAvgFreq laf = new LinkAvgFreq(graph);
		LinkMinFreq lmf = new LinkMinFreq(graph);
		
		
		for(int i = 0;i < list.size();i ++){
			Set<Integer> set = list.get(i);
			double avg = laf.singleLinkFreq(set);
			double min = lmf.singleLinkFreq(set);
			System.out.println("cluster i:" + i + " avg:" + avg + " min:" + min);
		}
	}

}
