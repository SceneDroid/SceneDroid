package main.java.client.statistic.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.analyzeModel.PathSummaryModel;
import main.java.analyze.model.analyzeModel.UnitNode;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.ctg.IntentRecieveModel;
import main.java.client.obj.model.ctg.IntentSummaryFeatureExtractor;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.obj.model.fragment.FragmentSummaryModel;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class DoStatistic {

	public static void updateXMLStatisticUseSummayMapForFragment(boolean entryMethod, MethodSummaryModel methodSummary,
			StatisticResult result) {
		XmlStatistic statistic = result.getXmlStatistic();
		if (entryMethod) {
			SootMethod sm = methodSummary.getMethod();
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (Global.v().getAppModel().getEntryMethod2Component().containsKey(sm))
				flag = true;
			if (!flag)
				return;
		}
		if (methodSummary != null) {
			writeForMethodSummary(methodSummary, entryMethod, statistic);
			writeForPathSummary(methodSummary, entryMethod, statistic);
			writeForFragmentSummary(methodSummary, entryMethod, statistic);
		}
	}

	public static void updateXMLStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel methodSummary,
			StatisticResult result) {
		XmlStatistic statistic = result.getXmlStatistic();

		if (entryMethod) {
			SootMethod sm = methodSummary.getMethod();
			SootClass sc = sm.getDeclaringClass();
			String source = SootUtils.getNameofClass(sc.getName());
			boolean flag = Global.v().getAppModel().getComponentMap().containsKey(source);
			if (Global.v().getAppModel().getEntryMethod2Component().containsKey(sm))
				flag = true;
			if (!flag)
				return;
		}
		if (methodSummary != null) {
			writeForMethodSummary(methodSummary, entryMethod, statistic);
			writeForPathSummary(methodSummary, entryMethod, statistic);
			writeForIntentSummary(methodSummary, entryMethod, statistic);
		}
	}

	/**
	 * writeForIntentSummary
	 * 
	 * @param methodSummary
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForIntentSummary(MethodSummaryModel methodSummary, boolean entryMethod, XmlStatistic statistic) {
		Element intentSummaryEle = new DefaultElement("Component");
		String sourceStr = SootUtils.getNameofClass(methodSummary.getComponentName());
		intentSummaryEle.addAttribute("source", sourceStr);
		Set<IntentSummaryModel> history = new HashSet<IntentSummaryModel>();
		for (ObjectSummaryModel singleObject : methodSummary.getSingleObjects()) {
			IntentSummaryModel intentSummary = (IntentSummaryModel) singleObject;
			if (history.contains(intentSummary))
				continue;
//			if (intentSummary.getSendIntent2ICCList().size() == 0 && intentSummary.getSetDestinationList().size() == 0)
//				continue;
			history.add(intentSummary);
			writeIntentSummary(intentSummary, intentSummaryEle, singleObject.getPathSummary(), methodSummary);
		}
		if (intentSummaryEle.hasContent()) {
			if (entryMethod)
				statistic.addEntryIntentSummaryEleList(intentSummaryEle);
			else
				statistic.addAllIntentSummaryEleList(intentSummaryEle);
		}
	}

	/**
	 * writeForIntentSummary
	 * 
	 * @param methodSummary
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForFragmentSummary(MethodSummaryModel methodSummary, boolean entryMethod,
			XmlStatistic statistic) {
		Element intentSummaryEle = new DefaultElement("Component");
		String sourceStr = methodSummary.getMethod().getDeclaringClass().getName();
		intentSummaryEle.addAttribute("source", sourceStr);
		Set<ObjectSummaryModel> history = new HashSet<ObjectSummaryModel>();
		for (ObjectSummaryModel singleObject : methodSummary.getSingleObjects()) {
			FragmentSummaryModel Singlefrag = (FragmentSummaryModel) singleObject;
			if (history.contains(Singlefrag))
				continue;
//			if (Singlefrag.getSendFragment2Start().size() == 0)
//				continue;
			history.add(Singlefrag);
			writeFragmentSummary(Singlefrag, intentSummaryEle, singleObject.getPathSummary(), methodSummary);
		}
		if (intentSummaryEle.hasContent()) {
			if (entryMethod)
				statistic.addEntryIntentSummaryEleList(intentSummaryEle);
			else
				statistic.addAllIntentSummaryEleList(intentSummaryEle);
		}
	}

	/**
	 * writeForPathSummary
	 * 
	 * @param methodSummary
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForPathSummary(MethodSummaryModel methodSummary, boolean entryMethod, XmlStatistic statistic) {
		if (methodSummary.getPathSet().size() == 0)
			return;

		Element pathSummaryEle = new DefaultElement("methodSummary");
		pathSummaryEle.addAttribute("source", methodSummary.getMethod().getSignature());

		for (PathSummaryModel pathSummary : methodSummary.getPathSet()) {
			writePathSummary(pathSummary, pathSummaryEle);
		}

		if (pathSummaryEle.element("pathSummary") != null) {
			if (entryMethod)
				statistic.addEntryPathSummaryEleList(pathSummaryEle);
			else
				statistic.addAllPathSummaryEleList(pathSummaryEle);
		}
	}

	/**
	 * writeForMethodSummary
	 * 
	 * @param methodSummary
	 * @param entryMethod
	 * @param statistic
	 */
	private static void writeForMethodSummary(MethodSummaryModel methodSummary, boolean entryMethod, XmlStatistic statistic) {
		Element methodSummaryEle = new DefaultElement("methodSummary");
		methodSummaryEle.addAttribute("source", methodSummary.getMethod().getSignature());

		writeMethodSummary(methodSummaryEle, methodSummary);

		if (methodSummaryEle.element("node") != null) {
			if (entryMethod) {
				statistic.addEntryMethodSummaryEleList(methodSummaryEle);
			} else {
				statistic.addAllMethodSummaryEleList(methodSummaryEle);
			}
		}

	}

	/**
	 * write Single Node Xml in IntentSummaryModel
	 * 
	 * @param stack
	 * @param path
	 * @param methodSummary
	 * @param topSummary
	 * @param i
	 */
	private static void writeMethodSummary(Element path, MethodSummaryModel methodSummary) {
		List<UnitNode> list = methodSummary.getNodePathList();
		for (UnitNode n : list) {
			if (n.getUnit() == null)
				continue;
			Element node = path.addElement("node");
			if (n.getType().length() > 0)
				node.addAttribute("type", n.getType());
			node.addAttribute("unit", n.getUnit().toString());
			if (n.getCondition() != null) {
				Element condInfo = node.addElement("condInfo");
				condInfo.addAttribute("value", n.getCondition().toString());
			}
		}
	}

	/**
	 * writePathSummary
	 * 
	 * @param pathSummary
	 * @param summary
	 */
	private static void writePathSummary(PathSummaryModel pathSummary, Element summary) {
		if (pathSummary.getNodes().size() == 0)
			return;
		Element icc = summary.addElement("pathSummary");
		writeMethod(icc, null,pathSummary, null);
		writePathSummaryICCNode(new ArrayList<String>(), pathSummary, icc);
	}

	/**
	 * writePathSummary
	 * 
	 * @param intentSummary
	 * @param summary
	 * @param pathSummary
	 */
	private static void writeIntentSummary(IntentSummaryModel intentSummary, Element summary, PathSummaryModel pathSummary,
			MethodSummaryModel methodSummary) {
		Element icc = new DefaultElement("intentSummary");
		// writeICCType(intentSummary, icc);
		writeMethod(icc, intentSummary, pathSummary, methodSummary);
		writeSource(intentSummary, icc, methodSummary);
		writeDestnition(intentSummary, icc);
		writeICCSendReceive(intentSummary, icc);
//		writeICCFlow(intentSummary, icc);
		writeSingleObjectICCNode(new ArrayList<String>(), intentSummary, icc);
//		if (icc.element("destinition") != null)
		if (icc.element("source") != null)
			summary.add(icc);
	}

	private static void writeFragmentSummary(FragmentSummaryModel singlefrag, Element intentSummaryEle,
			PathSummaryModel pathSummary, MethodSummaryModel methodSummary) {
		if (singlefrag.getSendFragment2Start().size() == 0)
			return;
		Element frag = new DefaultElement("FragmentSummary");
		writeMethod(frag, singlefrag, pathSummary,methodSummary);
		writeSource(singlefrag, frag, methodSummary);
		writeDestnition(singlefrag, frag);
//	    writeFragmentFlow(singlefrag, frag);
		writeSingleObjectICCNode(new ArrayList<String>(), singlefrag, frag);
//		if (frag.element("destinition") != null)
		intentSummaryEle.add(frag);

	}


	private static void writeMethod(Element icc, ObjectSummaryModel singleObject, PathSummaryModel pathSummary, MethodSummaryModel methodSummary) {
		Element method = icc.addElement("method");
		if (methodSummary != null){
			method.addAttribute("value",methodSummary.getMethod().getSignature());
		}
		Element methodtrace = icc.addElement("methodtrace");
		String methodTraceStr = "";
		if(singleObject!=null){
			for (SootMethod reused : singleObject.getReusedMthCallStack()) {
				methodTraceStr += reused.getSignature()+",";
			}
		}
		methodtrace.addAttribute("value",methodTraceStr + PrintUtils.printList(pathSummary.getMethodTrace()));
	}
	
	
	private static void writeFragmentFlow(main.java.client.obj.model.fragment.FragmentSummaryModel singlefrag,
			Element frag) {
		Element flow = new DefaultElement("flow");
		List<Unit> getCreateList = singlefrag.getCreateList();
		for (Unit u : getCreateList) {
			Element createList = flow.addElement("createList");
			createList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromParaList = singlefrag.getReceiveFromParaList();
		for (Unit u : getReceiveFromParaList) {
			Element receiveFromParaList = flow.addElement("receiveFromParaList");
			receiveFromParaList.addAttribute("value", u.toString());
		}
		List<Unit> getFragmentFromOutList = singlefrag.getGetFragmentFromOut();
		for (Unit u : getFragmentFromOutList) {
			Element fragmentFromOutList = flow.addElement("fragmentFromOutList");
			fragmentFromOutList.addAttribute("value", u.toString());
		}
		List<Unit> getDataHandleList = singlefrag.getDataHandleList();
		for (Unit u : getDataHandleList) {
			Element dataHandleList = flow.addElement("dataHandleList");
			dataHandleList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2FunList = singlefrag.getSendIntent2FunList();
		for (Unit u : getSendIntent2FunList) {
			Element sendIntent2FunList = flow.addElement("sendIntent2FunList");
			sendIntent2FunList.addAttribute("value", u.toString());
		}
		List<Unit> getSendFragment2Start = singlefrag.getSendFragment2Start();
		for (Unit u : getSendFragment2Start) {
			Element sendFragment2Start = flow.addElement("sendFragment2Start");
			sendFragment2Start.addAttribute("value", u.toString());
		}

		if (flow.hasContent())
			frag.add(flow);

	}

	private static void writeICCFlow(IntentSummaryModel intentSummary, Element icc) {
		Element flow = new DefaultElement("flow");
		List<Unit> getCreateList = intentSummary.getCreateList();
		for (Unit u : getCreateList) {
			Element createList = flow.addElement("createList");
			createList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromParaList = intentSummary.getReceiveFromParaList();
		for (Unit u : getReceiveFromParaList) {
			Element receiveFromParaList = flow.addElement("receiveFromParaList");
			receiveFromParaList.addAttribute("value", u.toString());
		}
		List<Unit> getReceiveFromOutList = intentSummary.getReceiveFromOutList();
		for (Unit u : getReceiveFromOutList) {
			Element receiveFromOutList = flow.addElement("receiveFromOutList");
			receiveFromOutList.addAttribute("value", u.toString());
		}
		List<Unit> getDataHandleList = intentSummary.getDataHandleList();
		for (Unit u : getDataHandleList) {
			Element dataHandleList = flow.addElement("dataHandleList");
			dataHandleList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2ICCList = intentSummary.getSendIntent2ICCList();
		for (Unit u : getSendIntent2ICCList) {
			Element sendIntent2ICCList = flow.addElement("sendIntent2ICCList");
			sendIntent2ICCList.addAttribute("value", u.toString());
		}
		List<Unit> getSendIntent2FunList = intentSummary.getSendIntent2FunList();
		for (Unit u : getSendIntent2FunList) {
			Element sendIntent2FunList = flow.addElement("sendIntent2FunList");
			sendIntent2FunList.addAttribute("value", u.toString());
		}
		if (flow.hasContent())
			icc.add(flow);
	}

	private static void writePathSummaryICCNode(List<String> context, PathSummaryModel pathSummary, Element icc) {
		List<UnitNode> nodeList = pathSummary.getNodes();
		// how to print node with its context??/
		int nodeId = 0;
		for (UnitNode node : nodeList) {
			Element ele = icc.addElement("node");
			if (node.getType().length() > 0)
				ele.addAttribute("type", node.getType());
			ele.addAttribute("method", node.getMethod().getDeclaringClass().getShortName() + " "
					+ node.getMethod().getName());
			ele.addAttribute("unit", node.getUnit().toString());

			String predsStr = "";
			if (node.getPreds().size() > 0) {
				for (UnitNode predNode : node.getPreds()) {
					predsStr += predNode.hashCode() + ", ";
				}
				predsStr = predsStr.substring(0, predsStr.length() - 2);
			}
			String succsStr = "";
			if (node.getSuccs().size() > 0) {
				for (UnitNode succNode : node.getSuccs()) {
					succsStr += succNode.hashCode() + ", ";
				}
				succsStr = succsStr.substring(0, succsStr.length() - 2);
			}
			Element basic = ele.addElement("nodeInfo");
			basic.addAttribute("nodeId", node.hashCode() + "");
			basic.addAttribute("predsStr", predsStr);
			basic.addAttribute("succsStr", succsStr);

			context = pathSummary.getNode2TraceMap().get(nodeId++);
			basic.addAttribute("context", PrintUtils.printList(context));
			if (node.getBaseNodePointToMap().containsKey(context) && node.getBaseNodePointedTo(context) != null) {
				Element unitPointTo = ele.addElement("baseNodePointTo");
				unitPointTo.addAttribute("value", node.getBaseNodePointedTo(context).hashCode() + "");
				unitPointTo.addAttribute("context", PrintUtils.printList(context) + "");
			}
			if (node.getNodeSetPointToMeMap().containsKey(context) && node.getNodeSetPointToMe(context) != null) {
				Element unitPointTo = ele.addElement("nodeSetPointToMe");
				String res = "";
				for (UnitNode tempNode : node.getNodeSetPointToMe(context)) {
					res += tempNode.hashCode() + ",";
				}
				unitPointTo.addAttribute("value", res);
				unitPointTo.addAttribute("context", PrintUtils.printList(context) + "");
			}
		}

	}

	private static void writeSingleObjectICCNode(List<String> context, ObjectSummaryModel singleObject, Element icc) {
		Element nodes = new DefaultElement("nodes");
		
		for (SootMethod method : singleObject.getReusedMthCallStack()) {
			Element ele = nodes.addElement("node");
			ele.addAttribute("method", method.getSignature());
		}
		for (UnitNode node : singleObject.getNodes()) {
			Element ele = nodes.addElement("node");
			ele.addAttribute("method", node.getMethod().getDeclaringClass().getShortName() + " "
					+ node.getMethod().getName());
			if (node.getType().length() > 0)
				ele.addAttribute("type", node.getType());
			ele.addAttribute("unit", node.getUnit().toString());

			String predsStr = "";
			if (node.getPreds().size() > 0) {
				for (UnitNode predNode : node.getPreds()) {
					predsStr += predNode.hashCode() + ", ";
				}
				predsStr = predsStr.substring(0, predsStr.length() - 2);
			}
			String succsStr = "";
			if (node.getSuccs().size() > 0) {
				for (UnitNode succNode : node.getSuccs()) {
					succsStr += succNode.hashCode() + ", ";
				}
				succsStr = succsStr.substring(0, succsStr.length() - 2);
			}
		}
		if (nodes.hasContent())
			icc.add(nodes);

	}

	private static void writeICCSendReceive(IntentSummaryModel intentSummary, Element icc) {
		List<String> actions = intentSummary.getSetActionValueList();
		List<String> category = intentSummary.getSetCategoryValueList();
		List<String> data = intentSummary.getSetDataValueList();
		List<String> type = intentSummary.getSetTypeValueList();
		BundleType extras = intentSummary.getSetExtrasValueList();
		List<String> flags = intentSummary.getSetFlagsList();
		boolean finish = intentSummary.isFinishFlag();
		if(actions.size()+category.size()+data.size()+type.size()+extras.getExtraDatas().size()>0){
			//ICTG construct
			if(MyConfig.getInstance().getMySwithch().isSetAttributeStrategy()){
				Element sender = new DefaultElement("sender");
				if (actions.size() > 0)
					sender.addAttribute("action", PrintUtils.printList(actions));
				if (category.size() > 0)
					sender.addAttribute("category", PrintUtils.printList(category));
				if (data.size() > 0)
					sender.addAttribute("data", PrintUtils.printList(data));
				if (type.size() > 0)
					sender.addAttribute("type", PrintUtils.printList(type));
				if(extras.getExtraDatas().size()>0 )
					sender.addAttribute("extras", extras.toString());
				if (flags.size() > 0)
					sender.addAttribute("flags", PrintUtils.printList(flags));
				if (finish)
					sender.addAttribute("componentFinish", "true");
				if (sender.attributeCount() > 0)
					icc.add(sender);
				if(icc.element("destinition")!=null){
					Attribute attr = icc.element("destinition").attribute("name");
					if(attr != null&& Global.v().getAppModel().getComponentMap().containsKey(attr.getValue())){
						ComponentModel component = Global.v().getAppModel().getComponentMap().get(attr.getValue());
						IntentRecieveModel receiveModel = component.getReceiveModel();
						receiveModel.getIntentObjsbyICCMsg().add(intentSummary);
					}
				}
			}
		}
		//Receive model construct
		if(MyConfig.getInstance().getMySwithch().isGetAttributeStrategy()){
			 List<String> actions2 = intentSummary.getGetActionCandidateList();
			 List<String> category2 = intentSummary.getGetCategoryCandidateList();
			 List<String> data2 = intentSummary.getGetDataCandidateList();
			 List<String> type2 = intentSummary.getGetTypeCandidateList();
			 BundleType extras2 = intentSummary.getGetExtrasCandidateList();
			 if(actions2.size()+category2.size()+data2.size()+type2.size()+extras2.getExtraDatas().size()>0){
				 Element receiver = new DefaultElement("receiver");
				 if (actions2.size() > 0)
					 receiver.addAttribute("action", PrintUtils.printList(actions2));
				 if (category2.size() > 0)
					 receiver.addAttribute("category", PrintUtils.printList(category2));
				 if (data2.size() > 0)
					 receiver.addAttribute("data", PrintUtils.printList(data2));
				 if (type2.size() > 0)
					 receiver.addAttribute("type", PrintUtils.printList(type2));
				 if(extras2.getExtraDatas().size()>0)
					 receiver.addAttribute("extras", extras2.toString());
				 if(receiver.attributeCount()>0)
					 icc.add(receiver);
				 Attribute attr = icc.element("source").attribute("name");
				 if(attr != null && Global.v().getAppModel().getComponentMap().containsKey(attr.getValue())){
					ComponentModel component = Global.v().getAppModel().getComponentMap().get(attr.getValue());
					IntentRecieveModel receiveModel = component.getReceiveModel();
					receiveModel.getIntentObjsbySpec().add(intentSummary);
				}
			 }
		}
	}

	private static void writeSource(ObjectSummaryModel singleObj, Element icc, MethodSummaryModel methodSummary) {
		Element desElement = icc.addElement("source");
		String source = SootUtils.getNameofClass(methodSummary.getComponentName());
		if(source!=null && source.length()>0)
			desElement.addAttribute("name", source);
	}

	private static void writeDestnition(ObjectSummaryModel singleObj, Element icc) {
		List<String> des = null;
		if (singleObj instanceof IntentSummaryModel)
			des = ((IntentSummaryModel) singleObj).getSetDestinationList();
		if (singleObj instanceof FragmentSummaryModel)
			des = ((FragmentSummaryModel) singleObj).getSetDestinationList();
		if (des.size() > 0) {
			Element desElement = icc.addElement("destinition");
			desElement.addAttribute("name", PrintUtils.printList(des));
		}
	}

	private static void writeICCType(IntentSummaryModel intentSummary, Element icc) {
		Element typeElement = new DefaultElement("summaryType");
		String summaryType = IntentSummaryFeatureExtractor.getSummaryStr(intentSummary);
		if (summaryType.length() > 0)
			typeElement.addAttribute("summaryType", summaryType);
		String summaryReceiveType = IntentSummaryFeatureExtractor.getReceiveStr(intentSummary);
		if (summaryReceiveType.length() > 0)
			typeElement.addAttribute("receiveType", summaryReceiveType);
		String summaryNewType = IntentSummaryFeatureExtractor.getNewStr(intentSummary);
		if (summaryNewType.length() > 0)
			typeElement.addAttribute("newType", summaryNewType);
		String summaryUsedType = IntentSummaryFeatureExtractor.getUseAttributeStr(intentSummary);
		if (summaryUsedType.length() > 0)
			typeElement.addAttribute("usedType", summaryUsedType);
		String summarySetType = IntentSummaryFeatureExtractor.getSetAttributeStr(intentSummary);
		if (summarySetType.length() > 0)
			typeElement.addAttribute("setType", summarySetType);
		String summarySendType = IntentSummaryFeatureExtractor.getSendStr(intentSummary);
		if (summarySendType.length() > 0)
			typeElement.addAttribute("sendType", summarySendType);
		if (typeElement.attributeCount() > 0)
			icc.add(typeElement);

	}

	/**
	 * countICCNumber
	 * 
	 * @param AppModel
	 *            .getInstance()
	 * @param entryMethod
	 *            only count entry method
	 */
	public static int countICCRelatedMethodNumber(boolean entryMethod, Map<String, MethodSummaryModel> summaryMap) {
		int sum = 0;
		for (Entry<String, MethodSummaryModel> en : summaryMap.entrySet()) {
			if (entryMethod) {
				SootMethod sm = SootUtils.getSootMethodBySignature(en.getKey());
				boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
				if (flag == false)
					continue;
			}
			sum++;
		}
		return sum;
	}

	/**
	 * updateSummaryStatisticUseSummayMap
	 * 
	 * @param summaryMap
	 * @param result
	 * @return
	 */
	public static void updateSummaryStatisticUseSummayMap(MethodSummaryModel model, StatisticResult result) {
		SummaryStatistic statistic = result.getSummaryStatistic();
		statistic.getSummariedMethods().add(model.getMethod().getSignature());
		if (isICCRelatedEntryMethod(model))
			statistic.getSummariedEntryMethods().add(model.getMethod().getSignature());
		for (String me : statistic.getSummariedEntryMethods()) {
			if (SootUtils.isLifeCycleMethods(me)) {
				statistic.addSummariedEntryLifeCycleMethods(me);
			} else {
				statistic.addSummariedEntryListenerMethods(me);
			}
		}
	}

	/**
	 * updateTraceStatisticUseSummayMap
	 * 
	 * @param entryMethod
	 * @param summaryMap
	 * @param result
	 */
	public static void updateTraceStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel intentSummary,
			StatisticResult result) {
		TraceStatistic statistic;
		if (entryMethod) {
			statistic = result.getEntryTraceStatistic();
		} else {
			statistic = result.getAllTraceStatistic();
		}

		int traceNum = 0, traceNumSum = 0;
		int traceDepth = 0;
		if (entryMethod) {
			SootMethod sm = intentSummary.getMethod();
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (flag == false)
				return;
		}
		traceDepth += intentSummary.getMaxMethodTraceDepth();
		for (PathSummaryModel si : intentSummary.getPathSet()) {
			traceNum += si.getMethodTrace().size() - 1;
			traceNumSum++;
		}
	}

	/**
	 * updateICCStatisticUseSummayMap add entry and non-entry node information
	 * to statistic xml file
	 * 
	 * @param entryMethod
	 * @param summaryMap
	 * @param result
	 */
	public static void updateICCStatisticUseSummayMap(boolean entryMethod, MethodSummaryModel methodSummary,
			StatisticResult result) {
		ICCStatistic statistic;
		if (entryMethod) {
			statistic = result.getEntryICCStatistic();
		} else {
			statistic = result.getAllICCStatistic();
		}

		// int entryNumber = countICCRelatedMethodNumber(entryMethod,
		// summaryMap);
		// statistic.addICCFlowNum(entryNumber);

		SootMethod sm = methodSummary.getMethod();
		if (entryMethod) {
			boolean flag = Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
			if (flag == false)
				return;
		}
		if (!statistic.getDestinationMap().containsKey(sm.getSignature())) {
			statistic.getDestinationMap().put(sm.getSignature(), new HashSet<String>());
		}
		for (ObjectSummaryModel singleObject : methodSummary.getSingleObjects()) {
			IntentSummaryModel intentSummary = (IntentSummaryModel) singleObject;
			Set<String> newDestinationSet = new HashSet<String>();
			for (String des : intentSummary.getSetDestinationList()) {
				newDestinationSet.add(des);
			}
			statistic.getDestinationMap().get(sm.getSignature()).addAll(newDestinationSet);

			String summaryType = IntentSummaryFeatureExtractor.getSummaryStr(intentSummary);
			addSummaryType2Map(intentSummary, summaryType, statistic.getIntentSummaryTypeMap());

			String summaryReceiveType = IntentSummaryFeatureExtractor.getReceiveStr(intentSummary);
			addSummaryType2Map(intentSummary, summaryReceiveType, statistic.getIntentSummaryReceiveTypeMap());

			String summaryNewType = IntentSummaryFeatureExtractor.getNewStr(intentSummary);
			addSummaryType2Map(intentSummary, summaryNewType, statistic.getIntentSummaryNewTypeMap());

			String summaryUsedType = IntentSummaryFeatureExtractor.getUseAttributeStr(intentSummary);
			addSummaryType2Map(intentSummary, summaryUsedType, statistic.getIntentSummaryUsedTypeMap());

			String summarySetType = IntentSummaryFeatureExtractor.getSetAttributeStr(intentSummary);
			addSummaryType2Map(intentSummary, summarySetType, statistic.getIntentSummarySetTypeMap());

			String summarySendType = IntentSummaryFeatureExtractor.getSendStr(intentSummary);
			addSummaryType2Map(intentSummary, summarySendType, statistic.getIntentSummarySendTypeMap());
		}

		for (Entry<String, Set<String>> en : statistic.getDestinationMap().entrySet()) {
			for (String des : en.getValue()) {
				if (des.startsWith(Global.v().getAppModel().getPackageName()))
					statistic.addIntraDestinationNum();
				else
					statistic.addInterDestinationNum();
			}
			int len = en.getValue().size();
			if (!statistic.getDestinationNum2MethodSet().containsKey(len))
				statistic.getDestinationNum2MethodSet().put(Integer.valueOf(len), new HashSet<String>());
			statistic.getDestinationNum2MethodSet().get(Integer.valueOf(len)).add(en.getKey());
		}

	}

	/**
	 * addSummaryType2Map
	 * 
	 * @param intentSummary
	 * @param type
	 * @param map
	 */
	private static void addSummaryType2Map(IntentSummaryModel intentSummary, String type,
			Map<String, Set<IntentSummaryModel>> map) {
		if (type.length() > 0) {
			if (!map.containsKey(type)) {
				map.put(type, new HashSet<IntentSummaryModel>());
			}
			map.get(type).add(intentSummary);
		}
	}

	private static boolean isICCRelatedEntryMethod(MethodSummaryModel intentSummary) {
		if (intentSummary.getPathSet().size() == 0)
			return false;
		SootMethod sm = null;
		try {
			sm = SootUtils.getSootMethodBySignature(intentSummary.getMethod().getSignature());
		} catch (Exception e) {
		}
		if (sm == null)
			return false;
		return Global.v().getAppModel().getEntryMethod2Component().containsKey(sm);
	}

}
