package main.java.client.toolEvaluate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import main.java.Global;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class ToolEvaluateClientOutput {

	/**
	 * write Dot File
	 * 
	 * @param dir
	 * @param file
	 * @param map
	 * @param b
	 * @param AppModel
	 *            .getInstance()
	 */
	public void writeDotFile(String dir, String file, ATGModel atgModel, boolean drawFragment) {
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
			// for (String component :
			// Global.v().getAppModel().getProviderMap().keySet()) {
			// writeWithColor(writer, component, pColor);
			// }
			if (drawFragment)
				for (String component : Global.v().getAppModel().getFragmentClasses()) {
					writeWithColor(writer, component, fColor);
				}
			for (Entry<String, Set<AtgEdge>> en : atgModel.getAtgEdges().entrySet()) {
				Set<AtgEdge> resList = en.getValue();
				for (AtgEdge edge : resList) {

					String s = edge.getSource().getClassName();
					s = s.split("\\.")[s.split("\\.").length - 1].replace("\"", "").replace("\'", "");

					String e = edge.getDestnation().getClassName();
					e = e.split("\\.")[e.split("\\.").length - 1].replace("\"", "").replace("\'", "");

					String endString = ";\n";
					// if(edge.getType() != AtgType.Act2Act) {
					// endString = "[style = dashed];\n";
					// }

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
}
