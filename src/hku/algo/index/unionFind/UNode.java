package hku.algo.index.unionFind;

/**
 * @author fangyixiang
 * @date Sep 16, 2015
 */
public class UNode {
	public int value = 0;
	public UNode parent = null;
	public int rank = -1;//���ĸ߶�
	public int represent = -1; //this variable is used for updating our tree index��ע��anchor vertex��
	
	
	public UNode(int value){
		this.value = value;
	}
}
