package main.java.client.obj.target.ctg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.OracleUtils;
import main.java.client.BaseClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.target.ctg.CTGReaderClient;
import main.java.client.related.gator.ATGReader;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class CTGReaderClient extends BaseClient {

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
		
		ATGModel model = Global.v().getiCTGModel().getOptModelwithoutFrag();
		ATGReader reader = new ATGReader(model);
		String fn = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator + ConstantUtils.ICTGFOLDETR + Global.v().getAppModel().getAppName()+"_CTG.txt";
		model.setATGFilePath(fn);
		if(reader.obtainATGfromFile()){
			reader.constructModelForICCBot();
		}
		
		ATGModel model2 = Global.v().getiCTGModel().getOptModel();
		ATGReader reader2 = new ATGReader(model2);
		String fn2 = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator + ConstantUtils.ICTGFOLDETR + Global.v().getAppModel().getAppName()+"_CTGwithFragment.txt";
		if(new File(fn2).exists()){
			model2.setATGFilePath(fn2);
		}else {
			//the old version do not have txt file 
			model2.setATGFilePath(MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
					+ File.separator + ConstantUtils.ICTGFOLDETR + Global.v().getAppModel().getAppName()+"_CTGwithFragment.dot");
		}
		if(reader2.obtainATGfromFile()){
			reader2.constructModelForICCBot();
		}
		System.out.println("Successfully analyze with CTGReaderClient.");
	}

	@Override
	public void clientOutput() throws IOException, DocumentException {
		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()+ File.separator;
		List<String> resList = new ArrayList<String>();
		ATGModel model = Global.v().getiCTGModel().getOptModelwithoutFrag();
		for(Set<AtgEdge> edges : model.getAtgEdges().values()){
			for(AtgEdge e: edges){
				resList.add(e.getDescribtion());
			}
		}
		OracleUtils.writeOracleModel(summary_app_dir + ConstantUtils.ORACLEFOLDETR, 
				Global.v().getAppModel().getAppName() + ConstantUtils.ORACLETEXT, resList);
	}
}