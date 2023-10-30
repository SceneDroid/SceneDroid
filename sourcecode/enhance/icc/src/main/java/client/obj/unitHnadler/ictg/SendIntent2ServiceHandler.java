package main.java.client.obj.unitHnadler.ictg;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.utils.ConstantUtils;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.obj.unitHnadler.UnitHandler;

public class SendIntent2ServiceHandler extends UnitHandler {

	IntentSummaryModel intentSummary;

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		intentSummary = ((IntentSummaryModel) singleObject);
		intentSummary.getSendIntent2ICCList().add(unit);
		intentSummary.setTargetType(ConstantUtils.SERVICE);
	}

}
