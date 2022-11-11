package main.java.client.related.gator;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.related.gator.model.GatorModel;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class GatorATGResultEvaluateClient extends BaseClient {

	/**
	 * analyze logic for single app
	 * 
	 * @return
	 */
	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}
		ATGModel model = Global.v().getGatorModel().getGatorAtgModel();
		model.setATGFilePath(ConstantUtils.GATORFOLDETR + Global.v().getAppModel().getAppName() + "_wtg.txt");
		ATGReader reader = new ATGReader(model);
		if(reader.obtainATGfromFile()){
			reader.constructModelForGator();
		}
		System.out.println("Successfully analyze with GatorGraphClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		AppModel appModel = Global.v().getAppModel();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.GATORFOLDETR);

		FileUtils.copyFile(ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.txt", summary_app_dir
				+ ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.txt");
		GatorModel model = Global.v().getGatorModel();
		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_GATOR;
		GatorClientOutput.writeDotFileofGator(summary_app_dir + ConstantUtils.GATORFOLDETR, dotname, model.getGatorAtgModel());
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.GATORFOLDETR + dotname, "pdf");
		
//		
//		FileUtils.copyFile(ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.dot", summary_app_dir
//				+ ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.dot");
//		FileUtils.copyFile(ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.pdf", summary_app_dir
//				+ ConstantUtils.GATORFOLDETR + appModel.getAppName() + "_wtg.pdf");
	}

}