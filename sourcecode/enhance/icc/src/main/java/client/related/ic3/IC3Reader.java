package main.java.client.related.ic3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.Scene;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.BroadcastReceiverModel;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.ContentProviderModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.component.ServiceModel;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.related.ic3.model.IC3Model;
import main.java.client.statistic.model.DoStatistic;
import main.java.client.statistic.model.StatisticResult;

public class IC3Reader extends Analyzer {
	Map<String, ComponentModel> IC3ComponentMap = new HashMap<String, ComponentModel>();
	IC3Model model;
	protected StatisticResult result;
	protected Map<String, MethodSummaryModel> summaryMap;

	public IC3Reader(StatisticResult result) {
		this.result = result;
		summaryMap = new HashMap<String, MethodSummaryModel>();
	}

	@Override
	public void analyze() {
		model = Global.v().getiC3Model();
//		model.setIC3FilePath(ConstantUtils.IC3FOLDETR + appModel.getPackageName() + "_"
//		+ appModel.getVersionCode() + ".json");
		File oldf = new File(ConstantUtils.IC3TMPFOLDETR + appModel.getPackageName() + "_"+ appModel.getVersionCode() + ".json");
		if(oldf.exists()){
			File newf = new File(ConstantUtils.IC3FOLDETR + appModel.getAppName()+ ".json");
			oldf.renameTo(newf);
		}
		model.setIC3FilePath(ConstantUtils.IC3FOLDETR + appModel.getAppName()+ ".json");
		if(obtainATGfromFile()){
			long time = runtimeAnalyze();
			componentAnalyze();
			ICCAnalyze();
		}
	}
	private boolean obtainATGfromFile() {
		File file = new File(model.getIC3FilePath());
		if (!file.exists()) {
			model.getIC3AtgModel().setExist(false);
			return false;
		}
		return true;
	}
	
	private void makeStatistic() {
		for (Entry<String, MethodSummaryModel> en : summaryMap.entrySet()) {
			DoStatistic.updateXMLStatisticUseSummayMap(true, en.getValue(), result);
			DoStatistic.updateXMLStatisticUseSummayMap(false, en.getValue(), result);
		}
	}
	
	/**
	 * extract the analysis time of IC3
	 * @return
	 */
	private long runtimeAnalyze() {
		String s = FileUtils.readJsonFile(model.getIC3FilePath());
		JSONObject jobj = JSON.parseObject(s);
		if (jobj != null) {
			Long start = jobj.getLong("analysis_start");
			Long end = jobj.getLong("analysis_end");
			return end - start;
		}
		return -1;
	}
	private void componentAnalyze() {
		String s = FileUtils.readJsonFile(model.getIC3FilePath());
		JSONObject jobj = JSON.parseObject(s);
		if (jobj == null) {
			model.getIC3AtgModel().setExist(false);
			return;
		}
		JSONArray components = jobj.getJSONArray("components");
		if (components == null)
			return;
		for (int i = 0; i < components.size(); i++) {
			JSONObject component = (JSONObject) components.get(i);
			ComponentModel componentModel = getComponentByKind(component.getString("kind"));

			String src = component.getString("name");
			componentModel.setComponetName(src);
			// src = src.split("\\.")[src.split("\\.").length-1];
			if (Global.v().getAppModel().getComponentMap().containsKey(src)) {
				IC3ComponentMap.put(src, componentModel);
				JSONArray intent_filters = component.getJSONArray("intent_filters");
				if (intent_filters == null)
					continue;
				for (int j = 0; j < intent_filters.size(); j++) {
					JSONObject intent_filter = (JSONObject) intent_filters.get(j);
					JSONArray attributes = intent_filter.getJSONArray("attributes");
					if (attributes == null)
						continue;
					IntentFilterModel ifModel = new IntentFilterModel();
					componentModel.getIntentFilters().add(ifModel);
					for (int k = 0; k < attributes.size(); k++) {
						JSONObject attribute = (JSONObject) attributes.get(k);
						String kind = attribute.getString("kind");
						Set<String> att_list = new HashSet<String>();
						if (attribute.getString("value") == null)
							continue;
						JSONArray values = attribute.getJSONArray("value");
						String value = attribute.getString("value");
						for (Object att : values) {
							att_list.add((String) att);
						}
						Data data = new Data();
						if (kind.equals("0") || kind.equals("ACTION"))
							ifModel.setAction_list(att_list);
						else if (kind.equals("1") || kind.equals("CATEGORY"))
							ifModel.setCategory_list(att_list);
						else if (kind.equals("4") || kind.equals("TYPE"))
							data.setMime_type(value);
						else if (kind.equals("6") || kind.equals("SCHEME"))
							data.setScheme(value);
						else if (kind.equals("9") || kind.equals("HOST"))
							data.setHost(value);
						else if (kind.equals("10") || kind.equals("PATH"))
							data.setPath(value);
						else if (kind.equals("11") || kind.equals("PORT"))
							data.setPort(value);
						if (data.toString().length() > 0)
							ifModel.getData_list().add(data);
					}
				}
			}
		}
	}

