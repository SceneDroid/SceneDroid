package main.java.client.manifest;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.soot.SootAnalyzer;
import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class ManifestClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
			SootAnalyzer sootAnalyzer = new SootAnalyzer();
			sootAnalyzer.analyze();
		}
		MainfestAnalyzer mainfestAnalyzer = new MainfestAnalyzer();
		mainfestAnalyzer.analyze();

		System.out.println("Successfully analyze with ManifestClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.MANIFOLDETR);

		/** manifest **/
		ManifestClientOutput.writeManifest(summary_app_dir + ConstantUtils.MANIFOLDETR, ConstantUtils.MANIFEST);
		// String content = Global.v().getAppModel().getAppName() + "\t" +
		// Global.v().getAppModel().getPackageName()
		// + "\t" + Global.v().getAppModel().getComponentMap().size() + "\t"
		// + Global.v().getAppModel().getExportedComponentMap().size() + "\t"
		// + Global.v().getAppModel().getActivityMap().size() + "\n";
		// FileUtils.writeText2File(MyConfig.getInstance().getResultFolder() +
		// "componentNumber.txt", content, true);

	}

}