package main.java.client.cg;

import heros.solver.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import main.java.Analyzer;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.soot.SootAnalyzer;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.LayoutFileParser;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * generate callgraph
 * 
 * @author 79940
 *
 */
public class CgConstructor extends Analyzer {
	SetupApplication setupApplication;
	MultiMap<SootClass, AndroidCallbackDefinition> callBacks;
	MultiMap<SootClass, SootClass> fragments;

	public CgConstructor() {
		super();
		setupApplication = new SetupApplication(MyConfig.getInstance().getAndroidJar(), appModel.getAppPath());
		callBacks = new HashMultiMap<SootClass, AndroidCallbackDefinition>();
		fragments = new HashMultiMap<SootClass, SootClass>();
	}

	@Override
	public void analyze() {
		// dummy constuct, call back collect, fragment collect
		try {
			constructDummyMainMethods();
		} catch (Exception e) {
			constructBySoot();
		}
		collectDummyAsEntries();
		collectLifeCycleAsEntries();
		collectSelfCollectEntries();
		// String x = PrintUtils.printMap(appModel.getEntryMethod2Component());
		// System.out.println(x);
		if (MyConfig.getInstance().getMySwithch().isCallBackSwitch()) {
			// collect stub APIs defined for AIDL, their implements are seen as
			// entries
			collectStubMethods();
			collectEntryPoints();
		}

		if (!MyConfig.getInstance().getMySwithch().isDummyMainSwitch()) {
			collectAllMethodsAsEntries();
		}
		// isFragmentSwitch
		if (MyConfig.getInstance().getMySwithch().isFragmentSwitch()) {
			collectFragmentClasses();
		} else {
			resetFragmentClasses();
		}
		if(!Scene.v().hasCallGraph()){
			System.out.println("Call Graph is empty.");
			appModel.setCg(new CallGraph());
		}else{
			appModel.setCg(Scene.v().getCallGraph());
			// System.out.println("Call Graph has " +
			// Scene.v().getCallGraph().size() + " edges.");
		}
	}

	private void constructBySoot() {
		if (appModel.getAppName() == null)
			return;
		SootAnalyzer.sootInit();
		sootTransform();
		SootAnalyzer.sootEnd();
//		System.out.println("Call Graph has " + Scene.v().getCallGraph().size() + " edges.");
	}

	/**
	 * dummy constuct, call back collect, fragment collect
	 * 
	 */
	private void constructDummyMainMethods() {
		setupApplication.getConfig().getCallbackConfig().setCallbackAnalysisTimeout(120);
		setupApplication.getConfig().setCallgraphAlgorithm(CallgraphAlgorithm.AutomaticSelection);
		setupApplication.getConfig().setMergeDexFiles(true);
		setupApplication.runInfoflow_dummy();

		String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
				+ File.separator;
		callBacks = setupApplication.getCallbackMethods();
		fragments = setupApplication.getFragmentClasses();
		FileUtils.delFolder("sootOutput");

	}