	private ComponentModel getComponentByKind(String kind) {
		if (kind.equals("0") || kind.equals("ACTIVITY"))
			return new ActivityModel(null);
		else if (kind.equals("1") || kind.equals("SERVICE"))
			return new ServiceModel(null);
		else if (kind.equals("2") || kind.equals("RECEIVER"))
			return new BroadcastReceiverModel(null);
		else if (kind.equals("3") || kind.equals("DYNAMIC_RECEIVER"))
			return new BroadcastReceiverModel(null);
		else if (kind.equals("4") || kind.equals("PROVIDER"))
			return new ContentProviderModel(null);
		return null;
	}

	private void ICCAnalyze() {
		String s = FileUtils.readJsonFile(model.getIC3FilePath());
		JSONObject jobj = JSON.parseObject(s);
		if (jobj == null)
			return;

		JSONArray components = jobj.getJSONArray("components");
		if (components == null)
			return;
		for (int i = 0; i < components.size(); i++) {
			JSONObject component = (JSONObject) components.get(i);
			String src = (String) component.get("name");
			// src = src.split("\\.")[src.split("\\.").length-1];
			JSONArray exit_points = component.getJSONArray("exit_points");
			if (exit_points == null)
				continue;
			for (int j = 0; j < exit_points.size(); j++) {
				JSONObject exit_point = (JSONObject) exit_points.get(j);
				JSONObject instruction = exit_point.getJSONObject("instruction");
				int instructionId = instruction.getInteger("id");
				String method = instruction.getString("method");
				String statement = instruction.getString("statement");
				String intentType = getIntentType(statement);
				String ICCkind = exit_point.getString("kind");

				JSONArray intents = exit_point.getJSONArray("intents");
				if (intents == null)
					continue;
				for (int k = 0; k < intents.size(); k++) {
					JSONObject intent = (JSONObject) intents.get(k);
					analyzeIntent(src, method, statement, instructionId, ICCkind, intent, intentType);
				}
			}
		}
	}

	private String getIntentType(String statement) {
		if (isSendIntent2ActivityMethod(statement)) {
			return ConstantUtils.ACTIVITY;
		} else if (isSendIntent2ServiceMethod(statement)) {
			return ConstantUtils.SERVICE;
		} else if (isSendIntent2ProviderMethod(statement)) {
			return ConstantUtils.PROVIDER;
		} else if (isSendIntent2ReceiverMethod(statement)) {
			return ConstantUtils.RECEIVER;
		}
		return "unkown";
	}

	public static boolean isSendIntent2ActivityMethod(String u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ActivityMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ActivityMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ReceiverMethod(String u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ReceiverMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ReceiverMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ProviderMethod(String u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ProviderMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ProviderMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ServiceMethod(String u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ServiceMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ServiceMethods[i]))
				return true;
		}
		return false;
	}

