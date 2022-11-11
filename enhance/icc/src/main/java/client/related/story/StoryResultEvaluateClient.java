package main.java.client.related.story;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.GraphUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.related.story.model.StoryModel;
import main.java.client.soot.SootAnalyzer;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class StoryResultEvaluateClient extends BaseClient {

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

		StoryReader story = new StoryReader(result);
		story.analyze();
		System.out.println("Successfully analyze with StoryClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		StoryClientOutput outer = new StoryClientOutput(this.result);
		StoryModel model = Global.v().getStoryModel();
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.STORYFOLDETR);

		String dotname = Global.v().getAppModel().getAppName() + "_" + ConstantUtils.ATGDOT_STORY;
		StoryClientOutput.writeDotFile(summary_app_dir + ConstantUtils.STORYFOLDETR, dotname, model.getStoryAtgModelWithoutFrag());
		GraphUtils.generateDotFile(summary_app_dir + ConstantUtils.STORYFOLDETR + dotname, "pdf");
		FileUtils.copyFile(model.getStoryFilePath(), summary_app_dir + ConstantUtils.STORYFOLDETR
				+ Global.v().getAppModel().getAppName() + ".txt");

		
		/** Intent **/
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.STORYFOLDETR, ConstantUtils.SINGLEOBJECT_ENTRY, true);
		outer.writeIntentSummaryModel(summary_app_dir + ConstantUtils.STORYFOLDETR, ConstantUtils.SINGLEOBJECT_ALL, false);
	}

}