package hku.algo.query1;

import hku.Config;
import hku.algo.AprioriPruner;
import hku.algo.FindCC;
import hku.algo.FindCCS;
import hku.algo.FindCKCore;
import hku.algo.KCore;
import hku.algo.TNode;
import hku.util.CCSSaver;
import hku.util.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Aug 17, 2015
 */
public class IncS {
	private int graph[][];//graph structure
	private String nodes[][];//the keywords of each node
	private TNode root;//the built index
	private int core[];
	private String ccsFile = null;
//	private CCSSaver saver = null;
	private int queryId = -1;
	private AprioriPruner apruner = null;
	private long startT = 0;
	
	public IncS(int graph[][], String nodes[][], TNode root, int core[], String ccsFile){
		this.graph = graph;
		this.nodes = nodes;
		this.root = root;
		this.core = core;
		this.ccsFile = ccsFile;
	}
	
	public int query(int queryId){
		System.out.println("IncS-queryId:" + queryId);
		this.queryId = queryId;
		this.startT = System.currentTimeMillis();
		if(core[queryId] < Config.k)   return 0;
		int qualifiedCC = 0;//the final number of qualified communitiesc,最后合格的社团个数
		
		//step 1: locate the node in the cck-core tree
		//步骤1：锁定CL-tree中的节点
		int minCore = Integer.MAX_VALUE;
		Map<Integer, TNode> cckMap = locateAllCK(root);
		//计算最小的core值
		for(int key:cckMap.keySet()){
			if(key < minCore){
				minCore = key;
			}
		}
		
		//step 2: generate seeds with keyword filtering
		//步骤2：利用关键词筛选，生成种子节点
		Set<String> unionSet = new HashSet<String>();
		for(int nghId:graph[queryId]){
			for(int j = 1;j < nodes[nghId].length;j ++){
				unionSet.add(nodes[nghId][j]);//unionSet内是query节点所有邻 居的关键词集合
			}
		}
		Map<String[], Integer> candMap = new HashMap<String[], Integer>();//<候选的关键词，core number>
		for(int i = 1;i < nodes[queryId].length;i ++){
			if(unionSet.contains(nodes[queryId][i])){
				String kws[] = {nodes[queryId][i]};//筛选出来的就是邻居和自己都有的属性
				candMap.put(kws, minCore); //[a keyword combination, minimum coreness]，初始的时候kws是单个关键词，core是最小的那个
			}
		}
		System.out.println("candMap.size:" + candMap.size() + " time:" + (System.currentTimeMillis() - startT));
		
		//step 3: enumerate all the possible keywords
		//步骤3：列举所有可能的关键词
		for(int iterK = 1;;iterK ++){
			System.out.println("G1 queryId:" + queryId + " kws.len:" + iterK);
			
			//step 1: check whether each candidate can result in a ccs
			//步骤1：检测每个候选的关键词是否能找到一个ccs
			List<String[]> validKwList = new ArrayList<String[]>();//有效的关键词列表
			List<Integer> validCoList = new ArrayList<Integer>();//有效的core值列表
			for(Entry<String[], Integer> entry:candMap.entrySet()){
				String kws[] = entry.getKey();
				
				System.out.print("Inc-S queryId:" + queryId + " We are considering: [" + kws[0]);
				for(int i = 1;i < kws.length;i ++)   System.out.print(" " + kws[i]);
				System.out.println("], coreness:" + entry.getValue());
				
				Set<Integer> ccsSet = findCCS(kws, cckMap.get(entry.getValue()));
				if(ccsSet.size() > 1){
					int min = Integer.MAX_VALUE; //the max k-core contains it
					for(int nodeId:ccsSet)   if(core[nodeId] < min)   min = core[nodeId];
					validKwList.add(kws);
					validCoList.add(min);
					System.out.println("queryId:" + queryId + " A community with size = " + ccsSet.size() + "   Time:" + (System.currentTimeMillis() - startT));
//					if(iterK >= 4)   saver.save(graph, nodes, queryId, ccsSet, kws); 
				}else{
					System.out.println("queryId:" + queryId + " No community Time:" + (System.currentTimeMillis() - startT));
				}
			}
			
			
			if(validKwList.size() == 0){
				break;
			}else{
				System.out.println("We have found " + validKwList.size() + " communities");
				qualifiedCC = validKwList.size();
			}
			
			//step 2: generate new candidates
			//步骤2:产生新的候选集
			Tool tool = new Tool();
			apruner = new AprioriPruner(validKwList);
			candMap = new HashMap<String[], Integer>();
			for(int i = 0;i < validKwList.size();i ++){
				for(int j = i + 1;j < validKwList.size();j ++){
					String kws1[] = validKwList.get(i);   int core1 = validCoList.get(i);
					String kws2[] = validKwList.get(j);   int core2 = validCoList.get(j);
					if(iterK == 1){//很明显，第一次迭代的时候关键词长度为1
						String newKws[] = {kws1[0], kws2[0]};
						if(kws1[0].compareTo(kws2[0]) > 0){
							newKws[0] = kws2[0];   newKws[1] = kws1[0];
						}
						candMap.put(newKws, (core1 > core2 ? core1 : core2));
					}else{
						boolean isCand = true;
						for(int ij = 0;ij < iterK - 1;ij ++){//比较前k-1个关键词，要保证相等，最后一个不相等就可以连接成k+1的候选集了
							if(kws1[ij].equals(kws2[ij]) == false){
								isCand = false;
								break;
							}
						}
						
						if(isCand){//把合适的关键词进行连接生成新的候选项集
							String newKws[] = new String[iterK + 1];
							for(int ij = 0;ij < iterK;ij ++)   newKws[ij] = kws1[ij];
							newKws[iterK] = kws2[iterK - 1];
							newKws = tool.sortKw(newKws); //sort the keywords,将关键词进行排序，按照字典顺序
							if(!apruner.isPruned(newKws))   candMap.put(newKws, (core1 > core2 ? core1 : core2));//
						}
					}
				}
			}
//			System.out.println("validKwList.size:" + validKwList.size() + "   candMap.size:" + candMap.size() + "\n");
		}
		
		return qualifiedCC;
	}
	
