package main.java.client.obj.target.ctg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.RAICCUtils;
import main.java.analyze.utils.SootUtils;
import main.java.client.obj.AnalyzerHelper;
import main.java.client.obj.unitHnadler.CreateHandler;
import main.java.client.obj.unitHnadler.MethodReturnHandler;
import main.java.client.obj.unitHnadler.ReceiveFromParaHandler;
import main.java.client.obj.unitHnadler.ReceiveFromRetValueHandler;
import main.java.client.obj.unitHnadler.UnitHandler;
import main.java.client.obj.unitHnadler.fragment.SetContentFunctionHandler;
import main.java.client.obj.unitHnadler.ictg.GetAttributeHandler;
import main.java.client.obj.unitHnadler.ictg.GetIntentExtraHandler;
import main.java.client.obj.unitHnadler.ictg.ReceiveFromOutHandler;
import main.java.client.obj.unitHnadler.ictg.SendIntent2ActivityHandler;
import main.java.client.obj.unitHnadler.ictg.SendIntent2ProviderHandler;
import main.java.client.obj.unitHnadler.ictg.SendIntent2ReceiverHandler;
import main.java.client.obj.unitHnadler.ictg.SendIntent2ServiceHandler;
import main.java.client.obj.unitHnadler.ictg.SendIntent2UnkownHandler;
import main.java.client.obj.unitHnadler.ictg.SetAttributeHandler;
import main.java.client.obj.unitHnadler.ictg.SetIntentExtraHandler;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIfStmt;

public class CTGAnalyzerHelper implements AnalyzerHelper {
	public List<String> objectIdentier;

	public CTGAnalyzerHelper() {
		objectIdentier = new ArrayList<String>();
		objectIdentier.add("android.content.Intent");
		objectIdentier.add("android.app.PendingIntent");
	}

	@Override
	public List<String> getObjectIdentier() {
		return this.objectIdentier;
	}

	@Override
	public boolean isMyTarget(Unit u) {
		if (MyConfig.getInstance().getMySwithch().isScenario_stack()) {
			if (isComponentFinishMethods(u)) {
				return true;
			}
		}
		if (isSetContentViewFunction(u)) {
			return true;
		}
		return false;
	}

	/**
	 * judge whether create or receive a new Intent
	 * 
	 * 
	 * @param unit
	 * @return
	 */
	@Override
	public boolean isTopTargetUnit(Unit unit) {
		if (MyConfig.getInstance().getMySwithch().isSetAttributeStrategy()) {
			if (isCreateMethod(unit)) {
				return true;
			} else if (isStaticCreateMethod(unit)) {
				return true;
			}
		}
		if (MyConfig.getInstance().getMySwithch().isGetAttributeStrategy()) {
			if (isReceiveFromOutMethod(unit)) {
				return true;
			}
		}
			
		if (isReceiveFromParatMethod(unit)) {
			return true;
		} else if (isReceiveFromRetValue(unit)) {
			return true;
		}
		
		return false;
	}

