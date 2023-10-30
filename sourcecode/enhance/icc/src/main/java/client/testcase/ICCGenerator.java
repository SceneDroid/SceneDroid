package main.java.client.testcase;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.Analyzer;
import main.java.Global;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.ctg.ICCMsg;
import main.java.client.obj.model.ctg.IntentSummaryModel;

public class ICCGenerator extends Analyzer {
	private String className;
	private Set<ICCMsg> ICCSet;
	
	
	public ICCGenerator(String className) {
		super();
		this.className = className;
		this.ICCSet = new HashSet<ICCMsg>();
	}

	@Override
	public void analyze() {
		ComponentModel component = appModel.getComponentMap().get(className);
		ICCGenerationFromReceivedIntentSummaryModel(component.getReceiveModel().getIntentObjsbyICCMsg());
		ICCGenerationFromSpecIntentSummaryModel(component.getReceiveModel().getIntentObjsbySpec());
		ICCGenerationFromManifest(component.getIntentFilters());
	}
	

	/**
	 * generate ICC messages according to the manifest specification
	 * @param intentFilters
	 */
	private void ICCGenerationFromManifest(List<IntentFilterModel> intentFilters) {
		for(IntentFilterModel model: intentFilters){
			Set<String> actions = model.getAction_list();
			Set<String> categories = model.getCategory_list();
			Set<Data> datas = model.getData_list();
			Set<String> types = model.getDatatype_list();
			BundleType extras = new BundleType();
			
			//generate ICCs use different strategies
			genereateICCRandomely(actions, categories, datas, types, extras);
			//genereateICCXXX
			//genereateICCYYY
		}
		
	}

	/**
	 * generate ICC messages according to intent summary model
	 * may from the java ICC receiving specification 
	 * may from the ICC instances received from the ICC callers
	 * @param modelSet
	 */
	private void genereateICCRandomely(Set<String> actions, Set<String> categories, Set<Data> datas, Set<String> types,
			BundleType extras) {
		Set<String> dataStrSet = new HashSet<String>();
		for(Data data: datas){
			dataStrSet.add(data.toString());
		}
		genereateICCRandomely(actions, categories, dataStrSet, types, extras);
		
	}

	/**
	 * generate ICC messages according to intent summary model
	 * may from the java ICC receiving specification 
	 * may from the ICC instances received from the ICC callers
	 * @param modelSet
	 */
	private void ICCGenerationFromReceivedIntentSummaryModel(Set<IntentSummaryModel> modelSet) {
		for(IntentSummaryModel model: modelSet){
			List<String> actions = model.getSetActionValueList();
			List<String> categories = model.getSetCategoryValueList();
			List<String> datas = model.getSetDataValueList();
			List<String> types = model.getSetTypeValueList();
			BundleType extras = model.getSetExtrasValueList();
			
			//generate ICCs use different strategies
			genereateICCRandomely(actions, categories, datas, types, extras);
			//genereateICCXXX
			//genereateICCYYY
		}
	} 
	/**
	 * generate ICC messages according to intent summary model
	 * may from the java ICC receiving specification 
	 * may from the ICC instances received from the ICC callers
	 * @param modelSet
	 */
	private void ICCGenerationFromSpecIntentSummaryModel(Set<IntentSummaryModel> modelSet) {
		for(IntentSummaryModel model: modelSet){
			List<String> actions = model.getGetActionCandidateList();
			List<String> categories = model.getGetCategoryCandidateList();
			List<String> datas = model.getGetDataCandidateList();
			List<String> types = model.getGetTypeCandidateList();
			BundleType extras = model.getGetExtrasCandidateList();
			
			//generate ICCs use different strategies
			genereateICCRandomely(actions, categories, datas, types, extras);
			//genereateICCXXX
			//genereateICCYYY
		}
	} 

	/**
	 * randomly generate one ICCMsg
	 * @param actions
	 * @param categories
	 * @param datas
	 * @param types
	 * @param extras
	 */
	@SuppressWarnings("unchecked")
	private void genereateICCRandomely(Collection<String> actions, Collection<String> categories, Collection<String> datas,
			Collection<String> types, BundleType extras) {
		ICCMsg msg = new ICCMsg();
		msg.setAction( getRandomElementFromSet(actions));
		msg.setCategory(getRandomElementSetFromSet(categories));
		msg.setData( getRandomElementFromSet(datas));
		msg.setType( getRandomElementFromSet(types));
		Set<String> extraSet = new HashSet<String>();
		for(List<ExtraData> eds: extras.obtainBundle().values())
			for(ExtraData ed: eds)
				extraSet.add(ed.toString());
		msg.setExtra(extraSet);
		addToICCSet(msg);
	}
	
	
	/**
	 * randomly generate one attribute for ICCMsg
	 * @param s
	 * @return  
	 */
	private String getRandomElementFromSet(Collection<String> collection){
		if(collection == null || collection.size()==0)
			return "";
		Object[] obj =collection.toArray();
		String res =  (String) obj[(int)(Math.random()*obj.length)];
		return res;
	}
	/**
	 * randomly generate a set of attribute for ICCMsg
	 * @param s
	 * @return 
	 * @return 
	 */
	private Set<String> getRandomElementSetFromSet(Collection<String> collection){
		if(collection == null || collection.size()==0)
			return new HashSet<String>();
		HashSet<String> newSet = new HashSet<String>();
		Object[] obj =collection.toArray();
		newSet.add((String) obj[(int)(Math.random()*obj.length)]);
		return newSet;
	}
	
