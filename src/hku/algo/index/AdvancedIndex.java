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
 * ���ò��鼯�ṹ������index
 */
public class AdvancedIndex {
	private String nodes[][] = null;//�ڵ�����
	private int graph[][] = null;//ͼ�ṹ
	private int core[] = null;//core number
	private int n = -1;
	private int coreReverseFang[] = null;//��core number�������еĽڵ�ID����
	private UNode unodeArr[] = null;
	private TNode invert[] = null;
	private Set<TNode> restNodeSet = null;
	private UnionFind uf = null;

	
	public AdvancedIndex(int graph[][], String nodes[][]){
		this.graph = graph;
		this.nodes = nodes;
	}
	
	public AdvancedIndex(String graphFile, String nodeFile){
		DataReader dataReader = new DataReader(graphFile, nodeFile);//���ļ�
		graph = dataReader.readGraph();//��ͼ�ṹ
		nodes = dataReader.readNode();//���ڵ�����
	}
	
	public TNode build(){
		//step 1: compute k-core
		//����1������k-core
		this.n = graph.length;//1 + actual node number n��ʵ�ʽڵ�����+1
		KCore kcore = new KCore(graph);
		core = kcore.decompose();//kcore�ֽ�
		int maxCore = kcore.obtainMaxCore();//���Ķȡ�Ҳ�������ĸ߶�
		coreReverseFang = kcore.obtainReverseCoreArr();//����coreness�������еĽڵ�id���б�
		System.out.println("k-core decomposition finished (maxCore= " + maxCore +  ").");
		
		//step 2: initialize the union-find data structure
		//����2����ʼ�����鼯�ṹ
		restNodeSet = new HashSet<TNode>();//the remaining nodes without parents ��δ�ҵ�parents��ʣ��ڵ�
		uf = new UnionFind();
		unodeArr = new UNode[n];
		for(int i = 1;i < n;i ++){
			UNode unode = new UNode(i);
			uf.makeSet(unode);//��ʼ����������Ԫ�ؼ���
			unodeArr[i] = unode;//��unode����һ��������
		}
		
		//step 3: build the tree in a bottom-up manner
		//����3���Ե����Ͻ�����
		int startIdx = 1;//��ʼ�Ľڵ�����
		invert = new TNode[n];//the invert index for graph nodes to their TNodes�� �ڵ��TNode�ĵ��ţ��±���ͼ�ڵ�id��ֵ��tree node��
		Set<Integer> core0Set = new HashSet<Integer>();//nodes with degree of 0����Ϊ0�Ľڵ㼯��
		for(int idx = 1;idx < n;idx ++){//idx is the index of array:coreReverseFang��idx�ǰ���core number�������е�coreReverseFang������
			int id = coreReverseFang[idx];//current node,  an actual node ID ��id������ʵ��node����
			int curCoreNum = core[id];//��ǰid��Ӧ��coreness
			
			if(curCoreNum > 0){//core��Ϊ0�Ľڵ�
				int nextIdx = idx + 1;
				if(nextIdx < n){
					int nextId = coreReverseFang[nextIdx];//��������һ���ڵ��id
					if(core[nextId] < curCoreNum){//ͬһ��ı������ˣ���ʼ�����������ڵ�
						//����startIdx��idx֮��Ľڵ��������ڵ㣬��Ҫ�����ò��鼯����ͨ�������з���
						handleALevel(startIdx, idx, curCoreNum); //generate nodes of tree index using nodes in [startIdx, idx] 
						
						for(int reIdx = startIdx;reIdx <= idx;reIdx ++){
							int reId = coreReverseFang[reIdx];//current node,  an actual node ID ��ǰ�Ľڵ㣬�����Ľڵ�ID
							UNode x = unodeArr[reId];//��ǰid��unode
							for(int nghId:graph[reId]){//consider all the neighbors of id ���ǵ�ǰid�������ھ�
								if(core[nghId] >= curCoreNum){ //�ھӵ�core�Ƚϴ��ֱ��union
									UNode y = unodeArr[nghId];//�ھ�id��unode
									uf.union(x, y);//������һ�����⣬handleAlevel�Ѿ������reid�����ڽӵ���ͨ���⣬��������һ����?������forѭ������ɾ����
								}
							}
							UNode xParent = uf.find(x);
							int xRepresent = uf.find(x).represent;
							//�����ǹ���updateAnchor(x, core[],y)���ﲻд��UF�����ݽṹ���棿
							if(core[xRepresent] > core[reId])   xParent.represent = reId;//update x.parent's represent attribute������anchor node
						}
						
						startIdx = nextIdx;//update the startIdx ������һ�ο�ʼ����Ľڵ����
					}// end if(core[nextId] < curCoreNum)
				}else if(nextIdx == n){//�Ѿ��������һ���ڵ�
					handleALevel(startIdx, idx, curCoreNum); //generate nodes of tree index using nodes in [startIdx, idx]
				}
			}else{//coreΪ0�Ľڵ�
				core0Set.add(id);
			}
		}
		
		//step 4: build the root node
		//����4������root�ڵ�
		TNode root = new TNode(0);
		root.setNodeSet(core0Set);//root��vertex set��coreΪ0�Ľڵ�
		root.setChildList(new ArrayList<TNode>(restNodeSet));
	//	System.out.println("after building the root:" + root.getChildList().size());
		
		//step 5: attach keywords
		//����5�����keyword
		AttachKw attacher = new AttachKw(nodes);
		root = attacher.attach(root);
		
		return root;
	}
	
