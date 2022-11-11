package main.java.analyze.utils.output;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Print related IO
 * 
 * @author 79940
 *
 */
public class PrintUtils {

	/**
	 * print object ob to file result/temp_output.txt
	 * 
	 * @param ob
	 */
	public synchronized static void printInfo(Object ob) {
		String path = "result/";
		FileUtils.createFolder(path);

		String s = ob.toString();
		FileUtils.writeText2File("result/temp_output.txt", s + "\n", true);
	}

	/**
	 * print set to string
	 * 
	 * @param set
	 * @return
	 */
	public static String printSet(Set<?> set) {
		String split = ", ";
		return printSet(set, split);
	}

	public static String printSet(Set<?> set, String split) {
		if (set == null)
			return "";
		String res = "";
		Iterator<?> it1 = set.iterator();
		while (it1.hasNext()) {
			Object ele = it1.next();
			if(ele!=null) res += ele.toString() + split;
		}
		if (res.endsWith(split))
			res = res.substring(0, res.length() - split.length());
		return res;
	}

	/**
	 * print list to string
	 * 
	 * @param list
	 * @return
	 */
	public static String printList(List<?> list) {
		String split = ", ";
		return printList(list, split);
	}

	/**
	 * print list to string
	 * 
	 * @param list
	 * @return
	 */
	public static String printList(List<?> list, String split) {
		if (list == null)
			return "";
		String res = "";
		for (Object o : list) {
			if(o!=null) res += o.toString() + split;
		}
		if (res.length() > 0)
			res = res.substring(0, res.length() - split.length());
		return res;
	}

	/**
	 * print map to string
	 * 
	 * @param map
	 * @return
	 */
	public static String printMap(Map<?, ?> map) {
		String split = "\n";
		if (map == null)
			return "";
		String res = "";
		for (Entry<?, ?> en : map.entrySet()) {
			res += "(Key:" + en.getKey() + "\nValues:" + en.getValue() + ")" + split;
		}
		if (res.length() > 0)
			res = res.substring(0, res.length() - split.length());
		return res;
	}

	/**
	 * print array to string
	 * 
	 * @param array
	 * @return
	 */
	public static String printArray(String[] array) {
		String split = ", ";
		if (array == null)
			return "";
		String res = "";
		for (String s : array)
			res += s + split;
		if (res.length() > 0)
			res = res.substring(0, res.length() - split.length());
		return res;
	}
}
