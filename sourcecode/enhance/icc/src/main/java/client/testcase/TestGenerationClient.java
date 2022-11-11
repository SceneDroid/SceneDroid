package main.java.client.testcase;

import java.io.IOException;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.SummaryLevel;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.client.obj.model.ctg.ICCMsg;
import main.java.client.obj.target.ctg.CTGClient;
import main.java.client.testcase.TestGenerationClient;

import org.dom4j.DocumentException;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class TestGenerationClient extends CTGClient {
	AppModel appModel = Global.v().getAppModel();
	
	@Override
	protected void setMySwitch() {
		MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setSetAttributeStrategy(true);
		MyConfig.getInstance().getMySwithch().setFragmentSwitch(false);
		MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.object);
		MyConfig.getInstance().getMySwithch().setDummyMainSwitch(true);
	}
	
	@Override
	protected void clientAnalyze() {
		super.clientAnalyze();
		
		System.out.println("Start Test Generation...");
		TestGenerationProcess tg = new TestGenerationProcess();
		tg.init();
		tg.createAndroidProject();

		// className : act to be analyzed
		for (String className : Global.v().getAppModel().getComponentMap().keySet()) {
			ICCGenerator generator = new ICCGenerator(className);
			generator.analyze();
			Set<ICCMsg> ICCs = generator.getICCSet();
			if (ICCs != null && ICCs.size() != 0) {
				tg.handleICCMsgs(ICCs, className);
			}
		}
		for(String key: appModel.getICCStringMap().keySet()){
			System.out.println("\nComponent: " +key);
			for(String ICC: appModel.getICCStringMap().get(key)){
				if(ICC.equals(""))
					ICC = "all null value";
				System.out.println("ICC: " +ICC);
			}
		}
		
		tg.generateManifest();
		tg.buildProject();

		System.out.println("End Test Generation...");
		System.out.println("Successfully analyze with TestGenerationClient.");
	}


	
	@Override
	public void clientOutput() throws IOException, DocumentException {
		super.clientOutput();
		
		
	}

 }