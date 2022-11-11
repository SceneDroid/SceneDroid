package main.java.client.related.gator;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import soot.Scene;
import soot.SootClass;
import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.obj.model.atg.ATGModel;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;

public class ATGReader extends Analyzer {
	ATGModel model;

	public ATGReader(ATGModel model) {
		this.model = model;
	}

	@Override
	public void analyze() {
	}
	
	public boolean obtainATGfromFile() {
		File file = new File(model.getATGFilePath());
		if (!file.exists()) {
			model.setExist(false);
			return false;
		}
		return true;
	}
	public void constructModelForGator() {
		List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
		for (String line : lines) {
			line = line.trim();
			line = line.replace("Node(", "");
			line = line.replace(")", "");
			line = line.replace(";", "");
			line = line.replace(" -> ", "->");
			if (line.split("->").length == 2) {
				String source = line.split("->")[0];
				String target = line.split("->")[1];
				AtgNode sNode = null, tNode = null;
				for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
					if (name.endsWith(source))
						sNode = new AtgNode(name);
					if (name.endsWith(target))
						tNode = new AtgNode(name);
				}
				if (sNode != null && tNode != null) {
					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
					model.addAtgEdges(sNode.getClassName(), edge);
				}
			}
		}
	}
	
	public void constructModelForICCBot() {
		List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
		for (String line : lines) {
			line = line.trim();
			line = line.replace(";", "");
			line = line.replace(" -> ", "->");
			if (line.split("->").length == 2) {
				String source = line.split("->")[0];
				String target = line.split("->")[1];
				AtgNode sNode = null, tNode = null;
				for (SootClass sClass : Scene.v().getApplicationClasses()) {
					String name = sClass.getName();
					if (name.endsWith(source))
						sNode = new AtgNode(name);
					if (name.endsWith(target))
						tNode = new AtgNode(name);
				}
				if (sNode != null && tNode != null) {
					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
					model.addAtgEdges(sNode.getClassName(), edge);
				}
			}
		}
	}
	
}
