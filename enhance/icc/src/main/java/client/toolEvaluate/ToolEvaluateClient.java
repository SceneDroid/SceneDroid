package main.java.client.toolEvaluate;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.labeledOracleModel.LabeledOracleReader;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.OracleUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.target.ctg.CTGReaderClient;
import main.java.client.related.a3e.A3EResultEvaluateClient;
import main.java.client.related.gator.GatorATGResultEvaluateClient;
import main.java.client.related.ic3.IC3ResultEvaluateClient;
import main.java.client.related.ic3dial.IC3DIALDroidResultEvaluateClient;
import main.java.client.related.story.StoryResultEvaluateClient;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class ToolEvaluateClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		new CTGReaderClient().start();
		
		new IC3ResultEvaluateClient().start();

		new IC3DIALDroidResultEvaluateClient().start();

		new GatorATGResultEvaluateClient().start();

		new A3EResultEvaluateClient().start();
		
		new StoryResultEvaluateClient().start();
		

//		dynamicResultAnalyzer analyzer = new dynamicResultAnalyzer();
//		analyzer.analyze();
//
//		manualResultAnalyzer analyzer2 = new manualResultAnalyzer();
//		analyzer2.analyze();

		LabeledOracleReader reader = new LabeledOracleReader();
		reader.analyze();

		System.out.println("Successfully analyze with ToolEvaluateClient.");

	}

	@Override
	public void clientOutput() {
		String appName = Global.v().getAppModel().getAppName();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + appName + File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.ORACLEFOLDETR);
		StringBuilder sb = new StringBuilder(MyConfig.getInstance().getMySwithch().toString());

//		dynamicModelContruction(sb, appName, summary_app_dir);
//		manualModelContruction(sb, appName, summary_app_dir);
//		oracleModelContruction(sb, appName, summary_app_dir);
		
		oracleEvaluate(sb);
		ICCBotEvaluate(sb);
		IC3Evaluate(sb);
		IC3DialEvaluate(sb);
		GatorEvaluate(sb);
		A3EEvaluate(sb); 
		StoryEvaluate(sb);

