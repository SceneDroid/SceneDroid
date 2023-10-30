package main.java.analyze.model.analyzeModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import main.java.analyze.model.analyzeModel.PathSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.output.PrintUtils;
import soot.SootMethod;
import soot.Unit;

public class ObjectSummaryModel implements Serializable, Cloneable {
	@JSONField(serialize = false)
	protected List<UnitNode> nodes;
	@JSONField(serialize = false)
	protected PathSummaryModel pathSummary;
	@JSONField(serialize = false)
	protected SootMethod method;
	@JSONField(serialize = false)
	protected List<Unit> createList;
	@JSONField(serialize = false)
	protected List<Unit> receiveFromParaList;
	@JSONField(serialize = false)
	protected List<Unit> receiveFromFromRetValueList;
	@JSONField(serialize = false)
	protected List<Unit> sendIntent2FunList;
	@JSONField(serialize = false)
	protected List<Unit> dataHandleList;
	@JSONField(serialize = false)
	protected boolean finishFlag;
	@JSONField(serialize = false)
	protected List<SootMethod> reusedMthCallStack;
	

	public ObjectSummaryModel(PathSummaryModel pathSummary) {
		setNodes(new ArrayList<UnitNode>());
		setPathSummary(pathSummary);
		if (pathSummary != null && pathSummary.getMethodSummary() != null)
			setMethod(pathSummary.getMethodSummary().getMethod());
		// setMethodTrace(new ArrayList<String>());

		setFinishFlag(false);
		setCreateList(new ArrayList<Unit>());
		setReceiveFromParaList(new ArrayList<Unit>());

		setReceiveFromFromRetValueList(new ArrayList<Unit>());

		setSendIntent2FunList(new ArrayList<Unit>());
		setDataHandleList(new ArrayList<Unit>());
		setReusedMthCallStack(new ArrayList<SootMethod>());
	}

	public void copy(ObjectSummaryModel temp) {
		setNodes(temp.nodes);
		setPathSummary(temp.pathSummary);
		// setMethodTrace(new ArrayList<>(temp.getMethodTrace()));
		setCreateList(temp.getCreateList());
		setReceiveFromParaList(temp.getReceiveFromParaList());

		setReceiveFromFromRetValueList(temp.getReceiveFromFromRetValueList());
		setSendIntent2FunList(temp.getSendIntent2FunList());
		setDataHandleList(temp.getDataHandleList());

	}

	public void merge(ObjectSummaryModel temp) {
		getNodes().addAll(temp.nodes);
		// for (String me : temp.getMethodTrace()) {
		// if (!getMethodTrace().contains(me))
		// getMethodTrace().add(me);
		// }
		getCreateList().addAll(temp.getCreateList());
		getReceiveFromParaList().addAll(temp.getReceiveFromParaList());
		getReceiveFromFromRetValueList().addAll(temp.getReceiveFromFromRetValueList());

		getSendIntent2FunList().addAll(temp.getSendIntent2FunList());
		getDataHandleList().addAll(temp.getDataHandleList());

		this.finishFlag = this.finishFlag | temp.finishFlag;
	}

//	@Override
//	public int hashCode() {
//		return this.toString().length();
//	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectSummaryModel) {
			ObjectSummaryModel model = (ObjectSummaryModel) obj;
			return model.toHashString().equals(this.toHashString());
		} else {
			return false;
		}
	}

	public StringBuilder appendList2SB(StringBuilder sb, List<?> list, String tag) {
		if (list.size() > 0)
			sb.append(tag + ":\n" + PrintUtils.printList(list, "\n") + "\n");
		return sb;
	}

	public String toHashString() {
		String res = "";
		res += createList.size();
		res += receiveFromParaList.size();
		res += receiveFromFromRetValueList.size();
		res += sendIntent2FunList.size();
		res += receiveFromParaList.size();
		for (UnitNode n : getNodes()) {
			res += n.getUnit().toString();
		}
		return res;
	}

	@Override
	public String toString() {
		String res = "";
		StringBuilder sb = new StringBuilder();
		appendList2SB(sb, getNodes(), "nodes");
		res += sb.toString();

		res += "finishFlag:" + finishFlag + "\n";

		res += "createList:" + PrintUtils.printList(createList) + "\n";
		res += "receiveFromParaList:" + PrintUtils.printList(receiveFromParaList) + "\n";
		res += "receiveFromFromRetValueList:" + PrintUtils.printList(receiveFromFromRetValueList) + "\n";
		res += "sendIntent2FunList:" + PrintUtils.printList(sendIntent2FunList) + "\n";
		res += "dataHandleList:" + PrintUtils.printList(dataHandleList, "\n") + "\n";
		return res;
	}
	@JSONField
	public List<Unit> getDataHandleList() {
		return dataHandleList;
	}

	public void setDataHandleList(List<Unit> dataHandleList) {
		this.dataHandleList = dataHandleList;
	}


	public PathSummaryModel getPathSummary() {
		return pathSummary;
	}

	public void setPathSummary(PathSummaryModel pathSummary) {
		this.pathSummary = pathSummary;
	}

	public List<UnitNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<UnitNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the method
	 */
	public SootMethod getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(SootMethod method) {
		this.method = method;
	}

	public List<Unit> getCreateList() {
		return createList;
	}

	public void setCreateList(List<Unit> createList) {
		this.createList = createList;
	}

	public List<Unit> getSendIntent2FunList() {
		return sendIntent2FunList;
	}

	public void setSendIntent2FunList(List<Unit> sendIntent2FunList) {
		this.sendIntent2FunList = sendIntent2FunList;
	}

	public List<Unit> getReceiveFromParaList() {
		return receiveFromParaList;
	}

	public void setReceiveFromParaList(List<Unit> receiveFromParaList) {
		this.receiveFromParaList = receiveFromParaList;
	}

	public List<Unit> getReceiveFromFromRetValueList() {
		return receiveFromFromRetValueList;
	}

	public void setReceiveFromFromRetValueList(List<Unit> receiveFromFromRetValueList) {
		this.receiveFromFromRetValueList = receiveFromFromRetValueList;
	}

	public boolean isFinishFlag() {
		return finishFlag;
	}

	public void setFinishFlag(boolean finishFlag) {
		this.finishFlag = finishFlag;
	}

	public void addNode(UnitNode node) {
			getNodes().add(node);
	}
	public List<SootMethod> getReusedMthCallStack() {
		return reusedMthCallStack;
	}

	public void setReusedMthCallStack(List<SootMethod> reusedMthCallStack) {
		this.reusedMthCallStack = reusedMthCallStack;
	}

}
