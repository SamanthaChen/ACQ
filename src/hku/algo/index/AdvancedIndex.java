package hku.algo.index;

import hku.algo.DataReader;
import hku.algo.KCore;
import hku.algo.TNode;
import hku.algo.index.unionFind.UNode;
import hku.algo.index.unionFind.UnionFind;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author fangyixiang
 * @date Sep 17, 2015
 * build the index using union-find data structure: correct, union later
 * 利用并查集结构来创建index
 */
public class AdvancedIndex {
	private String nodes[][] = null;//节点属性
	private int graph[][] = null;//图结构
	private int core[] = null;//core number
	private int n = -1;
	private int coreReverseFang[] = null;//按core number降序排列的节点ID数组
	private UNode unodeArr[] = null;
	private TNode invert[] = null;
	private Set<TNode> restNodeSet = null;
	private UnionFind uf = null;

	
	public AdvancedIndex(int graph[][], String nodes[][]){
		this.graph = graph;
		this.nodes = nodes;
	}
	
	public AdvancedIndex(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);//读文件
		graph = dataReader.readGraph();//读图结构
		nodes = dataReader.readNode();//读节点属性
	}
	
	public TNode build(){
		//step 1: compute k-core
		//步骤1：计算k-core
		this.n = graph.length;//1 + actual node number n是实际节点数量+1
		KCore kcore = new KCore(graph);
		core = kcore.decompose();//kcore分解
		int maxCore = kcore.obtainMaxCore();//最大的度。也就是树的高度
		coreReverseFang = kcore.obtainReverseCoreArr();//按照coreness降序排列的节点id的列表
		System.out.println("k-core decomposition finished (maxCore= " + maxCore +  ").");
		
		//step 2: initialize the union-find data structure
		//步骤2：初始化并查集结构
		restNodeSet = new HashSet<TNode>();//the remaining nodes without parents 还未找到parents的剩余节点
		uf = new UnionFind();
		unodeArr = new UNode[n];
		for(int i = 1;i < n;i ++){
			UNode unode = new UNode(i);
			uf.makeSet(unode);//初始化，建立单元素集合
			unodeArr[i] = unode;//将unode放在一个数组中
		}
		
		//step 3: build the tree in a bottom-up manner
		//步骤3：自底向上建立树
		int startIdx = 1;//开始的节点索引
		invert = new TNode[n];//the invert index for graph nodes to their TNodes， 节点和TNode的倒排（下标是图节点id，值是tree node）
		Set<Integer> core0Set = new HashSet<Integer>();//nodes with degree of 0，度为0的节点集合
		for(int idx = 1;idx < n;idx ++){//idx is the index of array:coreReverseFang，idx是按照core number降序排列的coreReverseFang的索引
			int id = coreReverseFang[idx];//current node,  an actual node ID 。id才是真实的node索引
			int curCoreNum = core[id];//当前id对应的coreness
			
			if(curCoreNum > 0){//core不为0的节点
				int nextIdx = idx + 1;
				if(nextIdx < n){
					int nextId = coreReverseFang[nextIdx];//遍历的下一个节点的id
					if(core[nextId] < curCoreNum){//同一层的遍历完了，开始处理生成树节点
						//利用startIdx和idx之间的节点生成树节点，主要是利用并查集对连通分量进行分组
						handleALevel(startIdx, idx, curCoreNum); //generate nodes of tree index using nodes in [startIdx, idx] 
						
						for(int reIdx = startIdx;reIdx <= idx;reIdx ++){
							int reId = coreReverseFang[reIdx];//current node,  an actual node ID 当前的节点，真正的节点ID
							UNode x = unodeArr[reId];//当前id的unode
							for(int nghId:graph[reId]){//consider all the neighbors of id 考虑当前id的所有邻居
								if(core[nghId] >= curCoreNum){ //邻居的core比较大就直接union
									UNode y = unodeArr[nghId];//邻居id的unode
									uf.union(x, y);//这里有一个问题，handleAlevel已经处理过reid和他邻接的连通问题，这里又来一遍吗?这两个for循环可以删掉吗
								}
							}
							UNode xParent = uf.find(x);
							int xRepresent = uf.find(x).represent;
							//这里是功能updateAnchor(x, core[],y)这里不写在UF的数据结构里面？
							if(core[xRepresent] > core[reId])   xParent.represent = reId;//update x.parent's represent attribute，更新anchor node
						}
						
						startIdx = nextIdx;//update the startIdx 更新下一次开始处理的节点序号
					}// end if(core[nextId] < curCoreNum)
				}else if(nextIdx == n){//已经到达最后一个节点
					handleALevel(startIdx, idx, curCoreNum); //generate nodes of tree index using nodes in [startIdx, idx]
				}
			}else{//core为0的节点
				core0Set.add(id);
			}
		}
		
		//step 4: build the root node
		//步骤4：建立root节点
		TNode root = new TNode(0);
		root.setNodeSet(core0Set);//root的vertex set是core为0的节点
		root.setChildList(new ArrayList<TNode>(restNodeSet));
	//	System.out.println("after building the root:" + root.getChildList().size());
		
		//step 5: attach keywords
		//步骤5：获得keyword
		AttachKw attacher = new AttachKw(nodes);
		root = attacher.attach(root);
		
		return root;
	}
	
	//old version: generate TNodes in the same level
	//在同一层内(即coreness相同的节点集合)生成TNodes
	private void handleALevel(int startIdx, int endIdx, int curCoreNum){
		//step 1: build another temporary union-find data structure
		//步骤1：建立另一个临时的并查集数据结构,利用并查集的数据结构合并对应的连通分量
		Map<Integer, UNode> idUFMap = new HashMap<Integer, UNode>();//id -> union-find node
		//这里应该是利用并查集的数据结构找core number都有curCoreNum中的连通分量
		for(int idx = startIdx;idx <= endIdx;idx ++){
			int id = coreReverseFang[idx];//a node's actual ID 节点的实际id
			if(!idUFMap.containsKey(id)){//如果map里面没有当前这个id，则加入map，这一步将Vk的节点存入idufmap
				UNode unode = new UNode(id);
				uf.makeSet(unode);
				idUFMap.put(id, unode);
			}
			for(int nghId:graph[id]){//遍历id的邻居
				if(core[nghId] >= core[id]){ //如果邻居的core 不小于 自己的core
					if(core[nghId] > core[id])   nghId = uf.find(unodeArr[nghId]).value;//replaced by parent， 若nghid的core大于id，用其父母替换
					if(!idUFMap.containsKey(nghId)){
						UNode unode = new UNode(nghId);
						uf.makeSet(unode);
						idUFMap.put(nghId, unode);
					}
					uf.union(idUFMap.get(id), idUFMap.get(nghId));//将两个id对应的unode合并
				}
			}//end for(int nghId:graph[id])
		}//end for(int idx = startIdx;idx <= endIdx;idx ++)
		
		//step 2: group nodes and find child nodes
		//步骤2：给节点分组，并找到child nodes
		Map<UNode, Set<Integer>> ufGNodeMap = new HashMap<UNode, Set<Integer>>();//<parent, nodeSet>
		Map<UNode, Set<TNode>> ufTNodeMap = new HashMap<UNode, Set<TNode>>();//<parent, childNode>
		for(int reId:idUFMap.keySet()){//consider all the nodes, including out nodes 考虑所有的节点
			UNode newParent = uf.find(idUFMap.get(reId));//in the new union-find，找到最远的父母？
			
			//group nodes ，同一层，同CoreNum的节点分成一组
			if(core[reId] == curCoreNum){
				if(ufGNodeMap.containsKey(newParent)){//按相同父母的分一组
					ufGNodeMap.get(newParent).add(reId);
				}else{
					Set<Integer> set = new HashSet<Integer>();
					set.add(reId);
					ufGNodeMap.put(newParent, set);
				}
			}
			
			//find childList ，若reid大于当前coreness，说明reid已经处理过了，所以直接找孩子列表
			if(core[reId] > curCoreNum){
				UNode oldParent = unodeArr[reId];//in the original union-find, reId is already an id of a parent node
				TNode tnode = invert[oldParent.represent];
				if(ufTNodeMap.containsKey(newParent)){
					ufTNodeMap.get(newParent).add(tnode);
				}else{
					Set<TNode> set = new HashSet<TNode>();
					set.add(tnode);
					ufTNodeMap.put(newParent, set);
				}
			}
		}
		
		//step 3: generate TNodes and build the connections
		//步骤3：产生TNode节点并建立节点之间的连接
		for(Map.Entry<UNode, Set<Integer>> entry:ufGNodeMap.entrySet()){
			UNode parent = entry.getKey();//并查集里面的父节点
			Set<Integer> nodeSet = entry.getValue();//树节点里包含的图节点集合
			Set<TNode> childSet = ufTNodeMap.get(parent);
			
			TNode tnode = new TNode(curCoreNum);
			tnode.setNodeSet(nodeSet);
			if(childSet != null)   tnode.setChildList(new ArrayList<TNode>(childSet));
			
			restNodeSet.add(tnode);//record it as it has no parent，记录为没有parents的节点
			for(int nodeId:tnode.getNodeSet())   invert[nodeId] = tnode;//update invert
			for(TNode subTNode:tnode.getChildList())   restNodeSet.remove(subTNode);//move some nodes
		}
	}
	
	public int[] getCore(){
		return core;
	}
	
	public TNode[] getInvert() {
		return invert;
	}

	//traverse the tree
	/**
	 * 递归的遍历多叉树
	 * */
	public static void traverse(TNode root){
		Iterator<Integer> iter = root.getNodeSet().iterator();
		System.out.print("k=" + root.getCore() + " size=" + root.getNodeSet().size() + " nodes:");
		while(iter.hasNext())   System.out.print(iter.next() + " ");
		System.out.println();
		
		for(int i = 0;i < root.getChildList().size();i ++){
			TNode tnode = root.getChildList().get(i);
			traverse(tnode);
		}
	}
	/**
	 * 打印多叉树
	 * */
	public static void displaytree(TNode root,int level){
		String preStr = "";
		for(int i=0;i<level;i++){
			preStr+="	";
		}
		for(int i=0;i<root.getChildList().size();i++){
			TNode curnode = root.getChildList().get(i);
			System.out.print(preStr+"-["+curnode.getCore()+"]{");//打印前缀空格+core
			//打印树节点中包含的图节点
			Iterator<Integer> iter = curnode.getNodeSet().iterator();
			while(iter.hasNext())   System.out.print(iter.next() + ",");
			System.out.println("}");
			
			if(!curnode.getChildList().isEmpty()){
				displaytree(curnode,level+1);
			}
			
		}
		
	}
	/**
	 * 测试的例子
	 * */
	public static void main(String[] args) {
		String graphfile = "E:/ACQ/Datasets/toy2-graph";
		String nodefile = "E:/ACQ/Datasets/toy2-node";//邻接矩阵的格式
		AdvancedIndex index = new AdvancedIndex(graphfile,nodefile);
		TNode treeroot = index.build();
		//traverse(treeroot);
		TNode fakeroot = new TNode(0);
		fakeroot.getChildList().add(treeroot);
		displaytree(fakeroot,0);
	}
}
