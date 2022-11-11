package main.java.client.obj.dataHnadler;

import java.util.Set;

import main.java.client.obj.dataHnadler.ictg.GetActionHandler;
import main.java.client.obj.dataHnadler.ictg.GetCategoryHandler;
import main.java.client.obj.dataHnadler.ictg.GetDataHandler;
import main.java.client.obj.dataHnadler.ictg.GetTypeHandler;
import main.java.client.obj.dataHnadler.ictg.SetActionHandler;
import main.java.client.obj.dataHnadler.ictg.SetCategoryHandler;
import main.java.client.obj.dataHnadler.ictg.SetComponentHandler;
import main.java.client.obj.dataHnadler.ictg.SetDataHandler;
import main.java.client.obj.dataHnadler.ictg.SetFlagHandler;
import main.java.client.obj.dataHnadler.ictg.SetTypeHandler;
import main.java.client.obj.model.ctg.IntentSummaryModel;

public abstract class DataHandler {

	public abstract void handleData(IntentSummaryModel intentSummary, String className, Set<String> dataSet);

	/**
	 * get the correct handler of target unit
	 * 
	 * @param m
	 * @param appModel
	 * @param intentSummary
	 * @param u
	 * @return
	 */
	public static DataHandler getDataHandler(String dataType) {
		if (dataType == null)
			return null;
		if (dataType.equals("setAction")) {
			return new SetActionHandler();
		} else if (dataType.equals("setCategory")) {
			return new SetCategoryHandler();
		} else if (dataType.equals("setData")) {
			return new SetDataHandler();
		} else if (dataType.equals("setType")) {
			return new SetTypeHandler();
		} else if (dataType.equals("setFlag")) {
			return new SetFlagHandler();
		} else if (dataType.equals("setComponent")) {
			return new SetComponentHandler();
		} else if (dataType.equals("getAction")) {
			return new GetActionHandler();
		} else if (dataType.equals("getCategory")) {
			return new GetCategoryHandler();
		} else if (dataType.equals("getData")) {
			return new GetDataHandler();
		} else if (dataType.equals("getType")) {
			return new GetTypeHandler();
		}

		return null;
	}

}
