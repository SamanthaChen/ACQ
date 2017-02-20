package hku.exp.effect;

import hku.Config;
import hku.algo.DataReader;
import hku.algo.previous.CodicilQuery;
import hku.exp.util.QueryIdReader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodCaseStudy {

	public static void main(String[] args) {
		DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpNode);
		int graph[][] = dataReader.readGraph();
		String nodes[][] = dataReader.readNode();
		
		String queryFile = Config.dblpGraph + "-query=100";
		QueryIdReader qReader = new QueryIdReader();
		List<Integer> queryIdList = qReader.read(queryFile);
		
		Config.clusterK = 50000;
		CodicilQuery query = new CodicilQuery(Config.dblpGraph, Config.dblpNode);
		
		CodKeyword ck = new CodKeyword(nodes);

		
		int queryId = 152532;//jim gray
		Set<Integer> set = query.query(queryId);
		ck.analyze(set);
		
		queryId = 15238;//jiawei han
		set = query.query(queryId);
		ck.analyze(set);
	}
}

