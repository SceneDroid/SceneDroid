package main.java.analyze.model.sootAnalysisModel;

import main.java.analyze.model.sootAnalysisModel.UnitContext;
import soot.SootMethod;
import soot.Unit;

public class UnitContext {
	private SootMethod prepreviousMethod;

	private SootMethod previousMethod;
	private SootMethod currentMethod;
	private Unit prepreviousUnit;
	private Unit previousUnit;
	private Unit currentUnit;

	@Override
	public Object clone() throws CloneNotSupportedException {
		UnitContext uc = new UnitContext();
		uc.prepreviousMethod = this.prepreviousMethod;
		uc.previousMethod = this.previousMethod;
		uc.currentMethod = this.currentMethod;
		uc.prepreviousUnit = this.prepreviousUnit;
		uc.previousUnit = this.previousUnit;
		uc.currentUnit = this.currentUnit;
		return uc;
	}

	public SootMethod getPrepreviousMethod() {
		return prepreviousMethod;
	}

	public void setPrepreviousMethod(SootMethod prepreviousMethod) {
		this.prepreviousMethod = prepreviousMethod;
	}

	public SootMethod getPreviousMethod() {
		return previousMethod;
	}

	public void setPreviousMethod(SootMethod previousMethod) {
		this.previousMethod = previousMethod;
	}

	public SootMethod getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(SootMethod currentMethod) {
		this.currentMethod = currentMethod;
	}

	public Unit getPrepreviousUnit() {
		return prepreviousUnit;
	}

	public void setPrepreviousUnit(Unit prepreviousUnit) {
		this.prepreviousUnit = prepreviousUnit;
	}

	public Unit getPreviousUnit() {
		return previousUnit;
	}

	public void setPreviousUnit(Unit previousUnit) {
		this.previousUnit = previousUnit;
	}

	public Unit getCurrentUnit() {
		return currentUnit;
	}

	public void setCurrentUnit(Unit currentUnit) {
		this.currentUnit = currentUnit;
	}

}
