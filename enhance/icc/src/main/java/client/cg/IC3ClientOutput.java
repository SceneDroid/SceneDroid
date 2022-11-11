package main.java.client.cg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class IC3ClientOutput {

	public static void writeDotFileofIC3(String dir, String file, ATGModel atgModel) {
		FileUtils.createFolder(dir);
		Set<String> histroy = new HashSet<String>();
		File f = new File(dir + file + ".dot");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			if (atgModel.getAtgEdges() == null)
				return;
			writer.write("digraph G {\n");
			String aColor = "red";
			String sColor = "royalblue";
			String rColor = "peru";
			String pColor = "violet";
			String fColor = "teal";
			String bgColor = "mintcream";

			String subgraphString = "subgraph cluster_legend{ \nbgcolor=" + bgColor + ";\n";
			subgraphString += "_Activity[color = " + aColor + "];\n";
			subgraphString += "_Service[color = " + sColor + "];\n";
			subgraphString += "_Provider[color = " + pColor + "];\n";
			subgraphString += "_Reciever[color = " + rColor + "];\n";
			subgraphString += "_Fragment[color = " + fColor + "];\n";
			subgraphString += "_Main[style=filled, fillcolor=orange, color = " + bgColor + "];\n";
			subgraphString += "_Exported[style=filled, fillcolor=lightpink, color = " + bgColor + "];\n";
			writer.write(subgraphString
					+ "_Main -> _Exported -> _Activity -> _Service -> _Reciever -> _Provider -> _Fragment; \n}\n");

			for (String component : Global.v().getAppModel().getActivityMap().keySet()) {
				writeWithColor(writer, component, aColor);
			}
			for (String component : Global.v().getAppModel().getServiceMap().keySet()) {
				writeWithColor(writer, component, sColor);
			}
			for (String component : Global.v().getAppModel().getRecieverMap().keySet()) {
				writeWithColor(writer, component, rColor);
			}
			for (String component : Global.v().getAppModel().getProviderMap().keySet()) {
				writeWithColor(writer, component, pColor);
			}

			for (Entry<String, Set<AtgEdge>> en : atgModel.getAtgEdges().entrySet()) {
				Set<AtgEdge> resList = en.getValue();
				for (AtgEdge edge : resList) {
					String s = edge.getSource().getClassName();

					String e = edge.getDestnation().getClassName();

					String endString = ";\n";

					String edgeStr = s + "->" + e + endString;

					if (!histroy.contains(edgeStr)) {
						writer.write(edgeStr);
						histroy.add(edgeStr);
					}
				}
			}
			writer.write("}\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void writeWithColor(BufferedWriter writer, String component, String color) throws IOException {
		String res = component.split("\\.")[component.split("\\.").length - 1];
		res = res.split("@")[0];
		res = AtgNode.getClassName(res);
		if (component.equals(Global.v().getAppModel().getMainActivity()))
			writer.write(res + "[style=filled, fillcolor=orange, color = " + color + "];\n");
		else if (Global.v().getAppModel().getExportedComponentMap().containsKey(component))
			writer.write(res + "[style=filled, fillcolor=pink, color = " + color + "];\n");
		else
			writer.write(res + "[color = " + color + "];\n");
	}

	public static void writeIccLinksConfigFile(String dir, String linkfile, ATGModel aAtgModel) {
		FileUtils.createFolder(dir);
		File f = new File(dir + linkfile);
		BufferedWriter writer = null;
		Set<String> histroy = new HashSet<String>();
		try {
			writer = new BufferedWriter(new FileWriter(f));
			if (aAtgModel == null)
				return;
			for (Entry<String, Set<AtgEdge>> en : aAtgModel.getAtgEdges().entrySet()) {
				Set<AtgEdge> resList = en.getValue();
				for (AtgEdge edge : resList) {
					String edgeStr = "";
					// edu.mit.icc_componentname_class_constant:
					// <edu.mit.icc_componentname_class_constant.OutFlowActivity:
					// void onCreate(android.os.Bundle)> [19-a]
					// edu.mit.icc_componentname_class_constant.InFlowActivity
					// {19}
					String pkg = Global.v().getAppModel().getPackageName();
					String method = edge.getMethodSig();
					String instuction = edge.getInstructionId() + "-" + edge.getiCCkind();
					String dest = edge.getDestnation().getClassName();
					String instructions = Global.v().getAppModel().getMethod2InstructionMap().get(method);
					if (instructions != null) {
						edgeStr = pkg + ": " + method + " [" + instuction + "] " + dest + " {" + instructions + "}\n";
						if (!histroy.contains(edgeStr)) {
							writer.write(edgeStr);
							histroy.add(edgeStr);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
