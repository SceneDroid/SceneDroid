package main.java.analyze.unitHnadler.exprHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.NewExpr;

public class NewExprHandler extends ExprHandler {
	private NewExpr exp;

	@Override
	public void init(Value val) {
		exp = (NewExpr) val;
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
