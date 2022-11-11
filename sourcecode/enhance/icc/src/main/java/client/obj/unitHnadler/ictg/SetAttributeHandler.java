package main.java.client.obj.unitHnadler.ictg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.dataHnadler.DataHandler;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.Unit;
import soot.Value;
import soot.toolkits.scalar.UnitValueBoxPair;

public class SetAttributeHandler extends UnitHandler {
	IntentSummaryModel intentSummary;

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		this.handleSingleObject(new Context(), singleObject);
	}

	@Override
	public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
		this.context = context;
		this.intentSummary = (IntentSummaryModel) singleObject;
		this.intentSummary.getDataHandleList().add(unit);
		setAttriAPIAnalyze(); // acid
	}

	@Override
	public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject, Unit targetUnit) {
		this.oldContextwithRealValue = oldContextwithRealValue;
		this.intentSummary = (IntentSummaryModel) singleObject;
		this.intentSummary.getDataHandleList().add(unit);
		this.targetUnit = targetUnit;
		setAttriAPIAnalyze(); // acid
	}

	/**
	 * calculate the value set of acdt from set stmt
	 * 
	 * @param unit
	 */
	void setAttriAPIAnalyze() {
		if (unit.toString().contains("android.content.Intent: android.content.Intent setAction(java.lang.String)")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setAction"));
		} else if (unit.toString().contains("android.content.Intent: void <init>(java.lang.String)")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setAction"));
		} else if (unit.toString().contains(
				"android.content.Intent: android.content.Intent addCategory(java.lang.String)")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setCategory"));
		} else if (unit.toString().contains("android.content.Intent: android.content.Intent setData(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setData"));
		} else if (unit.toString().contains("android.content.Intent: android.content.Intent setDataAndNormalize(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setData"));
		} else if (unit.toString().contains("setType(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setType"));
		} else if (unit.toString().contains("setDataAndType(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setData"));
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setType"));
		} else if (unit.toString().contains("setDataAndTypeAndNormalize(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setData"));
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setType"));
		} else if (unit.toString().contains("android.content.Intent: android.content.Intent setFlags(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setFlag"));
		} else if (unit.toString().contains("android.content.Intent: android.content.Intent addFlags(")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setFlag"));
		} else if (unit.toString().contains(
				"android.content.Intent: void <init>(android.content.Context,java.lang.Class)")) {
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setComponent"));
		} else if (unit.toString().contains(
				"void <init>(java.lang.String,android.net.Uri,android.content.Context,java.lang.Class)")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setAction"));
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setData"));
			addValue2GivenMap(3, this.unit, DataHandler.getDataHandler("setComponent"));
		} else if (unit.toString().contains(
				"void <init>(java.lang.String,android.net.Uri)")) {
			addValue2GivenMap(0, this.unit, DataHandler.getDataHandler("setAction"));
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setData"));
		} else if (unit.toString().contains("setClassName(android.content.Context,java.lang.String)")) {
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setComponent"));
		} else if (unit.toString().contains("setClassName(java.lang.String,java.lang.String)")) {
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setComponent"));
		} else if (unit.toString().contains("setComponent(")) {
			Value inputVar = getInputVar(0, unit);
			List<Unit> defs = SootUtils.getDefOfLocal(methodSig, inputVar, unit);
			for (Unit def : defs) {
				List<UnitValueBoxPair> uses = SootUtils.getUseOfLocal(methodSig, def);
				for (UnitValueBoxPair useBox : uses) {
					if (useBox.getUnit().toString()
							.contains("android.content.ComponentName: void <init>(java.lang.String,java.lang.String)")) {
						addValue2GivenMap(1, useBox.getUnit(), DataHandler.getDataHandler("setComponent"));
					} else if (useBox
							.getUnit()
							.toString()
							.contains(
									"android.content.ComponentName: void <init>(android.content.Context,java.lang.String)")) {
						addValue2GivenMap(1, useBox.getUnit(), DataHandler.getDataHandler("setComponent"));
					} else if (useBox
							.getUnit()
							.toString()
							.contains(
									"android.content.ComponentName: void <init>(android.content.Context,java.lang.Class)")) {
						addValue2GivenMap(1, useBox.getUnit(), DataHandler.getDataHandler("setComponent"));
					}
				}
			}
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setComponent"));
		} else if (unit.toString().contains("android.content.Intent setClass(android.content.Context,java.lang.Class)")) {
			addValue2GivenMap(1, this.unit, DataHandler.getDataHandler("setComponent"));
		} else {
			// pass parameters to deeper invocation
		}
	}

	private void addValue2GivenMap(int id, Unit unit, DataHandler dataHandler) {
		Value inputVar = getInputVar(id, unit);
		if (inputVar == null)
			return;
		Context objContextInner = new Context();
		if (oldContextwithRealValue != null) {
			objContextInner = constructContextObj(id + 1, unit);
		}
		ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
		List<String> resList = vo.getValueofVar(inputVar, unit, 0).getValues();
		Set<String> resSet = new HashSet<>();
		for(String ele: resList){
			if(ele!=null) resSet.add(ele);
		}
		dataHandler.handleData(intentSummary, SootUtils.getNameofClass(className), resSet);
	}

}
