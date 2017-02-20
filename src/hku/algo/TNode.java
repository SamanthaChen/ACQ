package hku.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author fangyixiang
 * @date Aug 11, 2015
 * A tree node in the cck-core tree index
 * CL-tree中的树节点
 */
public class TNode {
	private int core = - 1;//这个树节点的coreness大小
	private Set<Integer> nodeSet = null;//这个树节点中包含的图节点集合
	private List<TNode> childList = null;//这个树节点包含的孩子节点列表
	private Map<String, int[]> kwMap = null;//这个树节点包含的关键词倒排映射
	
	public TNode(int core){//构造器，用core构造
		this.core = core;
		this.nodeSet = new HashSet();
		this.childList = new ArrayList<TNode>();
		this.kwMap = new HashMap<String, int[]>();
	}
	
	//getter 和 setter
	public int getCore() {
		return core;
	}
	public void setCore(int core) {
		this.core = core;
	}
	
	public Set<Integer> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(Set<Integer> nodeSet) {
		this.nodeSet = nodeSet;
	}



	public Map<String, int[]> getKwMap() {
		return kwMap;
	}

	public void setKwMap(Map<String, int[]> kwMap) {
		this.kwMap = kwMap;
	}

	public List<TNode> getChildList() {
		return childList;
	}

	public void setChildList(List<TNode> childList) {
		this.childList = childList;
	}
}
