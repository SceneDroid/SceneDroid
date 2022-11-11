package main.java.client.soot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import main.java.Analyzer;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;

public class SootAnalyzer extends Analyzer {
	public SootAnalyzer() {
		super();
	}

	/**
	 * analyze using soot 1) set setActiveBody 2) def-use analyze
	 */
	@Override
	public void analyze() {
		if (appModel.getAppName() == null)
			return;
		sootInit();
		sootTransform();
		sootEnd();
		if (Global.v().getAppModel().getApplicationClassNames().size() == 0) {
			for (SootClass sc : Scene.v().getApplicationClasses()) {
				Global.v().getAppModel().addApplicationClassNames(sc.getName());
			}
			System.out.println("Soot Analysis finish.");
		}
	}

	/**
	 * initialize soot
	 */
	public static void sootInit() {
		soot.G.reset();
		Options.v().set_android_jars(MyConfig.getInstance().getAndroidJar());
		Options.v().set_process_dir(Collections.singletonList(Global.v().getAppModel().getAppPath()));
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_process_multiple_dex(true);
		if (MyConfig.getInstance().isJimple())
			Options.v().set_output_format(Options.output_format_jimple);
		else
			Options.v().set_output_format(Options.output_format_shimple);
		String out = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName() + File.separator
				+ ConstantUtils.SOOTOUTPUT;
		Options.v().set_output_dir(out);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().allow_phantom_refs();
		Options.v().set_whole_program(true);
		setExcludePackage();

	}

	/**
	 * add transforms for analyzing
	 */
	private void sootTransform() {
		String pack = "jtp";
		if (!MyConfig.getInstance().isJimple())
			pack = "stp";
		// set setActiveBody
		ActiveBodyTransformer abTran = new ActiveBodyTransformer();
		Transform t1 = new Transform(pack + ".bt", abTran);
		PackManager.v().getPack(pack).add(t1);
	}

	/**
	 * end setting of soot
	 */
	public static void sootEnd() {
		soot.Main.v().autoSetOptions();
		Scene.v().loadNecessaryClasses();
		Scene.v().loadBasicClasses();
		PackManager.v().runPacks();
	}

	/**
	 * packages refuse to be analyzed
	 */
	public static void setExcludePackage() {
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add("android.*");
		excludeList.add("androidx.*");
		excludeList.add("kotlin.*");
		excludeList.add("com.google.*");
		excludeList.add("soot.*");
		excludeList.add("junit.*");
		excludeList.add("java.*");
		excludeList.add("javax.*");
		excludeList.add("sun.*");
		excludeList.add("org.apache.*");
		excludeList.add("org.eclipse.*");
		excludeList.add("org.junit.*");
		excludeList.add("com.fasterxml.*");
		Options.v().set_exclude(excludeList);
	}
}
