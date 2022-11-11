package main.java.client.obj.target.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.client.obj.AnalyzerHelper;
import main.java.client.obj.unitHnadler.CreateHandler;
import main.java.client.obj.unitHnadler.ReceiveFromParaHandler;
import main.java.client.obj.unitHnadler.ReceiveFromRetValueHandler;
import main.java.client.obj.unitHnadler.UnitHandler;
import main.java.client.obj.unitHnadler.fragment.AddFunctionHandler;
import main.java.client.obj.unitHnadler.fragment.AddTabFunctionHandler;
import main.java.client.obj.unitHnadler.fragment.AddToBackStackHandler;
import main.java.client.obj.unitHnadler.fragment.BeginTransactionHandler;
import main.java.client.obj.unitHnadler.fragment.CommitHandler;
import main.java.client.obj.unitHnadler.fragment.DialogShowHandler;
import main.java.client.obj.unitHnadler.fragment.GetFragmentHandler;
import main.java.client.obj.unitHnadler.fragment.LoadFunctionHandler;
import main.java.client.obj.unitHnadler.fragment.ReplaceFunctionHandler;
import main.java.client.obj.unitHnadler.fragment.SetContentFunctionHandler;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIfStmt;

public class FragmentAnalyzerHelper implements AnalyzerHelper {
	public List<String> objectIdentier;

	public FragmentAnalyzerHelper() {
		objectIdentier = new ArrayList<String>();
		objectIdentier.add("android.support.v4.app.FragmentManager");
		objectIdentier.add("android.app.FragmentManager");
		objectIdentier.add("androidx.fragment.app.FragmentManager");
	}

	@Override
	public List<String> getObjectIdentier() {
		return this.objectIdentier;
	}

	@Override
	public boolean isMyTarget(Unit u) {
		if (isLoadFunction(u)) {
			return true;
		}
		if (isSetContentViewFunction(u)) {
			return true;
		}
		if (MyConfig.getInstance().getMySwithch().isAdapterSwitch()) {
			if (isAddTabFunction(u)) {
				return true;
			}
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
		if (isCreateMethod(unit)) {
			return true;
		} else if (isStaticCreateMethod(unit)) {
			return true;
		} else if (isReceiveFromParatMethod(unit)) {
			return true;
		} else if (isReceiveFromRetValue(unit)) {
			return true;
		} else if (isGetFragmentFunction(unit)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isWarpperTopTargetUnit(Unit unit) {
		// TODO Auto-generated method stub
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
		if (isCreateMethod(unit)) {
			return "CreateMethod";
		} else if (isStaticCreateMethod(unit)) {
			return "StaticCreateMethod";
		} else if (isReceiveFromParatMethod(unit)) {
			return "ReceiveFromParatMethod";
		} else if (isReceiveFromRetValue(unit)) {
			return "ReceiveFromRetValue";

			// static start
		} else if (isLoadFunction(unit)) {
			return "LoadFunction";
		} else if (isSetContentViewFunction(unit)) {
			return "setContentView";
		} else if (isAddTabFunction(unit)) {
			return "AddTab";

			// dynamic start
		} else if (isGetFragmentFunction(unit)) {
			return "getFragment";
		} else if (isAddFunction(unit)) {
			return "addFunction";
		} else if (isReplaceFunction(unit)) {
			return "replaceFunction";
		} else if (isBeginTransactionFunction(unit)) {
			return "beginTransaction";
		} else if (isAddToBackStackFunction(unit)) {
			return "addToBackStack";
		} else if (isCommit(unit)) {
			return "commit";
		} else if (isDialogShow(unit)) {
			return "dialogShow";

		} else if (isPassOutMethod(unit)) {
			return "PassOut";
		}
		// else if (isComponentFinishMethods(unit)) {
		// return "componentReturn";
		// }
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
		if (isCreateMethod(unit)) {
			return new CreateHandler();
		} else if (isReceiveFromParatMethod(unit)) {
			return new ReceiveFromParaHandler();
		} else if (isReceiveFromRetValue(unit)) {
			return new ReceiveFromRetValueHandler();
			// }else if (isComponentFinishMethods(unit)) {
			// return new MethodReturnHandler();

			// static start
		} else if (isLoadFunction(unit)) {
			return new LoadFunctionHandler();
		} else if (isSetContentViewFunction(unit)) {
			return new SetContentFunctionHandler();

		} else if (isAddTabFunction(unit)) {
			return new AddTabFunctionHandler();

			// dynamic start
		} else if (isGetFragmentFunction(unit)) {
			return new GetFragmentHandler();
		} else if (isAddFunction(unit)) {
			return new AddFunctionHandler();
		} else if (isReplaceFunction(unit)) {
			return new ReplaceFunctionHandler();
		} else if (isAddToBackStackFunction(unit)) {
			return new AddToBackStackHandler();
		} else if (isBeginTransactionFunction(unit)) {
			return new BeginTransactionHandler();
		} else if (isCommit(unit)) {
			return new CommitHandler();

		} else if (isDialogShow(unit)) {
			return new DialogShowHandler();
		}

		return null;
	}

	private boolean isDialogShow(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("show(")) {
			if (SootUtils.isDialogFragmentClass(invMethod.getMethodRef().declaringClass()))
				return true;
		}
		return false;
	}

	private boolean isGetFragmentFunction(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("app.FragmentManager getSupportFragmentManager()")) {
			return true;
		} else if (invMethod.toString().contains("app.FragmentManager getFragmentManager()")) {
			return true;
		} else if (invMethod.toString().contains("app.FragmentManager getChildFragmentManager()")) {
			return true;
		} 
		return false;
	}

	private boolean isCommit(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("app.FragmentTransaction: int commit()")) {
			return true;
		} else if (invMethod.toString().contains("app.FragmentTransaction: int commitAllowingStateLoss()")) {
			return true;
		}
		return false;
	}

	private boolean isBeginTransactionFunction(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("app.FragmentTransaction beginTransaction()")) {
			return true;
		}
		return false;
	}

	public boolean isAddTabFunction(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("void addTab(com.actionbarsherlock.app.ActionBar$Tab,java.lang.Class,int)")) {
			return true;
		}
		if (invMethod.toString().contains("void addTab(com.actionbarsherlock.app.ActionBar$Tab,java.lang.Class)")) {
			return true;
		}
		return false;
	}

	public boolean isLoadFunction(Unit unit) {
		InvokeExpr invMethod = SootUtils.getSingleInvokedMethod(unit);
		if (invMethod == null)
			return false;
		if (invMethod.toString().contains("void loadHeadersFromResource(int,java.util.List)")) {
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

	private boolean isReplaceFunction(Unit unit) {
		if (unit.toString().contains("app.FragmentTransaction replace("))
			return true;
		return false;
	}

	private boolean isAddFunction(Unit unit) {
		if (unit.toString().contains("app.FragmentTransaction add("))
			return true;
		return false;
	}

	private boolean isAddToBackStackFunction(Unit unit) {
		if (unit.toString().contains("app.FragmentTransaction addToBackStack("))
			return true;
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
	 * isComponentFinishMethods
	 * 
	 * @param u
	 * @return
	 */
	public static boolean isComponentFinishMethods(Unit u) {
		for (String s : ConstantUtils.componentOpMethods)
			if (u.toString().contains(s))
				return true;
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

			String className = v.getType().toString();
			if (className.length() == 0)
				continue;

			SootClass cls = Scene.v().getSootClass(className);
			if (SootUtils.isFragmentClass(cls))
				return true;
		}
		return false;
	}

}
