package main.java.analyze.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.analyze.utils.output.FileUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class OracleUtils {
	/**
	 * write oracle.txt
	 * 
	 * @param summary_app_dir
	 * @param appName
	 */
	public static void writeTagedOracleFile(String summary_app_dir, String appName) {
		String manualOracle = summary_app_dir + ConstantUtils.ORACLEFOLDETR + appName + ConstantUtils.ORACLEMANU;
		String dynaOracle = summary_app_dir + ConstantUtils.ORACLEFOLDETR + appName + ConstantUtils.ORACLEDYNA;

		List<String> resList = new ArrayList<String>();
		for (String s : FileUtils.getListFromFile(manualOracle)) {
			if (s.length() > 2 && !resList.contains(s.substring(2)))
				resList.add(s.substring(2));
		}
		for (String s : FileUtils.getListFromFile(dynaOracle)) {
			if (!resList.contains(s))
				resList.add(s);
		}
		try {
			writeOracleModel(summary_app_dir + ConstantUtils.ORACLEFOLDETR, appName + ConstantUtils.ORACLETEXT, resList);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * writeIntentSummaryModel
	 * 
	 * @param dir
	 * @param file
	 * @param resList
	 * @param entryMethod
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void writeOracleModel(String dir, String file, List<String> resList) throws DocumentException, IOException {
		Document document = FileUtils.xmlWriterBegin(dir, file, false);
		Element root = document.getRootElement();
		List<Element> eleList = new ArrayList<Element>();
		for (String line : resList) {
			if (line.startsWith("##") || !line.contains(" --> "))
				continue;
			Element e = new DefaultElement("OracleEdge");
			eleList.add(e);
			e.addAttribute("source", line.split(" --> ")[0]);
			e.addAttribute("destination", line.split(" --> ")[1]);
			e.addAttribute("method", "xxx");
			Element correctness = e.addElement("correctness");
			correctness.addAttribute("correctness", "true");
			correctness.addAttribute("incorrectType", "No.");
			correctness.addAttribute("incorrectReason", "");
			Element nodes = e.addElement("nodes");
			Element node = nodes.addElement("node");
			node.addAttribute("method", "");
			node.addAttribute("type", "");
			node.addAttribute("unit", "");
			Element tags = e.addElement("tags");
			Element entryMethod = tags.addElement("entryMethod");
			entryMethod.addAttribute("isLifeCycle", "");
			entryMethod.addAttribute("isNormalCallBack", "");
			entryMethod.addAttribute("isStaticCallBack", "");
			entryMethod.addAttribute("isOtherComplexCallBack", "");

			Element exitMethod = tags.addElement("exitMethod");
			exitMethod.addAttribute("isNormalSendICC", "");
			exitMethod.addAttribute("isWarpperSendICC", "");

			Element intentMatch = tags.addElement("intentMatch");
			intentMatch.addAttribute("isExplicit", "");
			intentMatch.addAttribute("isImplicit", "");

			Element analyzeScope = tags.addElement("analyzeScope");
			Element componentScope = analyzeScope.addElement("componentScope");
			componentScope.addAttribute("isActivity", "");
			componentScope.addAttribute("isService", "");
			componentScope.addAttribute("isBroadCast", "");
			componentScope.addAttribute("isDynamicBroadCast", "");

			Element nonComponentScope = analyzeScope.addElement("nonComponentScope");
			nonComponentScope.addAttribute("isFragment", "");
			nonComponentScope.addAttribute("isAdapter", "");
			nonComponentScope.addAttribute("isOtherClass", "");

			Element methodScope = analyzeScope.addElement("methodScope");
			methodScope.addAttribute("isLibraryInvocation", "");
			methodScope.addAttribute("isMultipleInvocation", "");
			methodScope.addAttribute("isBasicInvocation", "");
			methodScope.addAttribute("isAsyncInvocation", "");
			methodScope.addAttribute("isListenerInvocation", "");

			Element objectcope = analyzeScope.addElement("objectScope");
			objectcope.addAttribute("isStaticVal", "");
			objectcope.addAttribute("isStringOp", "");
			objectcope.addAttribute("isPolymorphic", "");

			Element sensitivity = analyzeScope.addElement("sensitivityScope");
			sensitivity.addAttribute("flow", "");
			sensitivity.addAttribute("path", "");
			sensitivity.addAttribute("context", "");
			sensitivity.addAttribute("object", "");
			sensitivity.addAttribute("field", "");
		}
		for (Element e : eleList) {
			root.add(e);
		}
		FileUtils.xmlWriteEnd(dir, file, document);
	}
}
