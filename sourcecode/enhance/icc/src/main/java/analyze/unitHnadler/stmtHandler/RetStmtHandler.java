package main.java.analyze.unitHnadler.stmtHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.RetStmt;

public class RetStmtHandler extends StmtHandler {

	private RetStmt retStmt;

	@Override
	public void init(Unit stmt) {
		retStmt = (RetStmt) stmt;
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
