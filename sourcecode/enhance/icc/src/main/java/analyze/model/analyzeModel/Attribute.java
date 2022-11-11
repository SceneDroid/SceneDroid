package main.java.analyze.model.analyzeModel;

import java.io.Serializable;

import main.java.analyze.model.analyzeModel.Attribute;
import soot.Unit;

/**
 * Intent attribute value and satisfy condition
 * 
 * @author 79940
 *
 */
public class Attribute implements Cloneable, Serializable {
	private static final long serialVersionUID = 3L;
	private String id;
	private String type;
	private String value;
	private String condition;
	private String conditionOfLeft;
	private int isSatisfy = 1;
	private Unit pointToUnit;

	public Attribute(String id, String type, String value, String condition) {
		this.id = id;
		this.type = type;
		this.value = value.replace("\"", "");
		this.condition = condition;
	}

	public Attribute(String id, String type, String value, String condition, String conditionOfLeft, Unit pointToUnit) {
		this(id, type, value, condition);
		this.setConditionOfLeft(conditionOfLeft);
		this.setPointToUnit(pointToUnit);
	}

	public Attribute(String id, String type, String value, String condition, int isSatisfy) {
		this(id, type, value, condition);
		this.isSatisfy = isSatisfy;
	}

	public Attribute(Attribute attr) {
		this.id = attr.id;
		this.type = attr.type;
		this.value = attr.value;
		this.condition = attr.condition;
		this.isSatisfy = attr.getIsSatisfy();
	}

	@Override
	public String toString() {
		return "type: " + type + ", value: " + value + ", strOp: " + condition + ", isSatisfy: " + getIsSatisfy()
				+ "\n";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Attribute attr2 = new Attribute(id, type, value, condition, getIsSatisfy());
		return attr2;
	}

	public String getConditionOfLeft() {
		return conditionOfLeft;
	}

	public void setConditionOfLeft(String conditionOfLeft) {
		this.conditionOfLeft = conditionOfLeft;
	}

	public Unit getPointToUnit() {
		return pointToUnit;
	}

	public void setPointToUnit(Unit pointToUnit) {
		this.pointToUnit = pointToUnit;
	}

	public void turnSatisfity() {
		this.isSatisfy = this.isSatisfy * -1;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getCondition() {
		return condition;
	}

	public int getIsSatisfy() {
		return isSatisfy;
	}

}