	/**
	 * add to set of ICC, for test generation
	 * @param msg 
	 */
	private void addToICCSet(ICCMsg msg) {
		Map<String, Set<String>> map = Global.v().getAppModel().getICCStringMap();
		if(!map.containsKey(className)){
			map.put(className, new HashSet<String>());
		}
		if(map.get(className).contains(msg.toString()))
			return;
		map.get(className).add(msg.toString());
		ICCSet.add(msg);	
	}

	
	/**
	 * generate ICC set to test case generator
	 * @return
	 */
	public Set<ICCMsg> getICCSet(){
		return this.ICCSet;
	}
	

//	/**
//	 * add Single Null to all used paths from appModel.pathMap_AU to
//	 * appModel.pathMap_SNV
//	 * 
//	 * @throws CloneNotSupportedException
//	 */
//	private void generateSingleNullAUPs2Map() throws CloneNotSupportedException {
//		for (ComponentModel component : appModel.getComponentMap().values()) {
//			addNullAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addActionAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addCategoryAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addDataAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addTypeAUPs(component.getReceiveModel().getPathMap_AU(), component.getReceiveModel().getPathMap_SNV());
//			addExtrasAUPs(component, component.getReceiveModel().getPathMap_AU(), component.getReceiveModel()
//					.getPathMap_SNV());
//		}
//	}
//
//	/**
//	 * add null ICC to mapTo
//	 * 
//	 * @param pathMap_SNV
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addNullAUPs(Map<String, Set<ICCMsg>> mapFrom, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : mapFrom.entrySet()) {
//			ICCMsg newMsg = new ICCMsg("");
//			CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//		}
//	}
//
//	/**
//	 * add null Action to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addActionAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg;
//				newMsg = icc.clone();
//				if (newMsg.getAction() != null) {
//					newMsg.setAction(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Category to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addCategoryAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg;
//				newMsg = icc.clone();
//				if (newMsg.getCategory() != null && newMsg.getCategory().size() != 0) {
//					newMsg.setCategory(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Data to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addDataAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg = icc.clone();
//				if (newMsg.getData() != null) {
//					newMsg.setData(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add null Type to mapTo
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addTypeAUPs(Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				ICCMsg newMsg = icc.clone();
//				if (newMsg.getType() != null) {
//					newMsg.setType(null);
//					CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//				}
//			}
//		}
//	}
//
//	/**
//	 * add Extra AUPs
//	 * 
//	 * @param component
//	 * 
//	 * @param mapFrom
//	 * @param mapTo
//	 * @throws CloneNotSupportedException
//	 */
//	private void addExtrasAUPs(ComponentModel component, Map<String, Set<ICCMsg>> map, Map<String, Set<ICCMsg>> mapTo)
//			throws CloneNotSupportedException {
//		for (Entry<String, Set<ICCMsg>> en : map.entrySet()) {
//			Set<ICCMsg> iccList = en.getValue();
//			if (iccList == null)
//				continue;
//			Set<ICCMsg> iccListCopy = new HashSet<ICCMsg>(iccList);
//			for (ICCMsg icc : iccListCopy) {
//				if (icc.getExtra() == null)
//					continue;
//				ICCMsg nullExtraMsg = icc.clone();
//				nullExtraMsg.setExtra(null);
//				CollectionUtils.addNewMsg2Map(component.getReceiveModel().getPathMap_SNV(), nullExtraMsg, en.getKey());
//
//				HashSet<String> extraCopy = new HashSet<String>(icc.getExtra());
//				for (String extraPair : extraCopy) {
//					String ss[] = extraPair.split(","); // extra:Bundle-"b1",(,String-"s2",)
//					for (int i = 0; i < ss.length; i++) {
//						if (ss[i].equals("(") || ss[i].equals(")"))
//							continue;
//						String str = ss[i];
//						String rmStr = str;
//						if (i < ss.length - 1 && ss[i + 1].equals("(")) {
//							rmStr += "," + ss[i + 1];
//							int num = 1, j = i + 2;
//							while (num > 0 && j < ss.length) {
//								if (ss[j].equals("("))
//									num++;
//								if (ss[j].equals(")"))
//									num--;
//								rmStr += "," + ss[j];
//								j++;
//							}
//						}
//						String addExtra = getAddExtra(extraPair, rmStr);
//						ICCMsg newMsg = icc.clone();
//						newMsg.getExtra().remove(extraPair);
//						newMsg.getExtra().add(addExtra);
//						CollectionUtils.addNewMsg2Map(mapTo, newMsg, en.getKey());
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * get Add Extra
//	 * 
//	 * @param extraPair
//	 * @param rmStr
//	 * @return
//	 */
//	private String getAddExtra(String extraPair, String rmStr) {
//		String addExtra = extraPair.replace(rmStr, "");
//		while (addExtra.contains(",,") || addExtra.contains("()")) {
//			addExtra = addExtra.replace(",,", ",");
//			addExtra = addExtra.replace("()", "");
//		}
//		if (addExtra.endsWith(","))
//			addExtra = addExtra.substring(0, addExtra.length() - 1);
//		return addExtra;
//	}

 }
