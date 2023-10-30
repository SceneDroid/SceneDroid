package main.java.client.toolEvaluate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Map.Entry;

//import jymbolic.android.resources.controls.ProcessManifest;
//import jymbolic.android.resources.xml.AXmlAttribute;
//import jymbolic.android.resources.xml.AXmlNode;
//import jymbolic.android.resources.xml.parser.AXmlHandler;
import main.java.Analyzer;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.component.BackStack;

public class dynamicResultAnalyzer extends Analyzer {
	private String appName;
	private String instrumentedFolder;
	private String oracleFolder;
	private String oracleLogPath;

	public dynamicResultAnalyzer() {
		super();
		this.appName = appModel.getAppName();
		this.instrumentedFolder = MyConfig.getInstance().getResultFolder() + appName + File.separator
				+ ConstantUtils.INSTRUFOLDER;
		this.oracleFolder = MyConfig.getInstance().getResultFolder() + appName + File.separator
				+ ConstantUtils.ORACLEFOLDETR;
		this.oracleLogPath = oracleFolder + appName + ConstantUtils.ORACLEDYNA;
	}

	@Override
	public void analyze() {
		ATGModel dyIctgModel = new ATGModel();
		if (FileUtils.isFileExist(oracleLogPath)) {
			dyIctgModel = constructFromExistingModel();
			Global.v().getiCTGModel().setDynamicModel(dyIctgModel);
		} else {
			FileUtils.createFolder(instrumentedFolder);
			getCoverage();
			constructFromLog(dyIctgModel, instrumentedFolder + appName + "_log_0.txt");
			constructFromLog(dyIctgModel, instrumentedFolder + appName + "_log_1.txt");
			constructFromLog(dyIctgModel, instrumentedFolder + appName + "_log_2.txt");
			constructFromLog(dyIctgModel, instrumentedFolder + appName + "_log_manual.txt");
			Global.v().getiCTGModel().setDynamicModel(dyIctgModel);
			writeDynaOracleFile(MyConfig.getInstance().getResultFolder() + appName + File.separator, appName);
		}
	}

	private void getCoverage() {
		String mehtodList = MyConfig.getInstance().getAppPath() + Global.v().getAppModel().getAppName()
				+ "_ins_info.txt";
		List<String> methodList = FileUtils.getListFromFile(mehtodList);
		Set<String> methodSet = new HashSet<String>(methodList);
		Set<String> coveredSet = new HashSet<String>();
		getCoveredMethod(coveredSet, instrumentedFolder + appName + "_log_0.txt");
		getCoveredMethod(coveredSet, instrumentedFolder + appName + "_log_1.txt");
		getCoveredMethod(coveredSet, instrumentedFolder + appName + "_log_2.txt");
		getCoveredMethod(coveredSet, instrumentedFolder + appName + "_log_manual.txt");
		double res = 1.0 * coveredSet.size() / methodSet.size();
		System.out.println(coveredSet.size() + "\t" + methodSet.size() + "\t" + res);
		FileUtils.writeText2File(MyConfig.getInstance().getResultWarpperFolder() + File.separator + "covrageResult.txt", Global.v().getAppModel()
				.getAppName()
				+ "\t" + coveredSet.size() + "\t" + methodSet.size() + "\t" + res + "\n", true);
	}

	private void getCoveredMethod(Set<String> coveredSet, String fn) {
		List<String> logList = FileUtils.getListFromFile(fn);
		for (String m : logList) {
			if (m.contains("M_ICCTAG: ")) {
				String method = m.split("M_ICCTAG: ")[1];
				coveredSet.add(method);
			}
		}
	}

	/**
	 * write CSipSimple_dynamic.txt
	 * 
	 * @param summary_app_dir
	 * @param appName
	 */
	private void writeDynaOracleFile(String summary_app_dir, String appName) {
		Set<String> oracleEdges = new HashSet<String>();
		String content = "## dynamic edges\n";
		for (Entry<String, Set<AtgEdge>> entry : Global.v().getiCTGModel().getDynamicModel().getAtgEdges().entrySet()) {
			for (AtgEdge oracleEdge : entry.getValue()) {
				if (!oracleEdges.contains(oracleEdge.getDescribtion())) {
					oracleEdges.add(oracleEdge.getDescribtion());
					content += oracleEdge.getDescribtion() + "\n";
				}
			}
		}
		String dynaOracle = summary_app_dir + ConstantUtils.ORACLEFOLDETR + appName + ConstantUtils.ORACLEDYNA;
		FileUtils.createFolder(summary_app_dir + ConstantUtils.ORACLEFOLDETR);
		FileUtils.writeText2File(dynaOracle, content, false);

	}

