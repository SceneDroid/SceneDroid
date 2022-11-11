package main.java.analyze.unitHnadler.exprHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.BinopExpr;

public class BinopExprHandler extends ExprHandler {
	private BinopExpr exp;

	@Override
	public void init(Value val) {
		exp = (BinopExpr) val;
	}

	@Override
	public Value getLeftValue() {
		return exp.getOp1();
	}

	@Override
	public List<Value> getRightValues() {
		List<Value> vals = new ArrayList<Value>();
		vals.add(exp.getOp1());
		vals.add(exp.getOp2());
		return vals;
	}
}
