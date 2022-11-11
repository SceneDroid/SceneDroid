package main.java.analyze.unitHnadler.stmtHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.JastAddJ.Expr;
import soot.JastAddJ.IfStmt;

public class IfStmtHandler extends StmtHandler {
	private IfStmt ifStmt;

	@Override
	public void init(Unit stmt) {
		ifStmt = (IfStmt) stmt;
	}

	@Override
	public Value getLeftValue() {
		return null;

	}

	public Expr getCondition() {
		return ifStmt.getCondition();

	}

	@Override
	public List<Value> getRightValues() {
		List<Value> vals = new ArrayList<Value>();
		return vals;
	}
}
