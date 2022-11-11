package main.java.client.obj.model.atg;

import java.util.List;
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
		obtainGatorfromFile();
	}

	private void obtainGatorfromFile() {
		List<String> lines = FileUtils.getListFromFile(model.getATGFilePath());
		for (String line : lines) {
			line = line.trim();
			if (line.split(" -> ").length == 2) {
				String source = line.split(" -> ")[0];
				String target = line.split(" -> ")[1];
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
}
