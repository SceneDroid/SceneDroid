package main.java.client.cg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.java.Analyzer;
import main.java.MyConfig;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.component.BroadcastReceiverModel;
import main.java.client.obj.model.component.ComponentModel;
import main.java.client.obj.model.component.Data;
import main.java.client.obj.model.component.IntentFilterModel;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.toolkits.scalar.UnitValueBoxPair;

public class DynamicReceiverCGAnalyzer extends Analyzer {
	private SootMethod m;

	public DynamicReceiverCGAnalyzer() {
		super();
	}

	/**
	 * store static value information get the init value of each static
	 * non-final variable
	 */
	@Override
	public void analyze() {
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
				if (!SootUtils.isNonLibClass(sc.getName()))
					continue;
			}
			for (SootMethod sm : sc.getMethods()) {
				m = sm;
				List<Unit> units = SootUtils.getUnitListFromMethod(m);
				for(Unit u: units){
					if (SootUtils.isBroadCastRegisterMethods(u.toString())) {
						InvokeExpr exp = SootUtils.getInvokeExp(u);
						if (exp != null && exp.getArgCount() == 2) {
							String receiverName = analyzeBroadCastReceiver(u, exp.getArg(0));
							if (receiverName.equals(""))
								continue;
							IntentFilterModel filterModel = analyzeIntentFilter(u, exp.getArg(1));
							ComponentModel receiver = new BroadcastReceiverModel(appModel);
							if (appModel.getApplicationClassNames().contains(receiverName)) {
								receiver = new BroadcastReceiverModel(appModel);
								receiver.addIntentFilter(filterModel);
								receiver.setComponetName(receiverName);
								appModel.getComponentMap().put(receiverName, receiver);
							} else {
								receiverName = sc.getName() + "_dynamicReceiver";
								if (appModel.getComponentMap().containsKey(receiverName)) {
//									if (appModel.getActivityMap().get(receiverName) != null) {
//										filterModel.getCategory_list().add("android.intent.category.DEFAULT");
//									}
									appModel.getComponentMap().get(receiverName).addIntentFilter(filterModel);
								}
							}
							appModel.getRecieverMap().put(receiverName, receiver);
						}
					}
				}
			}
		}
		 System.out.println("DynamicIntentFilterAnalyzer finish\n");
	}

	/**
	 * analyzeIntentFilter
	 * 
	 * @param u
	 * @param filterValue
	 * @return
	 */
	private IntentFilterModel analyzeIntentFilter(Unit u, Value filterValue) {
		IntentFilterModel filterModel = new IntentFilterModel();
		List<Unit> def_var_list = SootUtils.getDefOfLocal(m.getSignature(), filterValue, u);

		for (Unit defUnit : def_var_list) {
			List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(m.getSignature(), defUnit);
			for (UnitValueBoxPair useUnit : use_var_list) {
				Unit currentUnit = useUnit.getUnit();
				Data data = new Data();
				if (currentUnit.toString().contains("<init>(android.content.IntentFilter)")) {
					// ?
				} else if (currentUnit.toString().contains("<init>(java.lang.String)")) {
					addAttribute2Filter(currentUnit, 0, filterModel.getAction_list());
				} else if (currentUnit.toString().contains("<init>(java.lang.String,java.lang.String)")) {
					addAttribute2Filter(currentUnit, 0, filterModel.getAction_list());
					addAttribute2Filter(currentUnit, 0, filterModel.getDatatype_list());
				} else if (currentUnit.toString().contains("addAction(java.lang.String)")) {
					addAttribute2Filter(currentUnit, 0, filterModel.getAction_list());
				} else if (currentUnit.toString().contains("addCategory(java.lang.String)")) {
					addAttribute2Filter(currentUnit, 0, filterModel.getCategory_list());
				} else if (currentUnit.toString().contains("addDataAuthority(java.lang.String,java.lang.String)")) {
					data.setHost(getResult(currentUnit, 0));
					data.setPort(getResult(currentUnit, 0));
				} else if (currentUnit.toString().contains("addDataPath(java.lang.String,int)")) {
					data.setPath(getResult(currentUnit, 0));
					// type?
				} else if (currentUnit.toString().contains("addDataScheme(java.lang.String)")) {
					data.setScheme(getResult(currentUnit, 0));
				} else if (currentUnit.toString().contains("addDataType(java.lang.String)")) {
					addAttribute2Filter(currentUnit, 0, filterModel.getDatatype_list());
				} else if (currentUnit.toString().contains("setPriority(int)")) {
					// ?
				}
				if (data.toString().length() > 0)
					filterModel.getData_list().add(data);

			}
		}
		return filterModel;
	}

	/**
	 * analyzeBroadCastReceiver
	 * 
	 * @param u
	 * @param receiverValue
	 * @return
	 */
	private String analyzeBroadCastReceiver(Unit u, Value receiverValue) {
		String receiverName = "";
		List<Unit> def_var_list = SootUtils.getDefOfLocal(m.getSignature(), receiverValue, u);
		for (Unit defUnit : def_var_list) {
			receiverName = SootUtils.getTargetClassOfUnit(m, defUnit);
		}
		return receiverName;
	}

	/**
	 * add attribute to intent filter
	 * 
	 * @param currentUnit
	 * @param id
	 * @param attrList
	 */
	private void addAttribute2Filter(Unit currentUnit, int id, Set<String> attrList) {
		Value inputVar = getInputVar(currentUnit, 0);
		if (inputVar != null) {
			ValueObtainer vo = new ValueObtainer(m.getSignature(), "");
			Set<String> resSet = new HashSet<>(vo.getValueofVar(inputVar, currentUnit, 0).getValues());
			attrList.addAll(resSet);
		}

	}

	private String getResult(Unit currentUnit, int id) {
		Value inputVar = getInputVar(currentUnit, 0);
		if (inputVar != null) {
			ValueObtainer vo = new ValueObtainer(m.getSignature(), "");
			Set<String> resSet = new HashSet<>(vo.getValueofVar(inputVar, currentUnit, 0).getValues());
			if (resSet.size() > 0)
				return resSet.iterator().next();
		}
		return "";
	}

	/**
	 * tool getInputVar
	 * 
	 * @param u
	 * @param i
	 * @return
	 */
	private Value getInputVar(Unit u, int i) {
		Value inputVar = null;
		if (u instanceof JAssignStmt) {
			JAssignStmt as = (JAssignStmt) u;
			if (as.getInvokeExpr().getArgCount() > i)
				inputVar = as.getInvokeExpr().getArg(i);
		} else if (u instanceof JInvokeStmt) {
			JInvokeStmt inv = (JInvokeStmt) u;
			if (inv.getInvokeExpr().getArgCount() > i)
				inputVar = inv.getInvokeExpr().getArg(i);
		}
		return inputVar;
	}
}
