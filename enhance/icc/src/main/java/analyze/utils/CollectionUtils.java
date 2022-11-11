package main.java.analyze.utils;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.analyze.model.analyzeModel.Attribute;
import main.java.analyze.utils.CollectionUtils;
import main.java.analyze.utils.MapValueComparator;
import main.java.client.obj.model.ctg.ICCMsg;

public class CollectionUtils {

	/**
	 * add New Msg to Map
	 * 
	 * @param mapTo
	 * @param newMsg
	 * @param key
	 */
	public static void addNewMsg2Map(Map<String, Set<ICCMsg>> mapTo, ICCMsg newMsg, String key) {
		if (!mapTo.containsKey(key))
			mapTo.put(key, new HashSet<ICCMsg>());

		if (!mapTo.get(key).contains(newMsg))
			mapTo.get(key).add(newMsg);
	}

	/**
	 * add <key, set> to map
	 * 
	 * @param key
	 * @param resList
	 * @param map
	 */
	public static void add_set_to_map(String key, Set<String> resList, Map<String, Set<String>> map) {
		Set<String> set;
		if (map == null)
			return;
		if (map.containsKey(key)) {
			set = map.get(key);
		} else {
			set = new HashSet<String>();
		}
		set.addAll(resList);
		map.put(key, set);
	}

	/**
	 * get set from map with key
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public static Set<String> get_set_from_map(String key, Map<String, Set<String>> map) {
		Set<String> set = new HashSet<String>();
		if (map != null && map.containsKey(key)) {
			set = map.get(key);
		}
		return set;
	}

	/**
	 * add <key, s> to map
	 * 
	 * @param key
	 * @param s
	 * @param map
	 */
	public static void add_str_to_map(String key, String s, Map<String, String> map) {
		if (map == null || key == null)
			return;
		if (!map.containsKey(key)) {
			map.put(key, s);
		}
	}

	/**
	 * get attr from map with key
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public static String get_str_from_map(String key, Map<String, String> map) {
		String s = "";
		if (map != null && map.containsKey(key)) {
			s = map.get(key);
		}
		return s;
	}

	/**
	 * add <key, attribute> to map
	 * 
	 * @param key
	 * @param attr
	 * @param map
	 */
	public static void add_attribute_to_map(String key, Attribute attr, Map<String, Attribute> map) {
		if (map == null || key == null)
			return;
		if (!map.containsKey(key)) {
			map.put(key, attr);
		}
	}

	/**
	 * get attribute from map with key
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public static Attribute get_attribute_from_map(String key, Map<String, Attribute> map) {
		Attribute attr = null;
		if (map != null && map.containsKey(key)) {
			attr = map.get(key);
		}
		return attr;
	}

	/**
	 * remove duplicate element from list
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> removeDuplicate(List<String> list) {
		HashSet<String> h = new HashSet<String>(list);
		list.clear();
		list.addAll(h);
		return list;
	}

	/**
	 * sortMapByValue
	 * 
	 * @param oriMap
	 * @return
	 */
	public static Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator());

		Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
		Map.Entry<String, Integer> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

	/**
	 * ѭ��ʵ��dimValue�еĵѿ��������������result��
	 * 
	 * @param dimValue
	 *            ԭʼ����
	 * @param result
	 *            �������
	 */
	public static void circulate(List<List<String>> dimValue, List<List<String>> result) {
		int total = 1;
		for (List<String> list : dimValue) {
			total *= list.size();
		}
		String[] myResult = new String[total];
		int itemLoopNum = 1;
		int loopPerItem = 1;
		int now = 1;
		for (List<String> list : dimValue) {
			now *= list.size();
			int index = 0;
			int currentSize = list.size();
			itemLoopNum = total / now;
			loopPerItem = total / (itemLoopNum * currentSize);
			int myIndex = 0;
			for (String string : list) {
				for (int i = 0; i < loopPerItem; i++) {
					if (myIndex == list.size()) {
						myIndex = 0;
					}
					for (int j = 0; j < itemLoopNum; j++) {
						myResult[index] = (myResult[index] == null ? "" : myResult[index] + ",") + list.get(myIndex);
						index++;
					}
					myIndex++;
				}
			}
		}
		List<String> stringResult = Arrays.asList(myResult);
		for (String string : stringResult) {
			String[] stringArray = string.split(",");
			result.add(Arrays.asList(stringArray));
		}
	}
}

/**
 * MapValueComparator
 * 
 * @author 79940
 *
 */
class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {

	@Override
	public int compare(Entry<String, Integer> me1, Entry<String, Integer> me2) {

		return me2.getValue().compareTo(me1.getValue());
	}

}
