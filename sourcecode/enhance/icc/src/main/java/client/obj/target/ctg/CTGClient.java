package main.java.client.obj.target.ctg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.SummaryLevel;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.cg.DynamicReceiverCGAnalyzer;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.atg.AtgType;
import main.java.client.obj.target.ctg.CTGClient;
import main.java.client.obj.target.fragment.FragmentClient;
import main.java.client.soot.IROutputClient;
import main.java.client.statistic.model.StatisticResult;
import org.dom4j.DocumentException;

import soot.SootMethod;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class CTGClient extends BaseClient {

	
	/**
	 * analyze CTG for single app
	 */
	@Override
	protected void clientAnalyze() {
		result = new StatisticResult();
		
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}
		if (MyConfig.getInstance().isWriteSootOutput()) {
			new IROutputClient().start();
		}
		if (!MyConfig.getInstance().isCallGraphAnalyzeFinish()) {
			new CallGraphClient().start();
			MyConfig.getInstance().setCallGraphAnalyzeFinish(true);
		}
		if (!MyConfig.getInstance().isStaitiucValueAnalyzeFinish()) {
			if (MyConfig.getInstance().getMySwithch().isStaticFieldSwitch()) {
				StaticValueAnalyzer staticValueAnalyzer = new StaticValueAnalyzer();
				staticValueAnalyzer.analyze();
				MyConfig.getInstance().setStaitiucValueAnalyzeFinish(true);
			}
		}
		if (MyConfig.getInstance().getMySwithch().isDynamicBCSwitch()) {
			DynamicReceiverCGAnalyzer dynamicIntentFilterAnalyzer = new DynamicReceiverCGAnalyzer();
			dynamicIntentFilterAnalyzer.analyze();
		}

		if (MyConfig.getInstance().getMySwithch().isFragmentSwitch()) {
			if (!MyConfig.getInstance().isFragementAnalyzeFinish()) {
				new FragmentClient().start();
				MyConfig.getInstance().setFragementAnalyzeFinish(true);
			}
		}
		setMySwitch1();
		for (List<SootMethod> topoQueue : Global.v().getAppModel().getTopoMethodQueueSet()) {
			ObjectAnalyzer analyzer = new CTGAnalyzer(topoQueue, result);
			analyzer.analyze();
		}
		System.out.println("Successfully analyze with CTGClient.");
	}

	protected void setMySwitch1() {
		MyConfig.getInstance().getMySwithch().setSetDesRelatedAttributeOnlyStrategy(true);
		MyConfig.getInstance().getMySwithch().setSetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(false);
		MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.object);
	}

	protected void setMySwitch2() {
		MyConfig.getInstance().getMySwithch().setSetAttributeStrategy(false);
		MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.path);
	}
	@Override
	public void clientOutput() throws IOException, DocumentException {
		outputCTGInfo();
		if (!MyConfig.getInstance().isOracleConstructionClientFinish()) {
//			new ToolEvaluateClient().start();
		}
	}

	private void outputCTGInfo() {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);
		FileUtils.createFolder(summary_app_dir + ConstantUtils.FRAGFOLDETR);
		FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);

		CTGClientOutput outer = new CTGClientOutput(this.result);

		String ictgFolder = summary_app_dir + ConstantUtils.ICTGFOLDETR;
		String fragFolder = summary_app_dir + ConstantUtils.FRAGFOLDETR;
		System.out.println("writeComponentModel");
		/** Component **/
		outer.writeComponentModel(ictgFolder, ConstantUtils.COMPONENTMODEL);
		/** Method **/
		System.out.println("writeMethodSummaryModel");
		outer.writeMethodSummaryModel(ictgFolder, ConstantUtils.SINGLEMETHOD_ENTRY,true);
//		outer.writeMethodSummaryModel(ictgFolder, ConstantUtils.SINGLEMETHOD_ALL, false);
		if(MyConfig.getInstance().getMySwithch().isFragmentSwitch()){
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEMETHOD_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEMETHOD_ALL);
			FileUtils.copyFile(fragFolder+ConstantUtils.SINGLEMETHOD_ENTRY, ictgFolder+ConstantUtils.SINGLEMETHODFRAG_ENTRY);
		}
		
		/** Path **/
		System.out.println("writePathSummaryModel");
		outer.writePathSummaryModel(ictgFolder, ConstantUtils.SINGLEPATH_ENTRY, true);
//		outer.writePathSummaryModel(ictgFolder, ConstantUtils.SINGLEPATH_ALL, false);
		if(MyConfig.getInstance().getMySwithch().isFragmentSwitch()){
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEPATH_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEPATH_ALL);
			FileUtils.copyFile(fragFolder+ConstantUtils.SINGLEPATH_ENTRY, ictgFolder+ConstantUtils.SINGLEPATHFRAG_ENTRY);
		}
		
		/** Intent **/
		System.out.println("writeIntentSummaryModel");
		outer.writeIntentSummaryModel(ictgFolder, ConstantUtils.SINGLEOBJECT_ENTRY,true);
