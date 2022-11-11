package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.analyze.utils.output.PrintUtils;
import soot.SootMethod;

public class MethodSummaryModel {
	private SootMethod method;
	private String componentName;
	private Set<PathSummaryModel> pathSummarySet;
	private Set<PathSummaryModel> analyzedPathSummarySet;
	private Set<ObjectSummaryModel> singleObjectSet;
	private List<UnitNode> nodePathList;
	private int maxMethodTraceDepth;
	private Set<MethodSummaryModel> reuseModelSet;

	public MethodSummaryModel(String className, SootMethod method) {
		this.maxMethodTraceDepth = 0;
		this.componentName = className;
		this.method = method;
		this.nodePathList = new ArrayList<UnitNode>();
		this.pathSummarySet = new HashSet<PathSummaryModel>();
		this.analyzedPathSummarySet = new HashSet<PathSummaryModel>();
		this.singleObjectSet = new HashSet<ObjectSummaryModel>();
		this.setReuseModelSet(new HashSet<MethodSummaryModel>());
	}

	public MethodSummaryModel(MethodSummaryModel temp) {
		this.method = temp.method;
		this.componentName = temp.componentName;
		this.maxMethodTraceDepth = temp.maxMethodTraceDepth;
		this.nodePathList = temp.getNodePathList();
		this.pathSummarySet = new HashSet<PathSummaryModel>(temp.getPathSet());
		this.analyzedPathSummarySet = new HashSet<PathSummaryModel>(temp.getAnalyzedPathSummarySet());
		this.singleObjectSet = new HashSet<ObjectSummaryModel>(temp.getSingleObjectSet());
		this.setReuseModelSet(new HashSet<MethodSummaryModel>(temp.getReuseModelSet()));
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public StringBuilder appendSet2SB(StringBuilder sb, Set<?> set, String tag) {
		if (set.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printSet(set) + "\n");
		return sb;
	}

	public StringBuilder appendList2SB(StringBuilder sb, List<?> list, String tag) {
		if (list.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printList(list, "\n") + "\n");
		return sb;
	}

	public StringBuilder appendSet2SB(StringBuilder sb, Set<?> set, String tag, String split) {
		if (set.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printSet(set, split) + "\n");
		return sb;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendList2SB(sb, getNodePathList(), "pathList");
		return sb.toString();
	}

	public List<UnitNode> getNodePathList() {
		return nodePathList;
	}

	public SootMethod getMethod() {
		return method;
	}

	public int getMaxMethodTraceDepth() {
		return maxMethodTraceDepth;
	}

	public void setMaxMethodTraceDepth(int maxMethodTraceDepth) {
		this.maxMethodTraceDepth = maxMethodTraceDepth;
	}

	public Set<PathSummaryModel> getPathSet() {
		return pathSummarySet;
	}

	public void setPathSummarySet(Set<PathSummaryModel> pathSummarySet) {
		this.pathSummarySet = pathSummarySet;
	}

	/**
	 * @return the analyzedPathSummarySet
	 */
	public Set<PathSummaryModel> getAnalyzedPathSummarySet() {
		return analyzedPathSummarySet;
	}

	/**
	 * @param analyzedPathSummarySet
	 *            the analyzedPathSummarySet to set
	 */
	public void setAnalyzedPathSummarySet(Set<PathSummaryModel> analyzedPathSummarySet) {
		this.analyzedPathSummarySet = analyzedPathSummarySet;
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

	/**
	 * @return the reuseModelSet
	 */
	public Set<MethodSummaryModel> getReuseModelSet() {
		return reuseModelSet;
	}

	/**
	 * @param reuseModelSet
	 *            the reuseModelSet to set
	 */
	public void setReuseModelSet(Set<MethodSummaryModel> reuseModelSet) {
		this.reuseModelSet = reuseModelSet;
	}

	/**
	 * get all SingleObjects use time to replace space
	 * 
	 * @return
	 */
	public Set<ObjectSummaryModel> getSingleObjects() {
		Set<ObjectSummaryModel> objectSet = new HashSet<ObjectSummaryModel>();
		Set<MethodSummaryModel> history = new HashSet<MethodSummaryModel>();
		List<SootMethod> stack = new ArrayList<SootMethod>();
		getSingleObjectsIterative(this, objectSet, history, stack);

		return objectSet;
	}

	private void getSingleObjectsIterative(MethodSummaryModel methodSummary, Set<ObjectSummaryModel> objectSet,
			Set<MethodSummaryModel> history, List<SootMethod> stack) {
		stack.add(methodSummary.getMethod());
		if (history.contains(methodSummary))
			return;
		history.add(methodSummary);
		for(ObjectSummaryModel objSummary: methodSummary.getSingleObjectSet()){
			objSummary.setReusedMthCallStack(new ArrayList<SootMethod>(stack)); 
		}
		objectSet.addAll(methodSummary.getSingleObjectSet());
		for (MethodSummaryModel model : methodSummary.getReuseModelSet()) {
			getSingleObjectsIterative(model, objectSet, history, new ArrayList<SootMethod>(stack));
		}
	}
}