//		FilterAndEnhanceEvaluate(sb);

		outputNumberCount();
		outputGraphCount();
		outputOracleEvaluate();
		outputForPairwiseComparison();
		
		String content = sb.toString();
		FileUtils.writeText2File(summary_app_dir + ConstantUtils.ORACLEFOLDETR + appName + ConstantUtils.SCORERECORD,
				content, true);
	}


	private void outputForPairwiseComparison() {
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();
		
		pairwiseComparison("Gator", "IC3", gatorModel, ic3Model);
		pairwiseComparison("Gator", "IC3dial", gatorModel, IC3DialModel);
		pairwiseComparison("Gator", "A3E", gatorModel, a3eModel);
		pairwiseComparison("Gator", "Story", gatorModel, storyModelnoFrag);
		pairwiseComparison("Gator", "ICCBot", gatorModel, iccBotModelnoFrag);
		
		pairwiseComparison("IC3", "IC3dial", ic3Model, IC3DialModel);
		pairwiseComparison("IC3", "A3E", ic3Model, a3eModel);
		pairwiseComparison("IC3", "Story", ic3Model, storyModelnoFrag);
		pairwiseComparison("IC3", "ICCBot", ic3Model, iccBotModelnoFrag);
		
		pairwiseComparison("IC3dial","A3E",IC3DialModel, a3eModel);
		pairwiseComparison("IC3dial","Story",IC3DialModel, storyModelnoFrag);
		pairwiseComparison("IC3dial","ICCBot",IC3DialModel, iccBotModelnoFrag);
		
		pairwiseComparison("A3E","Story",a3eModel, storyModelnoFrag);
		pairwiseComparison("A3E","ICCBot",a3eModel, iccBotModelnoFrag);

		pairwiseComparison("Story","ICCBot",storyModelnoFrag, iccBotModelnoFrag);

		allComparison(iccBotModelnoFrag,ic3Model,IC3DialModel,gatorModel,a3eModel,storyModelnoFrag);
		
	}

	private void allComparison(ATGModel iccBotModelnoFrag, ATGModel ic3Model, ATGModel ic3DialModel, ATGModel gatorModel, ATGModel a3eModel, ATGModel storyModelnoFrag) {
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "allComparisonCount.txt";
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		for(Set<AtgEdge> edges : oracleModel.getAtgEdges().values()){
			for (AtgEdge oracleEdge : edges) {
				int num = 0;
				String tools= "";
				if(iccBotModelnoFrag.getFNSet().contains(oracleEdge.getDescribtion())) { num++; tools += "iccBot\t";}
				if(ic3Model.getFNSet().contains(oracleEdge.getDescribtion()))  { num++; tools += "ic3\t";}
				if(ic3DialModel.getFNSet().contains(oracleEdge.getDescribtion()))  { num++; tools += "ic3dial\t";}
				if(gatorModel.getFNSet().contains(oracleEdge.getDescribtion()))  { num++; tools += "gator\t";}
				if(a3eModel.getFNSet().contains(oracleEdge.getDescribtion()))  { num++; tools += "a3e\t";}
				if(storyModelnoFrag.getFNSet().contains(oracleEdge.getDescribtion()))  { num++; tools += "story\t";}

				FileUtils.writeText2File(filename,  oracleEdge.getDescribtion()+"\t"+num+"\t"+tools+"\n", true);
			}
		}

	}

	private String pairwiseComparison(String tagA, String tagB, ATGModel modelA, ATGModel modelB) {
		int oracle = 0, commonTP = 0, modelATP = 0, modelBTP = 0;
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		oracle = oracleModel.getOracleEdgeSize();
		modelATP = modelA.getTPSet().size();
		modelBTP = modelB.getTPSet().size();
		Set<String> modelCommon = new HashSet<String> (modelA.getTPSet());
		modelCommon.retainAll(modelB.getTPSet());
		commonTP = modelCommon.size();
		
		
		int mergedICC = 0, commonICC = 0, modelAICC = 0, modelBICC = 0;
		Set<String> modelMerge = new HashSet<String> (modelA.getEdgeSet());
		modelMerge.addAll(modelB.getEdgeSet());
		mergedICC = modelMerge.size();
		
		Set<String> modelCommon2 = new HashSet<String> (modelA.getEdgeSet());
		modelCommon2.retainAll(modelB.getEdgeSet());
		commonICC = modelCommon2.size();
		
		modelAICC = modelA.getEdgeSet().size();
		modelBICC = modelB.getEdgeSet().size();

		String res =  oracle +"\t" + modelATP +"\t" + modelBTP +"\t" + commonTP +"\t" +
			   mergedICC +"\t"  + modelAICC +"\t"  + modelBICC +"\t" + commonICC;
//		System.out.println(res);
		
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "pairwiseCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "app\ttool\tcommon\tonly\tlast\n", true);
		}
		String name = Global.v().getAppModel().getAppName();
		String commonTPRatio = divide(commonTP, oracle);
		String modelATPonlyRatio = divide(modelATP-commonTP, oracle);
		String modelBTPonlyRatio = divide(modelBTP-commonTP, oracle);
		String modelAFNRatio = divide(oracle-modelATP, oracle);
		String modelBFNRatio = divide(oracle-modelBTP, oracle);
		
		String commonICCRatio = divide(commonICC, mergedICC);
		String modelAICConlyRatio = divide(modelAICC-commonICC, mergedICC);
		String modelBICConlyRatio = divide(modelBICC-commonICC, mergedICC);
		String modelALackRatio = divide(mergedICC-modelAICC, mergedICC);
		String modelBLackRatio = divide(mergedICC-modelBICC, mergedICC);
		
		
		FileUtils.writeText2File(filename, name +"\t"+ tagA+"&"+tagB+"@TP\t"+tagA + "\t" + commonTPRatio +"\t" + modelATPonlyRatio +"\t"+ modelAFNRatio +"\n", true);
		FileUtils.writeText2File(filename, name +"\t"+ tagA+"&"+tagB+"@TP\t"+tagB + "\t" + commonTPRatio +"\t" + modelBTPonlyRatio +"\t"+ modelBFNRatio  +"\n", true);
		FileUtils.writeText2File(filename, name +"\t"+ tagA+"&"+tagB+"@All\t"+tagA + "\t" + commonICCRatio +"\t" + modelAICConlyRatio +"\t"+ modelALackRatio +"\n", true);
		FileUtils.writeText2File(filename, name +"\t"+ tagA+"&"+tagB+"@All\t"+tagB + "\t" + commonICCRatio +"\t" + modelBICConlyRatio +"\t"+ modelBLackRatio +"\n", true);
		
		return res;
	}

	public String divide(int a, int b){
		if(b==0)
			return "div0";
		return String.format("%.2f", 1.0*a/b);
	}
	
	private void FilterAndEnhanceEvaluate(StringBuilder sb) {
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		String content = Global.v().getAppModel().getAppName() + "\t";
		content += "filter\t" + manualModel.getFilteredNum() + "\t";
		content += "add\t" + manualModel.getEnhancedNum() + "\t";
		content += "filteredService\t" + manualModel.getFilteredServiceNum() + "\t";
		FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "edgeResult.txt", content, true);
		System.out.print(manualModel.getFilteredNum() + "\t");
		System.out.print(manualModel.getEnhancedNum() + "\t");

	}

	private void outputNumberCount() {
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel iccBotModel = Global.v().getiCTGModel().getOptModel();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();
		ATGModel storyModel = Global.v().getStoryModel().getStoryAtgModel();

		StringBuilder sb = new StringBuilder();
		
		sb.append(Global.v().getAppModel().getComponentMap().size() + "\t");
		
		sb.append(gatorModel.getComp2CompSize() + "\t");
		sb.append(ic3Model.getComp2CompSize() + "\t");
		sb.append(IC3DialModel.getComp2CompSize() + "\t");
		sb.append(a3eModel.getComp2CompSize() + "\t");
		sb.append(storyModelnoFrag.getComp2CompSize() + "\t");
		sb.append(iccBotModelnoFrag.getComp2CompSize() + "\t");
		
		sb.append(gatorModel.getAct2ActSize() + "\t");
		sb.append(ic3Model.getAct2ActSize() + "\t");
		sb.append(IC3DialModel.getAct2ActSize() + "\t");
		sb.append(a3eModel.getAct2ActSize() + "\t");
		sb.append(storyModelnoFrag.getAct2ActSize() + "\t");
		sb.append(iccBotModelnoFrag.getAct2ActSize() + "\t");
		
		sb.append(gatorModel.getComp2CompSize() + "\t");
		sb.append(ic3Model.getComp2CompSize() + "\t");
		sb.append(IC3DialModel.getComp2CompSize() + "\t");
		sb.append(a3eModel.getComp2CompSize() + "\t");
		sb.append(storyModel.getComp2CompSize() + "\t");
		sb.append(iccBotModel.getComp2CompSize() + "\t");
		
//		System.out.println("outputNumberCount: "+sb.toString());
		
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "numberCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t\t"  +  "C2C\t\t\t\t\t\t" + "A2A\t\t\t\t\t\t" + "CF2CF\t\t\t\t\t\t"+ "\n", true);
			FileUtils.writeText2File(filename, "\tComponent\t"  
					+  "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" 
					+"Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t"+ "\n", true);
		}
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}

	private void outputGraphCount() {
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();

		StringBuilder sb = new StringBuilder();

//		sb.append(String.format("%.2f", dynamicModel.getCompletenessScore()) + "\t");
//		sb.append(String.format("%.2f", manualModel.getCompletenessScore()) + "\t");
//		sb.append(String.format("%.2f", oracleModel.getCompletenessScore()) + "\t");
//
//		sb.append(String.format("%.2f", dynamicModel.getConnectionScore()) + "\t");
//		sb.append(String.format("%.2f", manualModel.getConnectionScore()) + "\t");
//		sb.append(String.format("%.2f", oracleModel.getConnectionScore()) + "\t");

		sb.append(iccBotModelnoFrag.getTotalCompNum() + "\t");
		
		
		sb.append(gatorModel.getSeparatedCompNum() + "\t");
		sb.append(ic3Model.getSeparatedCompNum() + "\t");
		sb.append(IC3DialModel.getSeparatedCompNum() + "\t");
		sb.append(a3eModel.getSeparatedCompNum() + "\t");
		sb.append(storyModelnoFrag.getSeparatedCompNum() + "\t");
		sb.append(iccBotModelnoFrag.getSeparatedCompNum() + "\t");
		
		
		sb.append(gatorModel.getMainNotReachableCompNum() + "\t");
		sb.append(ic3Model.getMainNotReachableCompNum() + "\t");
		sb.append(IC3DialModel.getMainNotReachableCompNum() + "\t");
		sb.append(a3eModel.getMainNotReachableCompNum() + "\t");
		sb.append(storyModelnoFrag.getMainNotReachableCompNum() + "\t");
		sb.append(iccBotModelnoFrag.getMainNotReachableCompNum() + "\t");
		
		sb.append(gatorModel.getExportNotReachableCompNum() + "\t");
		sb.append(ic3Model.getExportNotReachableCompNum() + "\t");
		sb.append(IC3DialModel.getExportNotReachableCompNum() + "\t");
		sb.append(a3eModel.getExportNotReachableCompNum() + "\t");
		sb.append(storyModelnoFrag.getExportNotReachableCompNum() + "\t");
		sb.append(iccBotModelnoFrag.getExportNotReachableCompNum() + "\t");
		
//		System.out.println("outputGraphCount: "+sb.toString());
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "graphCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t" +  "Total\t" + "separated\t\t\t\t\t\t" 
						+ "mainNot\t\t\t\t\t\t" + "exportNot\t\t\t\t\t\t" +"\n", true);
			String tools = "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t";
			FileUtils.writeText2File(filename,  "\t" +  "\t" + tools + tools+ tools + "\n", true);
		}
		
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}

	private void outputOracleEvaluate() {
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		ATGModel IC3DialModel = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		ATGModel gatorModel = Global.v().getGatorModel().getGatorAtgModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();

		StringBuilder sb = new StringBuilder();

		//oracle
		sb.append(iccBotModelnoFrag.getOracleEdgeSize() + "\t");

		sb.append(gatorModel.getFnEdgeSize() + "\t");
		sb.append(ic3Model.getFnEdgeSize() + "\t");
		sb.append(IC3DialModel.getFnEdgeSize() + "\t");
		sb.append(a3eModel.getFnEdgeSize() + "\t");
		sb.append(storyModelnoFrag.getFnEdgeSize() + "\t");
		sb.append(iccBotModelnoFrag.getFnEdgeSize() + "\t");
		
		
		sb.append(String.format("%.2f", gatorModel.getFalsenegativeScore())+ "\t");
		sb.append(String.format("%.2f", ic3Model.getFalsenegativeScore()) + "\t");
		sb.append(String.format("%.2f", IC3DialModel.getFalsenegativeScore()) + "\t");
		sb.append(String.format("%.2f", a3eModel.getFalsenegativeScore())+ "\t");
		sb.append(String.format("%.2f", storyModelnoFrag.getFalsenegativeScore())+ "\t");
		sb.append(String.format("%.2f", iccBotModelnoFrag.getFalsenegativeScore()));
		
//		System.out.println("outputOracleEvaluate: "+sb.toString());
		
		String filename = MyConfig.getInstance().getResultWarpperFolder() + File.separator + "oracleCount.txt";
		File f = new File(filename);
		if(!f.exists()){
			FileUtils.writeText2File(filename,  "\t"  + "Oracle\t" + "FN\t\t\t\t\t\t" + "FNRate\t\t\t\t\t\t" + "\n", true);
			FileUtils.writeText2File(filename,  "\t"  + "Oracle\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "Gator\tIC3\tIC3-Dial\tA3E\tStory\tICCBot\t" + "\n", true);
		}
		FileUtils.writeText2File(filename, Global.v().getAppModel().getAppName()
				+ "\t" + sb.toString() + "\n", true);
	}
	/**
	 * construct model use dynamic log information
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void dynamicModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		String dotname = appName + "_" + ConstantUtils.ICTGDYNAMIC;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname, dynamicModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname, "pdf");
		dynamicModel.evaluateGraphCount("dynamic oracle");

	}

	/**
	 * construct model use manual information
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void manualModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		String dotname2 = appName + "_" + ConstantUtils.ICTGDMANUAL;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname2, manualModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname2, "pdf");
		manualModel.evaluateGraphCount("manual oracle");
	}

	/**
	 * merge dynamic and manual oracle to oracleModel
	 * 
	 * @param sb
	 * @param appName
	 * @param summary_app_dir
	 */
	private void oracleModelContruction(StringBuilder sb, String appName, String summary_app_dir) {
		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

		ToolEvaluateClientOutput outer = new ToolEvaluateClientOutput();
		ATGModel.mergeNodels2newOne(dynamicModel, manualModel, oracleModel);
		String dotname3 = appName + "_" + ConstantUtils.ICTGORACLE;
		outer.writeDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR, dotname3, oracleModel, false);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.ORACLEFOLDETR + dotname3, "pdf");
		OracleUtils.writeTagedOracleFile(summary_app_dir, appName);
		oracleModel.evaluateGraphCount("whole oracle");
	}

	private void oracleEvaluate(StringBuilder sb) {
//		ATGModel dynamicModel = Global.v().getiCTGModel().getDynamicModel();
//		ATGModel manualModel = Global.v().getiCTGModel().getMannualModel();
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();

//		initStringBuilderConnection("oracleModel", sb);
//		dynamicModel.evaluateConnectivity("dynamicModel", sb);
//		manualModel.evaluateConnectivity("manualModel", sb);
//		oracleModel.evaluateConnectivity("oracleModel", sb);
		oracleModel.countTagForOracle();
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of ICCBot
	 * 
	 * @param sb
	 */
	private void ICCBotEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel iccBotModelnoFrag = Global.v().getiCTGModel().getOptModelwithoutFrag();
		if(iccBotModelnoFrag.isExist()){
			iccBotModelnoFrag.evaluateGraphCount("ICCBot      ");
	
			if (oracleModel.isExist()) {
				iccBotModelnoFrag.evaluateFalseNegative("ICCBot      ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool ICCBot doesn't exist, for false negative evaluation, please run the MainClient first.\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of IC3
	 * 
	 * @param sb
	 */
	private void IC3Evaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel ic3Model = Global.v().getiC3Model().getIC3AtgModel();
		if(ic3Model.isExist()) {
			ic3Model.evaluateGraphCount("IC3         ");
			if (oracleModel.isExist()) {
				ic3Model.evaluateFalseNegative("IC3         ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool IC3 doesn't exist, please add files into folder /relatedTools/IC3\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of IC3-dialdroid
	 * 
	 * @param sb
	 */
	private void IC3DialEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel ic3Model = Global.v().getiC3DialDroidModel().getIC3AtgModel();
		if(ic3Model.isExist()){
			ic3Model.evaluateGraphCount("IC3Dial     ");
			if (oracleModel.isExist()) {
				ic3Model.evaluateFalseNegative("IC3Dial     ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, for false negative evaluation, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool IC3-DIALDroid doesn't exist, please add files into folder /relatedTools/IC3-Dial\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}

	/**
	 * Evaluate the completeness/connection rate/false nagetive of Gator
	 * 
	 * @param sb
	 */
	private void GatorEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel GatorModel = Global.v().getGatorModel().getGatorAtgModel();
		if(GatorModel.isExist()){
			GatorModel.evaluateGraphCount("Gator         ");
			if (oracleModel.isExist()) {
				GatorModel.evaluateFalseNegative("Gator         ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool Gator doesn't exist, please add files into folder /relatedTools/Gator\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}
	

	private void A3EEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel a3eModel = Global.v().getA3eModel().geta3eAtgModel();
		if(a3eModel.isExist()){
			a3eModel.evaluateGraphCount("A3E         ");
			if (oracleModel.isExist()) {
				a3eModel.evaluateFalseNegative("A3E         ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool A3E doesn't exist, please add files into folder /relatedTools/A3E\n";
			sb.append(hint);
			System.out.println(hint);
		}
		
	}
	
	private void StoryEvaluate(StringBuilder sb) {
		ATGModel oracleModel = Global.v().getiCTGModel().getOracleModel();
		ATGModel storyModelnoFrag = Global.v().getStoryModel().getStoryAtgModelWithoutFrag();
		if(storyModelnoFrag.isExist()){
			storyModelnoFrag.evaluateGraphCount("Story         ");
			if (oracleModel.isExist()) {
				storyModelnoFrag.evaluateFalseNegative("Story         ");
			}else{
				String hint = "The results of labeled oracle doesn't exist, please add files into folder /labeledOracle\n";
				sb.append(hint);
				System.out.println(hint);
			}
		}else{
			String hint = "The results for tool Story doesn't exist, please add files into folder /relatedTools/StoryDistiller\n";
			sb.append(hint);
			System.out.println(hint);
		}
	}
}