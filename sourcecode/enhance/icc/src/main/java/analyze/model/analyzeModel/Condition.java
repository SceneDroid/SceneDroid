package main.java.analyze.model.analyzeModel;

public class Condition {
	protected Condition leftCondition;
	protected Condition rightCondition;
	protected String operation;

	public Condition() {

	}

	public Condition(Condition condition) {
		this.leftCondition = condition.leftCondition;
		this.rightCondition = condition.rightCondition;
		this.operation = condition.operation;
	}

	public Condition(Condition leftCondition, Condition rightCondition, String operation) {
		this.leftCondition = leftCondition;
		this.rightCondition = rightCondition;
		this.operation = operation;
	}

	public Condition(Condition oldCondition, ConditionLeaf leaf, String operation) {
		this.leftCondition = oldCondition;
		this.rightCondition = leaf;
		this.operation = operation;
	}

	public Condition getLeft() {
		return leftCondition;
	}

	public Condition getRight() {
		return rightCondition;
	}

	@Override
	public String toString() {
		String lStr = null, rStr = null;
		if (leftCondition != null)
			lStr = leftCondition.toString();
		if (rightCondition != null)
			rStr = rightCondition.toString();

		if (lStr == null && rStr == null)
			return null;
		if (lStr == null)
			return rStr;
		if (rStr == null)
			return lStr;

		return operation + " (" + lStr + " , " + rStr + ")";
	}

}
