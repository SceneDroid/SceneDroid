package main.java.client.statistic;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.statistic.model.ICCStatistic;
import main.java.client.statistic.model.StatisticResult;
import main.java.client.statistic.model.SummaryStatistic;
import main.java.client.statistic.model.TraceStatistic;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class StatisticClienOutput {

	StatisticResult result;

	public StatisticClienOutput(StatisticResult result) {
		this.result = result;
	}

	/**
	 * write SatisticResult.xml
	 * 
	 * @param dir
	 * @param file
	 * @param AppModel
	 *            .getInstance()
	 * @param b
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void writeSatisticModel(String dir, String file, boolean appendToExist) throws DocumentException,
			IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, appendToExist);
		Element root = document.getRootElement();
		Element app = root.addElement("app");

		/* app information */
		writeBasicSatistic(app);

		/* component information */
		writeComponentSatistic(app);

		/* method information */
		Element method = app.addElement("method");
		writeMehodSatistic(method);
		writeTraceNumSatistic(method);
		writeTraceDepthSatistic(method);

		/* icc information */
		Element flows = app.addElement("flows");
		writeICCSatistic(flows, result.getAllICCStatistic());
		writeICCSatistic(flows, result.getAllICCStatistic());
		FileUtils.xmlWriteEnd(dir, file, document);
	}

	/**
	 * writeBasicSatistic in writeSatisticModel
	 * 
	 * @param app
	 */
	private void writeBasicSatistic(Element app) {
		app.addAttribute("name", Global.v().getAppModel().getAppName());
		app.addAttribute("package", Global.v().getAppModel().getPackageName());
		app.addAttribute("cgEdgeNum", Global.v().getAppModel().getCg().size() + "");
	}

	/**
	 * writeComponentSatistic in writeSatisticModel
	 * 
	 * @param app
	 */
	private void writeComponentSatistic(Element app) {
		Element component = app.addElement("component");
		component.addAttribute("componentNumber", Global.v().getAppModel().getComponentMap().size() + "");
		component.addAttribute("exportedComponentNumber", Global.v().getAppModel().getExportedComponentMap().size()
				+ "");
		component.addAttribute("activityNumber", Global.v().getAppModel().getActivityMap().size() + "");
		component.addAttribute("servicetNumber", Global.v().getAppModel().getServiceMap().size() + "");
		component.addAttribute("broadCastNumber", Global.v().getAppModel().getRecieverMap().size() + "");
		component.addAttribute("contentProviderNumber", Global.v().getAppModel().getProviderMap().size() + "");
	}

	/**
	 * writeMehodSatistic in writeSatisticModel
	 * 
	 * @param app
	 */
	private void writeMehodSatistic(Element app) {
		SummaryStatistic statistic = result.getSummaryStatistic();
		Element basic = app.addElement("basic");
		Element all = basic.addElement("allIn");
		all.addAttribute("analyzedMethodNum", Global.v().getAppModel().getAllMethods().size() + "");
		all.addAttribute("summariedMethodNum", statistic.getSummariedMethods().size() + "");
		double ratio = 100.0 * statistic.getSummariedMethods().size() / Global.v().getAppModel().getAllMethods().size();
		all.addAttribute("SummariedMethodRatio", String.format("%.2f", ratio) + "%");

		Element entry = basic.addElement("entry");
		entry.addAttribute("analyzedEntryMethodNum", Global.v().getAppModel().getEntryMethod2Component().keySet()
				.size()
				+ "");
		entry.addAttribute("summariedEntryMethodNum", statistic.getSummariedEntryMethods().size() + "");
		double ratio2 = 100.0 * statistic.getSummariedEntryMethods().size()
				/ Global.v().getAppModel().getEntryMethod2Component().keySet().size();
		entry.addAttribute("SummariedEntryMethodRatio", String.format("%.2f", ratio2) + "%");
		entry.addAttribute("lifeCycleMethodsNum", statistic.getSummariedEntryLifeCycleMethods().size() + "");
		entry.addAttribute("listenerMethodsNum", statistic.getSummariedEntryListenerMethods().size() + "");
	}

	/**
	 * writeTraceNumSatistic in writeSatisticModel
	 * 
	 * @param method
	 */
	private void writeTraceNumSatistic(Element method) {
		Element methodTraceNumber = method.addElement("traceNumber");
		TraceStatistic statistic = result.getAllTraceStatistic();
		Element nonentry = methodTraceNumber.addElement("allIn");
		for (Entry<Integer, Set<String>> en : statistic.getMethodTraceNum2MethodSet().entrySet()) {
			nonentry.addAttribute("N" + en.getKey(), en.getValue().size() + "");
		}
		statistic = result.getEntryTraceStatistic();
		Element entry = methodTraceNumber.addElement("entry");
		for (Entry<Integer, Set<String>> en : statistic.getMethodTraceNum2MethodSet().entrySet()) {
			entry.addAttribute("N" + en.getKey(), en.getValue().size() + "");
		}
	}

	/**
	 * writeTraceDepthSatistic in writeSatisticModel
	 * 
	 * @param method
	 */
	private void writeTraceDepthSatistic(Element method) {
		Element methodTraceDepth = method.addElement("traceDepth");
		TraceStatistic statistic = result.getAllTraceStatistic();
		Element all = methodTraceDepth.addElement("allIn");
		for (Entry<Integer, Set<String>> en : statistic.getMethodTraceDepth2MethodSet().entrySet()) {
			all.addAttribute("D" + en.getKey(), en.getValue().size() + "");
		}
		statistic = result.getEntryTraceStatistic();
		Element entry = methodTraceDepth.addElement("entry");
		for (Entry<Integer, Set<String>> en : statistic.getMethodTraceDepth2MethodSet().entrySet()) {
			entry.addAttribute("D" + en.getKey(), en.getValue().size() + "");
		}
	}

	/**
	 * writeICCSatistic in writeSatisticModel
	 * 
	 * @param app
	 * @param statistic
	 */
	private void writeICCSatistic(Element app, ICCStatistic statistic) {
		Element icc = app.addElement("icc");
		icc.addAttribute("flowNum", statistic.getICCFlowNum() + "");

		Element iccDestination = icc.addElement("destinationNumber");
		for (Entry<Integer, Set<String>> en : statistic.getDestinationNum2MethodSet().entrySet()) {
			iccDestination.addAttribute("L" + en.getKey(), en.getValue().size() + "");
		}
		iccDestination.addAttribute("intraDestination", statistic.getIntraDestinationNum() + "");
		iccDestination.addAttribute("interDestination", statistic.getInterDestinationNum() + "");

		Element iccType = icc.addElement("type");
		for (Entry<String, Set<IntentSummaryModel>> en : statistic.getIntentSummaryTypeMap().entrySet())
			iccType.addAttribute(en.getKey(), en.getValue().size() + "");

		addElementInICC(icc, "newType", statistic.getIntentSummaryNewTypeMap());
		addElementInICC(icc, "receiveType", statistic.getIntentSummaryReceiveTypeMap());
		addElementInICC(icc, "usedType", statistic.getIntentSummaryUsedTypeMap());
		addElementInICC(icc, "setType", statistic.getIntentSummarySetTypeMap());
		addElementInICC(icc, "sendType", statistic.getIntentSummarySendTypeMap());

	}

	/**
	 * addElementInICC
	 * 
	 * @param icc
	 * @param key
	 * @param map
	 */
	private void addElementInICC(Element icc, String key, Map<String, Set<IntentSummaryModel>> map) {
		Element ele = icc.addElement(key);
		for (Entry<String, Set<IntentSummaryModel>> en : map.entrySet())
			ele.addAttribute(en.getKey(), en.getValue().size() + "");
	}

}
