package main.java.analyze.unitHnadler.stmtHandler;

import java.util.List;

import main.java.analyze.unitHnadler.exprHandler.ExprHandler;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

public class InvokeStmtHandler extends StmtHandler {

	private InvokeStmt invokeStmt;
	private InvokeExpr invokeExpr;

	@Override
	public void init(Unit stmt) {
		invokeStmt = (InvokeStmt) stmt;
		invokeExpr = invokeStmt.getInvokeExpr();
	}

	@Override
	public Value getLeftValue() {
		return ExprHandler.getExprHandler(invokeExpr).getLeftValue();
	}

	@Override
	public List<Value> getRightValues() {
		return ExprHandler.getExprHandler(invokeExpr).getRightValues();
	}

}
