package main.java.client.obj.model.component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

import main.java.analyze.utils.output.PrintUtils;

public class IntentFilterModel implements Serializable {
	private static final long serialVersionUID = 6L;
	@JSONField(serialize=false)
	private String priority = "0";
	@JSONField(name="action_manifest")
	private Set<String> action_list = new HashSet<String>();
	@JSONField(name="categories_manifest")
	private Set<String> category_list = new HashSet<String>();
	@JSONField(name="type_manifest")
	private Set<String> datatype_list = new HashSet<String>();
	@JSONField(name="data_manifest")
	private Set<Data> data_list = new HashSet<Data>();

	@Override
	public String toString() {
		String res = "actionlist:" + getAction_list() + ", ";
		res += "category_list:" + getCategory_list() + ", ";
		res += "data:" + PrintUtils.printSet(getData_list()) + "\n";
		return res;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Set<String> getAction_list() {
		return action_list;
	}

	public void setAction_list(Set<String> action_list) {
		this.action_list = action_list;
	}

	public Set<String> getCategory_list() {
		return category_list;
	}

	public void setCategory_list(Set<String> category_list) {
		this.category_list = category_list;
	}

	public Set<String> getDatatype_list() {
		return datatype_list;
	}

	public void setDatatype_list(Set<String> datatype_list) {
		this.datatype_list = datatype_list;
	}

	/**
	 * @return the data_list
	 */
	public Set<Data> getData_list() {
		return data_list;
	}

	/**
	 * @param data_list
	 *            the data_list to set
	 */
	public void setData_list(Set<Data> data_list) {
		this.data_list = data_list;
	}
}
