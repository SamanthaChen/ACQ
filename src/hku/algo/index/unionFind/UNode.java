package hku.algo.index.unionFind;

/**
 * @author fangyixiang
 * @date Sep 16, 2015
 */
public class UNode {
	public int value = 0;
	public UNode parent = null;
	public int rank = -1;//Ê÷µÄ¸ß¶È
	public int represent = -1; //this variable is used for updating our tree index£¨×¢£ºanchor vertex£©
	
	
	public UNode(int value){
		this.value = value;
	}
}
