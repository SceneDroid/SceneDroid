package main.java.client.obj.target.fragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.obj.model.atg.AtgType;
import main.java.client.statistic.model.StatisticResult;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class FragmentClientOutput {
	StatisticResult result;

	public FragmentClientOutput(StatisticResult fragment) {
		this.result = fragment;
	}

	/**
	 * writeMethodSummaryModel
	 * 
	 * @param entry
	 * 
	 * @param string
	 * @param topo
	 * @param AppModel
	 *            .getInstance()
	 * @throws IOException
	 */
	public void writeMethodSummaryModel(String dir, String file, boolean entryMethod) throws DocumentException,
			IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, false);
		Element root = document.getRootElement();
		List<Element> eleList = new ArrayList<Element>();
		if (entryMethod) {
			eleList = result.getXmlStatistic().getEntryMethodSummaryEleList();
		} else {
			eleList = result.getXmlStatistic().getAllMethodSummaryEleList();
		}
		for (Element e : eleList) {
			try{
				root.add(e);
			}catch(Exception e1){
			}
		}
		FileUtils.xmlWriteEnd(dir, file, document);
	}

	/**
	 * writePathSummaryModel write info about each icc flow
	 * 
	 * @param dir
	 * @param file
	 * @param AppModel
	 *            .getInstance()
	 * @param entryMethod
	 */
	public void writePathSummaryModel(String dir, String file, boolean entryMethod) throws DocumentException,
			IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, false);
		Element root = document.getRootElement();
		List<Element> eleList;
		if (entryMethod) {
			eleList = result.getXmlStatistic().getEntryPathSummaryEleList();
		} else {
			eleList = result.getXmlStatistic().getAllPathSummaryEleList();
		}

		for (Element e : eleList) {
			try{
				root.add(e);
			}catch(Exception e1){
			}
		}
		FileUtils.xmlWriteEnd(dir, file, document);
	}

	/**
	 * writeIntentSummaryModel
	 * 
	 * @param dir
	 * @param file
	 * @param entryMethod
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void writeSingleFragModel(String dir, String file, boolean entryMethod) throws DocumentException,
			IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, false);
		Element root = document.getRootElement();
		List<Element> eleList;
		if (entryMethod) {
			eleList = result.getXmlStatistic().getEntryIntentSummaryEleList();
		} else {
			eleList = result.getXmlStatistic().getAllIntentSummaryEleList();
		}
		for (Element e : eleList) {
			try{
				root.add(e);
			}catch(Exception e1){
			}
		}
		FileUtils.xmlWriteEnd(dir, file, document);

	}

	/**
	 * writeATGModel
	 * 
	 * @param string
	 * @param atg
	 * @param atgEdges
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void writeATGModel(String dir, String file, ATGModel atgModel) throws DocumentException, IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, false);
		Element root = document.getRootElement();
		for (Entry<String, Set<AtgEdge>> en : atgModel.getAtgEdges().entrySet()) {
			String className = en.getKey();
			Element source = root.addElement("source");
			source.addAttribute("name", className);
			Set<String> addedEdgeStr = new HashSet<String>();
			for (AtgEdge edge : en.getValue()) {
				Element desEle = new DefaultElement("destination");
				desEle.addAttribute("name", edge.getDestnation().getName());
				desEle.addAttribute("type", edge.getType().name());
				desEle.addAttribute("method", edge.getMethodSig());
				desEle.addAttribute("InstructionId", edge.getInstructionId() + "");
				if (edge.getIntentSummary() != null) {
					if (edge.getIntentSummary().getSetActionValueList().size() > 0)
						desEle.addAttribute("action",
								PrintUtils.printList(edge.getIntentSummary().getSetActionValueList()));
					if (edge.getIntentSummary().getSetCategoryValueList().size() > 0)
						desEle.addAttribute("category",
								PrintUtils.printList(edge.getIntentSummary().getSetCategoryValueList()));
					if (edge.getIntentSummary().getSetDataValueList().size() > 0)
						desEle.addAttribute("data", PrintUtils.printList(edge.getIntentSummary().getSetDataValueList()));
					if (edge.getIntentSummary().getSetTypeValueList().size() > 0)
						desEle.addAttribute("type", PrintUtils.printList(edge.getIntentSummary().getSetTypeValueList()));
					if (edge.getIntentSummary().getSetExtrasValueList() != null)
						desEle.addAttribute("extras", edge.getIntentSummary().getSetExtrasValueList().toString());
					if (edge.getIntentSummary().getSetFlagsList() != null)
						desEle.addAttribute("flags", PrintUtils.printList(edge.getIntentSummary().getSetFlagsList()));
					// single intent has finish, atg do not has finish
					if (edge.getIntentSummary().isFinishFlag())
						desEle.addAttribute("finish", "true");
				}

				if (!addedEdgeStr.contains(desEle.asXML())) {
					source.add(desEle);
					addedEdgeStr.add(desEle.asXML());
				}
			}
		}
		FileUtils.xmlWriteEnd(dir, file, document);

	}

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
	public void writeDotFile(String dir, String file, ATGModel atgModel, boolean skipNonComponentNode) {
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
			for (String component : Global.v().getAppModel().getFragmentClasses()) {
				writeWithColor(writer, component, fColor);
			}

			for (Entry<String, Set<AtgEdge>> en : atgModel.getAtgEdges().entrySet()) {
				Set<AtgEdge> resList = en.getValue();
				for (AtgEdge edge : resList) {
					if (skipNonComponentNode)
						if (edge.getType() == AtgType.Act2Class || edge.getType() == AtgType.Frag2Class
								|| edge.getType() == AtgType.NonAct2Class || edge.getType() == AtgType.Class2Any)
							continue;
					String s = edge.getSource().getClassName();
					s = s.split("\\.")[s.split("\\.").length - 1].replace("\"", "").replace("\'", "");

					String e = edge.getDestnation().getClassName();
					e = e.split("\\.")[e.split("\\.").length - 1].replace("\"", "").replace("\'", "");

					String endString = ";\n";
					if (edge.getType() != AtgType.Act2Act) {
						endString = "[style = dashed];\n";
					}

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

	private void writeWithColor(BufferedWriter writer, String component, String color) throws IOException {
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
