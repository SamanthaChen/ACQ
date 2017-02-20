package hku.exp.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Feb 20, 2016
 */
public class LinkAvgFreq {
	private int graph[][] = null;
	
	public LinkAvgFreq(int graph[][]){
		this.graph = graph;
	}
	
	//compute the LinkFreq for all the communities
	public double freq(List<Set<Integer>> ccsList){
		double sum = 0.0;
		for(Set<Integer> set:ccsList){
			sum += singleLinkFreq(set);
		}
		return sum / ccsList.size();
	}
	
	//compute the APJSim for a single community
	public double singleLinkFreq(Set<Integer> set){
		double edge = 0.0;
		List<Integer> list = new ArrayList<Integer>(set);
		for(int i = 0;i < list.size();i ++){
			int id1 = list.get(i);
			for(int j = 0;j < graph[id1].length;j ++){
				int id2 = graph[id1][j];
				if(set.contains(id2))   edge += 1.0;
			}
		}
		
		return edge / set.size();
	}
}
