package hku.exp.effect;

import hku.exp.sim.Pair;

import java.util.*;
/**
 * @author fangyixiang
 * @date Oct 29, 2015
 * compute the average document frequency for all the keywords involved
 */
public class AMAllFreqCaseStudy {
	private String nodes[][] = null;
	
	public AMAllFreqCaseStudy(String nodes[][]){
		this.nodes = nodes;
	}
	
	//compute the AWFreq for all the communities
	public List<String> sim(List<Set<Integer>> ccsList, int queryId){
		List<Map<String, Double>> list = new ArrayList<Map<String, Double>>();
		for(Set<Integer> set:ccsList){
			Map<String, Double> map = single(set, queryId);
			list.add(map);
		}
		
		Set<String> set = new HashSet<String>();
		for(Map<String, Double> map:list){
			for(String word:map.keySet()){
				set.add(word);
			}
		}
		
		Map<String, Double> map = new HashMap<String, Double>();
		for(String word:set){
			double sum = 0.0;
			for(Map<String, Double> mp:list){
				if(mp.containsKey(word)){
					sum += mp.get(word);
				}
			}
			map.put(word, sum / list.size());
		}
		
		Comparator<Pair> OrderIsdn =  new Comparator<Pair>(){  
            public int compare(Pair o1, Pair o2) {  
                if(o2.value > o1.value){
                	return 1;
                }else if(o2.value < o1.value){
                	return -1;
                }else{
                	return 0;
                }
            }  
        };  
        Queue<Pair> priorityQueue =  new PriorityQueue<Pair>(11, OrderIsdn);
		
        for(Map.Entry<String, Double> entry:map.entrySet()){
        	Pair p = new Pair();
        	p.word = entry.getKey();
        	p.value = entry.getValue();
        	priorityQueue.add(p);
        }
        
        System.out.println("priorityQueue.size:" + priorityQueue.size());
        int count = 0;
        List<String> sortWordList = new ArrayList<String>();
		while(priorityQueue.size() > 0){
			Pair p = priorityQueue.poll();
			sortWordList.add(p.word);
//			System.out.println(p.word + " " + p.value);
			System.out.print(p.value + " ");
			
			count += 1;
			if(count >= 50)   break;
		}
		System.out.println("\n");
		
		return sortWordList;
	}
	
	public void singleSim(Set<Integer> set, int queryId, List<String> sortWordList){
		Map<String, Double> map = single(set, queryId);
		
		Comparator<Pair> OrderIsdn =  new Comparator<Pair>(){  
            public int compare(Pair o1, Pair o2) {  
                if(o2.value > o1.value){
                	return 1;
                }else if(o2.value < o1.value){
                	return -1;
                }else{
                	return 0;
                }
            }  
        };  
        Queue<Pair> priorityQueue =  new PriorityQueue<Pair>(11, OrderIsdn);
		
        for(Map.Entry<String, Double> entry:map.entrySet()){
        	Pair p = new Pair();
        	p.word = entry.getKey();
        	p.value = entry.getValue();
        	priorityQueue.add(p);
        }
        
        System.out.println("priorityQueue.size:" + priorityQueue.size());
        int count = 0;
        while(priorityQueue.size() > 0){
			Pair p = priorityQueue.poll();
//			System.out.println(p.word + " " + p.value);
			System.out.print(p.value + " ");
			count += 1;
			if(count >= 50)   break;
		}
        System.out.println("\n");
	}
	
	private Map<String, Double> single(Set<Integer> set, int queryId){
		Map<String, Double> map = new HashMap<String, Double>();
		
		for(int id:set){
			for(int i = 1;i < nodes[id].length;i ++){
				String word = nodes[id][i];
				if(map.containsKey(word)){//consider all the keywords
					map.put(word, map.get(word) + 1);
				}else{
					map.put(word, 1.0);
				}
			}
		}
		
		for(Map.Entry<String, Double> entry:map.entrySet()){
			double freq = entry.getValue() / set.size();
			map.put(entry.getKey(), freq);
		}
		return map;
	}
}
