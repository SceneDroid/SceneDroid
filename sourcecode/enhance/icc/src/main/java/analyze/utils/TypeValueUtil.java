package main.java.analyze.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class TypeValueUtil {
	// extra intent method types
	public static final String[] intentExtraMethodTypes = { 
			"doubleArray", "double", "floatArray", "float", "intArray", "int", "IntegerArrayList",
			"longArray", "long", "shortArray", "short", "byteArray", "byte", "booleanArray", "boolean",
			"charArray", "char",  "CharSequenceArray", "CharSequenceArrayList","CharSequence", 
			"StringArray", "StringArrayList", "String",
			"ParcelableArray", "ParcelableArrayList", "Parcelable",	"Serializable",   "Bundle", "Extras"};
	
	
	public static Map<String, Set<String>> valueMap = new HashMap<String, Set<String>>();
	
	public static Map<String, Set<String>> getValueMap() {
		//"doubleArray", "double", "floatArray", "float", "intArray", "int", "IntegerArrayList",
//		"longArray", "long", "shortArray", "short", "byteArray", "byte", "booleanArray", "boolean",
		
		putValueInMap(valueMap, "doubleArray", "new double[1]");
		putValueInMap(valueMap, "doubleArray", "new double[3]");
		putValueInMap(valueMap, "double", "Double.MAX_VALUE");
		putValueInMap(valueMap, "double", "Double.MIN_VALUE");
		putValueInMap(valueMap, "double", "0.0");
		
		putValueInMap(valueMap, "floatArray", "new float[1]");
		putValueInMap(valueMap, "floatArray", "new float[3]");
		putValueInMap(valueMap, "float", "Double.MAX_VALUE");
		putValueInMap(valueMap, "float", "Double.MIN_VALUE");
		putValueInMap(valueMap, "float", "0.0");
		
		putValueInMap(valueMap, "intArray", "new int[1]");
		putValueInMap(valueMap, "intArray", "new int[3]");
		putValueInMap(valueMap, "int", "Double.MAX_VALUE");
		putValueInMap(valueMap, "int", "Double.MIN_VALUE");
		putValueInMap(valueMap, "int","0");
		
		putValueInMap(valueMap, "IntegerArrayList", "new ArrayList<Integer>(1)");
		putValueInMap(valueMap, "IntegerArrayList", "new ArrayList<Integer>(3)");
		
		putValueInMap(valueMap, "longArray", "new long[1]");
		putValueInMap(valueMap, "longArray", "new long[3]");
		putValueInMap(valueMap, "long", "Long.MAX_VALUE");
		putValueInMap(valueMap, "long", "Long.MAX_VALUE");
		putValueInMap(valueMap, "long", "0");
		
		putValueInMap(valueMap, "shortArray", "new short[1]");
		putValueInMap(valueMap, "shortArray", "new short[3]");
		putValueInMap(valueMap, "short", "Short.MAX_VALUE");
		putValueInMap(valueMap, "short", "Short.MIN_VALUE");
		putValueInMap(valueMap, "short","0");
		
		putValueInMap(valueMap, "byteArray", "new byte[1]");
		putValueInMap(valueMap, "byteArray", "new byte[3]");
		putValueInMap(valueMap, "byte", "Byte.MAX_VALUE");
		putValueInMap(valueMap, "byte", "Byte.MIN_VALUE");
		putValueInMap(valueMap, "byte","0");
		
		putValueInMap(valueMap, "booleanArray", "new boolean[1]");
		putValueInMap(valueMap, "booleanArray", "new boolean[3]");
		putValueInMap(valueMap, "boolean", "true");
		putValueInMap(valueMap, "boolean", "false");
		
//		"charArray", "char",  "CharSequenceArray", "CharSequenceArrayList","CharSequence", 
//		"StringArray", "StringArrayList", "String",
		
		putValueInMap(valueMap, "charArray", "new char[1]");
		putValueInMap(valueMap, "charArray", "new char[3]");
		putValueInMap(valueMap, "char", "d");
		putValueInMap(valueMap, "char", "*");
		
		putValueInMap(valueMap, "CharSequence", "a");
		putValueInMap(valueMap, "CharSequence", "$^%^(*");
		putValueInMap(valueMap, "CharSequenceArray", "new CharSequence[1]");
		putValueInMap(valueMap, "CharSequenceArray", "new CharSequence[3]");
		putValueInMap(valueMap, "CharSequenceArrayList", "new ArrayList<CharSequence>(1)");
		putValueInMap(valueMap, "CharSequenceArrayList", "new ArrayList<CharSequence>(3)");

		putValueInMap(valueMap, "String", "a");
		putValueInMap(valueMap, "String", "$^%^(*");
		putValueInMap(valueMap, "StringArray", "new String[1]");
		putValueInMap(valueMap, "StringArray", "new String[3]");
		putValueInMap(valueMap, "StringArrayList", "new ArrayList<String>(1)");
		putValueInMap(valueMap, "StringArrayList", "new ArrayList<String>(3)");
		
//		"ParcelableArray", "ParcelableArrayList", "Parcelable",	"Serializable",   "Bundle", "Extras"};
		
		putValueInMap(valueMap, "Serializable", "new Serializable");
		putValueInMap(valueMap, "Parcelable", "new Parcelable");
		putValueInMap(valueMap, "ParcelableArray", "new Parcelable[1]");
		putValueInMap(valueMap, "ParcelableArray", "new Parcelable[3]");
		putValueInMap(valueMap, "ParcelableArrayList", "new ArrayList<Parcelable>(1)");
		putValueInMap(valueMap, "ParcelableArrayList", "new ArrayList<Parcelable>(3)");
		
		putValueInMap(valueMap, "Bundle", "new Bundle");
		putValueInMap(valueMap, "Extras", "new Extras");
		
		return valueMap;
	}
	
	private static void putValueInMap(Map<String, Set<String>> map, String key, String value) {
		if(!map.containsKey(key)){
			map.put(key, new HashSet<String>());
			map.get(key).add("null");
			map.get(key).add("");
		}
		map.get(key).add(value);

	}
	
	public static String getTypevalueJsonString() {
		JSONObject jsonObj = (JSONObject) JSON.toJSON(TypeValueUtil.getValueMap());
        return JSON.toJSONString(jsonObj,SerializerFeature.PrettyFormat);

	}
}
