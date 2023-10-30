package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.analyze.utils.output.PrintUtils;

public class PathSummaryModel {
	private MethodSummaryModel methodSummary;
	private List<UnitNode> nodes;
	private List<String> methodTraceUnrepeated;
	private Set<ObjectSummaryModel> singleObjectSet;
	private Map<Integer, List<String>> node2TraceMap;

	public PathSummaryModel() {
		this(null);
	}

	public PathSummaryModel(MethodSummaryModel methodSummary) {
		setMethodSummary(methodSummary);
		setNodes(new ArrayList<UnitNode>());
		setMethodTrace(new LinkedList<String>());
		setSingleObjectSet(new HashSet<ObjectSummaryModel>());
		setNode2TraceMap(new HashMap<Integer, List<String>>());
	}

	public List<String> getMethodTrace() {
		return methodTraceUnrepeated;
	}

	public void setMethodTrace(List<String> methodTrace) {
		this.methodTraceUnrepeated = methodTrace;
	}

	public MethodSummaryModel getMethodSummary() {
		return methodSummary;
	}

	public void setMethodSummary(MethodSummaryModel methodSummary) {
		this.methodSummary = methodSummary;
	}

	public List<UnitNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<UnitNode> nodes) {
		this.nodes = nodes;
	}

	public void addNode(UnitNode node) {
		this.nodes.add(node);
	}

	public void copy(PathSummaryModel temp) {
		setMethodSummary(temp.getMethodSummary());
		setMethodTrace(new LinkedList<String>(temp.getMethodTrace()));
		setNode2TraceMap(new HashMap<Integer, List<String>>(temp.getNode2TraceMap()));

		// for(ObjectSummaryModel model :temp.getSingleObjectSet())
		// getSingleObjectSet().add(model);

		for (UnitNode n : temp.getNodes())
			addNode(n);

	}

	public void merge(PathSummaryModel temp, String curentContextSig) {
		if (this.hashCode() == temp.hashCode())
			return;
		for (String me : temp.getMethodTrace()) {
			if (!getMethodTrace().contains(me) && me.contains("\t"))
				getMethodTrace().add(me);
		}
		for (int i : temp.getNode2TraceMap().keySet()) {
			List<String> oldContext = temp.getNode2TraceMap().get(i);
			List<String> newContext = new ArrayList<String>();
			newContext.add(curentContextSig);
			newContext.addAll(oldContext);
			getNode2TraceMap().put(getNode2TraceMap().size(), newContext);
		}

		for (UnitNode n : temp.getNodes())
			addNode(n);
	}

	public StringBuilder appendList2SB(StringBuilder sb, List<?> list, String tag) {
		if (list.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printList(list, "\n") + "\n");
		return sb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendList2SB(sb, getNodes(), "nodes");
		return sb.toString();
	}

	public Map<Integer, List<String>> getNode2TraceMap() {
		return node2TraceMap;
	}

	public void setNode2TraceMap(HashMap<Integer, List<String>> hashMap) {
		this.node2TraceMap = hashMap;
	}

	/**
	 * @return the singleObjectSet
	 */
	public Set<ObjectSummaryModel> getSingleObjectSet() {
		return singleObjectSet;
	}

	/**
	 * @param singleObjectSet
	 *            the singleObjectSet to set
	 */
	public void setSingleObjectSet(Set<ObjectSummaryModel> singleObjectSet) {
		this.singleObjectSet = singleObjectSet;
	}
}
