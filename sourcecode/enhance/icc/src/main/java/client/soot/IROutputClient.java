package main.java.client.soot;

import main.java.MyConfig;
import main.java.client.BaseClient;
import main.java.client.soot.IROutputClient;
import soot.PackManager;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class IROutputClient extends BaseClient {

	@Override
	protected void clientAnalyze() {
		if (!MyConfig.getInstance().isSootAnalyzeFinish()) {
			SootAnalyzer sootAnalyzer = new SootAnalyzer();
			sootAnalyzer.analyze();
		}

		System.out.println("Successfully analyze with IROutputClient.");
	}

	@Override
	public void clientOutput() {
		PackManager.v().writeOutput();
	}

}