	/**
	 * if isDummyMainSwitch is false
	 */
	private void collectAllMethodsAsEntries() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sMethod : sc.getMethods()) {
				if (!appModel.getEntryMethod2Component().containsKey(sMethod)) {
					appModel.addEntryMethod2Component(sMethod, sc);
				}
			}
		}
	}

	/**
	 * if isDummyMainSwitch is true
	 */
	private void collectDummyAsEntries() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sMethod : sc.getMethods()) {
				if (SootUtils.isComponentEntryMethod(sMethod))
					if (!appModel.getEntryMethod2Component().containsKey(sMethod)) {
						appModel.addEntryMethod2Component(sMethod, sc);
						appModel.getEntryMethods().add(sMethod);
					}
			}
		}
	}

	private void collectSelfCollectEntries() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sMethod : sc.getMethods()) {
				if (SootUtils.isSelfEntryMethods(sMethod.getSignature()))
					if (!appModel.getEntryMethod2Component().containsKey(sMethod)) {
						appModel.addEntryMethod2Component(sMethod, sc);
						appModel.getEntryMethods().add(sMethod);
					}
			}
		}

	}

	/**
	 * if isDummyMainSwitch is true
	 */
	private void collectLifeCycleAsEntries() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sMethod : sc.getMethods()) {
				if (SootUtils.isLifeCycleMethods(sMethod.getSignature()))
					if (!appModel.getEntryMethod2Component().containsKey(sMethod)) {
						appModel.addEntryMethod2Component(sMethod, sc);
						appModel.getEntryMethods().add(sMethod);
					}
			}
		}
	}

	/**
	 * if isFragmentSwitch is true collectFragmentClasses
	 * appModel.getFragmentClasses()
	 */
	private void collectFragmentClasses() {
		ARSCFileParser resParser = new ARSCFileParser();
		try {
			resParser.parse(appModel.getAppPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		appModel.setResParser(resParser);
		LayoutFileParser layoutParser = new LayoutFileParser(appModel.getPackageName(), resParser);
		layoutParser.parseLayoutFile(appModel.getAppPath());
		PackManager.v().getPack("wjtp").apply();
		MultiMap<String, SootClass> fragments = layoutParser.getFragments();
		for (Pair<String, SootClass> pair : fragments) {
			Global.v().getFragmentModel().getXmlFragmentMap().putIfAbsent(pair.getO1(), new ArrayList<SootClass>());
			Global.v().getFragmentModel().getXmlFragmentMap().get(pair.getO1()).add(pair.getO2());
		}

		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (SootUtils.isFragmentClass(sc)) {
				appModel.getFragmentClasses().add(sc.getName());
			}
		}
	}

	/**
	 * if isFragmentSwitch is false
	 */
	private void resetFragmentClasses() {
		appModel.setFragmentClasses(new HashSet<String>());

	}

	/**
	 * collectStubMethods collect method defined in AIDL files
	 */
	private void collectStubMethods() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootMethod sMethod : sc.getMethods()) {
				if (SootUtils.isStubEntryMethod(sMethod)) {
					appModel.getStubs().add(sMethod.getSubSignature());
				}
			}
		}
	}

	/**
	 * collect dummy main and callbacks
	 */
	private void collectEntryPoints() {
		addCallBackListeners();
		for (Pair<SootClass, AndroidCallbackDefinition> cb : callBacks) {
			SootMethod sm = cb.getO2().getTargetMethod();
			if (!appModel.getEntryMethod2Component().containsKey(sm)) {
				appModel.addEntryMethod2Component(sm, cb.getO1());
				appModel.getEntryMethods().add(sm);
			}
		}
		for (SootClass sClass : Scene.v().getApplicationClasses()) {
			for (int i =0; i< sClass.getMethods().size(); i++) {
				SootMethod sMethod  =  sClass.getMethods().get(i);
				String tag = sMethod.getSignature();
				// collect callbacks
				if (!SootUtils.hasSootActiveBody(sMethod))
					continue;
				Iterator<Unit> it = SootUtils.getSootActiveBody(sMethod).getUnits().iterator();
				while (it.hasNext()) {
					Unit u = it.next();
					InvokeExpr invoke = SootUtils.getInvokeExp(u);
					if (invoke != null) {
						collectUserCustumizedListeners(sMethod, u, invoke);
					}
				}
			}
		}
	}

	/**
	 * addCallBackListeners from "AndroidCallbacks.txt"
	 */
	private void addCallBackListeners() {

		Global.v().getAppModel().setCallbacks(FileUtils.getSetFromFile(ConstantUtils.DEFAULTCALLBACKFILE));

		Set<String> callBacks = new HashSet<String>();
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			for (SootClass interFace : sc.getInterfaces()) {
				if (!callBacks.contains(interFace.getName())) {
					if (interFace.getName().contains("Listener") || interFace.getName().contains("listener")
							|| interFace.getName().contains("callback") || interFace.getName().contains("Callback"))
						callBacks.add(interFace.getName());
				}
			}
		}
		Global.v().getAppModel().getCallbacks().addAll(callBacks);
	}

	/**
	 * collectUserCustumizedListeners add user custmized callbacks
	 * 
	 * @param sm
	 * @param u
	 * @param invoke
	 */
	private void collectUserCustumizedListeners(SootMethod sm, Unit u, InvokeExpr invoke) {
		SootMethod invMethod = invoke.getMethod();
		int id = 0;
		for (Type type : invMethod.getParameterTypes()) {
			if (Global.v().getAppModel().getCallbacks().contains(type.toString())) {
				Value value = invoke.getArg(id);
				List<Unit> defs = SootUtils.getDefOfLocal(sm.getSignature(), value, u);
				for (Unit defUnit : defs) {
					String targetClsStr = SootUtils.getTargetClassListenerBelongto(defUnit);
					SootClass targetCls = SootUtils.getSootClassByName(targetClsStr);
					if (targetCls == null) {
						continue;
					}
					for (SootClass interfaceClass : targetCls.getInterfaces()) {
						for (SootMethod interfaceMethod : interfaceClass.getMethods()) {
							SootMethod realInvolkedMethod = SootUtils.getMethodBySubSignature(targetCls,
									interfaceMethod.getSubSignature());
							if (realInvolkedMethod != null) {
								if (!appModel.getEntryMethod2Component().containsKey(realInvolkedMethod)) {
									appModel.addEntryMethod2Component(realInvolkedMethod, sm.getDeclaringClass());
									appModel.getEntryMethods().add(realInvolkedMethod);
									if (appModel.getComponentMap().containsKey(sm.getDeclaringClass().getName()))
										continue;
									if (appModel.getFragmentClasses().contains(sm.getDeclaringClass().getName()))
										continue;
									Pair<SootMethod, Unit> pair = new Pair<SootMethod, Unit>(sm, u);
									appModel.getEntryMethod2MethodAddThisCallBack().putIfAbsent(pair,
											new HashSet<SootMethod>());
									appModel.getEntryMethod2MethodAddThisCallBack().get(pair).add(realInvolkedMethod);
								}
							}
						}
					}
				}
			}
			id++;
		}
	}

	/**
	 * add transforms for analyzing
	 */
	private void sootTransform() {
		String algo = MyConfig.getInstance().getCallgraphAlgorithm();
		switch (algo) {
		case "CHA":
			Options.v().setPhaseOption("cg.cha", "on");
			break;
		case "SPARK":
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg.spark", "string-constants:true");
			break;
		default:
			break;
		}
	}

}