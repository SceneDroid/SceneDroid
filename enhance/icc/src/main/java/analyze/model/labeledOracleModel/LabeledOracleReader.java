package main.java.analyze.model.labeledOracleModel;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.utils.ConstantUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LabeledOracleReader extends Analyzer {
	private String appName;
	private String labeledOracleFolder;
	private String filePath;

	@Override
	public void analyze() {
		this.appName = appModel.getAppName();
		this.labeledOracleFolder = ConstantUtils.LABELEDORACLEFOLDETR;
		this.filePath = labeledOracleFolder + appName + ConstantUtils.ORACLETEXT;

		Global.v().setLabeledOracleModel(readLabeledOracleModelFromFile());
	}

	private LabeledOracleModel readLabeledOracleModelFromFile() {
		LabeledOracleModel model = new LabeledOracleModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		System.out.println(filePath);
		File xmlFile = new File(filePath);
		if (!xmlFile.exists())
			return model;
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(xmlFile);
			Element rootElement = document.getRootElement();
			Iterator<?> iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element oracleEdge = (Element) iterator.next();
				String classNameS = oracleEdge.attributeValue("source");
				String classNameT = oracleEdge.attributeValue("destination");
				IccTag iccTag = new IccTag(classNameS,classNameT);
				
				model.addLabeledOracle(iccTag);
				AtgEdge edge = new AtgEdge(new AtgNode(classNameS), new AtgNode(classNameT), "", -1, "");
				oracleModel.addAtgEdges(classNameS, edge);
				
				Iterator<?> iterator1 = oracleEdge.elementIterator();
				while (iterator1.hasNext()) {
					Element subEle = (Element) iterator1.next();
					if (subEle.getName().equals("comment")) {
						String comment = subEle.getStringValue();
						String[] ss = comment.split("->");
						iccTag.setMethodTrace(Arrays.asList(ss));
					}
					if (subEle.getName().equals("callLines")) {
						Iterator<?> iterator2 = subEle.elementIterator();
						while (iterator2.hasNext()) {
							Element line = (Element) iterator2.next();
							iccTag.getCallLineTrace().add(line.attribute("src").getStringValue());
						}
					}
					if (subEle.getName().equals("tags")) {
						Element entryMethod = subEle.element("entryMethod");
						iccTag.setLifeCycle(entryMethod.attributeValue("isLifeCycle").equals("true"));
						iccTag.setStaticCallBack(entryMethod.attributeValue("isStaticCallBack").equals("true"));
						iccTag.setDynamicCallBack(entryMethod.attributeValue("isDynamicCallBack").equals("true"));
						iccTag.setImpliciyCallBack(entryMethod.attributeValue("isImplicitCallback").equals("true"));
						if (iccTag.isStaticCallBack() || iccTag.isDynamicCallBack() || iccTag.isImpliciyCallBack())
							iccTag.setCallBackInclude(true);
						else {
							iccTag.setLifeCycleOnly(true);
						}

						Element exitMethod = subEle.element("exitMethod");
						if(exitMethod != null){
							iccTag.setNormalSendICC(exitMethod.attributeValue("isNormalSendICC").equals("true"));
							iccTag.setWarpperSendICC(exitMethod.attributeValue("isAtypicalSendICC").equals("true"));
						}
						Element intentMatch = subEle.element("intentMatch");
						if(intentMatch != null){
							iccTag.setExplicit(intentMatch.attributeValue("isExplicit").equals("true"));
							iccTag.setImplicit(intentMatch.attributeValue("isImplicit").equals("true"));
						}
						Element componentScope = subEle.element("analyzeScope").element("componentScope");
						if(componentScope != null){
							iccTag.setActivity(componentScope.attributeValue("isActivity").equals("true"));
							iccTag.setService(componentScope.attributeValue("isService").equals("true"));
							iccTag.setBroadCast(componentScope.attributeValue("isBroadCast").equals("true"));
							iccTag.setDynamicBroadCast(componentScope.attributeValue("isDynamicBroadCast").equals("true"));
							if (iccTag.isService() || iccTag.isBroadCast() || iccTag.isDynamicBroadCast()) {
								iccTag.setNonActivity(true);
							}
						}

						Element nonComponentScope = subEle.element("analyzeScope").element("nonComponentScope");
						if(nonComponentScope != null){
							iccTag.setFragment(nonComponentScope.attributeValue("isFragment").equals("true"));
							iccTag.setAdapter(nonComponentScope.attributeValue("isAdapter").equals("true"));
							iccTag.setWidget(nonComponentScope.attributeValue("isWidget").equals("true"));
							iccTag.setOtherClass(nonComponentScope.attributeValue("isOtherClass").equals("true"));
							if (iccTag.isFragment() || iccTag.isAdapter() || iccTag.isWidget()) {
								iccTag.setNonComponent(true);
							}
						}

						Element methodScope = subEle.element("analyzeScope").element("methodScope");
						if(methodScope != null){
							iccTag.setLibraryInvocation(methodScope.attributeValue("isLibraryInvocation").equals("true"));
							iccTag.setAsyncInvocation(methodScope.attributeValue("isAsyncInvocation").equals("true"));
							iccTag.setPolymorphic(methodScope.attributeValue("isPolymorphic").equals("true"));
						}

						Element objectScope = subEle.element("analyzeScope").element("objectScope");
						if(objectScope != null){
							iccTag.setStaticVal(objectScope.attributeValue("isStaticVal").equals("true"));
							iccTag.setStringOp(objectScope.attributeValue("isStringOp").equals("true"));
							iccTag.setNoExtra(objectScope.attributeValue("isNoExtra").equals("true"));
						}
						Element sensitivityScope = subEle.element("analyzeScope").element("sensitivityScope");
						if(sensitivityScope != null){
							iccTag.setFlowSensitive(sensitivityScope.attributeValue("flow").equals("true"));
							iccTag.setPathSensitive(sensitivityScope.attributeValue("path").equals("true"));
							iccTag.setContextSensitive(sensitivityScope.attributeValue("context").equals("true"));
							iccTag.setObjectSensitive(sensitivityScope.attributeValue("object").equals("true"));
							iccTag.setFieldSensitive(sensitivityScope.attributeValue("field").equals("true"));
						}
						//modify the format of labeled oracle
						Element intentFieldScope = subEle.element("analyzeScope").element("intentFieldScope");
						if(intentFieldScope != null){
							iccTag.setStaticVal(intentFieldScope.attributeValue("isStaticVal").equals("true"));
							iccTag.setStringOp(intentFieldScope.attributeValue("isStringOp").equals("true"));
							iccTag.setNoExtra(intentFieldScope.attributeValue("isNoExtra").equals("true"));
							iccTag.setContextSensitive(intentFieldScope.attributeValue("isContext").equals("true"));
						}
					}
				}
				iccTag.postAnalyzeTags();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return model;
	}

}
