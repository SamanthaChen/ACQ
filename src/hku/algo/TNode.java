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
 * CL-tree�е����ڵ�
 */
public class TNode {
	private int core = - 1;//������ڵ��coreness��С
	private Set<Integer> nodeSet = null;//������ڵ��а�����ͼ�ڵ㼯��
	private List<TNode> childList = null;//������ڵ�����ĺ��ӽڵ��б�
	private Map<String, int[]> kwMap = null;//������ڵ�����Ĺؼ��ʵ���ӳ��
	
	public TNode(int core){//����������core����
		this.core = core;
		this.nodeSet = new HashSet();
		this.childList = new ArrayList<TNode>();
		this.kwMap = new HashMap<String, int[]>();
	}
	
	//getter �� setter
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
