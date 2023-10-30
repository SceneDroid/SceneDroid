package main.java.client.related.ic3dial;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
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
public class IC3DIALDroidResultEvaluateClient extends BaseClient {

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

		IC3DialReader ic3 = new IC3DialReader(result);
		ic3.analyze();
		System.out.println("Successfully analyze with IC3DailGraphClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		IC3DialClientOutput outer = new IC3DialClientOutput(this.result);
		IC3Model model = Global.v().getiC3DialDroidModel();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR);

		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_IC3DIAL;
		IC3DialClientOutput.writeDotFileofIC3(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR, dotname,
				model.getIC3AtgModel());
		IC3DialClientOutput.writeIccLinksConfigFile(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR,
				ConstantUtils.LINKFILE, model.getIC3AtgModel());
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR + dotname, "pdf");
		FileUtils.copyFile(model.getIC3FilePath(), summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR
				+ Global.v().getAppModel().getAppName() + ".json");

		/** Intent **/
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR,
				ConstantUtils.SINGLEOBJECT_ENTRY, true);
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.IC3DIALDROIDFOLDETR,
				ConstantUtils.SINGLEOBJECT_ALL, false);
	}

}