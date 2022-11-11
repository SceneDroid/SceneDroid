package main.java.client.obj.unitHnadler;

import java.util.List;

import main.java.Global;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.model.analyzeModel.ParameterSource;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.NestableObj;
import main.java.analyze.utils.SootUtils;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public abstract class UnitHandler {
	protected AppModel appModel;
	protected Unit unit;

	protected SootMethod methodUnderAnalyze;
	protected String methodSig;
	protected String className;

	protected Context context;
	protected Context oldContextwithRealValue;
	protected Unit targetUnit;

	public void init(SootMethod method, Unit unit) {
		this.methodUnderAnalyze = method;
		this.unit = unit;
		this.methodSig = method.getSignature();
		this.className = SootUtils.getNameofClass(method.getDeclaringClass().getName());
		this.appModel = Global.v().getAppModel();

	}

	public void handleSingleObject(ObjectSummaryModel singleObject) {
	}

	public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
	}

	public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject,
			Unit handleTargetUnit) {
	}

	// public abstract void recordAnalysis();

	/**
	 * construct dummy context for inner function
	 * 
	 * @param unit
	 * @param id
	 * @param oldObjContext
	 * @param handleTarget
	 * 
	 */
	public Context constructContextObj(int usedLocation, Unit unit) {
		Context objContextInner = new Context();
		int innerContextLocation = 0;
		List<ParameterSource> psList = appModel.getUnit2ParameterSource().get(unit);
		for (ParameterSource psInTarget : psList) {
			if (psInTarget.getCurrentMethod().getSignature().equals(methodSig))
				innerContextLocation = psInTarget.getContextLocationId();
		}
		int realLocation = collectRealLocationInContext(usedLocation, unit, methodSig);
		// strange?
		if (oldContextwithRealValue.getObjs().size() <= realLocation)
			return objContextInner;

		for (int id = 0; id <= innerContextLocation; id++) {
			if (id < innerContextLocation)
				objContextInner.addObj(new NestableObj(""));
			else {
				objContextInner.addObj(oldContextwithRealValue.getObjs().get(realLocation));
			}
		}
		return objContextInner;
	}

	/**
	 * collectUse2OldContextLocation used location to parameter location
	 * 
	 * @param unit
	 * @param usedLocation
	 * @param use2OldContextLocation
	 * @param psList
	 */
	public int collectRealLocationInContext(int usedLocation, Unit unit, String methodSig) {
		InvokeExpr targetExp = SootUtils.getInvokeExp(targetUnit);
		String targetStopMethodSig = targetExp.getMethod().getSignature();
		List<ParameterSource> psList = appModel.getUnit2ParameterSource().get(unit);
		for (ParameterSource psInTarget : psList) {
			// InvokeExpr psInTargetExp =
			// SootUtils.getInvokeExp(psInTarget.getUnit());
			for (List<ParameterSource> tempList : appModel.getUnit2ParameterSource().values()) {
				for (ParameterSource temp : tempList) {
					InvokeExpr tempExp = SootUtils.getInvokeExp(temp.getUnit());
					if (tempExp.getMethod().getSignature().equals(psInTarget.getCurrentMethod().getSignature())) {
						if (!temp.getCurrentMethod().getSignature().equals(targetStopMethodSig)) // reach
																									// the
																									// top
																									// unit
																									// from
																									// bottom
																									// up
							continue;
						if (temp.getUseLocationId() == usedLocation) {
							return collectRealLocationInContext(temp.getContextLocationId(), temp.getUnit(), tempExp
									.getMethod().getSignature());
						}
					}
				}
			}
		}
		for (ParameterSource psInTarget : psList) {
			if (psInTarget.getUseLocationId() == usedLocation) {
				return psInTarget.getContextLocationId();
			}
		}
		return usedLocation;
	}

	public Value getInputVar(int i, Unit unit) {
		Value inputVar = null;
		if (unit instanceof JAssignStmt) {
			JAssignStmt as = (JAssignStmt) unit;
			if (as.getInvokeExpr().getArgCount() > i)
				inputVar = as.getInvokeExpr().getArg(i);
		} else if (unit instanceof JInvokeStmt) {
			JInvokeStmt inv = (JInvokeStmt) unit;
			if (inv.getInvokeExpr().getArgCount() > i)
				inputVar = inv.getInvokeExpr().getArg(i);
		}
		return inputVar;
	}

}
