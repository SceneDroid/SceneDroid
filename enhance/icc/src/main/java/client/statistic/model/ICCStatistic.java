package main.java.client.statistic.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.java.client.obj.model.ctg.IntentSummaryModel;

public class ICCStatistic {

	private int iCCFlowNum = 0;
	private Map<String, Set<IntentSummaryModel>> intentSummaryTypeMap;
	private Map<String, Set<IntentSummaryModel>> intentSummaryReceiveTypeMap;
	private Map<String, Set<IntentSummaryModel>> intentSummaryUsedTypeMap;
	private Map<String, Set<IntentSummaryModel>> intentSummarySetTypeMap;
	private Map<String, Set<IntentSummaryModel>> intentSummaryNewTypeMap;
	private Map<String, Set<IntentSummaryModel>> intentSummarySendTypeMap;
	private Map<String, Set<String>> destinationMap;
	private Map<Integer, Set<String>> destinationNum2MethodSet;
	private int intraDestinationNum;
	private int interDestinationNum;

	public ICCStatistic() {
		intentSummaryTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		intentSummaryReceiveTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		intentSummarySetTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		intentSummaryUsedTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		intentSummaryNewTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		intentSummarySendTypeMap = new HashMap<String, Set<IntentSummaryModel>>();
		destinationMap = new HashMap<String, Set<String>>();
		destinationNum2MethodSet = new HashMap<Integer, Set<String>>();
	}

	public int getICCFlowNum() {
		return iCCFlowNum;
	}

	public void setICCFlowNum(int iCCFlowNum) {
		this.iCCFlowNum = iCCFlowNum;
	}

	public void addICCFlowNum(int iCCFlowNum) {
		this.iCCFlowNum += iCCFlowNum;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummaryTypeMap() {
		return intentSummaryTypeMap;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummaryReceiveTypeMap() {
		return intentSummaryReceiveTypeMap;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummarySetTypeMap() {
		return intentSummarySetTypeMap;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummaryUsedTypeMap() {
		return intentSummaryUsedTypeMap;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummaryNewTypeMap() {
		return intentSummaryNewTypeMap;
	}

	public Map<String, Set<IntentSummaryModel>> getIntentSummarySendTypeMap() {
		return intentSummarySendTypeMap;
	}

	public Map<String, Set<String>> getDestinationMap() {
		return destinationMap;
	}

	public Map<Integer, Set<String>> getDestinationNum2MethodSet() {
		return destinationNum2MethodSet;
	}

	public int getIntraDestinationNum() {
		return intraDestinationNum;
	}

	public void addIntraDestinationNum() {
		this.intraDestinationNum++;
	}

	public int getInterDestinationNum() {
		return interDestinationNum;
	}

	public void addInterDestinationNum() {
		this.interDestinationNum++;
	}

}
