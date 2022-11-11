package main.java.client.obj.target.ctg;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.component.IntentFilterModel;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.statistic.model.DoStatistic;
import main.java.client.statistic.model.StatisticResult;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class CTGAnalyzer extends ObjectAnalyzer {

	public CTGAnalyzer(List<SootMethod> topoQueue, StatisticResult result) {
		super(topoQueue, new CTGAnalyzerHelper(), result);
	}

	@Override
	public void assignForObjectName() {
		this.objectName = "main.java.client.obj.model.ctg.IntentSummaryModel";
	}

	/**
	 * getSingleClassAnalyze
	 */
	@Override
	public void getSingleComponent(MethodSummaryModel methodSummary) {
		ComponentModel model = appModel.getComponentMap().get(
				SootUtils.getNameofClass(methodUnderAnalysis.getDeclaringClass().getName()));
		if (model == null)
			return;
		for (ObjectSummaryModel singleObject : methodSummary.getSingleObjects()) {
			IntentSummaryModel intentSummary = (IntentSummaryModel) singleObject;
			model.getReceiveModel().getReceivedActionSet().addAll(intentSummary.getGetActionCandidateList());
			model.getReceiveModel().getReceivedCategorySet().addAll(intentSummary.getGetCategoryCandidateList());
			model.getReceiveModel().getReceivedDataSet().addAll(intentSummary.getGetDataCandidateList());
			model.getReceiveModel().getReceivedTypeSet().addAll(intentSummary.getGetTypeCandidateList());
			for (Entry<String, List<ExtraData>> entry : intentSummary.getGetExtrasCandidateList().obtainBundle().entrySet())
				model.getReceiveModel().getReceivedExtraData().obtainBundle().put(entry.getKey(), entry.getValue());

			model.getSendModel().getSendActionSet().addAll(intentSummary.getSetActionValueList());
			model.getSendModel().getSendCategorySet().addAll(intentSummary.getSetCategoryValueList());
			model.getSendModel().getSendDataSet().addAll(intentSummary.getSetDataValueList());
			model.getSendModel().getSendTypeSet().addAll(intentSummary.getSetTypeValueList());
			model.getSendModel().getSendFlagSet().addAll(intentSummary.getSetFlagsList());
			for (Entry<String, List<ExtraData>> entry : intentSummary.getSetExtrasValueList().obtainBundle().entrySet())
				model.getSendModel().getSendExtraData().obtainBundle().put(entry.getKey(), entry.getValue());

		}
	}

	/**
	 * analyzeCurrentSetMethods for a set of method
	 */
	@Override
	public void drawATGandStatistic(MethodSummaryModel model) {
		if (model == null)
			return;
		generateATGInfo(model);
		makeStatistic(model);
	}

	/**
	 * generateATGInfo
	 * 
	 * @param model
	 */
	private void generateATGInfo(MethodSummaryModel methodSummary) {
		SootMethod sootMtd = methodSummary.getMethod();
		if (MyConfig.getInstance().getMySwithch().isImplicitLaunchSwitch()) {
			implicitDestinationAnalyze(methodSummary);
		}
		SootClass cls = null;
		if (appModel.getEntryMethod2Component().containsKey(sootMtd))
			cls = appModel.getEntryMethod2Component().get(sootMtd);
		// trick method
		if (cls == null) {
			String currentClsName = SootUtils.getNameofClass(sootMtd.getDeclaringClass());
			if (appModel.getComponentMap().containsKey(currentClsName))
				cls = sootMtd.getDeclaringClass();
			else
				return;
		}
		List<SootClass> subClasses = new ArrayList<SootClass>();
		boolean isEntry = appModel.getEntryMethods().contains(sootMtd);// ||
																		// sootMtd.getName().startsWith(ConstantUtils.ENTRYID
		if (MyConfig.getInstance().getMySwithch().isDummyMainSwitch() && isEntry == false)
			return;
		if (MyConfig.getInstance().getMySwithch().isPolymSwitch()) {
			try {
				subClasses = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(cls);
			} catch (Exception e) {
				subClasses.add(cls);
			}
		} else {
			subClasses.add(cls);
		}
		for (SootClass sootCls : subClasses) {
			if (sootCls.getMethodUnsafe(sootMtd.getSubSignature()) == null || sootCls == sootMtd.getDeclaringClass()) {
				String src = sootCls.getName();
				for (ObjectSummaryModel intentSummary : methodSummary.getSingleObjects()) {
					if (((IntentSummaryModel) intentSummary).getSendIntent2ICCList().size() == 0)
						continue;
					getTargetOfSrc((IntentSummaryModel) intentSummary, src);
				}
			}
		}
	}

	/**
	 * getTargetOfSrc
	 * 
	 * @param intentSummary
	 * @param src
	 */
	private void getTargetOfSrc(IntentSummaryModel intentSummary, String src) {
		SootMethod method = intentSummary.getMethod();
		Unit unit = intentSummary.getSendIntent2ICCList().iterator().next();
		int instructionId = SootUtils.getIdForUnit(unit, method);
		for (String des : intentSummary.getSetDestinationList()) {
			ComponentModel comp = appModel.getComponentMap().get(des);
			AtgEdge edge;
			if (comp != null && SootUtils.getUnitListFromMethod(methodUnderAnalysis).contains(unit)){
				edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), instructionId,
						comp.getComponentType());
				edge.setIntentSummary(intentSummary);
				Global.v().getiCTGModel().getOptModel().addAtgEdges(src, edge);
			}else {
				edge = new AtgEdge(new AtgNode(src), new AtgNode(des), method.getSignature(), -1, "c");
				edge.setIntentSummary(intentSummary);
				Global.v().getiCTGModel().getOptModel().addAtgEdges(src, edge);
			}
			
			

			String name = SootUtils.getNameofClass(src);
			ComponentModel sourceComponent = Global.v().getAppModel().getComponentMap().get(name);
			if (sourceComponent != null) {
				sourceComponent.getSendModel().getIccTargetSet().add(des);
			}
		}
	}

	/**
	 * implicitDestinationAnalyze
	 * 
	 * @param intentSummary
	 */
	private void implicitDestinationAnalyze(MethodSummaryModel methodSummary) {
		for (ObjectSummaryModel singleObject : methodSummary.getSingleObjects()) {
			IntentSummaryModel intentSummary = (IntentSummaryModel) singleObject;

			if (intentSummary.getSendIntent2ICCList().size() == 0)
				continue;
			if (intentSummary.getSetDestinationList().size() > 0)
				continue;
			List<String> actionSet = intentSummary.getSetActionValueList();
			List<String> cateSet = intentSummary.getSetCategoryValueList();
			List<String> dataSet = intentSummary.getSetDataValueList();
			List<String> typeSet = intentSummary.getSetTypeValueList();
			if (actionSet.size() + cateSet.size() + dataSet.size() + typeSet.size() > 0) {
				analyzeDesinationByACDT(intentSummary);
				/** add destination match **/
			}
		}
	}

	/**
	 * for implicit ICC destination match ICC-intent filter match rule
	 * 
	 * @param intentSummary
	 * @param acdtSet
	 * @param set
	 */
	private void analyzeDesinationByACDT(IntentSummaryModel intentSummary) {
		List<String> summaryActionSet = intentSummary.getSetActionValueList();
		List<String> summaryCateSet = intentSummary.getSetCategoryValueList();
		List<String> summaryDataSet = intentSummary.getSetDataValueList();
		List<String> summaryTypeSet = intentSummary.getSetTypeValueList();
		List<String> resSet = intentSummary.getSetDestinationList();
		boolean findTarget = false;
		for (ComponentModel component : appModel.getComponentMap().values()) {
			for (IntentFilterModel filter : component.getIntentFilters()) {
				Set<String> filterActionSet = filter.getAction_list();
				Set<String> filterCateSet = filter.getCategory_list();
				Set<Data> filterDataSet = filter.getData_list();
				Set<String> filterTypeSet = filter.getDatatype_list();
				if (filterActionSet.size() == 0 && filterCateSet.size() == 0)
					continue;

				boolean actionTarget = false, cateTarget = true, dataTarget = false, typeTarget = true;
				// if a action is find same with one action in filer, matched
				// usually, only one action in summary
				//如果该过滤器未列出任何操作，则 Intent 没有任何匹配项，因此所有 Intent 均无法通过测试。但是，如果 Intent 未指定操作，则只要过滤器内包含至少一项操作，就可以通过测试。
				for (String action : filterActionSet ) {
					if (summaryActionSet.contains(action))
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
				for (String type : filterTypeSet) {
					if (!summaryTypeSet.contains(type))
						typeTarget = false;
				}
				boolean flag1 = actionTarget && cateTarget && dataTarget && typeTarget;
				if (flag1) {
					if (component.getComponentType().contains(intentSummary.getTargetType())) {
						findTarget = true;
						resSet.add(component.getComponetName());
					}
				}
			}
		}
		if (!findTarget) {
			String res = "interICC_" + PrintUtils.printList(summaryActionSet) + ", "
					+ PrintUtils.printList(summaryCateSet);
			res = res.substring(0, res.length());
			resSet.add(res);
		}
	}

	/**
	 * makeStatistic
	 * 
	 * @param model
	 */
	private void makeStatistic(MethodSummaryModel model) {
		// computeTraceDepth();
//		DoStatistic.updateTraceStatisticUseSummayMap(false, model, result);
//		DoStatistic.updateTraceStatisticUseSummayMap(true, model, result);

		DoStatistic.updateICCStatisticUseSummayMap(false, model, result);
		DoStatistic.updateICCStatisticUseSummayMap(true, model, result);

		DoStatistic.updateSummaryStatisticUseSummayMap(model, result);
		DoStatistic.updateXMLStatisticUseSummayMap(true, model, result);
		DoStatistic.updateXMLStatisticUseSummayMap(false, model, result);

	}

	/**
	 * computeTraceDepth
	 * 
	 */
	public void computeTraceDepth() {
		for (Entry<String, MethodSummaryModel> en : currentSummaryMap.entrySet()) {
			MethodSummaryModel intentSummary = en.getValue();
			computeTraceDepthForOne(intentSummary, intentSummary, 1);
		}
	}

	/**
	 * used in computeTraceDepth
	 * 
	 * @param currentSummary
	 * @param topSummary
	 * @param i
	 */
	private void computeTraceDepthForOne(MethodSummaryModel currentSummary, MethodSummaryModel topSummary, int i) {
		List<UnitNode> list = currentSummary.getNodePathList();
		for (UnitNode n : list) {
			if (n.getInterFunNode() != null) {
				if (topSummary.getMaxMethodTraceDepth() < i)
					topSummary.setMaxMethodTraceDepth(i);
				computeTraceDepthForOne(n.getInterFunNode(), topSummary, i + 1);
			}
		}
	}

}
