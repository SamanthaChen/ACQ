package hku.exp.sim;

import java.util.*;
/**
 * @author fangyixiang
 * @date Oct 29, 2015
 * compute the APJSim
 */
public class APJSim {
	private String nodes[][] = null;
	
	public APJSim(String nodes[][]){
		this.nodes = nodes;
	}
	
	//compute the APJSim for all the communities
	public double sim(List<Set<Integer>> ccsList){
		double sum = 0.0;
		for(Set<Integer> set:ccsList){
			sum += singleSim(set);
		}
		return sum / ccsList.size();
	}
	
	//compute the APJSim for a single community
	public double singleSim(Set<Integer> set){
		//this is designed to void enumerating all the possible pairs
		if(set.size() > 1000){
			List<Integer> list = new ArrayList<Integer>(set);
			Set<Integer> tmpSet = new HashSet<Integer>();
			while(tmpSet.size() < 1000){
				Random rand = new Random();
				int index = rand.nextInt(list.size());
				tmpSet.add(list.get(index));
			}
			set = tmpSet;
		}
		
		double simSum = 0.0;
		int count = 0;
		for(int nodeA: set){
			for(int nodeB: set){
				if(nodeA != nodeB){
					double simValue = jaccardSim(nodes[nodeA], nodes[nodeB]);
					simSum += simValue;
					count += 1;
				}
			}
		}
		
		return simSum / count;
	}
	
	//compute the Jaccard similarity
	private double jaccardSim(String a[], String b[]){
		Set<String> set = new HashSet<String>();
        for(int i = 1;i < a.length;i ++)   set.add(a[i]);
       
        double share = 0.0;
        for(int i = 1;i < b.length;i ++){
            if(set.contains(b[i])){
                share += 1;
            }else{
            	set.add(b[i]);
            }
        }
       
        return share / set.size();
	}
}
