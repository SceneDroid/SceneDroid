package main.java.client.cg;

import java.io.File;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.manifest.ManifestClient;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class CallGraphClient extends BaseClient {

	@Override
	protected void clientAnalyze() {

		if (!MyConfig.getInstance().isManifestAnalyzeFinish()) {
			new ManifestClient().start();
			MyConfig.getInstance().setManifestAnalyzeFinish(true);
		}

		CgConstructor cgAnalyzer = new CgConstructor();
		cgAnalyzer.analyze();
		System.out.println("Call Graph Construction finish.");
		CgModify cgModify = new CgModify();
		cgModify.analyze();
		System.out.println("Call Graph Enhancing finish.");

		System.out.println("Successfully analyze with CallGraphClient.");

	}

	@Override
	public void clientOutput() {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.CGFOLDETR);

		/** call graph **/
		CgClientOutput.writeCG(summary_app_dir + ConstantUtils.CGFOLDETR, 
				ConstantUtils.CG, Global.v().getAppModel().getCg());
		CgClientOutput.writeCGToString(summary_app_dir + ConstantUtils.CGFOLDETR, 
				Global.v().getAppModel().getAppName()+"_cg.txt", Global.v().getAppModel().getCg());
		CgClientOutput.writeTopoMethodFile(summary_app_dir + ConstantUtils.CGFOLDETR, ConstantUtils.TOPO, Global.v()
				.getAppModel().getTopoMethodQueue());
	}

}