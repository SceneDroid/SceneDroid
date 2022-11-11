package main.java.analyze.model.analyzeModel;

import main.java.analyze.utils.SootUtils;
import soot.SootMethod;
import soot.Unit;

public class ParameterSource {
	private SootMethod currentMethod;
	private Unit unit;
	private int contextLocationId;
	private int useLocationId;

	public ParameterSource(String method_name, Unit unit, int contextLocationId, int useLocationId) {
		this.setCurrentMethod(SootUtils.getSootMethodBySignature(method_name));
		this.setUnit(unit);
		this.setContextLocationId(contextLocationId);
		this.setUseLocationId(useLocationId);
	}

	@Override
	public String toString() {
		String res = "";
		res += "\ncurrentMethod.getSignature():" + currentMethod.getSignature() + "\n";
		res += "unit:" + unit + "\n";
		res += "useLocationId:" + useLocationId + "\n";
		res += "contextLocationId:" + contextLocationId;

		return res;
	}

	/**
	 * @return the currentMethod
	 */
	public SootMethod getCurrentMethod() {
		return currentMethod;
	}

	/**
	 * @param currentMethod
	 *            the currentMethod to set
	 */
	public void setCurrentMethod(SootMethod currentMethod) {
		this.currentMethod = currentMethod;
	}

	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * @return the useLocationId
	 */
	public int getUseLocationId() {
		return useLocationId;
	}

	/**
	 * @param useLocationId
	 *            the useLocationId to set
	 */
	public void setUseLocationId(int useLocationId) {
		this.useLocationId = useLocationId;
	}

	/**
	 * @return the contextLocationId
	 */
	public int getContextLocationId() {
		return contextLocationId;
	}

	/**
	 * @param contextLocationId
	 *            the contextLocationId to set
	 */
	public void setContextLocationId(int contextLocationId) {
		this.contextLocationId = contextLocationId;
	}
}
