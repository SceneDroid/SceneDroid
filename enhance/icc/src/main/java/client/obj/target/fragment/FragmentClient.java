package main.java.client.obj.target.fragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.ObjectAnalyzer;
import main.java.client.obj.target.ctg.StaticValueAnalyzer;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.DocumentException;

import soot.SootMethod;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class FragmentClient extends BaseClient {

	/**
	 * analyze logic for single app
	 * 
	 * @return
	 */
	@Override
	protected void clientAnalyze() {
		result = new StatisticResult();
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
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
		for (List<SootMethod> topoQueue : Global.v().getAppModel().getTopoMethodQueueSet()) {
			ObjectAnalyzer analyzer = new FragmentAnalyzer(topoQueue, result);
			analyzer.analyze();
		}
		System.out.println("Successfully analyze with FragmentClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.FRAGFOLDETR);

		FragmentClientOutput outer = new FragmentClientOutput(this.result);
		/** Method **/
		outer.writeMethodSummaryModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEMETHOD_ENTRY,true);
//		outer.writeMethodSummaryModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEMETHOD_ALL, false);

		/** Path **/
		outer.writePathSummaryModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEPATH_ENTRY, true);
//		outer.writePathSummaryModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEPATH_ALL, false);

		/** Intent **/
		outer.writeSingleFragModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
		outer.writeSingleFragModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);

		outer.writeATGModel(summary_app_dir + ConstantUtils.FRAGFOLDETR, ConstantUtils.ICTGMERGE + ".xml", Global.v()
				.getFragmentModel().getAtgModel());

		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ICTGFRAG;
		outer.writeDotFile(summary_app_dir + ConstantUtils.FRAGFOLDETR, dotname, Global.v().getFragmentModel()
				.getAtgModel(), true);
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.FRAGFOLDETR + dotname, "pdf");
	}

}