	private void analyzeIntent(String src, String method, String statement, int instructionId, String iCCkind,
			JSONObject intent, String intentType) {
		JSONArray attributes = intent.getJSONArray("attributes");
		if (attributes == null)
			return;
		String data = "";
		IntentSummaryModel intentSummary = new IntentSummaryModel(null);
		intentSummary.setTargetType(intentType);
		intentSummary.setNodes(new ArrayList<UnitNode>());
		boolean hasDes = false;
		for (int m = 0; m < attributes.size(); m++) {
			JSONObject attribute = (JSONObject) attributes.get(m);
			String kind = attribute.getString("kind");
			JSONArray values = attribute.getJSONArray("value");
			String value = attribute.getString("value");
			if (kind.equals("0") || kind.equals("ACTION")) {// ACTION
				for (int n = 0; n < values.size(); n++)
					intentSummary.getSetActionValueList().add(values.getString(n));
			} else if (kind.equals("1") || kind.equals("CATEGORY")) {// CATEGORY
				for (int n = 0; n < values.size(); n++){
					intentSummary.getSetCategoryValueList().add(values.getString(n));
				}
			} else if (kind.equals("2") || kind.equals("PACKAGE")) {// CLASS
			} else if (kind.equals("3") || kind.equals("CLASS")) {// CLASS
				String des = values.getString(0).replace("/", ".");
				if (Global.v().getAppModel().getComponentMap().containsKey(des)) {
					intentSummary.getSetDestinationList().add(des);
					AtgEdge edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method, instructionId, iCCkind);
					model.getIC3AtgModel().addAtgEdges(src, edge);
					addToSummaryMap(src, method, intentSummary);
				}
				hasDes = true;
			} else if (kind.equals("7") || kind.equals("EXTRA")) {
				BundleType bt = intentSummary.getSetExtrasValueList();
				List<ExtraData> eds = new ArrayList<ExtraData>();
				for (int n = 0; n < values.size(); n++) {
					ExtraData ed = new ExtraData();
					ed.setName(values.getString(n));
					eds.add(ed);
					bt.obtainBundle().put(values.getString(n), eds);

				}
			} else if (kind.equals("4") || kind.equals("TYPE"))
				data += value;
			else if (kind.equals("5") || kind.equals("URI"))
				data += value;
			else if (kind.equals("6") || kind.equals("SCHEME"))
				data += value;
			else if (kind.equals("9") || kind.equals("HOST"))
				data += value;
			else if (kind.equals("10") || kind.equals("PATH"))
				data += value;
			else if (kind.equals("11") || kind.equals("PORT"))
				data += value;
			if (data.toString().length() > 0) {
				intentSummary.getSetDataValueList().add(data);
			}
		}
		if (hasDes)
			return;
		List<String> resSet = analyzeDesinationByACDT(src, intentSummary);
		for (String des : resSet) {
			// des = des.split("\\.")[des.split("\\.").length-1];
			AtgEdge edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method, instructionId, iCCkind);
			model.getIC3AtgModel().addAtgEdges(src, edge);
			addToSummaryMap(src, method, intentSummary);
		}

	}

	private void addToSummaryMap(String src, String method, IntentSummaryModel intentSummary) {
		if (SootUtils.getSootMethodBySignature(method) != null) {
			MethodSummaryModel methodSummary = summaryMap.get(method);
			if (!summaryMap.containsKey(method)) {
				methodSummary = new MethodSummaryModel(SootUtils.getNameofClass(src), Scene.v().getMethod(method));
				summaryMap.put(method, methodSummary);
			}
			methodSummary.getSingleObjects().add(intentSummary);
		}

	}

	private List<String> analyzeDesinationByACDT(String src, IntentSummaryModel intentSummary) {
		List<String> summaryActionSet = intentSummary.getSetActionValueList();
		List<String> summaryCateSet = intentSummary.getSetCategoryValueList();
		List<String> summaryDataSet = intentSummary.getSetDataValueList();
		List<String> resSet = new ArrayList<String>();
		
		for (ComponentModel component : IC3ComponentMap.values()) {
			for (IntentFilterModel filter : component.getIntentFilters()) {
				Set<String> filterActionSet = filter.getAction_list();
				Set<String> filterCateSet = filter.getCategory_list();
				Set<Data> filterDataSet = filter.getData_list();
				if (filterActionSet.size() == 0 && filterCateSet.size() == 0)
					continue;

				boolean actionTarget = false, cateTarget = true, dataTarget = false;
				// if a action is find same with one action in filer, matched
				// usually, only one action in summary
				for (String action : summaryActionSet) {
					if (filterActionSet.contains(action))
						actionTarget = true;
				}
				if(!filterActionSet.isEmpty() && summaryActionSet.isEmpty())
					actionTarget = true;
				/**
				 * android will add android.intent.category.DEFAULT to all
				 * implicit Activity ICC.
				 * https://developer.android.com/guide/components
				 * /intents-filters.html
				 **/
				boolean addDefault = false;
				if (component instanceof ActivityModel) {
					if (!summaryCateSet.contains("android.intent.category.DEFAULT")){
						summaryCateSet.add("android.intent.category.DEFAULT");
						addDefault = true;
					}
					if (!filterCateSet.contains("android.intent.category.DEFAULT"))
						cateTarget = false;
				}
				// all the category in a summary must find a match one in filter
				for (String category : summaryCateSet  ) {
					if (!filterCateSet.contains(category))
						cateTarget = false;
				}
				if(addDefault)
					summaryCateSet.remove("android.intent.category.DEFAULT");
				if (filterDataSet.size() == 0)
					dataTarget = true;
				else {
					for (String data : summaryDataSet) {
						if (dataTarget == true)
							break;
						for (Data ifData : filterDataSet) {
							boolean ifMatch = true;
							if (ifData.getHost().length() > 0 && !data.toString().contains(ifData.getHost()))
								ifMatch = false;
							else if (ifData.getPath().length() > 0 && !data.toString().contains(ifData.getPath()))
								ifMatch = false;
							else if (ifData.getPort().length() > 0 && !data.toString().contains(ifData.getPort()))
								ifMatch = false;
							else if (ifData.getScheme().length() > 0 && !data.toString().contains(ifData.getScheme()))
								ifMatch = false;
							else if (ifData.getMime_type().length() > 0
									&& !data.toString().contains(ifData.getMime_type()))
								ifMatch = false;
							if (ifMatch == true) {
								dataTarget = true;
								break;
							}
						}
					}
				}
				if ((actionTarget && cateTarget && dataTarget)) {
					if (component.getComponentType().equals(intentSummary.getTargetType())) {
						resSet.add(component.getComponetName());
					}
				}
			}
		}
		return resSet;
	}

}