	//locate a list of tnodes, each of which has (1)coreness>=Config.k and (2) contains queryId
	//这个就是core-locating，需要满足的条件：1.coreness不小于配置文件中的k 2. 包含queryID
	/**
	 * 定位满足条件的tnodes：1.coreness不小于配置文件中的k。2.包含查询节点
	 * 返回的map<core,root>
	 * */
	private Map<Integer, TNode> locateAllCK(TNode root){
		//step 1: find nodes with coreNumber=Config.k using BFS
		//步骤1：利用广度优先找到所有coreness=k的节点
		List<TNode> candRootList = new ArrayList<TNode>();
		Queue<TNode> queue = new LinkedList<TNode>(); 
		queue.add(root);
		
		while(queue.size() > 0){
			TNode curNode = queue.poll();
			for(TNode tnode:curNode.getChildList()){
				if(tnode.getCore() < Config.k){
					queue.add(tnode);//小于k的往下查
				}else{//the candidate root node must has coreness at least Config.k
					candRootList.add(tnode);//大于等于k的加入候选
				}
			}
		}
//		System.out.println("candRootList.size:" + candRootList.size());
		
		//step 2: locate a list of ck-cores
		//步骤2：定位一条包含query节点的路径的点
		Map<Integer, TNode> cckMap = null;
		for(TNode tnode:candRootList){
			cckMap = findCK(tnode);
			if(cckMap != null){
				break;
			}
		}
		System.out.println("cckMap.size:" + cckMap.size());
		
		return cckMap;
	}
	
