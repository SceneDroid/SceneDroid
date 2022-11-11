package main.java.analyze.model.analyzeModel;

import java.util.ArrayList;
import java.util.List;

import main.java.analyze.utils.output.PrintUtils;
import soot.Unit;
import soot.Value;

public class ConditionLeaf extends Condition {
	private Unit unit;
	private boolean satisfy;
	private Value conditionNode;
	private Attribute updatedAttri;
	private List<Unit> pointToUnit;

	public ConditionLeaf(Unit unit, Value conditionNode, boolean satisfy, String operation, Attribute updatedAttri) {
		this.setUnit(unit);
		this.setSatisfy(satisfy);
		this.operation = operation;
		this.setUpdatedAttri(updatedAttri);
		this.setConditionNode(conditionNode);
		this.setPointToUnit(new ArrayList<Unit>());
	}

	public ConditionLeaf(ConditionLeaf condition) {
		this.setUnit(condition.getUnit());
		this.setSatisfy(condition.isSatisfy());
		this.operation = condition.operation;
		this.setUpdatedAttri(condition.getUpdatedAttri());
		this.setConditionNode(condition.getConditionNode());
		this.setPointToUnit(new ArrayList<Unit>());
	}

	public Value getConditionNode() {
		return conditionNode;
	}

	public void setConditionNode(Value conditionNode) {
		this.conditionNode = conditionNode;
	}

	public boolean isSatisfy() {
		return satisfy;
	}

	public void setSatisfy(boolean satisfy) {
		this.satisfy = satisfy;
	}

	public Attribute getUpdatedAttri() {
		return updatedAttri;
	}

	public void setUpdatedAttri(Attribute updatedAttri) {
		this.updatedAttri = updatedAttri;
	}

	public List<Unit> getPointToUnit() {
		return pointToUnit;
	}

	public void setPointToUnit(List<Unit> pointToUnit) {
		this.pointToUnit = pointToUnit;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		String newCond = null;
//		 if(getUpdatedAttri()==null){
//		 if(!isSatisfy())
//		 newCond = "NOT (" + getConditionNode().toString()+")";
//		 else
//		 newCond = getConditionNode().toString();
//		 }else{
//		 newCond = getUpdatedAttri().toString().replace("\n", "");
//		 }
//		 if(getPointToUnit()!=null)
//		 newCond += ", pointTo: "+ PrintUtils.printList(getPointToUnit())+"-"
//		 + getPointToUnit().hashCode();
		if (getUpdatedAttri() != null) {
			newCond = getUpdatedAttri().toString().replace("\n", "");
			if (getPointToUnit() != null)
				newCond += ", pointTo: " + PrintUtils.printList(getPointToUnit()) + "-" + getPointToUnit().hashCode();
		}
		return newCond;
	}

}
