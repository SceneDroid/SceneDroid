package main.java.client.obj.target.ctg;

import java.util.List;
import java.util.Map;

import main.java.Analyzer;
import main.java.Global;
import main.java.analyze.utils.RAICCUtils;
import main.java.analyze.utils.SootUtils;
import main.java.client.obj.target.ctg.ATGPreprocess;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

/**
 * Analyzer Class
 * 
 * @author hanada
 * @version 2.0
 */
public class ATGPreprocess extends Analyzer {

	@Override
	public void analyze() {
		Map<String, String> method2InstructionMap = Global.v().getAppModel().getMethod2InstructionMap();
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sm : sc.getMethods()) {
				int i = 0;
				List<Unit> units = SootUtils.getUnitListFromMethod(sm);
				for(Unit u: units){
					if (CTGAnalyzerHelper.isSendIntent2IccMethod(u) || RAICCUtils.isWrapperMethods(u)) {
						String res = method2InstructionMap.get(sm.getSignature());
						if (res == null) {
							res = i + "";
							method2InstructionMap.put(sm.getSignature(), res);
						} else {
							res += "," + i;
							method2InstructionMap.put(sm.getSignature(), res);
						}
					}
					i++;
				}

			}
		}
		System.out.println("ATGPreprocess finish\n");
	}

}