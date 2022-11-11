package main.java.analyze.model.analyzeModel;

import heros.solver.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.PrintUtils;
import main.java.client.obj.model.component.ActivityModel;
import main.java.client.obj.model.component.ComponentModel;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.scalar.UnitValueBoxPair;

public class AppModel implements Serializable {
	private static final long serialVersionUID = 1L;
	// app info
	private String appName;
	private String appPath;
	private String mainActivity;
	private String manifestString;
	private String packageName;
	private int versionCode;
	private String permission;
	private Set<String> usesPermissionSet;
	private Set<String> applicationClassNames;


	// call graph related
	private Set<SootMethod> allMethods;
	private Set<SootMethod> entryMethods;
	private CallGraph cg;
	private ReachableMethods reachableMethodsFromCg;
	private List<SootMethod> topoMethodQueue;
	private Set<List<SootMethod>> topoMethodQueueSet;

	// components
	private HashMap<String, ComponentModel> componentMap;
	private HashMap<String, ComponentModel> activityMap;
	private HashMap<String, ComponentModel> serviceMap;
	private HashMap<String, ComponentModel> providerMap;
	private HashMap<String, ComponentModel> receiverMap;
	private HashMap<String, ComponentModel> exportedComponentMap;
	
	private Set<String> FragmentClasses;
	private Set<String> callbacks;
	private Set<String> stubs;
	private Set<String> extendedPkgss;
	
	// test generation
	private HashMap<String, ActivityModel> toBeAnalyzedActivityMap;

	// info collect
	private ARSCFileParser resParser;

	private Map<String, String> StaticRefSignature2initAssignMap; // for static
	private Map<String, Set<StaticFiledInfo>> StaticRefSignature2UnitMap; // for
																			// static
	private Map<String, String> method2InstructionMap;
	private Map<String, Set<String>> method2PotentionFragMap;

	private Map<Unit, UnitNode> unit2NodeMap;
	private Map<String, Set<SootMethod>> unit2TargetsMap;
	private Map<String, Attribute> unit2Attribute; // for extra data value store

	private Map<Unit, List<UnitValueBoxPair>> def2UseMap;
	private Map<Pair<Value, Unit>, List<Unit>> unit2defMap;
	private Map<SootMethod, SootClass> entryMethod2Component;
	private Map<Pair<SootMethod, Unit>, Set<SootMethod>> entryMethod2MethodAddThisCallBack;
	private Map<SootClass, SootClass> fragment2Component;
	private Map<Unit, List<ParameterSource>> unit2ParameterSource;
	private Map<String, Set<String>> ICCStringMap;
	
	
	public AppModel() {
		String name = MyConfig.getInstance().getAppName();
		appPath = MyConfig.getInstance().getAppPath() + name;
		appName = name.substring(0, name.length() - 4);
		manifestString = "";
		packageName = "";
		permission = "";
		mainActivity = "";

		allMethods = new HashSet<>();
		entryMethods = new HashSet<>();
		topoMethodQueue = new ArrayList<SootMethod>();
		topoMethodQueueSet = new HashSet<List<SootMethod>>();

		activityMap = new HashMap<String, ComponentModel>();
		serviceMap = new HashMap<String, ComponentModel>();
		providerMap = new HashMap<String, ComponentModel>();
		receiverMap = new HashMap<String, ComponentModel>();
		componentMap = new HashMap<String, ComponentModel>();
		exportedComponentMap = new HashMap<String, ComponentModel>();
		FragmentClasses = new HashSet<String>();
		applicationClassNames = new HashSet<String>();

		toBeAnalyzedActivityMap = new HashMap<String, ActivityModel>();
		usesPermissionSet = new HashSet<String>();
		StaticRefSignature2initAssignMap = new HashMap<String, String>();
		StaticRefSignature2UnitMap = new HashMap<String, Set<StaticFiledInfo>>();
		method2InstructionMap = new HashMap<String, String>();
		method2PotentionFragMap = new HashMap<String, Set<String>>();

		unit2NodeMap = new HashMap<Unit, UnitNode>();
		unit2TargetsMap = new HashMap<String, Set<SootMethod>>();
		unit2Attribute = new HashMap<String, Attribute>();

		setDef2UseMap(new HashMap<Unit, List<UnitValueBoxPair>>());
		setUnit2defMap(new HashMap<Pair<Value, Unit>, List<Unit>>());
		callbacks = new HashSet<>();
		stubs = new HashSet<>();
		entryMethod2Component = new HashMap<SootMethod, SootClass>();
		entryMethod2MethodAddThisCallBack = new HashMap<Pair<SootMethod, Unit>, Set<SootMethod>>();
		fragment2Component = new HashMap<SootClass, SootClass>();
		unit2ParameterSource = new HashMap<Unit, List<ParameterSource>>();
		setExtendedPakgs(new HashSet<String>());
		this.ICCStringMap = new HashMap<String, Set<String>>();
	}

