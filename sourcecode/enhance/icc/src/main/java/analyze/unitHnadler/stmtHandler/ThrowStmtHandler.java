package main.java.analyze.unitHnadler.stmtHandler;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.ThrowStmt;

public class ThrowStmtHandler extends StmtHandler {
	private ThrowStmt throwStmt;

	@Override
	public void init(Unit stmt) {
		throwStmt = (ThrowStmt) stmt;
	}

	@Override
	public Value getLeftValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Value> getRightValues() {
		List<Value> vals = new ArrayList<Value>();
		return vals;
	}

}
