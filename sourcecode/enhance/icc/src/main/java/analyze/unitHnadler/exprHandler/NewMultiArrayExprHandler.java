package main.java.analyze.unitHnadler.exprHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.NewMultiArrayExpr;

public class NewMultiArrayExprHandler extends ExprHandler {
	private NewMultiArrayExpr exp;

	@Override
	public void init(Value val) {
		exp = (NewMultiArrayExpr) val;
	}

	@Override
	public Value getLeftValue() {
		return null;
	}

	@Override
	public List<Value> getRightValues() {
		List<Value> vals = new ArrayList<Value>();
		return vals;
	}

}
