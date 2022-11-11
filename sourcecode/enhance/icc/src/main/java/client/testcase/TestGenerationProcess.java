package main.java.client.testcase;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.AppModel;
import main.java.analyze.utils.AndroidUtils;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.StringUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.ctg.ICCMsg;

/**
 * This test class shows how to use the API interface of ACTS to build a test
 * set.
 *
 */
public class TestGenerationProcess {
	private AppModel appModel;
	private int appId;
	private String iccMsg;
	private String projectPath;
	private String pro_name;

	public TestGenerationProcess() {
		this.appModel = Global.v().getAppModel();
		this.appId = 0;
		String folder = MyConfig.getInstance().getResultFolder() + ConstantUtils.TESTCASEFOLDER + appModel.getAppName()
				+ File.separator;
		this.iccMsg = folder + ConstantUtils.ICCMSGFILE;
		this.pro_name = StringUtils.getProjectName(appModel.getAppName());
		this.projectPath = folder + ConstantUtils.APKTESTCASEFOLDER + pro_name;
	}

	/**
	 * step1: create Android Project as test case
	 */
	public void createAndroidProject() {
		System.out.println(ConstantUtils.CREATEPROJECT + projectPath);
		FileUtils.createFolder(projectPath);
		AndroidUtils.creatProject(appModel.getPackageName(), projectPath, pro_name);
		String srcf = ConstantUtils.SRC + File.separator + ConstantUtils.APKFLAGPREFIX + appModel.getPackageName()
				+ ConstantUtils.APKFLAGSUFFIX + File.separator;
		MyConfig.getInstance().setSrcFolder(
				srcf.replace(".", File.separator).replace(File.separator + File.separator, File.separator));
		copySerFiles2Project(projectPath);
	}

	/**
	 * step2: handle ICC Msgs
	 * 
	 * @param iCCs
	 * @param className
	 */
	public void handleICCMsgs(Set<ICCMsg> iCCs, String className) {
		Set<String> ACDTStr = new HashSet<String>();
		for (ICCMsg msg : iCCs) {
			String atdtPrefix = "";
			if (msg.getAction() != null && !msg.getAction().equals("") && !msg.getAction().equals("\"\""))
				atdtPrefix += msg.getAction() + ";;";
			else
				atdtPrefix += "null;;";

			if (msg.getCategory() != null && msg.getCategory().size() > 0) {
				for (String c : msg.getCategory()) {
					atdtPrefix += c;
					break;
				}
			} else{
				atdtPrefix += "null";
			}
			atdtPrefix += ";;";

			if (msg.getData() != null && !msg.getData().equals("") && !msg.getData().equals("\"\""))
				atdtPrefix += msg.getData();
			else if (msg.getPath() != null || msg.getPort() != null || msg.getScheme() != null || msg.getHost() != null)
				atdtPrefix += "\"" + StringUtils.refineString(msg.getScheme()) + "://"
						+ StringUtils.refineString(msg.getHost()) + ":" + StringUtils.refineString(msg.getPort()) + "/"
						+ StringUtils.refineString(msg.getPath()) + "\"";
			else
				atdtPrefix += "null";
			atdtPrefix += ";;";

			if (msg.getType() != null && !msg.getType().equals("") && !msg.getType().equals("\"\""))
				atdtPrefix += msg.getType() + ";;";
			else
				atdtPrefix += "null;;";
			ACDTStr.add(atdtPrefix);

			String extraSuffix = "";
			if (msg.getExtra() != null) {
				String[] extra_pairs = PrintUtils.printSet(msg.getExtra()).replace(" ", "").split(",");
				for (String extra_pair : extra_pairs) {
					if (extra_pair.length() == 0)
						continue;
					String[] ss = extra_pair.split("-");
					String extra_type = ss[0];
					if (ss.length > 1 || extra_type.equals("Extras")) {
						String extra_key = "";
						if (extra_type.equals("Extras"))
							extra_key = extra_type + "Obj";
						else
							extra_key = StringUtils.refineString(extra_pair.split("-")[1]);
						if (extra_key.equals(""))
							extra_key = ConstantUtils.UNKOWN;
						extraSuffix += handleExtraAccordingToTypeAbnormal(extra_type, extra_key);
					} else {
						extraSuffix += extra_pair + ",";
					}
				}
				ACDTStr.add(atdtPrefix + extraSuffix);
				if (extraSuffix.length() > 0) {
					for (int i = 0; i < extraSuffix.split(",").length; i++) {
						String tempSuffix = "";
						for (int j = 0; j < extraSuffix.split(",").length; j++) {
							String extra = extraSuffix.split(",")[j];
							if (extra.split("-").length > 1 && !extra.split("-")[0].equals("Extras"))
								if (j == i)
									continue;
							tempSuffix += extraSuffix.split(",")[j] + ",";
						}
						ACDTStr.add(atdtPrefix + tempSuffix);
					}
				}
			}
		}
		Iterator<String> it2 = ACDTStr.iterator();
		while (it2.hasNext()) {
			String acdt = it2.next();
			genJavaFile(projectPath + File.separator + MyConfig.getInstance().getSrcFolder() + File.separator
					+ ConstantUtils.ACTIVITY + "_", null, className.replace("$", "_dollar_"), acdt);
		}
	}

