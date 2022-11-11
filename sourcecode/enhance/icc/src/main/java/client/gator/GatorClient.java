package main.java.client.gator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import brut.common.BrutException;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;

public class GatorClient  extends BaseClient {
	String gatorTmpFolder;
	PrintStream out = null;
	@Override
	protected void clientAnalyze() {
		//only linux is allowed for tool Gator
		String OS = System.getProperty("os.name").toLowerCase();
	    if (OS.indexOf("win") >= 0) {
	      throw new RuntimeException("Only Linux is allowed for tool Gator!");
	    }
	    
		gatorTmpFolder = "gator_tmp";
		//invoke apktool
		invokeApkTool();
		invokeGator();
		
	}

	private void invokeApkTool() {
		String[] args= {"d","-f", "-o", gatorTmpFolder, Global.v().getAppModel().getAppPath()};
		try {
			brut.apktool.Main.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrutException e) {
			e.printStackTrace();
		}
	}
	

	private void invokeGator() {
		addShutdownOperation();
		String[] clearCmd = {"rm", "/tmp/"+Global.v().getAppModel().getAppName()+"-*.xml"};
		runProcess(clearCmd);
		String[] args = {
//				   "java","-cp", "lib/sootandroid-1.0-SNAPSHOT-all.jar", "presto.android.Main",
				   "-sootandroidDir", "lib",
		           "-sdkDir", "lib",
		           "-listenerSpecFile", "data/listeners.xml",
		           "-wtgSpecFile", "data/wtg.xml",
		           "-resourcePath", gatorTmpFolder+"/res",
		           "-manifestFile", gatorTmpFolder+"/AndroidManifest.xml",
		           "-project", Global.v().getAppModel().getAppPath(),
		           "-apiLevel", "android-1",
		           "-guiAnalysis",
		           "-benchmarkName", Global.v().getAppModel().getAppName(),
		           "-libraryPackageListFile", "data/libPackages.txt",
		           "-android", "lib/platforms/android-1/android.jar",
		           "-client", MyConfig.getInstance().getGatorClient()};
//		presto.android.Main.main(args);
		
	}

	/**
	 * //because gator always invoke system.exit, add output
	 */
	private void addShutdownOperation() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				FileUtils.delAllFile(gatorTmpFolder);
				System.out.println("addShutdownHook");
				clientOutput();
			}
		});	 
	}

	@Override
	public void clientOutput(){
		String client =MyConfig.getInstance().getGatorClient();
		switch (client) {
		case "GUIHierarchyPrinterClient": 
			String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
					+ File.separator;
			String gatorDir = summary_app_dir + ConstantUtils.GATORFOLDER+client+File.separator;
			FileUtils.createFolder(gatorDir);
			String copyCmd[] = {"mv", "/tmp/"+Global.v().getAppModel().getAppName()+"-*.xml", gatorDir+Global.v().getAppModel().getAppName()+"-"+client+".xml"};
			runProcess(copyCmd);
		default:
			break;
		}
		
	}
	public void runProcess(String[] cmd) {
		try {
			String cmds[]  = {"/bin/sh","-c",String.join(" ", cmd)};
			Process pb = Runtime.getRuntime().exec(cmds);
			printLines(pb.getInputStream());
			printLines(pb.getErrorStream());
			pb.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void printLines(InputStream in) {
		String line = null;
		BufferedReader br  = new BufferedReader(new InputStreamReader(in));
		try {
			while((line=br.readLine())!=null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
