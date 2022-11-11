package main.java.client.statistic;

import java.io.File;
import java.io.IOException;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;
import main.java.client.obj.target.ctg.CTGClient;
import main.java.client.statistic.StatisticClient;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class StatisticClient extends BaseClient {

	/**
	 * analyze logic for single app
	 * 
	 * @return
	 */
	@Override
	protected void clientAnalyze() {
		CTGClient client = new CTGClient();
		result = client.getResult();
		client.start();
		System.out.println("Successfully analyze with StatisticClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.STATFOLDETR);

		StatisticClienOutput outer = new StatisticClienOutput(this.result);

		/** statistic **/
		outer.writeSatisticModel(summary_app_dir + ConstantUtils.STATFOLDETR, ConstantUtils.STATISTIC, false);
		outer.writeSatisticModel(MyConfig.getInstance().getResultFolder(), ConstantUtils.STATISTIC, true);
	}

}