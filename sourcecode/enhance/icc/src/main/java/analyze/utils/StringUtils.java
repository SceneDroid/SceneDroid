package main.java.analyze.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * refine cls name
	 * 
	 * @param str
	 * @return
	 */
	public static String getclsName(String str) {
		String clsName = str.replace("class \"L", "");
		clsName = clsName.replace(";\"", "");
		clsName = clsName.replace("/", ".");
		return clsName;
	}

	/**
	 * refineString
	 * 
	 * @param old
	 * @return
	 */
	public static String refineString(String old) {
		if (old == null || old.equals("\"\""))
			return "null";
		String newStr = old.replace("\\", "").replace("\"", "");
		return newStr;
	}

	/**
	 * judge whther a string is integer
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (str == null || str.equals(""))
			return false;
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static String getProjectName(String str) {
		String regEx = "[^a-z|^A-Z|^0-9|^_]";
		Pattern p = Pattern.compile(regEx);
		Matcher n = p.matcher(str);
		return n.replaceAll("_");
	}
}
