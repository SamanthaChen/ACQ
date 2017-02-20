package hku.exp.effect;

import hku.exp.sim.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class CodKeyword {
	private String nodes[][] = null;
	
	public CodKeyword(String nodes[][]){
		this.nodes = nodes;
	}
	
	public void analyze(Set<Integer> set){
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
        Queue<Pair> queue =  new PriorityQueue<Pair>(11, OrderIsdn);
		
        //step 1: get the frequency
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
		for(Map.Entry<String, Double> entry:map.entrySet()){
        	Pair p = new Pair();
        	p.word = entry.getKey();
        	p.value = entry.getValue();
        	queue.add(p);
        }
		
		//step 2: output
		String kws[] = new String[1000000];
		double frq[] = new double[1000000];
		int index = 0;
		while(queue.size() > 0){
			Pair pair = queue.poll();
			kws[index] = pair.word;
			frq[index] = pair.value;
//			System.out.println(kws[index] + "\t" + frq[index]);
			index ++;
		}
		System.out.println();
		
		System.out.println("The community size: " + set.size());
		System.out.println("The number of distinct keywords: " + index);
		
		System.out.println("The top-6 keywords: ");
		for(int i = 0;i < 6;i ++)   System.out.print(kws[i] + " ");
		System.out.println();
		
		for(int i = 0;i < 30;i ++)  System.out.print(frq[i] + " ");
		System.out.println();
	}
	
	private Map<String, Double> single(Set<Integer> set){
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
