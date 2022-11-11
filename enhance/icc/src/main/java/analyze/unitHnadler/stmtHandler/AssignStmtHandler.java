package main.java.analyze.unitHnadler.stmtHandler;

import java.util.ArrayList;
import java.util.List;

import main.java.analyze.unitHnadler.exprHandler.ExprHandler;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Expr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;

public class AssignStmtHandler extends StmtHandler {

	private AssignStmt assign;

	@Override
	public void init(Unit stmt) {
		assign = (AssignStmt) stmt;
	}

	@Override
	public Value getLeftValue() {
		return assign.getLeftOp();

	}

	@Override
	public List<Value> getRightValues() {
		// rvalue = constant | expr | local | instance_field_ref | array_ref |
		// static_field_ref |next_next_stmt_address;
		List<Value> vals = new ArrayList<Value>();
		if (assign.getRightOp() instanceof Expr) {
			ExprHandler handler = ExprHandler.getExprHandler(assign.getRightOp());
			return handler.getRightValues();
		} else if (assign.getRightOp() instanceof Constant) {
			return vals;
		} else if (assign.getRightOp() instanceof Local) {
			vals.add(assign.getRightOp());
			return vals;
		} else if (assign.getRightOp() instanceof StaticFieldRef) {
			return vals;
		} else if (assign.getRightOp() instanceof InstanceFieldRef) {
			return vals;
		} else if (assign.getRightOp() instanceof ArrayRef) {
			return vals;
		}
		return vals;
	}

}
