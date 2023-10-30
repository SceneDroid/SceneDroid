package main.java.client.related.a3e;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.related.a3e.model.A3EModel;
import main.java.client.soot.SootAnalyzer;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class A3EResultEvaluateClient extends BaseClient {

	/**
	 * analyze logic for single app
	 * 
	 * @return
	 */
	@Override
	protected void clientAnalyze() {
		result = new StatisticResult();
		if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
			SootAnalyzer analyzer = new SootAnalyzer();
			analyzer.analyze();
			MyConfig.getInstance().setSootAnalyzeFinish(true);
		}
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}

		A3EReader a3e = new A3EReader(result);
		a3e.analyze();
		System.out.println("Successfully analyze with A3EClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		A3EClientOutput outer = new A3EClientOutput(this.result);
		A3EModel model = Global.v().getA3eModel();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.A3EFOLDETR);

		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_A3E;
		A3EClientOutput.writeDotFileofA3E(summary_app_dir + ConstantUtils.A3EFOLDETR, dotname, model.geta3eAtgModel());
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.A3EFOLDETR + dotname, "pdf");
		FileUtils.copyFile(model.geta3eFilePath(), summary_app_dir + ConstantUtils.A3EFOLDETR
				+ Global.v().getAppModel().getAppName() + ".xml");
		FileUtils.copyFile(model.geta3eFilePath()+".dot", summary_app_dir + ConstantUtils.A3EFOLDETR + dotname+"_original" + ".dot");
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.A3EFOLDETR + dotname+"_original", "pdf");
		
		/** Intent **/
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.A3EFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.A3EFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
	}

}