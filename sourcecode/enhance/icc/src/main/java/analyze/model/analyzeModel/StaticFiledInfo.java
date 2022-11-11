package main.java.analyze.model.analyzeModel;

import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class StaticFiledInfo {
	private SootMethod sm;
	private Unit u;
	private Value value;

	public StaticFiledInfo(SootMethod sm, Unit u, Value value) {
		this.sm = sm;
		this.u = u;
		this.value = value;
	}

	/**
	 * @return the sm
	 */
	public SootMethod getSootMethod() {
		return sm;
	}

	/**
	 * @return the u
	 */
	public Unit getUnit() {
		return u;
	}

	/**
	 * @return the value
	 */
	public Value getValue() {
		return value;
	}

}
