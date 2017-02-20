package hku.exp.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Feb 20, 2016
 */
public class LinkPent {
	private int graph[][] = null;
	
	public LinkPent(int graph[][]){
		this.graph = graph;
	}
	
	//compute the MinLinkFreq for all the communities
	public double freq(List<Set<Integer>> ccsList){
		double sum = 0.0;
		for(Set<Integer> set:ccsList){
			double pent = singleLinkFreq(set);
			sum += pent;
		}
		
		return sum / ccsList.size();
	}
	
	//compute the MinLinkFreq for a single community
	public double singleLinkFreq(Set<Integer> set){
		double all6 = 0;
		List<Integer> list = new ArrayList<Integer>(set);
		for(int i = 0;i < list.size();i ++){
			int id1 = list.get(i);
			int count = 0;
			for(int j = 0;j < graph[id1].length;j ++){
				int id2 = graph[id1][j];
				if(set.contains(id2))   count += 1;
			}
			if(count >= 6)   all6 += 1;
		}
		
		return all6 / set.size();
	}
}