	@Override
	public String toString() {
		String res = "";
		res += "appName: " + appName + "\n";
		res += "appPath: " + appPath + "\n";
		res += "packageName: " + packageName + "\n";
		res += "permission: " + permission + "\n";
		res += "usesPermissionSet: " + PrintUtils.printSet(usesPermissionSet) + "\n";
		res += "activityMap: " + PrintUtils.printMap(activityMap) + "\n";
		res += "serviceMap: " + PrintUtils.printMap(serviceMap) + "\n";
		res += "providerMap: " + PrintUtils.printMap(providerMap) + "\n";
		res += "receiverMap: " + PrintUtils.printMap(receiverMap) + "\n";
		res += "eaMap: " + PrintUtils.printMap(exportedComponentMap) + "\n";

		return res;
	}

	/**
	 * print the call graph
	 */
	public void printCg() {
		if (cg == null)
			return;
		Iterator<Edge> it = cg.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			String caller = edge.getSrc().method().getSignature();
			String callee = edge.getTgt().method().getSignature();
			System.out.println(caller + " --> " + callee);
		}
	}

	
	public Map<String, Attribute> getUnit2Attribute() {
		return unit2Attribute;
	}

	public String getAppPath() {
		return appPath;
	}

	public Set<String> getUsesPermissionSet() {
		return usesPermissionSet;
	}

	public void setUsesPermissionSet(Set<String> usesPermissionSet) {
		this.usesPermissionSet = usesPermissionSet;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getManifestString() {
		return manifestString;
	}

	public void setManifestString(String manifestString) {
		this.manifestString = manifestString;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public HashMap<String, ComponentModel> getActivityMap() {
		return activityMap;
	}

	public HashMap<String, ComponentModel> getServiceMap() {
		return serviceMap;
	}

	public HashMap<String, ComponentModel> getProviderMap() {
		return providerMap;
	}

	public HashMap<String, ComponentModel> getRecieverMap() {
		return receiverMap;
	}

	public HashMap<String, ComponentModel> setComponentMap(HashMap<String, ComponentModel> map) {
		componentMap.putAll(map);
		return componentMap;
	}

	public HashMap<String, ComponentModel> getComponentMap() {
		return componentMap;
	}

	public HashMap<String, ActivityModel> getToBeAnalyzedActivityMap() {
		return toBeAnalyzedActivityMap;
	}

	public HashMap<String, ComponentModel> getExportedComponentMap() {
		return exportedComponentMap;
	}

	public String getAppName() {
		return appName;
	}

	public Map<String, String> getStaticRefSignature2initAssignMap() {
		return StaticRefSignature2initAssignMap;
	}


	public List<SootMethod> getTopoMethodQueue() {
		return topoMethodQueue;
	}

	public CallGraph getCg() {
		return cg;
	}

	public void setCg(CallGraph callGraph) {
		this.cg = callGraph;

	}

	public String getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(String mainActivity) {
		this.mainActivity = mainActivity;
	}

	public ReachableMethods getReachableMethods() {
		return reachableMethodsFromCg;
	}

	public void setReachableMethods(ReachableMethods reachableMethods) {
		this.reachableMethodsFromCg = reachableMethods;
	}

	public Map<Unit, UnitNode> getUnit2NodeMap() {
		return unit2NodeMap;
	}

	public Set<SootMethod> getAllMethods() {
		return allMethods;
	}

	public void addMethod(SootMethod method) {
		this.allMethods.add(method);
	}

	public ARSCFileParser getResParser() {
		return resParser;
	}

	public void setResParser(ARSCFileParser resParser) {
		this.resParser = resParser;
	}

	public Set<String> getFragmentClasses() {
		return FragmentClasses;
	}

	public void setFragmentClasses(Set<String> fragmentClasses) {
		FragmentClasses = fragmentClasses;
	}

	public Map<String, Set<String>> getMethod2PotentionFragMap() {
		return method2PotentionFragMap;
	}

	public void setMethod2PotentionFragMap(Map<String, Set<String>> method2PotentionDesMap) {
		this.method2PotentionFragMap = method2PotentionDesMap;
	}

	public Map<String, Set<SootMethod>> getUnit2TargetsMap() {
		return unit2TargetsMap;
	}

	public void setUnit2TargetsMap(Map<String, Set<SootMethod>> unit2TargetsMap) {
		this.unit2TargetsMap = unit2TargetsMap;
	}

	// public Set<Unit> getUnitsWithUnknownPara() {
	// return unitsWithUnknownPara;
	// }
	//
	// public void setUnitsWithUnknownPara(Set<Unit> unitsWithUnknownPara) {
	// this.unitsWithUnknownPara = unitsWithUnknownPara;
	// }

	public Set<List<SootMethod>> getTopoMethodQueueSet() {
		return topoMethodQueueSet;
	}

	public void setTopoMethodQueueSet(Set<List<SootMethod>> topoMethodQueueSet) {
		this.topoMethodQueueSet = topoMethodQueueSet;
	}

	/**
	 * @return the method2InstructionMap
	 */
	public Map<String, String> getMethod2InstructionMap() {
		return method2InstructionMap;
	}

	/**
	 * @param method2InstructionMap
	 *            the method2InstructionMap to set
	 */
	public void setMethod2InstructionMap(Map<String, String> method2InstructionMap) {
		this.method2InstructionMap = method2InstructionMap;
	}

	/**
	 * @return the versionCode
	 */
	public int getVersionCode() {
		return versionCode;
	}

	/**
	 * @param versionCode
	 *            the versionCode to set
	 */
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	/**
	 * @return the def2UseMap
	 */
	public Map<Unit, List<UnitValueBoxPair>> getDef2UseMap() {
		return def2UseMap;
	}

	/**
	 * @param def2UseMap
	 *            the def2UseMap to set
	 */
	public void setDef2UseMap(Map<Unit, List<UnitValueBoxPair>> def2UseMap) {
		this.def2UseMap = def2UseMap;
	}

	/**
	 * @return the unit2defMap
	 */
	public Map<Pair<Value, Unit>, List<Unit>> getUnit2defMap() {
		return unit2defMap;
	}

	/**
	 * @param unit2defMap
	 *            the unit2defMap to set
	 */
	public void setUnit2defMap(Map<Pair<Value, Unit>, List<Unit>> unit2defMap) {
		this.unit2defMap = unit2defMap;
	}

	/**
	 * @return the callbacks
	 */
	public Set<String> getCallbacks() {
		return callbacks;
	}

	/**
	 * @param callbacks
	 *            the callbacks to set
	 */
	public void setCallbacks(Set<String> callbacks) {
		this.callbacks = callbacks;
	}

	/**
	 * @return the staticRefSignature2UnitMap
	 */
	public Map<String, Set<StaticFiledInfo>> getStaticRefSignature2UnitMap() {
		return StaticRefSignature2UnitMap;
	}

	/**
	 * @param staticRefSignature2UnitMap
	 *            the staticRefSignature2UnitMap to set
	 */
	public void setStaticRefSignature2UnitMap(Map<String, Set<StaticFiledInfo>> staticRefSignature2UnitMap) {
		StaticRefSignature2UnitMap = staticRefSignature2UnitMap;
	}

	/**
	 * @return the callBacks2Component
	 */
	public Map<SootMethod, SootClass> getEntryMethod2Component() {
		return entryMethod2Component;
	}

	/**
	 * @param callBacks2Component
	 *            the callBacks2Component to set
	 */
	public void setEntryMethod2Component(Map<SootMethod, SootClass> callBacks2Component) {
		this.entryMethod2Component = callBacks2Component;
	}

	public void addEntryMethod2Component(SootMethod sm, SootClass sc) {
		if (sm.getDeclaringClass().getName().contains(".R$"))
			return;
		if (sm.getDeclaringClass().getName().contains(ConstantUtils.DUMMYMAIN))
			sc = SootUtils.getRealClassofDummy(sm);
		if (sc == null)
			return;
		this.entryMethod2Component.put(sm, sc);
	}

	/**
	 * @return the fragment2Component
	 */
	public Map<SootClass, SootClass> getFragment2Component() {
		return fragment2Component;
	}

	/**
	 * @param fragment2Component
	 *            the fragment2Component to set
	 */
	public void setFragment2Component(Map<SootClass, SootClass> fragment2Component) {
		this.fragment2Component = fragment2Component;
	}

	/**
	 * @return the stubs
	 */
	public Set<String> getStubs() {
		return stubs;
	}

	/**
	 * @param stubs
	 *            the stubs to set
	 */
	public void setStubs(Set<String> stubs) {
		this.stubs = stubs;
	}

	/**
	 * @return the entryMethod2MethodAddThisCallBack
	 */
	public Map<Pair<SootMethod, Unit>, Set<SootMethod>> getEntryMethod2MethodAddThisCallBack() {
		return entryMethod2MethodAddThisCallBack;
	}

	/**
	 * @param entryMethod2MethodAddThisCallBack
	 *            the entryMethod2MethodAddThisCallBack to set
	 */
	public void setEntryMethod2MethodAddThisCallBack(
			Map<Pair<SootMethod, Unit>, Set<SootMethod>> entryMethod2MethodAddThisCallBack) {
		this.entryMethod2MethodAddThisCallBack = entryMethod2MethodAddThisCallBack;
	}

	/**
	 * @return the unit2ParameterSource
	 */
	public Map<Unit, List<ParameterSource>> getUnit2ParameterSource() {
		return unit2ParameterSource;
	}

	/**
	 * @param unit2ParameterSource
	 *            the unit2ParameterSource to set
	 */
	public void setUnit2ParameterSource(Map<Unit, List<ParameterSource>> unit2ParameterSource) {
		this.unit2ParameterSource = unit2ParameterSource;
	}

	public void addUnit2ParameterSource(Unit unit, ParameterSource ps) {
		if (!unit2ParameterSource.containsKey(unit))
			unit2ParameterSource.put(unit, new ArrayList<ParameterSource>());
		for (ParameterSource exist : unit2ParameterSource.get(unit)) {
			if (exist.toString().equals(ps.toString()))
				return;
		}
		unit2ParameterSource.get(unit).add(ps);
	}

	/**
	 * @return the applicationClassNames
	 */
	public Set<String> getApplicationClassNames() {
		return applicationClassNames;
	}

	/**
	 * @param applicationClassNames
	 *            the applicationClassNames to set
	 */
	public void setApplicationClassNames(Set<String> applicationClassNames) {
		this.applicationClassNames = applicationClassNames;
	}

	/**
	 * @param applicationClasses
	 *            the applicationClasses to set
	 */
	public void addApplicationClassNames(String sc) {
		this.applicationClassNames.add(sc);
	}

	/**
	 * @return the extendedLibs
	 */
	public Set<String> getExtendedPakgs() {
		return extendedPkgss;
	}

	/**
	 * @param extendedLibs
	 *            the extendedLibs to set
	 */
	public void setExtendedPakgs(Set<String> extendedLibs) {
		this.extendedPkgss = extendedLibs;
	}

	/**
	 * @return the entryMethods
	 */
	public Set<SootMethod> getEntryMethods() {
		return entryMethods;
	}

	/**
	 * @param entryMethods
	 *            the entryMethods to set
	 */
	public void setEntryMethods(Set<SootMethod> entryMethods) {
		this.entryMethods = entryMethods;
	}

	/**
	 * @return the iCCStringMap
	 */
	public Map<String, Set<String>> getICCStringMap() {
		return ICCStringMap;
	}

	/**
	 * @param iCCStringMap the iCCStringMap to set
	 */
	public void setICCStringMap(Map<String, Set<String>> iCCStringMap) {
		ICCStringMap = iCCStringMap;
	}

}
