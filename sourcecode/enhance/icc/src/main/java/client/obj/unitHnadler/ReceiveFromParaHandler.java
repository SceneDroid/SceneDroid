package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;

public class ReceiveFromParaHandler extends UnitHandler {

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		singleObject.getReceiveFromParaList().add(unit);
	}

}
