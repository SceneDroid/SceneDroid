package main.java.analyze.model.sootAnalysisModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.analyze.model.sootAnalysisModel.NestableObj;

/**
 * monitor java object, which can contain a map of objects
 * 
 * @author 79940
 *
 */
public class NestableObj {
	private String name = "";
	private Map<String, NestableObj> objs = new HashMap<String, NestableObj>();
	private List<String> values = new ArrayList<String>();

	public NestableObj(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(List<String> values) {
		if (values == null)
			return;
		for (String s : values) {
			s = s.replaceAll("\"", "");
			this.values.add(s);
		}
	}

	public void addValue(String s) {
		if (s == null)
			return;
		s = s.replaceAll("\"", "");
		this.values.add(s);
	}

	public Map<String, NestableObj> getObjs() {
		return objs;
	}

	public List<String> getValues() {
		return values;
	}

	public void addObj(NestableObj o) {
		objs.put(o.name, o);

	}

	public NestableObj getObj(String s) {
		return objs.get(s);

	}

	@Override
	public String toString() {
		String res = "objs: ";
		for (Entry<String, NestableObj> en : objs.entrySet())
			res += "(" + en.getKey() + " - " + en.getValue() + "), ";

		res += "\nvals: ";
		for (String s : values)
			res += s + ", ";

		return res;
	}

}
