package main.java.analyze.unitHnadler.exprHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.InstanceOfExpr;

public class InstanceOfExprHandler extends ExprHandler {

	private InstanceOfExpr exp;

	@Override
	public void init(Value val) {
		exp = (InstanceOfExpr) val;
	}

	@Override
	public Value getLeftValue() {
		return null;

	}

	@Override
	public List<Value> getRightValues() {
		List<Value> vals = new ArrayList<Value>();
		vals.add(exp.getOp());
		return vals;
	}

}
