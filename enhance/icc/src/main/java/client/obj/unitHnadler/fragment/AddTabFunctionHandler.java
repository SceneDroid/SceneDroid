package main.java.client.obj.unitHnadler.fragment;

import java.io.IOException;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.fragment.FragmentSummaryModel;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.Unit;
import soot.Value;

public class AddTabFunctionHandler extends UnitHandler {
	Context context;
	FragmentSummaryModel singleFrag;

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		this.handleSingleObject(new Context(), singleObject);
	}

	@Override
	public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
		this.context = context;
		this.singleFrag = (FragmentSummaryModel) singleObject;
		this.singleFrag.getSendFragment2Start().add(unit);
		try {
			addTabAnalyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject, Unit targetUnit) {
		this.oldContextwithRealValue = oldContextwithRealValue;
		this.singleFrag = (FragmentSummaryModel) singleObject;
		this.singleFrag.getDataHandleList().add(unit);
		this.targetUnit = targetUnit;
		try {
			addTabAnalyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addTabAnalyze() throws IOException {
		int id = 1;
		Value inputVar = getInputVar(id, unit);
		if (inputVar == null)
			return;
		Context objContextInner = new Context();
		if (oldContextwithRealValue != null) {
			objContextInner = constructContextObj(id + 1, unit);
		}
		ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
		String className = vo.getValueofVar(inputVar, unit, 0).getValues().get(0);
		className = className.replace("/", ".").replace("class L", "").replace(";", "");
		className = className.split("@")[0];
		this.singleFrag.getSetDestinationList().add(className);
	}

}