	//check whether a subtree rooted at "root" contains queryId or not
	/**
	 * 这个方法是返回一个<core，包含query节点的树节点>
	 * 把DFS找到包含query节点的路径上经过的点都保存起来，意思是找一个联系到query节点的连通分量？
	 * */
	private Map<Integer, TNode> findCK(TNode root){
		if(root.getCore() <= core[queryId]){//实际上传递过来的root的core只能大于等于k，所以这里是等于k的情况
			if(root.getNodeSet().contains(queryId)){
				Map<Integer, TNode> cckMap = new HashMap<Integer, TNode>();
				cckMap.put(root.getCore(), root);
				return cckMap;
			}else{//这里是大于k，所以找孩子节点
				Map<Integer, TNode> cckMap = null;
				for(TNode tnode:root.getChildList()){
					cckMap = findCK(tnode);//DFS找
					if(cckMap != null){
						cckMap.put(root.getCore(), root);//把DFS找到包含query节点的路径上经过的点都保存起来
						break;
					}
				}
				
				return cckMap;
			}
		}else{
			return null;
		}
	}
	
	//find a context community
	/**
	 * find a context community（找到一个包含关键词的社团集合？）
	 * */
	private Set<Integer> findCCS(String kws[], TNode root){
		//step 1: keyword filtering
		//步骤1：关键词筛选
		Set<Integer> targetNodeSet = new HashSet<Integer>();
		Queue<TNode> queue = new LinkedList<TNode>(); 
		queue.add(root);
		while(queue.size() > 0){
			TNode curNode = queue.poll();
			
			//intersection on the inverted list
			//属性倒排的交集
			Map<String, int[]> kwMap = curNode.getKwMap();
			Set<Integer> intersecSet = new HashSet<Integer>();
			boolean isFirst = true;
			for(int i = 0;i < kws.length;i ++){
				if(kwMap.containsKey(kws[i])){
					int invert[] = kwMap.get(kws[i]);//获得包含kws[i]的节点
					if(isFirst){
						isFirst = false;
						for(int j = 0;j < invert.length;j ++)   intersecSet.add(invert[j]);
					}else{
						Set<Integer> tmpSet = new HashSet<Integer>();
						for(int j = 0;j < invert.length;j ++){
							if(intersecSet.contains(invert[j])){
								tmpSet.add(invert[j]);
							}
						}
						intersecSet = tmpSet;
					}
				}else{//this keyword is not contained. Skip all the nodes!!!
					//若是kwMap都不包含，这些关键词，直接跳过所有的节点
					intersecSet = null;
					break;
				}
			}
			if(intersecSet != null)   targetNodeSet.addAll(intersecSet);//collect all the candidate nodes，搜集所有的候选节点
			for(TNode tnode:curNode.getChildList())   queue.add(tnode);//继续检查孩子节点
		}
			
		// count the number of nodes and edges
		//计算节点和边的数目
		FindCC findCC = new FindCC(graph, targetNodeSet, queryId);
		Set<Integer> ccNodeSet = findCC.findCC();//找到目标集合内的连通分量
		int nodeNum = ccNodeSet.size();
		int edgeNum = findCC.getEdge();
		
		//find the ccs
		//找到Gk[S',q]
		Set<Integer> ccsSet = new HashSet<Integer>();
		if(edgeNum - nodeNum >= (Config.k * Config.k - Config.k) / 2 - 1){//剪枝的一个定理，检测是否能生成k-core
			List<Integer> curList = new ArrayList<Integer>();//this list serves as a map (newID -> original ID)
			curList.add(-1);//for consuming space purpose
			curList.addAll(ccNodeSet);
			if(curList.size() > 1){
				FindCCS finder = new FindCCS(graph, curList, queryId);
				ccsSet = finder.findRobustCCS();
			}
		}
		System.out.print("ccsSet:");
		for(int i:ccsSet){
			System.out.print(i+" ");
		}
		System.out.println("");
		return ccsSet;
	}
}