	//old version: generate TNodes in the same level
	//��ͬһ����(��coreness��ͬ�Ľڵ㼯��)����TNodes
	private void handleALevel(int startIdx, int endIdx, int curCoreNum){
		//step 1: build another temporary union-find data structure
		//����1��������һ����ʱ�Ĳ��鼯���ݽṹ,���ò��鼯�����ݽṹ�ϲ���Ӧ����ͨ����
		Map<Integer, UNode> idUFMap = new HashMap<Integer, UNode>();//id -> union-find node
		//����Ӧ�������ò��鼯�����ݽṹ��core number����curCoreNum�е���ͨ����
		for(int idx = startIdx;idx <= endIdx;idx ++){
			int id = coreReverseFang[idx];//a node's actual ID �ڵ��ʵ��id
			if(!idUFMap.containsKey(id)){//���map����û�е�ǰ���id�������map����һ����Vk�Ľڵ����idufmap
				UNode unode = new UNode(id);
				uf.makeSet(unode);
				idUFMap.put(id, unode);
			}
			for(int nghId:graph[id]){//����id���ھ�
				if(core[nghId] >= core[id]){ //����ھӵ�core ��С�� �Լ���core
					if(core[nghId] > core[id])   nghId = uf.find(unodeArr[nghId]).value;//replaced by parent�� ��nghid��core����id�����丸ĸ�滻
					if(!idUFMap.containsKey(nghId)){
						UNode unode = new UNode(nghId);
						uf.makeSet(unode);
						idUFMap.put(nghId, unode);
					}
					uf.union(idUFMap.get(id), idUFMap.get(nghId));//������id��Ӧ��unode�ϲ�
				}
			}//end for(int nghId:graph[id])
		}//end for(int idx = startIdx;idx <= endIdx;idx ++)
		
		//step 2: group nodes and find child nodes
		//����2�����ڵ���飬���ҵ�child nodes
		Map<UNode, Set<Integer>> ufGNodeMap = new HashMap<UNode, Set<Integer>>();//<parent, nodeSet>
		Map<UNode, Set<TNode>> ufTNodeMap = new HashMap<UNode, Set<TNode>>();//<parent, childNode>
		for(int reId:idUFMap.keySet()){//consider all the nodes, including out nodes �������еĽڵ�
			UNode newParent = uf.find(idUFMap.get(reId));//in the new union-find���ҵ���Զ�ĸ�ĸ��
			
			//group nodes ��ͬһ�㣬ͬCoreNum�Ľڵ�ֳ�һ��
			if(core[reId] == curCoreNum){
				if(ufGNodeMap.containsKey(newParent)){//����ͬ��ĸ�ķ�һ��
					ufGNodeMap.get(newParent).add(reId);
				}else{
					Set<Integer> set = new HashSet<Integer>();
					set.add(reId);
					ufGNodeMap.put(newParent, set);
				}
			}
			
			//find childList ����reid���ڵ�ǰcoreness��˵��reid�Ѿ�������ˣ�����ֱ���Һ����б�
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
		//����3������TNode�ڵ㲢�����ڵ�֮�������
		for(Map.Entry<UNode, Set<Integer>> entry:ufGNodeMap.entrySet()){
			UNode parent = entry.getKey();//���鼯����ĸ��ڵ�
			Set<Integer> nodeSet = entry.getValue();//���ڵ��������ͼ�ڵ㼯��
			Set<TNode> childSet = ufTNodeMap.get(parent);
			
			TNode tnode = new TNode(curCoreNum);
			tnode.setNodeSet(nodeSet);
			if(childSet != null)   tnode.setChildList(new ArrayList<TNode>(childSet));
			
			restNodeSet.add(tnode);//record it as it has no parent����¼Ϊû��parents�Ľڵ�
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
	 * �ݹ�ı��������
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
	 * ��ӡ�����
	 * */
	public static void displaytree(TNode root,int level){
		String preStr = "";
		for(int i=0;i<level;i++){
			preStr+="	";
		}
		for(int i=0;i<root.getChildList().size();i++){
			TNode curnode = root.getChildList().get(i);
			System.out.print(preStr+"-["+curnode.getCore()+"]{");//��ӡǰ׺�ո�+core
			//��ӡ���ڵ��а�����ͼ�ڵ�
			Iterator<Integer> iter = curnode.getNodeSet().iterator();
			while(iter.hasNext())   System.out.print(iter.next() + ",");
			System.out.println("}");
			
			if(!curnode.getChildList().isEmpty()){
				displaytree(curnode,level+1);
			}
			
		}
		
	}
	/**
	 * ���Ե�����
	 * */
	public static void main(String[] args) {
		String graphfile = "E:/ACQ/Datasets/toy2-graph";
		String nodefile = "E:/ACQ/Datasets/toy2-node";//�ڽӾ���ĸ�ʽ
		AdvancedIndex index = new AdvancedIndex(graphfile,nodefile);
		TNode treeroot = index.build();
		//traverse(treeroot);
		TNode fakeroot = new TNode(0);
		fakeroot.getChildList().add(treeroot);
		displaytree(fakeroot,0);
	}
}
