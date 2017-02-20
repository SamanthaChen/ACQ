package hku.exp.sim;

import java.util.*;
/**
 * @author fangyixiang
 * @date Oct 29, 2015
 * compute the average document frequency
 */
public class AMFreq {
	private String nodes[][] = null;
	
	public AMFreq(String nodes[][]){
		this.nodes = nodes;
	}
	
	//compute the AMFreq for all the communities
	public double sim(List<Set<Integer>> ccsList, int queryId){
		double sum = 0.0;
		for(Set<Integer> set:ccsList){
			sum += singleSim(set, queryId);
		}
		return sum / ccsList.size();
	}
	
	//compute the AMFreq for a single community
	public double singleSim(Set<Integer> set, int queryId){
		Set<String> qSet = new HashSet<String>();
		for(int i = 1;i < nodes[queryId].length;i ++){
			qSet.add(nodes[queryId][i]);
		}
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(int id:set){
			for(int i = 1;i < nodes[id].length;i ++){
				String word = nodes[id][i];
				if(qSet.contains(word)){//only consider words appearing in q's keyword set
					if(map.containsKey(word)){
						map.put(word, map.get(word) + 1);
					}else{
						map.put(word, 1);
					}
				}
			}
		}
		
		double sum = 0.0;
		for(Map.Entry<String, Integer> entry:map.entrySet()){
			sum += entry.getValue() * 1.0 / set.size(); //document frequency
		}
		
		return sum / qSet.size();
	}
}