	/**
	 * step3: generate Manifest file
	 */
	public void generateManifest() {
		String manifest_path = projectPath + File.separator + ConstantUtils.MANIFEST;

		List<String> lines = FileUtils.getListFromFile(manifest_path);
		if (lines == null)
			return;
		FileUtils.writeText2File(manifest_path, "", false);
		String content = "";
		for (String line : lines) {
			if (line.contains("@string/app_name"))
				line = line.replace("@string/app_name", appModel.getAppName() + "_Launcher");
			if (line.contains("</manifest>")) {
				if (appModel.getPermission().length() != 0)
					content += "\t<uses-permission android:name=\"" + appModel.getPermission() + "\"/>\n";
				for (ComponentModel component : appModel.getActivityMap().values()) {
					if (component.getPermission() != null && component.getPermission().length() > 0) {
						content += "\t<uses-permission android:name=\"" + component.getPermission() + "\"/>\n";
					}
				}
				for (ComponentModel component : appModel.getServiceMap().values()) {
					if (component.getPermission() != null && component.getPermission().length() > 0) {
						content += "\t<uses-permission android:name=\"" + component.getPermission() + "\"/>\n";
					}
				}
				for (ComponentModel component : appModel.getProviderMap().values()) {
					if (component.getPermission() != null && component.getPermission().length() > 0) {
						content += "\t<uses-permission android:name=\"" + component.getPermission() + "\"/>\n";
					}
				}
				for (ComponentModel component : appModel.getRecieverMap().values()) {
					if (component.getPermission() != null && component.getPermission().length() > 0) {
						content += "\t<uses-permission android:name=\"" + component.getPermission() + "\"/>\n";
					}
				}
			}
			if (line.contains("</application>")) {
				for (int i = 1; i <= appId; i++) {
					content += "\t\t<activity android:name=\"." + ConstantUtils.ACTIVITY + "_" + i
							+ "\" android:exported=\"true\"/>\n";
				}
			}
			content += line;
		}
		FileUtils.writeText2File(manifest_path, content + "\n", true);
	}

