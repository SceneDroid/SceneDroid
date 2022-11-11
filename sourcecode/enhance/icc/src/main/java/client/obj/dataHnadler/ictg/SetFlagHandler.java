package main.java.client.obj.dataHnadler.ictg;

import java.util.HashSet;
import java.util.Set;

import main.java.analyze.utils.StringUtils;
import main.java.client.obj.dataHnadler.DataHandler;
import main.java.client.obj.model.component.Flag;
import main.java.client.obj.model.ctg.IntentSummaryModel;

public class SetFlagHandler extends DataHandler {

	@Override
	public void handleData(IntentSummaryModel intentSummary, String key, Set<String> dataSet) {
		Set<String> newDataSet = new HashSet<String>();
		for (String data : dataSet) {
			if (StringUtils.isInteger(data)) {
				Flag flag = new Flag();
				try {
					newDataSet.add(flag.getFlag(Integer.parseInt(data)));
				} catch (Exception NumberFormatException) {
					newDataSet.add(data);
				}
			} else{
				newDataSet.add(data);
			}
		}
		intentSummary.getSetFlagsList().addAll(newDataSet);
	}
}