	/**
	 * judge whether create or receive a new Intent
	 * 
	 * 
	 * @param unit
	 * @return
	 */
	@Override
	public boolean isWarpperTopTargetUnit(Unit unit) {
		if (MyConfig.getInstance().getMySwithch().isSetAttributeStrategy()) {
			if (MyConfig.getInstance().getMySwithch().isWrapperAPISwitch()) {
				if (RAICCUtils.isIntentSenderCreation(unit)) {
					return true;
				} else if (RAICCUtils.isPendingIntentCreation(unit)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * getTypeofUnit
	 * 
	 * @param m
	 * 
	 * @param unit
	 * @return
	 */
	@Override
	public String getTypeofUnit(SootMethod m, Unit unit) {
		if (unit == null)
			return "";

		// inter-function
		if (isReceiveFromParatMethod(unit)) {
			return "ReceiveIntentFromParatMethod";
		} else if (isReceiveFromRetValue(unit)) {
			return "ReceiveIntentFromRetValue";
		}
		// finish
		if (MyConfig.getInstance().getMySwithch().isScenario_stack()) {
			if (isComponentFinishMethods(unit)) {
				return "componentReturn";
			}
		}
		
		// set
		if (MyConfig.getInstance().getMySwithch().isSetAttributeStrategy()) {
			if (isCreateMethod(unit)) {
				return "CreateMethod";
			} else if (isStaticCreateMethod(unit)) {
				return "StaticCreateMethod";
			}else if (isSetAttributeMethod(unit)) {
				return "SetAttribute";
			} else if (!MyConfig.getInstance().getMySwithch().isSetDesRelatedAttributeOnlyStrategy() && isSetIntentExtraMethod(unit)) {
				return "SetIntentExtra";
			} else if (isInitIntentMethod(unit)) {
				return "InitIntent";
			} else if (isSetContentViewFunction(unit)) {
				return "setContentView";
				
			// send out
			} else if (isSendIntent2IccMethod(unit)) {
				return "SendIntent2ICC";
			} else if (RAICCUtils.isWrapperMethods(unit)) {
				return "sendOutWrapperredIntent";
			} else if (RAICCUtils.isIntentSenderCreation(unit)) {
				return "intentSenderCreation";
			} else if (RAICCUtils.isPendingIntentCreation(unit)) {
				return "pendingIntentCreation";
			}else if (isPassOutMethod(unit)) {
				return "PassOutIntent";
			}
		}
		// get
		if (MyConfig.getInstance().getMySwithch().isGetAttributeStrategy()) {
			if (isReceiveFromOutMethod(unit)) {
				return "ReceiveIntentFromOutMethod";
			}else if (isGetAttributeMethod(unit)) {
				return "GetAttribute";
			} else if (isGetIntentExtraMethod(unit)) {
				return "GetIntentExtra";
			}else if (isPassOutMethod(unit)) {
				return "PassOutIntent";
			}
		}
		
		return "";
	}

	/**
	 * get the correct handler of target unit
	 * 
	 * @param methodUnderAnalysis
	 * @param appModel
	 * @param intentSummary
	 * @param unit
	 * @return
	 */
	@Override
	public UnitHandler getUnitHandler(Unit unit) {
		if (unit == null)
			return null;
		
		
		// set
		if (isCreateMethod(unit)) {
			return new CreateHandler();
		}else if (isInitIntentMethod(unit)) {
			return new SetAttributeHandler();
		}else if (isSetAttributeMethod(unit)) {
			return new SetAttributeHandler();
		} else if (isSetIntentExtraMethod(unit)) {
			return new SetIntentExtraHandler();
		}
		else if (isSetContentViewFunction(unit)) {
			return new SetContentFunctionHandler();

		} 
		// send out
		if (isSendIntent2ActivityMethod(unit)) {
			return new SendIntent2ActivityHandler();
		} else if (isSendIntent2ServiceMethod(unit)) {
			return new SendIntent2ServiceHandler();
		} else if (isSendIntent2ProviderMethod(unit)) {
			return new SendIntent2ProviderHandler();
		} else if (isSendIntent2ReceiverMethod(unit)) {
			return new SendIntent2ReceiverHandler();
		} else if (MyConfig.getInstance().getMySwithch().isWrapperAPISwitch()) {
			if (RAICCUtils.isWrapperMethods(unit))
				return new SendIntent2UnkownHandler();
		} else if (RAICCUtils.isIntentSenderCreation(unit)) {
			return  new ReceiveFromOutHandler();
		} else if (RAICCUtils.isPendingIntentCreation(unit)) {
			return new ReceiveFromOutHandler();
		}
		// inter-function
		if (isReceiveFromParatMethod(unit)) {
			return new ReceiveFromParaHandler();
		} else if (isReceiveFromRetValue(unit)) {
			return new ReceiveFromRetValueHandler();
		} else if (isComponentFinishMethods(unit)) {
			return new MethodReturnHandler();
		}
		
		if (isReceiveFromOutMethod(unit)) {
			return new ReceiveFromOutHandler();
		}else if (isGetAttributeMethod(unit)) {
			return new GetAttributeHandler();
		} else if (isGetIntentExtraMethod(unit)) {
			return new GetIntentExtraHandler();
		}
		return null;
	}

	/**
	 * judge isReceiveIntentFromPara
	 * 
	 * @param u
	 * @return
	 */
	@Override
	public boolean isReceiveFromParatMethod(Unit u) {
		boolean res = false;
		for (String s : objectIdentier) {
			String pattern = ".*@parameter\\d+: " + s + ".*";
			res |= Pattern.matches(pattern, u.toString());
		}
		return res;
	}

	/**
	 * judge isCreateIntentMethod
	 * 
	 * @param u
	 * @return
	 */
	@Override
	public boolean isCreateMethod(Unit u) {
		for (String s : objectIdentier) {
			if (u.toString().endsWith("new " + s)) {
				if (u instanceof JIfStmt)
					return false;
				if (u instanceof JGotoStmt)
					return false;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isStaticCreateMethod(Unit u) {
		if (MyConfig.getInstance().getMySwithch().isStaticFieldSwitch()) {
			if (u instanceof JAssignStmt) {
				// static assignment!!!!!!!!! search class fields
				if (u.toString().contains("ipcIntent"))
					return false;
				JAssignStmt ass = (JAssignStmt) u;
				for (String s : objectIdentier) {
					if (ass.containsFieldRef() && ass.getFieldRef().getType().toString().equals(s))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * judge isPassOutIntentMethod
	 * 
	 * @param u
	 * @return
	 */
	@Override
	public boolean isPassOutMethod(Unit u) {
		InvokeExpr invoke = SootUtils.getInvokeExp(u);
		if (invoke == null)
			return false;
		Iterator<Value> it = invoke.getArgs().iterator();
		while (it.hasNext()) {
			Value v = it.next();
			for (String s : objectIdentier) {
				if (v.getType().toString().contains(s))
					return true;
			}
		}
		return false;
	}

	/**
	 * isReceiveIntentFromRetValue
	 * 
	 * @param unit
	 * @return
	 */
	@Override
	public boolean isReceiveFromRetValue(Unit unit) {
		InvokeExpr invokStmt = SootUtils.getInvokeExp(unit);
		if (invokStmt == null)
			return false;
		if (SootUtils.hasSootActiveBody(invokStmt.getMethod())) {
			for (String s : objectIdentier) {
				if (invokStmt.getMethod().getReturnType().toString().equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isInitIntentMethod(Unit unit) {
		String u = unit.toString();
		if (u.toString().contains("android.content.Intent: void <init>")) {
			return true;
		}
		return false;
	}

	/**
	 * judge is_get_intent_extra_method
	 * 
	 * @param unit
	 * @return
	 */
	public static boolean isSetIntentExtraMethod(Unit unit) {
		String u = unit.toString();
		if (u.toString().contains("android.content.Intent") || u.toString().contains("android.os.Bundle")
				|| u.toString().contains("android.os.BaseBundle")) {
			if (u.toString().contains("goto ") || u.toString().contains("sgoto "))
				return false;
			if (u.toString().startsWith("if "))
				return false;
			for (int i = 0; i < ConstantUtils.setIntnetExtraMethods.length; i++) {
				if (u.toString().contains(ConstantUtils.setIntnetExtraMethods[i])){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isSetIntentExtraMethod(String u) {
		if (u.toString().contains("android.content.Intent") || u.toString().contains("android.os.Bundle")
				|| u.toString().contains("android.os.BaseBundle")) {
			if (u.toString().contains("goto ") || u.toString().contains("sgoto "))
				return false;
			if (u.toString().startsWith("if "))
				return false;
			for (int i = 0; i < ConstantUtils.putBundlleExtraMethods.length; i++) {
				if (u.toString().contains(ConstantUtils.putBundlleExtraMethods[i])){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * judge is_get_bundle_extra_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetBundleExtraMethod(String u) {
		if (u.toString().contains("android.content.Intent") || u.toString().contains("android.os.Bundle")) {
			if (u.toString().contains("goto ") || u.toString().contains("sgoto "))
				return false;
			if (u.toString().startsWith("if "))
				return false;
			for (int i = 0; i < ConstantUtils.getBundlleExtraMethods.length; i++) {
				if (u.toString().contains(ConstantUtils.getBundlleExtraMethods[i]))
					return true;
			}
		}
		return false;
	}

	/**
	 * judge is_Extra_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isExtraMethod(String u) {
		return isGetBundleExtraMethod(u) || isGetIntentExtraMethod(u);
	}

	/**
	 * judge is_get_intent_extra_method
	 * 
	 * @param unit
	 * @return
	 */
	public static boolean isGetIntentExtraMethod(Unit unit) {
		String u = unit.toString();
		if (u.toString().contains("android.content.Intent") || u.toString().contains("android.os.Bundle")
				|| u.toString().contains("android.os.BaseBundle")) {
			if (u.toString().contains("goto ") || u.toString().contains("sgoto "))
				return false;
			if (u.toString().startsWith("if "))
				return false;
			for (int i = 0; i < ConstantUtils.getIntnetExtraMethods.length; i++) {
				if (u.toString().contains(ConstantUtils.getIntnetExtraMethods[i]))
					return true;
			}
		}
		return false;
	}

	public static boolean isGetIntentExtraMethod(String u) {
		if (u.toString().contains("android.content.Intent") || u.toString().contains("android.os.Bundle")
				|| u.toString().contains("android.os.BaseBundle")) {
			if (u.toString().contains("goto ") || u.toString().contains("sgoto "))
				return false;
			if (u.toString().startsWith("if "))
				return false;
			for (int i = 0; i < ConstantUtils.getIntnetExtraMethods.length; i++) {
				if (u.toString().contains(ConstantUtils.getIntnetExtraMethods[i]))
					return true;
			}
		}
		return false;
	}

	public boolean isComponentFinishMethods(Unit u) {
		for (String s : ConstantUtils.componentOpMethods)
			if (u.toString().contains(s))
				return true;
		return false;
	}

	/**
	 * judge is_getAttribute_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetAttributeMethod(Unit u) {
		if (!(u instanceof JAssignStmt))
			return false;
		for (int i = 0; i < ConstantUtils.getAttributeMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.getAttributeMethods[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * judge is_getAttribute_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isSetAttributeMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.setAttributeMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.setAttributeMethods[i]))
				return true;
		}
		return false;
	}

	/**
	 * judge is_getAction_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetActionMethod(Unit u) {
		if (!(u instanceof JAssignStmt))
			return false;
		if (u.toString().contains(ConstantUtils.getAttributeMethods[0]))
			return true;
		return false;
	}

	/**
	 * judge is_getCategory_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetCategoryMethod(Unit u) {
		if (!(u instanceof JAssignStmt))
			return false;
		if (u.toString().contains(ConstantUtils.getAttributeMethods[1]))
			return true;
		return false;
	}

	/**
	 * judge is_getData_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetDataMethod(Unit u) {
		if (!(u instanceof JAssignStmt))
			return false;
		if (u.toString().contains(ConstantUtils.getAttributeMethods[2]))
			return true;
		if (u.toString().contains(ConstantUtils.getAttributeMethods[3]))
			return true;
		return false;
	}

	/**
	 * judge is_getType_method
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isGetTypeMethod(Unit u) {
		if (!(u instanceof JAssignStmt))
			return false;
		if (u.toString().contains(ConstantUtils.getAttributeMethods[4]))
			return true;
		return false;
	}

	/**
	 * get_type_of_intent_extra from m
	 * 
	 * @param m
	 * @return
	 */
	public static String getTypeOfIntentExtra(String m) {
		for (int i = 0; i < ConstantUtils.getIntnetExtraMethods.length; i++) {
			if (m.contains(ConstantUtils.getIntnetExtraMethods[i]))
				return ConstantUtils.intentExtraMethodTypes[i];
		}
		return null;
	}

	/**
	 * get_type_of_get_bundle_extra from m
	 * 
	 * @param m
	 * @return
	 */
	public static String getTypeOfGetBundleExtra(String m) {
		for (int i = 0; i < ConstantUtils.getBundlleExtraMethods.length; i++) {
			if (m.contains(ConstantUtils.getBundlleExtraMethods[i]))
				return ConstantUtils.bundleExtraMethodTypes[i];
		}
		return null;
	}

	/**
	 * get_type_of_set_bundle_extra from m
	 * 
	 * @param m
	 * @return
	 */
	public static String getTypeOfSetBundleExtra(String m) {
		for (int i = 0; i < ConstantUtils.putBundlleExtraMethods.length; i++) {
			if (m.contains(ConstantUtils.putBundlleExtraMethods[i]))
				return ConstantUtils.bundleExtraMethodTypes[i];
		}
		return null;
	}
	
	/**
	 * judge isSendIntentMethod
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isSendIntent2IccMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ActivityMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ActivityMethods[i]))
				return true;
		}
		for (int i = 0; i < ConstantUtils.sendIntent2ServiceMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ServiceMethods[i]))
				return true;
		}
		for (int i = 0; i < ConstantUtils.sendIntent2ReceiverMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ReceiverMethods[i]))
				return true;
		}
		for (int i = 0; i < ConstantUtils.sendIntent2ProviderMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ProviderMethods[i]))
				return true;
		}
		return false;
	}

	public static boolean isSendIntent2ActivityMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ActivityMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ActivityMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ReceiverMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ReceiverMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ReceiverMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ProviderMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ProviderMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ProviderMethods[i]))
				return true;
		}
		return false;
	}

	private boolean isSendIntent2ServiceMethod(Unit u) {
		for (int i = 0; i < ConstantUtils.sendIntent2ServiceMethods.length; i++) {
			if (u.toString().contains(ConstantUtils.sendIntent2ServiceMethods[i]))
				return true;
		}
		return false;
	}

	/**
	 * judge isReceiveIntentMethod
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isReceiveFromOutMethod(Unit u) {
		if (u.toString().endsWith("android.content.Intent getIntent()>()")) {
			if (u instanceof JIfStmt)
				return false;
			if (u instanceof JGotoStmt)
				return false;
			return true;
		}
		return false;
	}

	public boolean isSetContentViewFunction(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("void setContentView(int)")) {
			return true;
		}
		return false;
	}
}