	/**
	 * step4: build Project
	 */
	public void buildProject() {
		try {
			AndroidUtils.buildApp(projectPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		moveGeneratedAPK(projectPath);// move app from bin to projectPath
	}

	/**
	 * copySerFiles2Project "MySerializable" "MyParcelable"
	 * 
	 * @param folder
	 */
	public void copySerFiles2Project(String folder) {

		String java_file_path = folder + File.separator + MyConfig.getInstance().getSrcFolder();

		String templateF = ConstantUtils.TEAMPLATEFOLDER + ConstantUtils.SERJAVA + ".java";
		String desF = java_file_path + ConstantUtils.SERJAVA + ".java";
		// Utils.copyFile(templateF, desF);
		List<String> lines = FileUtils.getListFromFile(templateF);
		FileUtils.writeText2File(desF, "package " + ConstantUtils.APKFLAGPREFIX + appModel.getPackageName()
				+ ConstantUtils.APKFLAGSUFFIX + ";\n", false);
		for (String line : lines)
			FileUtils.writeText2File(desF, line + "\n", true);

		templateF = ConstantUtils.TEAMPLATEFOLDER + ConstantUtils.PARJAVA + ".java";
		desF = java_file_path + ConstantUtils.PARJAVA + ".java";
		// Utils.copyFile(templateF, desF);
		List<String> lines2 = FileUtils.getListFromFile(templateF);
		FileUtils.writeText2File(desF, "package " + ConstantUtils.APKFLAGPREFIX + appModel.getPackageName()
				+ ConstantUtils.APKFLAGSUFFIX + ";\n", false);
		for (String line : lines2)
			FileUtils.writeText2File(desF, line + "\n", true);

		String layout_path = folder + File.separator + "res\\layout\\";

		templateF = ConstantUtils.TEAMPLATEFOLDER + ConstantUtils.LAYOUTMAIN;
		desF = layout_path + ConstantUtils.LAYOUTMAIN;

		List<String> lines3 = FileUtils.getListFromFile(templateF);
		FileUtils.writeText2File(desF, "", false);
		for (String line : lines3)
			FileUtils.writeText2File(desF, line + "\n", true);

	}

	/**
	 * gen java file
	 * 
	 * @param java_file_path
	 * @param icc
	 * @param clsname
	 * @param acdt
	 */
	public void genJavaFile(String java_file_path, ICCMsg icc, String clsname, String acdt) {
		HashSet<String> decSet = new HashSet<String>();
		String java_template_file_path = java_file_path + ".java";
		java_file_path = java_file_path + (++appId) + ".java";
		FileUtils.copyFile(java_template_file_path, java_file_path);

		FileUtils.writeText2File(iccMsg, appId + "\t" + clsname + "\t" + acdt + "\n", true);

		List<String> lines = FileUtils.getListFromFile(java_file_path);
		FileUtils.writeText2File(java_file_path, "", false);
		String content = "";
		boolean startLaunch = false;
		for (String line : lines) {
			if (line.contains("public class Activity_ extends Activity"))
				line = "public class Activity_" + appId + " extends Activity";

			FileUtils.writeText2File(java_file_path, line + "\n", true);
			// add after statement
			if (line.contains("import android.os.Bundle;")) {
				content = "import android.content.Intent;\n";
				content += "import java.util.List;\n";
				content += "import java.lang.reflect.Array;\n";
				content += "import android.util.Log;\n";
				content += "import android.content.ComponentName;\n";
				content += "import android.os.Parcelable;\n";
				content += "import java.io.Serializable;;\n";
				content += "import java.util.ArrayList;\n";
				content += "import android.net.Uri;\n";
				content += "import android.view.View;\n";
				content += "import android.view.View.OnClickListener;\n";
				content += "import android.widget.Button;\n";
				content += "import java.lang.reflect.Method;\n";
				content += "import android.app.Activity;\n";
				content += "import android.content.Context;\n";

				FileUtils.writeText2File(java_file_path, content, true);
			}
			if (line.equals("{") && !startLaunch) {
				startLaunch = true;
				content = "\tpublic void launch() throws Exception{\n";
				
				content += "\t\tClassLoader loader;\n";
				content += "\t\tContext invokee;\n";
				content += "\t\tClass<?> util;\n";
				content += "\t\tIntent mIntent = new Intent();\n";
				content += "\t\tmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);\n";
				content += "\t\tComponentName cn = new ComponentName(\"" + appModel.getPackageName() + "\",\""
						+ clsname.replace("_dollar_", "$") + "\");\n";
				content += "\t\tmIntent.setComponent(cn);\n";

				String[] acdtVals = acdt.split(";;");
				if (!acdtVals[0].equals("null")) {
					if (acdtVals[0].contains("\\") && !acdtVals[0].contains("\\\\"))
						acdtVals[0] = acdtVals[0].replace("\\", "");
					content += "\t\tmIntent.setAction(\"" + acdtVals[0].replace("\"", "") + "\");\n";
				}
				if (!acdtVals[1].equals("null"))
					content += "\t\tmIntent.addCategory(\"" + acdtVals[1].replace("\"", "") + "\");\n";
				if (!acdtVals[2].equals("null"))
					content += "\t\tmIntent.setData(Uri.parse(\"" + acdtVals[2].replace("\"", "") + "\"));\n";
				if (!acdtVals[3].equals("null"))
					content += "\t\tmIntent.setType(\"" + acdtVals[3].replace("\"", "") + "\");\n";
				FileUtils.writeText2File(java_file_path, content, true);
				
				// new Bundle must show up before its use
				// generate extra data

				if (acdt.split(";;").length == 5) {
					String extras = acdt.split(";;")[4];
//					if(extras.contains("ExtrasObj"))
//						System.out.println();
					generateExtrasInJava(decSet, extras, java_file_path, "Intent->mIntent");
				}
				content = "\t\tstartActivity(mIntent);\n";
				content += "\t\t//" + acdt + "\n";
				if (icc != null)
					content += "\t\t//" + icc.getExtra() + "\n";
				content += "\t}\n";
				FileUtils.writeText2File(java_file_path, content, true);
			}
			if (line.contains("setContentView(R.layout.main);")) {
				String launchStr = "\t\ttry {\n" + "\t\t\tlaunch();\n" + "\t\t} catch (Exception e) {\n"
						+ "\t\t\te.printStackTrace();\n" + "\t\t}\n";
				FileUtils.writeText2File(java_file_path, launchStr, true);

				content = "\t\tButton button1=(Button)findViewById(R.id.button1);\n";
				content += "\t\tbutton1.setOnClickListener(new OnClickListener() {\n";
				content += "\t\t\t@Override\n";
				content += "\t\t\tpublic void onClick(View v) {\n";
				String launchStr2 = "\t\t\t\ttry {\n" + "\t\t\t\t\tlaunch();\n" + "\t\t\t\t} catch (Exception e) {\n"
						+ "\t\t\t\t\te.printStackTrace();\n" + "\t\t\t\t}\n";
				content += launchStr2;
				content += "\t\t}});\n";

				FileUtils.writeText2File(java_file_path, content, true);
			}
		}

	}

	/**
	 * generate extra info in java file
	 * 
	 * @param decSet
	 * @param extras
	 * @param java_file_path
	 * @param objPair
	 */
	private void generateExtrasInJava(HashSet<String> decSet, String extras, String java_file_path, String objPair) {
		if (objPair.equals(""))
			return;
		String objName = StringUtils.refineString(objPair.split("->")[1]);
		Stack<String> extraSt = new Stack<String>();
		String[] extra_pairs = extras.split(",");
		for (String extra_pair : extra_pairs) {
			if (extra_pair.equals(")")) {
				String todoExtras = "";
				String top = extraSt.pop();
				while (!top.equals("(")) {
					todoExtras = top + "," + todoExtras;
					top = extraSt.pop();
				}
				String newObjPair = "";
				for (int i = extraSt.size() - 1; i >= 0; i--) {
					if (extraSt.get(i).contains("Bundle") || extraSt.get(i).contains("Extras")) {
						String ss[] = extraSt.get(i).split("->");
						if (ss.length > 1) {
							newObjPair = extraSt.get(i); // Utils.refineString(ss[1]);
							break;
						}
					}
				}
				generateExtrasInJava(decSet, todoExtras, java_file_path, newObjPair);
			} else {
				extraSt.push(extra_pair);
			}
		}
		while (!extraSt.empty()) {
			String topEd = extraSt.pop();
			// the container, e.g., Bundle b (int a); Bundle b = new Bundle();
			objectDeclaration(decSet, java_file_path, objPair);
			// the current obj, e.g, Parceable p;
			if (!topEd.contains("Bundle") && !topEd.contains("Extras"))
				objectDeclaration(decSet, java_file_path, topEd);
			handleSingleExtraData(topEd, java_file_path, objName);
		}
	}

	/**
	 * generate object declaration info in java file
	 * 
	 * @param decSet
	 * @param java_file_path
	 * @param extra_pair
	 */
	private void objectDeclaration(HashSet<String> decSet, String java_file_path, String extra_pair) {
		if (!extra_pair.contains("->"))
			return;
		String[] ss = extra_pair.split("->");
		String extra_type = ss[0];

		String extra_cls_type = "";
		if (ss.length > 2)
			extra_cls_type = "\"" + ss[2] + "\"";

		// decSet
		String content = "";
		if (extra_type.equals("Extras")) {
			content = "\t\tBundle ExtrasObj = new Bundle();\n";
			if (!decSet.contains(content)) {
				decSet.add(content);
			} else {
				content = "";
			}
			FileUtils.writeText2File(java_file_path, content, true);
		} else {
			String extra_key = StringUtils.refineString(ss[1]);
			if (extra_key.equals(""))
				return;
			extra_key = extra_key.replace(".", "_dot_");
			extra_key = extra_key.replace("-", "_line_");
			extra_key = extra_key.replace(":", "_maohao_");
			if (extra_type.equals("Bundle")) {
				content = "\t\tBundle " + extra_key + " = new Bundle();\n";
				if (!decSet.contains(content)) {
					decSet.add(content);
				} else {
					// content = "\t\t" + extra_key + " = new Bundle();\n";
					content = "";
				}
			} else if (extra_type.equals("Serializable") || extra_type.equals("Parcelable")) {
				if (extra_cls_type.equals("\"" + "SerializableObj" + "\"")) {
					content = "\t\tMySerializable " + extra_key + " = new MySerializable();\n";
					if (!decSet.contains(content)) {
						decSet.add(content);
					}
				}else if (extra_cls_type.equals("\"" + "android.content.Intent" + "\"")) {
					content = "\t\tIntent " + extra_key + " = new Intent();\n";
					if (!decSet.contains(content)) {
						decSet.add(content);
					}
				} else if (extra_cls_type.equals("\"" + "ParcelableObj" + "\"")) {
					content = "\t\tMyParcelable " + extra_key + " = new MyParcelable();\n";
					if (!decSet.contains(content)) {
						decSet.add(content);
					}
				} else {
					content = "\t\t" + "invokee = this.createPackageContext(\"" + appModel.getPackageName()
							+ "\"," + "Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);\n";
					content += "\t\t" + "loader = invokee.getClassLoader();\n";
					content += "\t\t" + "util = loader.loadClass(" + extra_cls_type + ");\n";
					content += "\t\tParcelable " + extra_key + " = (Parcelable)util.newInstance();\n";
					if (!decSet.contains(content)) {
						decSet.add(content);
					}
				}
			} else if (extra_type.contains("ArrayList")) {
				String type = extra_type.replace("ArrayList", "");
				if(type.length()>0)
					content = "\t\tArrayList<" + type + "> " + extra_key + " = new ArrayList<" + type + ">();\n";
				else
					content = "\t\tArrayList<?> " + extra_key + " = new ArrayList();\n";
				if (!decSet.contains(content)) {
					decSet.add(content);
				} else {
					content = "\t\t" + extra_key + " = new ArrayList<" + type + ">();\n";
				}
			} else if (extra_type.contains("Array") || extra_type.contains("[]")) {
				String type = extra_type.replace("Array", "").replace("[]", "");
				content = "\t\t" + type + "[] " + extra_key + " = new " + type + "[1];\n";
				if (!decSet.contains(content)) {
					decSet.add(content);
				} else {
					content = "\t\t" + extra_key + " = new " + type + "[1];\n";
				}
			}
			FileUtils.writeText2File(java_file_path, content, true);
		}
	}

	/**
	 * handle Single Extra Data
	 * 
	 * @param extra_pair
	 * @param java_file_path
	 * @param objName
	 */
	public void handleSingleExtraData(String extra_pair, String java_file_path, String objName) {
		objName = objName.replace(".", "_dot_");
		objName = objName.replace("-", "_line_");
		objName = objName.replace(":", "_maohao_");
		if (!extra_pair.contains("->"))
			return;
		String[] ss = extra_pair.split("->");
		String extra_type = ss[0];
		String content = "";
		if (extra_type.equals("Extras")) {
			content += "\t\tmIntent.putExtras(ExtrasObj);\n";
		} else {
			String extra_key = StringUtils.refineString(ss[1]);
			String extra_value = ss[2];
			// there is something wrong when param contains "."
			extra_key = extra_key.replace(".", "_dot_");
			extra_key = extra_key.replace("-", "_line_");
			extra_key = extra_key.replace(":", "_maohao_");
			// bundle api different with intent api
			String putAPI = getPutAPI(extra_type, objName);

			if (!extra_value.equals("null")) {
				if (extra_type.equals("String") || extra_type.equals("CharSequence") || extra_type.equals("Object"))
					extra_value = "\"" + extra_value + "\"";
				else if (extra_type.equals("Char") || extra_type.equals("char"))
					extra_value = "'" + extra_value + "'";

				if (!SootUtils.isStringType(extra_type)) {
					extra_value = extra_key;
				} else {
					if (extra_type.contains("ArrayList")) {
						content += "\t\t" + extra_key + ".add(" + extra_value + ");\n";
						extra_value = extra_key;
					} else if (extra_type.contains("Array")) {
						content += "\t\t" + extra_key + "[0] = " + extra_value + ";\n";
						extra_value = extra_key;
					} else if (extra_type.contains("[]")) {
						extra_value = extra_key;
					}
				}
				content += "\t\t" + objName + "." + putAPI + "(\""
						+ extra_key + "\", "
						+ extra_value + ");\n";
			}
		}
		FileUtils.writeText2File(java_file_path, content + "\n", true);
	}

	/**
	 * get Put API according to extra_type
	 * 
	 * @param extra_type
	 * @param objName
	 * @return
	 */
	public String getPutAPI(String extra_type, String objName) {
		String putAPI = null;
		if (SootUtils.isArrayListType(extra_type)) {
			String suffix = "";
			if (objName.equals("mIntent"))
				suffix = "Extra";
			if (extra_type.equals("IntegerArrayList"))
				putAPI = "putIntegerArrayList" + suffix;
			else if (extra_type.equals("ParcelableArrayList"))
				putAPI = "putParcelableArrayList" + suffix;
			else if (extra_type.equals("StringArrayList"))
				putAPI = "putStringArrayList" + suffix;
		} else {
			if (objName.equals("mIntent"))
				putAPI = "putExtra";
			else
				putAPI = "put" + extra_type.substring(0, 1).toUpperCase() + extra_type.substring(1);
		}
		putAPI = putAPI.replace("[]", "");
		return putAPI;
	}

	/**
	 * handle Extra According To Type Abnormal
	 * 
	 * @param extra_type
	 * @param extra_key
	 * @return
	 */
	private String handleExtraAccordingToTypeAbnormal(String extra_type, String extra_key) {
		String result = "";
		int n = new Random().nextInt(2);
		if (extra_type.equals("Int") || extra_type.equals("int")) {
			if (n == 0)
				result += extra_type + "->" + extra_key + "->Integer.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Integer.MIN_VALUE,";
		} else if (extra_type.equals("Float") || extra_type.equals("float")) {
			if (n == 1)
				result += extra_type + "->" + extra_key + "->Float.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Float.MIN_VALUE,";
		} else if (extra_type.equals("Double") || extra_type.equals("double")) {
			if (n == 0)
				result += extra_type + "->" + extra_key + "->Double.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Double.MIN_VALUE,";
		} else if (extra_type.equals("Short") || extra_type.equals("short")) {
			if (n == 1)
				result += extra_type + "->" + extra_key + "->Short.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Short.MIN_VALUE,";
		} else if (extra_type.equals("Long") || extra_type.equals("long")) {
			if (n == 0)
				result += extra_type + "->" + extra_key + "->Long.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Long.MIN_VALUE,";
		} else if (extra_type.equals("Byte") || extra_type.equals("byte")) {
			if (n == 1)
				result += extra_type + "->" + extra_key + "->Byte.MAX_VALUE,";
			else
				result += extra_type + "->" + extra_key + "->Byte.MIN_VALUE,";
		} else if (extra_type.equals("String") || extra_type.equals("CharSequence")) {
			if (n == 1)
				result += extra_type + "->" + extra_key + "->" + "!@#$%^ds:+_" + ",";
			else
				result += extra_type + "->" + extra_key + "->" + "999999999999999999999999999999999999999999999999999"
						+ ",";
		} else if (extra_type.equals("Char") || extra_type.equals("char")) {
			result += extra_type + "->" + extra_key + "->" + "\\\\" + ",";
		} else if (extra_type.equals("Boolean") || extra_type.equals("boolean")) {
			if (n == 1)
				result += extra_type + "->" + extra_key + "->" + "true" + ",";
			else
				result += extra_type + "->" + extra_key + "->" + "false" + ",";
		} else if (extra_type.contains("@")) {
			if (extra_type.contains("Serializable") && extra_type.split("@").length > 1)
				result += extra_type.split("@")[0] + "->" + extra_key + "->" + extra_type.split("@")[1] + ",";
			else if (extra_type.contains("Parcelable") && extra_type.split("@").length > 1)
				result += extra_type.split("@")[0] + "->" + extra_key + "->" + extra_type.split("@")[1] + ",";
			else
				result += extra_type.split("@")[0] + "->" + extra_key + "->" + extra_type + "Obj" + ",";
		} else {
			result += extra_type + "->" + extra_key + "->" + extra_type + "Obj" + ",";
		}
		return result;
	}

	/**
	 * move Generated APK
	 */
	public void moveGeneratedAPK(String projectPath) {
		String folder = MyConfig.getInstance().getResultFolder() + ConstantUtils.TESTCASEFOLDER + appModel.getAppName()
				+ File.separator + ConstantUtils.APKTESTCASEFOLDER;
		FileUtils.createFolder(folder);

		String oriPath = projectPath + File.separator + "bin" + File.separator + pro_name + "-debug.apk";
		String desPath = folder + "test.apk";
		FileUtils.moveFile(oriPath, desPath);
	}

	/**
	 * Initialize for test generation
	 */
	public void init() {
		String testCasefolder = MyConfig.getInstance().getResultFolder() + ConstantUtils.TESTCASEFOLDER
				+ appModel.getAppName() + File.separator;
		String appProjectFolder = MyConfig.getInstance().getResultFolder() + ConstantUtils.TESTCASEFOLDER
				+ appModel.getAppName() + File.separator + ConstantUtils.GENERATEDAPPFOLDER;

		FileUtils.createFolder(testCasefolder);
		FileUtils.delFolder(appProjectFolder);
		
	}

}