//		outer.writeIntentSummaryModel(ictgFolder, ConstantUtils.SINGLEOBJECT_ALL, false);
		if(MyConfig.getInstance().getMySwithch().isFragmentSwitch()){
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEOBJECT_ENTRY);
//			outer.appendInfo(ictgFolder, fragFolder, ConstantUtils.SINGLEOBJECT_ALL);
			FileUtils.copyFile(fragFolder+ConstantUtils.SINGLEOBJECT_ENTRY, ictgFolder+ConstantUtils.SINGLEOBJECTFRAG_ENTRY);
		}
		
		
		/** ICTG **/
		// merge frag and component
		ATGModel.mergeNodels2newOne(null, Global.v().getFragmentModel()
			.getAtgModel(), Global.v().getiCTGModel().getOptModel());
		ATGModel ictgMergedModel = Global.v().getiCTGModel().getOptModel();
		// ictgMergedModel
		System.out.println("writeAtgModeTxtFile");
		String txtName = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGMERGE + ".txt";
		outer.writeAtgModeTxtFile(ictgFolder, txtName, ictgMergedModel, false);
		System.out.println("writeDotFile");
		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGMERGE;
		outer.writeDotFile(ictgFolder, dotname, ictgMergedModel, true);
//		if (ictgMergedModel.getComp2CompSize() < 1800)
		System.out.println("writeATGModel");
		outer.writeATGModel(ictgFolder, ConstantUtils.ICTGMERGE + ".xml",
				ictgMergedModel);
		
//		// ictgOptModel
		Global.v().getiCTGModel().setOptModelwithoutFrag(getIctgOptModel());
		ATGModel ictgOptModel = Global.v().getiCTGModel().getOptModelwithoutFrag();
		System.out.println("writeAtgModeTxtFile2");
		String txtName2 = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT + ".txt";
		outer.writeAtgModeTxtFile(ictgFolder, txtName2, ictgOptModel, false);
		System.out.println("writeDotFile2");
		String dotname2 = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGOPT;
		outer.writeDotFile(ictgFolder, dotname2, ictgOptModel, false);
//		if (ictgMergedModel.getComp2CompSize() < 1800)
		System.out.println("writeATGModel2");
		outer.writeATGModel(ictgFolder, ConstantUtils.ICTGOPT + ".xml", ictgOptModel);
		
		GraphUtils.generateDotFile(ictgFolder + dotname2, "pdf");
		GraphUtils.generateDotFile(ictgFolder + dotname, "pdf");
		// outer.writeIccLinksConfigFile(summary_app_dir +
		// ConstantUtils.ICTGFOLDETR, ConstantUtils.LINKFILE, ictgOptModel);
		
	}

	
	private ATGModel getIctgOptModel() {
		ATGModel ictgOptModel = new ATGModel();
		ATGModel mergedIctgModel = Global.v().getiCTGModel().getOptModel();
		Map<String, Set<String>> desfrag2StratcomMap = new HashMap<String, Set<String>>();
		for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
			for (AtgEdge edge : entry.getValue()) {
				if (edge.getType().equals(AtgType.Act2Frag) || edge.getType().equals(AtgType.NonAct2Frag)) {
					String souComp = edge.getSource().getName();
					String desFrag = edge.getDestnation().getName();
					if (!desfrag2StratcomMap.containsKey(desFrag))
						desfrag2StratcomMap.put(desFrag, new HashSet<String>());
					desfrag2StratcomMap.get(desFrag).add(souComp);
				}
			}
		}
		while (true) { // fix point
			boolean changed = false;
			for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
				for (AtgEdge edge : entry.getValue()) {
					if (edge.getType().equals(AtgType.Frag2Frag)) {
						String souFrag = edge.getSource().getName();
						String desFrag = edge.getDestnation().getName();
						if (desfrag2StratcomMap.containsKey(souFrag)) {
							if (!desfrag2StratcomMap.containsKey(desFrag)) {
								desfrag2StratcomMap.put(desFrag, new HashSet<String>());
							}
							for (String com : desfrag2StratcomMap.get(souFrag)) {
								if (!desfrag2StratcomMap.get(desFrag).contains(com)) {
									desfrag2StratcomMap.get(desFrag).add(com);
									changed = true;
								}
							}
						}
					}
				}
			}
			if (changed == false)
				break;
		}

		for (Entry<String, Set<AtgEdge>> entry : mergedIctgModel.getAtgEdges().entrySet()) {
			for (AtgEdge edge : entry.getValue()) {
				// if(edge.getSource().getClassName().contains("ItemlistFragment"))
				// System.out.println(edge);
				switch (edge.getType()) {
				case Act2Act:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case NonAct2Act:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case Act2NonAct:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case NonAct2NonAct:
					ictgOptModel.addAtgEdges(entry.getKey(), edge);
					break;
				case Frag2Act:
					if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
						for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
							AtgEdge edgeCopy = new AtgEdge(edge);
							edgeCopy.setSource(new AtgNode(startCom));
							ictgOptModel.addAtgEdges(startCom, edgeCopy);
						}
					}
					break;
				case Frag2NonAct:
					if (desfrag2StratcomMap.containsKey(edge.getSource().getClassName())) {
						for (String startCom : desfrag2StratcomMap.get(edge.getSource().getClassName())) {
							AtgEdge edgeCopy = new AtgEdge(edge);
							edgeCopy.setSource(new AtgNode(startCom));
							ictgOptModel.addAtgEdges(startCom, edgeCopy);
						}
					}
					break;
				default:
					break;
				}
			}
		}

		return ictgOptModel;
	}

	protected void setMySwitch() {
		// TODO Auto-generated method stub
		
	}
}