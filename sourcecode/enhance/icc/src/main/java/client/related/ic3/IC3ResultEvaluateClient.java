package main.java.client.related.ic3;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.related.ic3.IC3ResultEvaluateClient;
import main.java.client.related.ic3.model.IC3Model;
import main.java.client.soot.SootAnalyzer;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class IC3ResultEvaluateClient extends BaseClient {

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

		IC3Reader ic3 = new IC3Reader(result);
		ic3.analyze();
		System.out.println("Successfully analyze with IC3GraphClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		IC3ClientOutput outer = new IC3ClientOutput(this.result);
		IC3Model model = Global.v().getiC3Model();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.IC3FOLDETR);

		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_IC3;
		IC3ClientOutput.writeDotFileofIC3(summary_app_dir + ConstantUtils.IC3FOLDETR, dotname, model.getIC3AtgModel());
		IC3ClientOutput.writeIccLinksConfigFile(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.LINKFILE,
				model.getIC3AtgModel());
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.IC3FOLDETR + dotname, "pdf");
		FileUtils.copyFile(model.getIC3FilePath(), summary_app_dir + ConstantUtils.IC3FOLDETR
				+ Global.v().getAppModel().getAppName() + ".json");

		/** Intent **/
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3FOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
	}

}