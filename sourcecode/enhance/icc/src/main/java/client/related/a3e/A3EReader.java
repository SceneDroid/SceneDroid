package main.java.client.related.a3e;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.model.analyzeModel.MethodSummaryModel;
import main.java.analyze.utils.ConstantUtils;
import main.java.client.obj.model.atg.AtgEdge;
import main.java.client.obj.model.atg.AtgNode;
import main.java.client.related.a3e.model.A3EModel;
import main.java.client.statistic.model.StatisticResult;

public class A3EReader extends Analyzer {
	A3EModel model;
	protected StatisticResult result;
	protected Map<String, MethodSummaryModel> summaryMap;

	public A3EReader(StatisticResult result) {
		this.result = result;
		summaryMap = new HashMap<String, MethodSummaryModel>();
	}

	@Override
	public void analyze() {
		model = Global.v().getA3eModel();
		model.seta3eFilePath(ConstantUtils.A3EFOLDETR + appModel.getAppName() + ".g.xml");
		if(obtainATGfromFile())
			try {
				constructModel();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
	}

	private boolean obtainATGfromFile() {
		File file = new File(model.geta3eFilePath());
		if (!file.exists()) {
			model.geta3eAtgModel().setExist(false);
			return false;
		}
		return true;
	}
	private void constructModel() throws DocumentException {
		//1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File(model.geta3eFilePath()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()){ 
        	String source = "";
            Element activityEle = iterator.next();
            List<Attribute> attributes = activityEle.attributes();
            for (Attribute attribute : attributes) {
               if(attribute.getName().equals("name"))
            	   source = attribute.getValue().replace("-alias", "");
            }
            Iterator<Element> iterator1 = activityEle.elementIterator();
            while (iterator1.hasNext()){
            	String destination = "";
                Element subActivityEle = iterator1.next();
                List<Attribute> attributes2 = subActivityEle.attributes();
                for (Attribute attribute : attributes2) {
                   if(attribute.getName().equals("name")){
                	   destination = attribute.getValue().replace("-alias", "");
                	   AtgNode sNode = null, tNode = null;
	       				for (String name : Global.v().getAppModel().getComponentMap().keySet()) {
	       					if (name.endsWith(source))
	       						sNode = new AtgNode(name);
	       					if (name.endsWith(destination))
	       						tNode = new AtgNode(name);
	       				}
	       				if (sNode != null && tNode != null) {
	       					AtgEdge edge = new AtgEdge(sNode, tNode, "", -1, "");
	       					model.geta3eAtgModel().addAtgEdges(sNode.getClassName(), edge);
	       				}
                   }
                }
            }
        }
	}
}
