package main.java.analyze.utils;

import heros.solver.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.StaticFiledInfo;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.output.FileUtils;
import soot.Body;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JRetStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.ShimpleBody;
import soot.shimple.internal.SPhiExpr;
import soot.shimple.toolkits.scalar.ShimpleLocalDefs;
import soot.shimple.toolkits.scalar.ShimpleLocalUses;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

/**
 * type judgment method judgment get information
 * 
 * @author 79940
 *
 */
public class SootUtils {

	/**
	 * get the real name of the component of current dummyMain method instead of
	 * dummyMainClass
	 * 
	 * @param sootMtd
	 * @return
	 */
	public static SootClass getRealClassofDummy(SootMethod sootMtd) {
		SootClass cls;
		Type type = sootMtd.getReturnType();
		if (!(type instanceof RefType))
			return null;
		cls = ((RefType) type).getSootClass();
		return cls;
	}

	/**
	 * judge for each field in class and methods
	 * 
	 * @param sc
	 */
	public static boolean isDialogFragmentClass(SootClass sc) {
		if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch() && !SootUtils.isNonLibClass(sc.getName()))
			return false;
		SootClass superCls = sc;
		while (true) {
			if (SootUtils.isOriginDialogFragmentClass(superCls)) {
				return true;
			}
			if (!superCls.hasSuperclass())
				return false;
			superCls = superCls.getSuperclass();
		}
	}

	/**
	 * get Type of ClassName
	 * 
	 * @param sc
	 * @return
	 */
	public static String getNameofClass(SootClass sc) {
		return getNameofClass(sc.getName());
	}

	/**
	 * get Type of ClassName
	 * 
	 * @param sc
	 * @return lamda: dev.ukanth.ufirewall.preferences.-
	 *         $$Lambda$ExpPreferenceFragment$ZOS3OXrmCOVpoNyVtmIXEyWQLi0
	 *         anonymous:
	 *         dev.ukanth.ufirewall.preferences.ExpPreferenceFragment$1
	 */
	public static String getNameofClass(String sc) {
		if (Global.v().getAppModel().getComponentMap().keySet().contains(sc))
			return sc;
		String className = sc.replace("-$$Lambda$", "").split("\\$")[0];
		if (className.length() == 0)
			className = sc.replace("-", "_").replace("$", "_");
		return className;
	}

	/**
	 * get Type of ClassName
	 * 
	 * @param sc
	 * @return
	 */
	public static String getTypeofClassName(String className) {
		Set<String> activitySet = Global.v().getAppModel().getActivityMap().keySet();
		Set<String> serviceSet = Global.v().getAppModel().getServiceMap().keySet();
		Set<String> providerSet = Global.v().getAppModel().getProviderMap().keySet();
		Set<String> receiverSet = Global.v().getAppModel().getRecieverMap().keySet();
		Set<String> fragmentSet = Global.v().getAppModel().getFragmentClasses();

		if (fragmentSet.contains(className)) {
			return "fragment";
		}
		if (activitySet.contains(className)) {
			return "activity";
		}
		if (serviceSet.contains(className)) {
			return "service";
		}
		if (providerSet.contains(className)) {
			return "provider";
		}
		if (receiverSet.contains(className)) {
			return "receiver";
		}
		return "other";
	}

	/**
	 * get Type of ClassName
	 * 
	 * @param sc
	 * @return
	 */
	public static String getTypeofClassName(SootClass sc) {
		String className = getNameofClass(sc);
		return getTypeofClassName(className);
	}

	/**
	 * judge for each field in class and methods
	 * 
	 * @param sc
	 */
	public static boolean isFragmentClass(SootClass sc) {
		if(sc==null) return false;
		if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch() && !SootUtils.isNonLibClass(sc.getName()))
			return false;
		if (Global.v().getAppModel().getComponentMap().containsKey(sc.getName()))
			return false;
		SootClass superCls = sc;
		while (true) {
			if (SootUtils.isOriginFragmentClass(superCls)) {
				return true;
			}
			if (!superCls.hasSuperclass())
				return false;
			superCls = superCls.getSuperclass();
		}
	}

	private static boolean isOriginDialogFragmentClass(SootClass sc) {
		if (sc == null)
			return false;
		for (int i = 0; i < ConstantUtils.dialogFragmentClasses.length; i++) {
			if (sc.getName().equals(ConstantUtils.dialogFragmentClasses[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * isFragmentClass used in PotentialRelationAnalyzer
	 * 
	 * @param sc
	 * @return
	 */
	public static boolean isOriginFragmentClass(SootClass sc) {
		if (sc == null)
			return false;
		for (int i = 0; i < ConstantUtils.fragmentClasses.length; i++) {
			if (sc.getName().equals(ConstantUtils.fragmentClasses[i])) {

				return true;
			}
		}
		return false;
	}

	/**
	 * isComponentClass used in PotentialRelationAnalyzer
	 * 
	 * @param sc
	 * @return
	 */
	public static boolean isComponentClass(SootClass sc) {
		for (int i = 0; i < ConstantUtils.componentClasses.length; i++) {
			if (sc.getName().contains(ConstantUtils.componentClasses[i]))
				return true;
		}
		return false;
	}

	/**
	 * judege a method is entry callback or not
	 * 
	 * @param methodName
	 * @param className
	 * @return
	 */
	public static boolean isComponentEntryMethod(SootMethod method) {

		if (method.getName().startsWith(ConstantUtils.DUMMYMAIN))
			return true;
		if (Global.v().getAppModel().getStubs().contains(method.getSubSignature()))
			return true;

		return false;
	}

	/**
	 * judege a method is entry callback or not
	 * 
	 * @param methodName
	 * @param className
	 * @return
	 */
	public static boolean isStubEntryMethod(SootMethod method) {
		if (method.getDeclaringClass().getName().contains("$Stub$Proxy"))
			return true;
		return false;
	}

	/**
	 * judge whether active body exist or not if not, retrieve it
	 * 
	 * @param sm
	 * @return
	 */
	public static boolean hasSootActiveBody(SootMethod sm) {
		ArrayList<String> excludeList = new ArrayList<String>();
		addExcludeList(excludeList);

		boolean ready = false;
		if (sm.hasActiveBody()) {
			ready = true;
		} else {
			if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
				if (!SootUtils.isNonLibClass(sm.getDeclaringClass().getName()))
					return false;
			}
			if (!SootUtils.isNonLibClass(sm.getDeclaringClass().getName())) {
				for (String exPrex : excludeList) {
					if (sm.getSignature().startsWith(exPrex))
						return false;
				}
			}
			try {
				sm.retrieveActiveBody();
				ready = true;
			} catch (RuntimeException e) {
			}
		}
		return ready;
	}

	private static void addExcludeList(ArrayList<String> excludeList) {
		excludeList.add("<android.");
		excludeList.add("<androidx.");
		excludeList.add("<kotlin.");
		excludeList.add("<com.google.");
		excludeList.add("<soot.");
		excludeList.add("<junit.");
		excludeList.add("<java.");
		excludeList.add("<javax.");
		excludeList.add("<sun.");
		excludeList.add("<org.apache.");
		excludeList.add("<org.eclipse.");
		excludeList.add("<org.junit.");
		excludeList.add("<com.fasterxml.");
	}

	/**
	 * judge isLifeCycleMethods
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLifeCycleMethods(String str) {
		for (int i = 0; i < ConstantUtils.lifeCycleMethodsSet.size(); i++) {
			if (str.contains(ConstantUtils.lifeCycleMethodsSet.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * judge isLifeCycleMethods
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCallBackMethodShort(String str) {
		final String[] lifeCycleMethods = { "onCreate", "onStart", "onResume", "onPause", "onStop", "onRestart",
				"onDestroy", "onStartCommand", "onBind", "onUnbind", "onRebind", "onReceive" };
		List<String> lifeCycleMethodsSet = Arrays.asList(lifeCycleMethods);
		for (int i = 0; i < lifeCycleMethodsSet.size(); i++) {
			if (str.equals(lifeCycleMethodsSet.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * judge isLifeCycleMethods
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCallBackMethods(String str) {
		// final String[] lifeCycleMethods = {"onCreate", "onStart", "onResume",
		// "onPause","onStop","onRestart","onDestroy","onStartCommand",
		// "onBind", "onUnbind", "onRebind","onReceive"};
		// List<String> lifeCycleMethodsSet = Arrays.asList(lifeCycleMethods);
		// for (int i = 0; i < lifeCycleMethodsSet.size(); i++) {
		// if (str.equals(lifeCycleMethodsSet.get(i)))
		// return true;
		// }
		if (str.startsWith("on"))// || str.equals("<init>")
			return true;
		return false;
	}

	/**
	 * judge isLifeCycleMethods
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isSelfEntryMethods(String str) {
		for (int i = 0; i < ConstantUtils.selfEntryMethodsSet.size(); i++) {
			if (str.contains(ConstantUtils.selfEntryMethodsSet.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * judge isLifeCycleMethods
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBroadCastRegisterMethods(String str) {
		if (str.toString()
				.contains(
						"android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter)"))
			return true;
		return false;
	}

	/**
	 * judge is new Intent stmt
	 * 
	 * @param s
	 * @return
	 */
	public static Boolean isNewIntent(Unit s) {
		return s.toString().endsWith("new android.content.Intent");
	}

	/**
	 * judge type is_bundle_extra
	 * 
	 * @param s
	 * @return
	 */
	public static Boolean isBundleExtra(String s) {
		return s.contains("Bundle");
	}

	/**
	 * judge type is_extras_extra
	 * 
	 * @param s
	 * @return
	 */
	public static Boolean isExtrasExtra(String s) {
		return s.contains("Extras");
	}

	/**
	 * judge type isIntentExtra
	 * 
	 * @param s
	 * @return
	 */
	public static Boolean isIntentExtra(String s) {
		return s.contains("Intent");
	}

	/**
	 * judge type isStringType
	 * 
	 * @param extra_type
	 * @return
	 */
	public static boolean isStringType(String extra_type) {
		String no[] = { "Bundle", "Parcelable", "Serializable", "Extras", "ArrayList", "Array" };
		for (String s : no)
			if (extra_type.contains(s))
				return false;
		return true;
	}

	/**
	 * judge type isArrayListType
	 * 
	 * @param extra_type
	 * @return
	 */
	public static boolean isArrayListType(String extra_type) {
		String no[] = { "IntegerArrayList", "ParcelableArrayList", "StringArrayList" };
		for (String s : no)
			if (extra_type.contains(s))
				return true;
		return false;
	}

	/**
	 * judge is_parOrSer_extra type
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isParOrSerExtra(String type) {
		if (type.contains("Parcelable") || type.contains("Serializable"))
			return true;
		return false;
	}

	// method judgment

	/**
	 * judge isSafeLibMethod
	 * 
	 * @param methodStr
	 * @return
	 */
	public static int isSafeLibMethod(String methodStr) {
		for (int i = 0; i < ConstantUtils.unsafePrefix.length; i++) {
			if (methodStr.startsWith(ConstantUtils.unsafePrefix[i]))
				return -1;
		}
		for (int i = 0; i < ConstantUtils.safePrefix.length; i++) {
			if (methodStr.startsWith(ConstantUtils.safePrefix[i]))
				return 1;
		}
		return 0;
	}

	/**
	 * judge isExitPoint method
	 * 
	 * @param methodStr
	 * @return
	 */
	public static boolean isExitPoint(String methodStr) {
		for (int i = 0; i < ConstantUtils.exitpoint.length; i++) {
			if (methodStr.contains(ConstantUtils.exitpoint[i]))
				return true;
		}
		return false;
	}

	/**
	 * judge is_implicit_execute method
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isImplicitExecute(String s) {
		return s.contains(" execute(java.lang.Runnable)");
	}

	/**
	 * judge is_implicit_execute_implict method
	 * 
	 * @param caller
	 * @return
	 */
	public static boolean isImplicitExecuteImplict(String caller) {
		for (String s : ConstantUtils.implicitExecutes)
			if (caller.contains(s))
				return true;
		return false;
	}

	/**
	 * judge is_IntraInvoke_method method
	 * 
	 * @param u
	 * @param cls
	 * @return
	 */
	public static boolean isIntraInvokeMethod(Unit u, String cls) {
		String id = "<" + cls + ":";
		if (u.toString().contains("invoke") && u.toString().contains(id))
			return true;
		return false;
	}

	// get information

	/**
	 * get InvokeExp from useUnit
	 * 
	 * @param useUnit
	 * @return
	 */
	public static InvokeExpr getInvokeExp(Unit useUnit) {
		InvokeExpr invoke = null;
		if (useUnit instanceof JAssignStmt) {
			JAssignStmt jas = (JAssignStmt) useUnit;
			if (jas.containsInvokeExpr()) {
				invoke = jas.getInvokeExpr();
			}
		} else if (useUnit instanceof JInvokeStmt) {
			invoke = ((JInvokeStmt) useUnit).getInvokeExpr();
		} else if (useUnit instanceof JStaticInvokeExpr) {
			invoke = ((JStaticInvokeExpr) useUnit);
		}
		return invoke;
	}

	/**
	 * getSingleInvokedMethod
	 * 
	 * @param invoke
	 * @param u
	 */
	public static InvokeExpr getSingleInvokedMethod(Unit u) {
		InvokeExpr invoke = null;
		if (u instanceof JAssignStmt) {
			JAssignStmt jas = (JAssignStmt) u;
			if (jas.containsInvokeExpr()) {
				invoke = jas.getInvokeExpr();
			}
		} else if (u instanceof JInvokeStmt) {
			invoke = ((JInvokeStmt) u).getInvokeExpr();
		} else if (u instanceof JStaticInvokeExpr) {
			invoke = ((JStaticInvokeExpr) u);
		}
		return invoke;

	}

	/**
	 * getInvokedMethod
	 * 
	 * @param u
	 * @return
	 */
	public static Set<SootMethod> getInvokedMethodSet(SootMethod sm, Unit u) {
		InvokeExpr invoke = getSingleInvokedMethod(u);
		if (invoke != null) { // u is invoke stmt
			if (Global.v().getAppModel().getUnit2TargetsMap().containsKey(u.toString() + u.hashCode())) {
				return Global.v().getAppModel().getUnit2TargetsMap().get(u.toString() + u.hashCode());
			}
			return addInvokedMethods(sm, u, invoke);
		}
		return new HashSet<SootMethod>();
	}

	/**
	 * getActiveBody
	 * 
	 * @param sm
	 * @return
	 */
	@SuppressWarnings("finally")
	public static Body getSootActiveBody(SootMethod sm) {
		if (sm == null)
			return null;
		if (sm.hasActiveBody())
			return sm.getActiveBody();
		else {
			if (SootUtils.isNonLibClass(sm.getDeclaringClass().getName())) {
				try {
					return sm.retrieveActiveBody();
				} catch (Exception e) {
				} finally {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * get body of sm, without add it to methodsToBeProcessed
	 * 
	 * @param target
	 * @param sm
	 * @param appModel
	 * @return
	 */
	public static List<Body> getBodySetofMethod(SootMethod sm) {
		List<Body> bodys = new ArrayList<Body>();
		if (sm.getName().equals("<init>")) {
			for (SootMethod sm2 : sm.getDeclaringClass().getMethods()) {
				if (hasSootActiveBody(sm2) && Global.v().getAppModel().getEntryMethod2Component().containsKey(sm2)) {
					bodys.add(getSootActiveBody(sm2));
				}
			}
		}

		if (hasSootActiveBody(sm)) {
			bodys.add(getSootActiveBody(sm));
		} else if (sm.isAbstract()) {
			Set<SootClass> subClasses = new HashSet<>();
			if (Scene.v().hasFastHierarchy()) {
				if (sm.getDeclaringClass().isInterface()) {
					subClasses = Scene.v().getFastHierarchy().getAllImplementersOfInterface(sm.getDeclaringClass());
				} else {
					subClasses = (Set<SootClass>) Scene.v().getFastHierarchy().getSubclassesOf(sm.getDeclaringClass());
				}
			}
			for (SootClass sc : subClasses) {
				try {
					SootMethod sm2 = sc.getMethodByName(sm.getName());
					if (sm2 != null && hasSootActiveBody(sm2)) {
						bodys.add(getSootActiveBody(sm2));
					}
				} catch (Exception e) {
				}
			}
		}
		return bodys;
	}

	public static SootMethod getMethodBySubSignature(SootClass sootClass, String subSignature) {
		if (sootClass == null) {
			return null;
		}
		SootMethod resultMethod = null;
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.getSubSignature().contains(subSignature)
					&& sootMethod.getDeclaration().contains("transient")
					&& !sootMethod.getDeclaration().contains("volatile")) {
				return sootMethod;
			}
			if (sootMethod.getSubSignature().contains(subSignature)
					&& sootMethod.getDeclaration().contains("transient")) {
				resultMethod = sootMethod;
				continue;
			}
			if (sootMethod.getSubSignature().contains(subSignature) && resultMethod == null) {
				resultMethod = sootMethod;
			}
		}

		if (resultMethod == null && sootClass.hasSuperclass()) {
			SootClass superclass = sootClass.getSuperclass();
			return getMethodBySubSignature(superclass, subSignature);
		}

		return resultMethod;
	}

	/**
	 * addInvokedMethods in cg construction
	 * 
	 * @param sourceCls
	 * @param sm
	 * @param u
	 * @param invoke
	 * @param listenerAdd
	 * @return
	 */
	public static Set<SootMethod> addInvokedMethods(SootMethod sm, Unit u, InvokeExpr invoke) {
		Set<SootMethod> targetSet = new HashSet<SootMethod>();
		SootMethod invMethod = invoke.getMethod();
		if (invMethod != null) {
			if (MyConfig.getInstance().getMySwithch().isAsyncMethodSwitch()) {
				if (invMethod.toString().contains("<java.lang.Thread: void start()>")) {
					SootMethod runMethod = transformStart2Run(sm, invoke, u);
					if (runMethod != null)
						targetSet.add(runMethod);
				} else if (invMethod.toString().contains("void runOnUiThread(java.lang.Runnable)")) {
					SootMethod runMethod = transformStart2runOnUiThread(sm, invoke, u);
					if (runMethod != null)
						targetSet.add(runMethod);
				} else if (invMethod.toString().contains(
						"android.os.Handler: boolean postDelayed(java.lang.Runnable,long)")) {
					SootMethod runMethod = transformpostDelayed2Run(sm, invoke, u);
					if (runMethod != null)
						targetSet.add(runMethod);
				} else if (invMethod.toString().contains("android.os.AsyncTask execute(java.lang.Object[])")
						|| invMethod.toString().contains("android.os.AsyncTask executeOnExecutor(")) {
					Set<SootMethod> runMethods = transformAsyncTask2LifeCycles(sm, invoke, u);
					for (SootMethod runMethod : runMethods)
						targetSet.add(runMethod);
				} else {
					// get the actual class of the base box in this invocation
					if (invoke instanceof AbstractInstanceInvokeExpr) {
						Value base = ((AbstractInstanceInvokeExpr) invoke).getBase();
						List<Unit> defs = SootUtils.getDefOfLocal(sm.getSignature(), base, u);
						for (Unit defUnit : defs) {
							String targetCls = ((AbstractDefinitionStmt) defUnit).getRightOp().getType().toString();
							if (targetCls != null && targetCls.length() > 0) {
								SootClass targetClass = SootUtils.getSootClassByName(targetCls);
								if (targetClass == null)
									continue;
								SootMethod targetMtd = SootUtils.getMethodBySubSignature(targetClass,
										invMethod.getSubSignature());
								if (targetMtd == null)
									continue;
								targetSet.add(targetMtd);
								break;
							}
						}
					}
					targetSet.add(invMethod);
				}
				Pair<SootMethod, Unit> pair = new Pair<SootMethod, Unit>(sm, u);
				Set<SootMethod> listenerMehods = Global.v().getAppModel().getEntryMethod2MethodAddThisCallBack()
						.get(pair);
				if (listenerMehods != null) {
					targetSet.addAll(listenerMehods);
				}
				targetSet.remove(sm);
			} else {
				targetSet.add(invMethod);
			}
		}
		Global.v().getAppModel().getUnit2TargetsMap().put(u.toString() + u.hashCode(), targetSet);
		return targetSet;
	}

	public static boolean isClassInSystemPackage(String className) {
		return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
				|| className.startsWith("sun.") || className.startsWith("org.omg.")
				|| className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
				|| className.startsWith("com.android.") || className.startsWith("com.ibm.")
				|| className.startsWith("com.sun.") || className.startsWith("com.apple.")
				|| className.startsWith("org.w3c.") || className.startsWith("soot");
	}

	public static Set<SootClass> getSootClassesInvoked(SootClass sootClass, Set<SootClass> visitiedClasses,
			Set<SootMethod> visitiedSootMethods) {
		Set<SootClass> sootClassesInvoked = new HashSet<>();

		if (visitiedClasses == null) {
			visitiedClasses = new HashSet<>();
		}
		if (visitiedClasses.contains(sootClass) || isClassInSystemPackage(sootClass.getName())) {
			return sootClassesInvoked;
		}
		sootClassesInvoked.add(sootClass);
		visitiedClasses.add(sootClass);
		List<SootMethod> sootMethods = sootClass.getMethods();
		for (int i = 0; i < sootMethods.size(); i++) {
			SootMethod sootMethod = sootMethods.get(i);
			if (visitiedSootMethods == null) {
				visitiedSootMethods = new HashSet<>();
			}
			if (visitiedSootMethods.contains(sootMethod)) {
				continue;
			}
			visitiedSootMethods.add(sootMethod);
			for (Unit unit : getUnitListFromMethod(sootMethod)) {
				if (unit instanceof Stmt) {
					Stmt stmt = (Stmt) unit;
					if (stmt.containsInvokeExpr()) {
						if (stmt.getInvokeExpr().getMethodRef().getDeclaringClass().getName().startsWith("java."))
							continue;
						InvokeExpr invokeExpr = stmt.getInvokeExpr();
						SootMethod invokeMethod = invokeExpr.getMethod();
						SootClass invokeClass = invokeMethod.getDeclaringClass();
						sootClassesInvoked.add(invokeClass);
						sootClassesInvoked.addAll(getSootClassesInvoked(invokeClass, visitiedClasses,
								visitiedSootMethods));
					} else if (unit instanceof JAssignStmt) {
						JAssignStmt assignStmt = (JAssignStmt) unit;
						if (assignStmt.rightBox == null) {
							continue;
						}
						Type type = assignStmt.rightBox.getValue().getType();
						if (type instanceof RefType) {
							RefType refType = (RefType) type;
							SootClass refClass = refType.getSootClass();
							if (refClass.getName().startsWith("java."))
								continue;
							sootClassesInvoked.add(refClass);
							sootClassesInvoked.addAll(getSootClassesInvoked(refClass, visitiedClasses,
									visitiedSootMethods));
						}
					}
				}
			}
		}
		return sootClassesInvoked;
	}

	/**
	 * transformStart2Run add call edge
	 * 
	 * @param invoke
	 * @param u
	 * @return
	 */
	protected static SootMethod transformStart2Run(SootMethod sm, InvokeExpr invoke, Unit u) {
		String runSignature = "";
		List<Unit> defs = SootUtils
				.getDefOfLocal(sm.getSignature(), ((AbstractInstanceInvokeExpr) invoke).getBase(), u);
		for (Unit def : defs) {
			String type = SootUtils.getTargetClassOfUnit(sm, def);
			if (type.equals("java.lang.Thread")) {
				List<UnitValueBoxPair> uses = SootUtils.getUseOfLocal(sm.getSignature(), def);
				for (UnitValueBoxPair vb : uses) {
					Unit useUnit = vb.getUnit();
					InvokeExpr inv = SootUtils.getInvokeExp(useUnit);
					if (inv == null)
						continue;
					if (inv.getMethod().getSignature().equals("<java.lang.Thread: void <init>(java.lang.Runnable)>")) {
						ValueObtainer vo = new ValueObtainer(sm.getSignature(), "", new Context(), new Counter());
						Set<String> resSet = new HashSet<>(vo.getValueofVar(inv.getArg(0), useUnit, 0).getValues());
						if (resSet != null && resSet.size() > 0)
							type = new ArrayList<String>(resSet).get(0).replace("new ", "");
					}
				}
			}
			runSignature = "<" + type + ": void run()>";
		}
		if (runSignature.length() > 0) {
			SootMethod runMethod = SootUtils.getSootMethodBySignature(runSignature);
			if (runMethod != null) {
				return runMethod;
			}
		}
		return null;
	}

	/**
	 * transformpostDelayed2Eun add call edge
	 * 
	 * @param invoke
	 * @param u
	 * @return
	 */
	protected static SootMethod transformpostDelayed2Run(SootMethod sm, InvokeExpr invoke, Unit u) {
		String runSignature = "";
		Value val = null;
		if(invoke instanceof AbstractInvokeExpr){
			val = ((AbstractInvokeExpr) invoke).getArg(0);
			List<Unit> defs = SootUtils.getDefOfLocal(sm.getSignature(),val , u);
			for (Unit def : defs) {
				String type = SootUtils.getTargetClassOfUnit(sm, def);
				runSignature = "<" + type + ": void run()>";
			}
			if (runSignature.length() > 0) {
				SootMethod runMethod = SootUtils.getSootMethodBySignature(runSignature);
				if (runMethod != null) {
					return runMethod;
				}
			}
		}
		return null;
	}

	/**
	 * transformStart2Run add call edge
	 * 
	 * @param invoke
	 * @param u
	 * @return
	 */
	protected static SootMethod transformStart2runOnUiThread(SootMethod sm, InvokeExpr invoke, Unit u) {
		String runSignature = "";
		Value v = null;
		if (invoke instanceof JVirtualInvokeExpr)
			v = ((JVirtualInvokeExpr) invoke).getArg(0);
		else if (invoke instanceof JSpecialInvokeExpr)
			v = ((JSpecialInvokeExpr) invoke).getArg(0);
		else
			return null;
		List<Unit> defs = SootUtils.getDefOfLocal(sm.getSignature(), v, u);
		for (Unit def : defs) {
			String type = SootUtils.getTargetClassOfUnit(sm, def);
			runSignature = "<" + type + ": void run()>";
		}
		if (runSignature.length() > 0) {
			SootMethod runMethod = SootUtils.getSootMethodBySignature(runSignature);
			if (runMethod != null) {
				return runMethod;
			}
		}
		return null;
	}

	/**
	 * transformAsyncTask2LifeCycles
	 * 
	 * @param sm
	 * @param invoke
	 * @param u
	 */
	private static Set<SootMethod> transformAsyncTask2LifeCycles(SootMethod sm, InvokeExpr invoke, Unit u) {
		Set<SootMethod> res = new HashSet<SootMethod>();
		Set<String> candidateMethods = new HashSet<String>();
		if (!(invoke instanceof AbstractInstanceInvokeExpr))
			return res;
		Value val = ((AbstractInstanceInvokeExpr) invoke).getBase();
		List<Unit> defs = SootUtils.getDefOfLocal(sm.getSignature(), val, u);
		for (Unit def : defs) {
			String type = SootUtils.getTargetClassOfUnit(sm, def);
			candidateMethods.add("<" + type + ": java.lang.Object doInBackground(java.lang.Object[])>");
			candidateMethods.add("<" + type + ": void onPostExecute(java.lang.Object)>");
			candidateMethods.add("<" + type + ": void onPreExecute()>");

			candidateMethods.add("<" + type + ": void cancel(boolean)>");
			candidateMethods.add("<" + type + ": void onCancelled(java.lang.Object)>");
			candidateMethods.add("<" + type + ": void (java.lang.Object[])>");
			candidateMethods.add("<" + type + ": void publishProgress()>");
		}
		for (String runSignature : candidateMethods) {
			if (runSignature.length() > 0) {
				SootMethod runMethod = SootUtils.getSootMethodBySignature(runSignature);
				if (runMethod != null) {
					res.add(runMethod);
				}
			}
		}
		return res;
	}

	/**
	 * get Soot Method By Signature
	 * 
	 * @param signature
	 * @return
	 */
	public static SootMethod getSootMethodBySignature(String signature) {
		SootMethod sm = null;
		if (signature == null || signature.length() == 0)
			return null;
		try {
			sm = Scene.v().getMethod(signature);
			if (sm != null)
				return sm;
		} catch (Exception e) {
		}
		return sm;

	}

	/**
	 * get SootClass By Signature
	 * 
	 * @param signature
	 * @return
	 */
	public static SootClass getSootClassByName(String signature) {
		if (signature == null || signature.length() == 0)
			return null;
		try {
			for (SootClass sc : Scene.v().getApplicationClasses()) {
				if (sc.getName().equals(signature))
					return sc;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * get Return List
	 * 
	 * @param sm
	 * @return
	 */
	public static List<Unit> getRetList(SootMethod sm) {
		List<Unit> rets = null;
		if (hasSootActiveBody(sm)) {
			rets = new ArrayList<Unit>();
			for (Unit ret_u : getUnitListFromMethod(sm)) {
				if (ret_u instanceof JReturnStmt) {
					rets.add(ret_u);
				}
			}
		}
		return rets;
	}

	/**
	 * get Def Of Local
	 * 
	 * @param local
	 * @param u
	 * @return
	 */
	public static List<Unit> getDefOfLocal(String methodName, Value val, Unit u) {
		List<Unit> res = new ArrayList<Unit>();
		if (!(val instanceof Local))
			return res;

		Pair<Value, Unit> pair = new Pair<Value, Unit>(val, u);
		if (Global.v().getAppModel().getUnit2defMap().containsKey(pair))
			return Global.v().getAppModel().getUnit2defMap().get(pair);

		Local local = (Local) val;
		SootMethod sm = null;
		try {
			sm = SootUtils.getSootMethodBySignature(methodName);
		} catch (Exception e) {
			return res;
		}
		if (sm == null)
			return res;
		Body b = getSootActiveBody(sm);
		if (b == null)
			return res;
		UnitGraph graph = new ExceptionalUnitGraph(b);
		if (MyConfig.getInstance().isJimple()) {
			try {
				SimpleLocalDefs defs = new SimpleLocalDefs(graph);
				res = defs.getDefsOfAt(local, u);
			} catch (Exception e) {
				res = new ArrayList<Unit>();
			}
		} else {
			try {
				ShimpleLocalDefs defs = new ShimpleLocalDefs((ShimpleBody) getSootActiveBody(sm));
				res = defs.getDefsOfAt(local, u);
			} catch (Exception e) {
				res = new ArrayList<Unit>();
			}
		}
		Global.v().getAppModel().getUnit2defMap().put(pair, res);
		return res;

	}

	/**
	 * get Use Of Local
	 * 
	 * @param appModel
	 * @param mc
	 * @param unit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<UnitValueBoxPair> getUseOfLocal(String methodName, Unit defUnit) {
		List<UnitValueBoxPair> res = new ArrayList<UnitValueBoxPair>();
		if (Global.v().getAppModel().getDef2UseMap().containsKey(defUnit))
			return Global.v().getAppModel().getDef2UseMap().get(defUnit);

		SootMethod sm = SootUtils.getSootMethodBySignature(methodName);
		Body b = getSootActiveBody(sm);
		if (b == null)
			return res;
		UnitGraph graph = new ExceptionalUnitGraph(b);
		if (MyConfig.getInstance().isJimple()) {
			try {
				SimpleLocalUses uses = new SimpleLocalUses(graph, new SimpleLocalDefs(graph));
				res = uses.getUsesOf(defUnit);
			} catch (Exception e) {
				res = new ArrayList<UnitValueBoxPair>();
			}
		} else {
			try {
				ShimpleLocalUses uses = new ShimpleLocalUses((ShimpleBody) getSootActiveBody(sm));
				res = uses.getUsesOf(defUnit);
			} catch (Exception e) {
				res = new ArrayList<UnitValueBoxPair>();
			}
		}
		Global.v().getAppModel().getDef2UseMap().put(defUnit, res);
		return res;
	}

	/**
	 * get Compared Unit
	 * 
	 * @param use_var_list
	 * @return
	 */
	public static List<UnitValueBoxPair> getComparedUnit(List<UnitValueBoxPair> use_var_list) {
		List<UnitValueBoxPair> resList = new ArrayList<UnitValueBoxPair>();
		for (UnitValueBoxPair ubp : use_var_list) {
			Unit u = ubp.getUnit();
			for (String s : ConstantUtils.comparedMethods) {
				if (u.toString().contains(s)) {
					resList.add(ubp);
					break;
				}
			}
		}
		return resList;
	}

	/**
	 * getTargetClassOfUnit used in broadcast type analyze
	 * 
	 * @param defUnit
	 * @return
	 */
	public static String getTargetClassListenerBelongto(Unit defUnit) {
		String className = "";
		if (defUnit instanceof JAssignStmt) {
			JAssignStmt assignDefUnit = (JAssignStmt) defUnit;
			Value rValue = assignDefUnit.rightBox.getValue();
			if (rValue instanceof JInstanceFieldRef) {
				JInstanceFieldRef fieldRef = (JInstanceFieldRef) rValue;
				if (fieldRef.getBase().getType() instanceof PrimType)
					className = fieldRef.getField().getType().toString();
				else {
					RefType type = (RefType) fieldRef.getBase().getType();
					SootClass instanceClass = type.getSootClass();
					className = instanceClass.getName();// + "@" +
														// fieldRef.getField().getName();
				}
			} else if (rValue instanceof JNewExpr) {
				JNewExpr newExpr = (JNewExpr) rValue;
				className = newExpr.getType().toString();
			} else if (rValue instanceof StaticFieldRef) { // static
				StaticFieldRef sfr = (StaticFieldRef) rValue;
				className = sfr.getField().getDeclaringClass().getName();
			} else if (rValue instanceof JInstanceFieldRef) { // static
				JInstanceFieldRef jif = (JInstanceFieldRef) rValue;
				className = jif.getField().getDeclaringClass().getName();
			} else if (rValue instanceof JVirtualInvokeExpr) {
				JVirtualInvokeExpr expr = (JVirtualInvokeExpr) rValue;
				className = expr.getBase().getType().toString();
			} else if (rValue instanceof JStaticInvokeExpr) {
				JStaticInvokeExpr expr = (JStaticInvokeExpr) rValue;
				className = expr.getMethod().getReturnType().toString();
			} else if (rValue instanceof InvokeExpr) {
				InvokeExpr expr = (InvokeExpr) rValue;
				className = expr.getMethod().getReturnType().toString();
			} else if (rValue instanceof SPhiExpr) {
				SPhiExpr expr = (SPhiExpr) rValue;
				className = expr.getType().toString();
				// for (ValueUnitPair arg : expr.getArgs()) {
				// className = arg.getValue().getType().toString();
				// }
			} else if (rValue instanceof JimpleLocal) {
				JimpleLocal expr = (JimpleLocal) rValue;
				className = expr.getType().toString();
			} else if (rValue instanceof JCastExpr) {
				JCastExpr expr = (JCastExpr) rValue;
				className = expr.getCastType().toString();
			} else {
			}
		} else if (defUnit instanceof JIdentityStmt) {
			JIdentityStmt identifyDefUnit = (JIdentityStmt) defUnit;
			Value rValue = identifyDefUnit.rightBox.getValue();
			if (rValue instanceof ThisRef) {
				ThisRef thisRef = (ThisRef) rValue;
				className = thisRef.getType().toString();
			} else if (rValue instanceof ParameterRef) {
				// from parameter, which is unkonwn
				className = null;
			}
		}
		return className;
	}

	/**
	 * getTargetClassOfUnit used in broadcast type analyze
	 * 
	 * @param sm
	 * @param defUnit
	 * @return
	 */
	public static String getTargetClassOfUnit(SootMethod sm, Unit defUnit) {
		String className = "";
		if (defUnit instanceof JAssignStmt) {
			JAssignStmt assignDefUnit = (JAssignStmt) defUnit;
			Value rValue = assignDefUnit.rightBox.getValue();
			if (rValue instanceof JInstanceFieldRef) {
				JInstanceFieldRef fieldRef = (JInstanceFieldRef) rValue;
				if (fieldRef.getField().getType() instanceof PrimType)
					className = fieldRef.getField().getType().toString();
				else {
					SootField field = fieldRef.getField();
					Set<StaticFiledInfo> infos = Global.v().getAppModel().getStaticRefSignature2UnitMap()
							.get(field.getSignature());
					if (infos != null) {
						for (StaticFiledInfo info : infos) {
							if (info.getSootMethod().equals(sm)) {
								className = getTargetClassOfUnit(sm, info.getUnit());
								break;
							}
						}
					}
					if (className.length() == 0) {
						RefType type = (RefType) fieldRef.getField().getType();
						// Object defs =
						// SootUtils.getDefOfLocal(fieldRef.getField(),
						// defUnit);
						SootClass instanceClass = type.getSootClass();
						className = instanceClass.getName();// + "@" +
															// fieldRef.getField().getName();
					}
				}
			} else if (rValue instanceof JNewExpr) {
				JNewExpr newExpr = (JNewExpr) rValue;
				className = newExpr.getType().toString();
			} else if (rValue instanceof StaticFieldRef) { // static
				StaticFieldRef sfr = (StaticFieldRef) rValue;
				className = sfr.getField().getDeclaringClass().getName();
			} else if (rValue instanceof JInstanceFieldRef) { // static
				JInstanceFieldRef jif = (JInstanceFieldRef) rValue;
				className = jif.getField().getDeclaringClass().getName();
			} else if (rValue instanceof JVirtualInvokeExpr) {
				JVirtualInvokeExpr expr = (JVirtualInvokeExpr) rValue;
				className = expr.getMethod().getReturnType().toString();
			} else if (rValue instanceof JStaticInvokeExpr) {
				JStaticInvokeExpr expr = (JStaticInvokeExpr) rValue;
				className = expr.getMethod().getReturnType().toString();
			} else if (rValue instanceof InvokeExpr) {
				InvokeExpr expr = (InvokeExpr) rValue;
				className = expr.getMethod().getReturnType().toString();
			} else if (rValue instanceof SPhiExpr) {
				SPhiExpr expr = (SPhiExpr) rValue;
				className = expr.getType().toString();
				// for (ValueUnitPair arg : expr.getArgs()) {
				// className = arg.getValue().getType().toString();
				// }
			} else if (rValue instanceof JimpleLocal) {
				JimpleLocal expr = (JimpleLocal) rValue;
				className = expr.getType().toString();
			} else if (rValue instanceof JCastExpr) {
				JCastExpr expr = (JCastExpr) rValue;
				className = expr.getCastType().toString();
			} else {
			}
		} else if (defUnit instanceof JIdentityStmt) {
			JIdentityStmt identifyDefUnit = (JIdentityStmt) defUnit;
			Value rValue = identifyDefUnit.rightBox.getValue();
			if (rValue instanceof ThisRef) {
				ThisRef thisRef = (ThisRef) rValue;
				className = thisRef.getType().toString();
			} else if (rValue instanceof ParameterRef) {
				ParameterRef pr = (ParameterRef) rValue;
				className = pr.getType().toString();
			}
		}
		return className;
	}

	public static boolean isMethodReturnUnit(Unit u) {
		if (u instanceof JReturnStmt || u instanceof JRetStmt || u instanceof JReturnVoidStmt)
			return true;
		return false;
	}

	public static int getIdForUnit(String statement, String method) {
		SootMethod sm = SootUtils.getSootMethodBySignature(method);
		int id = 0;
		for (Unit currentUnit : getUnitListFromMethod(sm)) {
			if (currentUnit.toString().equals(statement)) {
				return id;
			}
			++id;
		}

		return -1;
	}

	public static int getIdForUnit(Unit unit, SootMethod sm) {
		int id = 0;
		Body body = getSootActiveBody(sm);
		if (body == null)
			return -1;
		for (Unit currentUnit : getUnitListFromMethod(sm)) {
			if (currentUnit == unit) {
				return id;
			}
			++id;
		}

		return -1;
	}

	/**
	 * isExtendedLibClass
	 * 
	 * @param clsName
	 */
	public static boolean isNonLibClass(String clsName) {
		for (String lib : Global.v().getAppModel().getExtendedPakgs()) {
			if (clsName.startsWith(lib)) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Set<String>> getCgMap() {
		List<String> cgList = FileUtils.getListFromFile(MyConfig.getInstance().getResultFolder()
				+ Global.v().getAppModel().getAppName() + File.separator + ConstantUtils.CGFOLDETR + ConstantUtils.CG);
		Map<String, Set<String>> cgMap = new HashMap<String, Set<String>>();
		for (String line : cgList) {
			// <dev.ukanth.ufirewall.Api: boolean
			// assertBinaries(android.content.Context,boolean)> ->
			// <dev.ukanth.ufirewall.util.G: void <clinit>()>
			if (!line.contains(" -> "))
				continue;
			String left = line.split(" -> ")[0];
			String leftClass = left.split(" ")[0];
			leftClass = leftClass.substring(1, leftClass.length() - 1);
			String leftmethod = left.split(" ")[2];
			leftmethod = leftmethod.split("\\(")[0];
			String key = leftClass + " " + leftmethod;

			String right = line.split(" -> ")[1];
			String rightClass = right.split(" ")[0];
			rightClass = rightClass.substring(1, rightClass.length() - 1);
			String rightmethod = right.split(" ")[2].split("\\(")[0];
			String val = rightClass + " " + rightmethod;

			if (!cgMap.containsKey(key))
				cgMap.put(key, new HashSet<String>());
			cgMap.get(key).add(val);
		}
		return cgMap;
	}

	public static Map<String, Set<String>> getCgMapWithSameName() {
		List<String> cgList = FileUtils.getListFromFile(MyConfig.getInstance().getResultFolder()
				+ Global.v().getAppModel().getAppName() + File.separator + ConstantUtils.CGFOLDETR + ConstantUtils.CG);
		Map<String, Set<String>> cgMap = new HashMap<String, Set<String>>();
		for (String line : cgList) {
			// <dev.ukanth.ufirewall.Api: boolean
			// assertBinaries(android.content.Context,boolean)> ->
			// <dev.ukanth.ufirewall.util.G: void <clinit>()>
			if (!line.contains(" -> "))
				continue;
			String left = line.split(" -> ")[0];
			String leftClass = left.split(" ")[0];
			leftClass = leftClass.substring(1, leftClass.length() - 1);
			leftClass = SootUtils.getNameofClass(leftClass);
			String classTypeL = SootUtils.getTypeofClassName(leftClass);
			if (classTypeL.equals("other"))
				continue;
			if (classTypeL.equals("fragment"))
				continue;
			if (classTypeL.equals("provider"))
				continue;

			String leftmethod = left.split(" ")[2];
			leftmethod = leftmethod.split("\\(")[0];
			String key = leftClass + " " + leftmethod;
			String right = line.split(" -> ")[1];
			String rightClass = right.split(" ")[0];
			rightClass = rightClass.substring(1, rightClass.length() - 1);
			rightClass = SootUtils.getNameofClass(rightClass);
			String classTypeR = SootUtils.getTypeofClassName(leftClass);
			if (classTypeR.equals("other"))
				continue;
			if (classTypeR.equals("fragment"))
				continue;
			if (classTypeR.equals("provider"))
				continue;
			String rightmethod = right.split(" ")[2].split("\\(")[0];
			String val = rightClass + " " + rightmethod;

			if (leftmethod.equals(rightmethod)) {
				SootClass leftCls = getSootClassByName(leftClass);
				SootClass rightCls = getSootClassByName(rightClass);
				if (leftCls == null || rightCls == null)
					continue;
				if (leftCls.getSuperclass().getName().equals(rightCls.getName())) {
					if (!cgMap.containsKey(key))
						cgMap.put(key, new HashSet<String>());
					cgMap.get(key).add(val);
				}
			}
		}
		return cgMap;
	}

	/**
	 * get units from a method
	 * soot has StackOverflowError bug for lookup unit
	 * @param m
	 * @return
	 */
	public static List<Unit> getUnitListFromMethod(SootMethod m) {
		List<Unit> units = new ArrayList<Unit>();
		if (m == null || SootUtils.hasSootActiveBody(m) == false)
			return units;
		Iterator<Unit> it = SootUtils.getSootActiveBody(m).getUnits().iterator();
		while (it.hasNext()) {
			Unit u = null;
			try{
				u = it.next();
				if(u instanceof JLookupSwitchStmt){
					u.toString(); //drop it for a bug in soot.jar
				}
			}catch(StackOverflowError e){
//				e.printStackTrace();
				continue;
			}
			units.add(u);
		}
		return units;
	}

}
