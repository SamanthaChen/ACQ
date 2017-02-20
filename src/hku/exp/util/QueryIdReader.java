package hku.exp.util;
import java.io.*;
import java.util.*;
/**
 * @author fangyixiang
 * @date Oct 13, 2015
 * read the queryId
 */
public class QueryIdReader {
	private List<Integer> list;
	
	public List<Integer> read(String fileName){
		list = new ArrayList<Integer>();
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = stdin.readLine()) != null){
				String s[] = line.split(" ");
				int id = Integer.parseInt(s[0]);
				list.add(id);
			}
			stdin.close();
		}catch(Exception e){e.printStackTrace();}
		return list;
	}
}
