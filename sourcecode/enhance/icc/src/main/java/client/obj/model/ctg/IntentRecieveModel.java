package main.java.client.obj.model.ctg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.Global;
import main.java.analyze.model.analyzeModel.Attribute;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;

public class IntentRecieveModel {
	private Set<String> receivedActionSet;
	private Set<String> receivedCategorySet;
	private Set<String> receivedDataSet;
	private Set<String> receivedTypeSet;
	private BundleType receivedExtraData;

	private Map<String, Set<ICCMsg>> pathMap;
	private Map<String, Set<ICCMsg>> pathMap_AU;
	private Map<String, Set<ICCMsg>> pathMap_SNV;
	private Map<String, Set<List<Attribute>>> globalPathMap;

	private Set<IntentSummaryModel> IntentObjsbyICCMsg;
	private Set<IntentSummaryModel> IntentObjsbySpec;
	public IntentRecieveModel() {
		receivedActionSet = new HashSet<String>();
		receivedCategorySet = new HashSet<String>();
		receivedDataSet = new HashSet<String>();
		receivedTypeSet = new HashSet<String>();
		receivedExtraData = new BundleType();

		setPathMap(new HashMap<String, Set<ICCMsg>>());
		setPathMap_AU(new HashMap<String, Set<ICCMsg>>());
		setPathMap_SNV(new HashMap<String, Set<ICCMsg>>());
		setGlobalPathMap(new HashMap<String, Set<List<Attribute>>>());
		
		setIntentObjsbyICCMsg(new HashSet<IntentSummaryModel>());
		setIntentObjsbySpec(new HashSet<IntentSummaryModel>());
	}

	public Set<String> getReceivedActionSet() {
		return receivedActionSet;
	}

	public void setReceivedActionSet(Set<String> receivedActionSet) {
		this.receivedActionSet = receivedActionSet;
	}

	public Set<String> getReceivedCategorySet() {
		return receivedCategorySet;
	}

	public void setReceivedCategorySet(Set<String> receivedCategorySet) {
		this.receivedCategorySet = receivedCategorySet;
	}

	public Set<String> getReceivedDataSet() {
		return receivedDataSet;
	}

	public void setReceivedDataSet(Set<String> receivedDataSet) {
		this.receivedDataSet = receivedDataSet;
	}

	public Set<String> getReceivedTypeSet() {
		return receivedTypeSet;
	}

	public void setReceivedTypeSet(Set<String> receivedTypeSet) {
		this.receivedTypeSet = receivedTypeSet;
	}

	public BundleType getReceivedExtraData() {
		return receivedExtraData;
	}

	public void setReceivedExtraData(BundleType bundleItem) {
		this.receivedExtraData = bundleItem;
	}

	public Map<String, Set<ICCMsg>> getPathMap_AU() {
		return pathMap_AU;
	}

	public void setPathMap_AU(Map<String, Set<ICCMsg>> pathMap_AU) {
		this.pathMap_AU = pathMap_AU;
	}

	public Map<String, Set<ICCMsg>> getPathMap_SNV() {
		return pathMap_SNV;
	}

	public void setPathMap_SNV(Map<String, Set<ICCMsg>> pathMap_SNV) {
		this.pathMap_SNV = pathMap_SNV;
	}

	public Map<String, Set<ICCMsg>> getPathMap() {
		return pathMap;
	}

	public void setPathMap(Map<String, Set<ICCMsg>> pathMap) {
		this.pathMap = pathMap;
	}

	public Map<String, Set<List<Attribute>>> getGlobalPathMap() {
		return globalPathMap;
	}

	public void setGlobalPathMap(Map<String, Set<List<Attribute>>> globalPathMap) {
		this.globalPathMap = globalPathMap;
	}

	/**
	 * get Used ACDTStr single null value
	 * 
	 * @param clsname
	 * @return
	 */
	public static Set<ICCMsg> getUsedACDTStrSNV(String clsname) {
		ComponentModel componentInstance = Global.v().getAppModel().getComponentMap().get(clsname);
		if (!componentInstance.getReceiveModel().getPathMap_SNV().containsKey(clsname))
			return null;
		else
			return componentInstance.getReceiveModel().getPathMap_SNV().get(clsname);
	}

	/**
	 * get Used ACDTStr All used
	 * 
	 * @param clsname
	 * @return
	 */
	public static Set<ICCMsg> getUsedACDTStrAU(String clsname) {
		ComponentModel componentInstance = Global.v().getAppModel().getComponentMap().get(clsname);
		if (!componentInstance.getReceiveModel().getPathMap_AU().containsKey(clsname))
			return null;
		else
			return componentInstance.getReceiveModel().getPathMap_AU().get(clsname);
	}


	/**
	 * @return the intentObjsbySpec
	 */
	public Set<IntentSummaryModel> getIntentObjsbySpec() {
		return IntentObjsbySpec;
	}

	/**
	 * @param intentObjsbySpec the intentObjsbySpec to set
	 */
	public void setIntentObjsbySpec(Set<IntentSummaryModel> intentObjsbySpec) {
		IntentObjsbySpec = intentObjsbySpec;
	}

	/**
	 * @return the intentObjsbyICCMsg
	 */
	public Set<IntentSummaryModel> getIntentObjsbyICCMsg() {
		return IntentObjsbyICCMsg;
	}

	/**
	 * @param intentObjsbyICCMsg the intentObjsbyICCMsg to set
	 */
	public void setIntentObjsbyICCMsg(Set<IntentSummaryModel> intentObjsbyICCMsg) {
		IntentObjsbyICCMsg = intentObjsbyICCMsg;
	}
}
