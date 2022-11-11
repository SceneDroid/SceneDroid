package main.java.client.obj.unitHnadler;

import main.java.analyze.model.analyzeModel.ObjectSummaryModel;

public class ReceiveFromRetValueHandler extends UnitHandler {

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		singleObject.getReceiveFromFromRetValueList().add(unit);
	}

}
