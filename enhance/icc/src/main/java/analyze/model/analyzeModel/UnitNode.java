package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.MyConfig;
import soot.SootMethod;
import soot.Unit;

public class UnitNode {
	private SootMethod m;
	private Unit unit;
	private String type;
	private Set<UnitNode> preds;
	private Set<UnitNode> succs;
	private String condition;
	private MethodSummaryModel interFunNode;
	private Map<List<String>, UnitNode> baseNodePointToMap;
	private Map<List<String>, List<UnitNode>> nodeSetPointToMeMap;
	private boolean isFormalParameter;

	public UnitNode(Unit unit, SootMethod m, String type) {
		this.unit = unit;
		this.m = m;
		this.type = type;
		this.preds = new HashSet<UnitNode>();
		this.succs = new HashSet<UnitNode>();
		this.isFormalParameter = false;
		this.baseNodePointToMap = new HashMap<List<String>, UnitNode>();
		this.nodeSetPointToMeMap = new HashMap<List<String>, List<UnitNode>>();
	}

	@Override
	public String toString() {
		String condInfo = "";
		if (getCondition() != null)
			condInfo = "Condition: " + getCondition() + "\n";
		String predsStr = "preds=";
		if (getPreds().size() > 0) {
			for (UnitNode node : getPreds()) {
				predsStr += node.hashCode() + ", ";
			}
		}
		String succsStr = "succs=";
		if (getSuccs().size() > 0) {
			for (UnitNode node : getSuccs()) {
				succsStr += node.hashCode() + ", ";
			}
		}
		if (getUnit() == null) {
			if (getSuccs().size() > 1)
				return "RootId: " + this.hashCode() + "\n" + succsStr + "\n";
			else
				return "";
		}

		String res = "";
		res += "NodeId: " + this.hashCode() + ", " + predsStr + succsStr + "\n";
		res += "type: " + getType() + "\n";
		res += "Unit: " + getUnit().toString() + "\n";
		res = res.substring(0, res.length() - 1) + "\n";

		// res += condInfo;
		// if (getInterFunNode() != null)
		// res += "{interFunNode: " + getInterFunNode().toString() + "}";
		return res;
	}

	/**
	 * n.printTree(new HashSet<Node>());
	 * 
	 * @param history
	 */
	public void printTree(Set<UnitNode> history) {
		if (history.contains(this))
			return;
		history.add(this);
		System.out.println("-------------------");
		System.out.println(this);
		for (UnitNode node : getSuccs()) {
			node.printTree(history);
		}
	}

	public boolean isFormalParameter() {
		return isFormalParameter;
	}

	public void setFormalParameter(boolean isFormalParameter) {
		this.isFormalParameter = isFormalParameter;
	}

	public Set<UnitNode> getPreds() {
		return preds;
	}

	public void setPreds(Set<UnitNode> preds) {
		this.preds = preds;
	}

	public Set<UnitNode> getSuccs() {
		return succs;
	}

	public void setSuccs(Set<UnitNode> succs) {
		this.succs = succs;
	}

	public MethodSummaryModel getInterFunNode() {
		return interFunNode;
	}

	public void setInterFunNode(MethodSummaryModel interFunNode) {
		this.interFunNode = new MethodSummaryModel(interFunNode);
	}

	public void addBaseNodePointToMap(List<String> context, UnitNode baseNodePointTo) {
		this.baseNodePointToMap.put(context, baseNodePointTo);
	}

	public Map<List<String>, UnitNode> getBaseNodePointToMap() {
		return baseNodePointToMap;
	}

	public UnitNode getBaseNodePointedTo(List<String> context) {
		return baseNodePointToMap.get(context);
	}

	public Map<List<String>, List<UnitNode>> getNodeSetPointToMeMap() {
		return nodeSetPointToMeMap;
	}

	public List<UnitNode> getNodeSetPointToMe(List<String> context) {
		return nodeSetPointToMeMap.get(context);
	}

	public void addNodeSetPointToMeMap(List<String> context, List<UnitNode> baseNodePointTo) {
		nodeSetPointToMeMap.put(context, baseNodePointTo);
	}

	public void addNodeSetPointToMeMap(List<String> context, UnitNode baseNodePointTo) {
		if (!nodeSetPointToMeMap.containsKey(context)) {
			nodeSetPointToMeMap.put(context, new ArrayList<UnitNode>());
		}
		if(nodeSetPointToMeMap.get(context).size()< MyConfig.getInstance().getMaxObjectSummarySize())
			nodeSetPointToMeMap.get(context).add(baseNodePointTo);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SootMethod getMethod() {
		return m;
	}

}