	private ATGModel constructFromExistingModel() {
		ATGModel model = new ATGModel();
		FileUtils.createFolder(oracleFolder);
		List<String> logs = FileUtils.getListFromFile(oracleLogPath);
		String spliter = " --> ";
		String comment = "##";
		for (String line : logs) {
			if (line.contains(spliter) && !line.contains(comment)) {
				String content = line.trim();
				String classNameS = content.split(spliter)[0];
				String classTypeS = SootUtils.getTypeofClassName(classNameS);

				String classNameT = content.split(spliter)[1];
				String classTypeT = SootUtils.getTypeofClassName(classNameT);

				if (classTypeS.equals("other") || classTypeT.equals("other"))
					continue;
				if (classTypeS.equals("fragment") || classTypeS.equals("other"))
					continue;
				if (classTypeS.equals("provider") || classTypeS.equals("other"))
					continue;

				AtgEdge edge = new AtgEdge(new AtgNode(classNameS), new AtgNode(classNameT), "", -1, "");
				model.addAtgEdges(classNameS, edge);
			}
		}
		return model;
	}

	private void constructFromLog(ATGModel dyIctgModel, String instrumentedLogPath) {
		int x = 1;
		System.out.println(instrumentedLogPath + " is handling...");
		List<String> history = new ArrayList<String>();
		BackStack bs = new BackStack();

		List<String> logs = preprocessLogs(instrumentedLogPath);
		for (String line : logs) {
			if (line.contains(appModel.getMainActivity().replace(".", "/") + ";---><init>()V")) {
				bs.getBackStack().clear();
			}
			if (line.contains(appModel.getMainActivity().replace(".", "/") + ";--->onCreate(Landroid/os/Bundle;)V")) {
				bs.getBackStack().clear();
			}
			if (line.contains("M_ICCTAG: ")) {
				String content = line.split("M_ICCTAG: ")[1];
				String className = content.substring(1, content.length()).split(";--->")[0].replace("/", ".");
				String classType = SootUtils.getTypeofClassName(className);
				if (classType.equals("other")) {
					className = SootUtils.getNameofClass(className);
					classType = SootUtils.getTypeofClassName(className);
				}
				String methodName = content.split(";--->")[1].split("\\(")[0];
				if (classType.equals("other"))
					continue;
				if (classType.equals("fragment"))
					continue;
				if (classType.equals("provider"))
					continue;
				if (!SootUtils.isCallBackMethods(methodName)) {
					continue;
				}
				if (x == 0)
					System.out.println(className + " " + methodName);

				String lastClass = "";
				boolean newEdge = false;
				if (classType.equals("service") && methodName.equals("onCreate")) {
					newEdge = true;
					if (bs.getBackStack().empty())
						continue;
					lastClass = bs.getBackStack().peek().getO1();
				} else if (classType.equals("receiver") && methodName.equals("onReceive")) {
					newEdge = true;
					if (bs.getBackStack().empty())
						continue;
					lastClass = bs.getBackStack().peek().getO1();
				} else if (classType.equals("activity")) {
					newEdge = bs.addComponent(className, methodName);
					lastClass = bs.getTopKey(2);
				}
				if (newEdge) { // && !history.contains(className+" " +lastClass)
					AtgEdge edge = new AtgEdge(new AtgNode(lastClass), new AtgNode(className), methodName, -1,
							classType);
					boolean add = dyIctgModel.addAtgEdges(lastClass, edge);
					if (add && x == 0) {
						System.out.println(bs.toString());
						System.out.println(lastClass + "----" + className + "\n");

					}
					history.add(lastClass + " " + className);
				}

			} else if (line.contains("K_ICCTAG: ")) {
				bs.getBackStack().clear();
			} else {
				bs.getBackStack().clear();
			}
		}
	}

	private List<String> preprocessLogs(String instrumentedLogPath) {
		List<String> res = new ArrayList<String>();
		Map<String, Set<String>> cgMap = SootUtils.getCgMapWithSameName();
		String lastMethod = "";
		String thisMethod = "";
		String father = "";
		String son = "";
		List<String> lines = FileUtils.getListFromFile(instrumentedLogPath);
		for (String line : lines) {
			if (line.contains("M_ICCTAG: ")) {
				String content = line.split("M_ICCTAG: ")[1];
				String className = content.substring(1, content.length()).split(";--->")[0].replace("/", ".");
				String classType = SootUtils.getTypeofClassName(className);
				if (classType.equals("other")) {
					className = SootUtils.getNameofClass(className);
					classType = SootUtils.getTypeofClassName(className);
				}
				if (!className.equals(father) && !className.equals(son)) {
					father = "";
					son = "";
				}
				String methodName = content.split(";--->")[1].split("\\(")[0];
				thisMethod = className + " " + methodName;
				if (cgMap.containsKey(thisMethod) && cgMap.get(thisMethod).contains(lastMethod)) {
					// System.err.println(lastMethod+ " --> " +thisMethod);
					father = lastMethod.split(" ")[0];
					son = className;
					String temp = res.get(res.size() - 1);
					res.remove(res.size() - 1);
					String line2 = temp.replace(father.replace(".", "/"), son.replace(".", "/"));
					res.add(line2);
				}
				if (className.equals(father)) {
					line = line.replace(father.replace(".", "/"), son.replace(".", "/"));
				}
				lastMethod = thisMethod;
			}
			res.add(line);
		}
		return res;

	}

}
