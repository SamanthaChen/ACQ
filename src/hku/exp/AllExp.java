package hku.exp;

import hku.Config;
import hku.exp.effect.MetricExp;

/**
 * @author fangyixiang
 * @date Oct 28, 2015
 * We run all the experiments in a collected manner
 */
public class AllExp {

	public static void main(String[] args) {
		String graphFileArr[] = {Config.flickrGraph, Config.tencentGraph};
		String nodesFileArr[] = {Config.flickrNode, Config.tencentNode};
		
		for(int i = 0;i < graphFileArr.length;i ++){
			String graphFile = graphFileArr[i];
			String nodeFile = nodesFileArr[i];
			
			MetricExp exp0 = new MetricExp();
			exp0.exp(graphFile, nodeFile);
			
			KExp exp1 = new KExp();
			exp1.expIndex(graphFile, nodeFile);
			
			SubKwQueryExp exp2 = new SubKwQueryExp();
			exp2.exp(graphFile, nodeFile);
			
			SubGraphQueryExp exp3 = new SubGraphQueryExp();
			exp3.exp(graphFile, nodeFile);
			
			ShareKeywordExp exp4 = new ShareKeywordExp();
			exp4.exp(graphFile, nodeFile);
			
			CompareExp exp5 = new CompareExp();
			exp5.exp(graphFile, nodeFile);
			
			VariantExp exp6 = new VariantExp();
			exp6.exp(graphFile, nodeFile);
			
			KExp exp7 = new KExp();
			exp7.expBasic(graphFile, nodeFile);
			
		}
	}

}
