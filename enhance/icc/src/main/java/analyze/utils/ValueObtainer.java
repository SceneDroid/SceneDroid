package main.java.analyze.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.model.analyzeModel.ParameterSource;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.model.sootAnalysisModel.NestableObj;
import main.java.analyze.utils.ValueObtainer;
import soot.Body;
import soot.Local;
import soot.PrimType;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.internal.SPhiExpr;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.toolkits.scalar.ValueUnitPair;

/**
 * value analyze
 * 
 * @author 79940
 *
 */
public class ValueObtainer {
	private String flag;
	private Context contextsValue;
	private String method_name;
	private Map<Value, List<Value>> dependentMap;
	private Counter counter;

	public ValueObtainer(String method_name, String flag) {
		this(method_name, flag, new Context(), new Counter());
	}

	public ValueObtainer(String method_name, String flag, Counter counter) {
		this(method_name, flag, new Context(), counter);
	}

	public ValueObtainer(String method_name, String flag, Context contextsValue, String targetname, Counter counter) {
		this(method_name, flag, new Context(), counter);
	}

	public ValueObtainer(String method_name, String flag, Context contextsValue, Counter counter) {
		this.method_name = method_name;
		this.flag = flag;
		this.contextsValue = contextsValue;
		this.dependentMap = new HashMap<Value, List<Value>>();
		this.counter = counter;
	}

	/**
	 * get value of strVal in unit u
	 * 
	 * @param strVal
	 * @param u
	 * @return
	 */
	public NestableObj getValueofVar(Value strVal, Unit u, int depth) {
//		System.out.println(depth+"  " +strVal.toString() +"  "+u.toString());
		if (depth >= 5)
			return new NestableObj("");
		NestableObj resList = new NestableObj(strVal.toString());
		counter.setGetVarDepth(counter.getGetVarDepth() + 1);
		if (counter.getGetVarDepth() > ConstantUtils.GETVALUELIMIT)
			return resList;

		if (strVal instanceof Constant) {
			resList.addValue(strVal.toString());// signature,
		} else {
			if (!(strVal instanceof Local))
				return null;
			List<Unit> def_var_list = SootUtils.getDefOfLocal(method_name, strVal, u);
			if (def_var_list.size() > 0) {
				for (Unit defUnit : def_var_list) {
					if (defUnit == u)
						continue;
					if (defUnit instanceof JAssignStmt) {
						JAssignStmt jas2 = (JAssignStmt) defUnit;
						Value val = jas2.rightBox.getValue();
						if (val instanceof JInstanceFieldRef) {
							JInstanceFieldRef jifr = (JInstanceFieldRef) val;
							if (jifr.getBase() == strVal)
								continue;
							NestableObj base = getValueofVar(jifr.getBase(), defUnit, 0);
							NestableObj field = base.getObjs().get(jifr.getField().getSignature());
							if (field != null) {
								resList.setValues(field.getValues());
							} else {
								resList.addValue("new " + val.getType());
							}
						} else if (val instanceof Constant) {
							resList.addValue(val.toString());// signature,
						} else if (val instanceof StaticFieldRef) { // static
							SootField field = ((StaticFieldRef) val).getField();
							if (Global.v().getAppModel().getStaticRefSignature2initAssignMap()
									.containsKey(field.getSignature())) {
								String staval = Global.v().getAppModel().getStaticRefSignature2initAssignMap()
										.get(field.getSignature());
								resList.addValue(staval);
							} else
								resList.addValue("new " + val.getType());
						} else if (val instanceof JVirtualInvokeExpr || val instanceof JStaticInvokeExpr) {
							NestableObj obj = null;
							try {
								obj = stringApisOperation(defUnit, depth);
							} catch (Exception e) {

							}
							if (obj != null)
								resList.setValues(obj.getValues());
							else {
								if (val.toString().contains("getName()")
										|| val.toString().contains("getCanonicalName()")
										|| val.toString().contains("toString()")
										|| val.toString().contains("getClass()")) {
									if (val instanceof JVirtualInvokeExpr) {
										resList.setValues(getValueofVar(((AbstractInstanceInvokeExpr) val).getBase(),
												defUnit, depth + 1).getValues());
										return resList;
									}
								} else if (val.toString().contains("java.lang.Class forName(java.lang.String)")
										|| val.toString().contains("android.net.Uri parse(java.lang.String)")) {
									if (val instanceof JStaticInvokeExpr) {
										resList.setValues(getValueofVar(((JStaticInvokeExpr) val).getArg(0), defUnit,
												depth + 1).getValues());
										return resList;
									}
								}else if(val.toString().contains("getPackageName()")){
									resList.addValue(Global.v().getAppModel().getPackageName());
									return resList;
								}
								JAssignStmt ass = (JAssignStmt) defUnit;
								List<Body> bodys = SootUtils.getBodySetofMethod(ass.getInvokeExpr().getMethod());
								for (Body b : bodys) {
									try {
										resList.setValues(handleCallerEdgePara(defUnit, ass.getInvokeExpr(),
												b.getMethod(), depth + 1).getValues());
									} catch (StackOverflowError e) {
									}
								}
							}
						} else if (val instanceof InvokeExpr) {
							InvokeExpr inv = (InvokeExpr) val;
							List<Body> bodys = SootUtils.getBodySetofMethod(inv.getMethod());
							for (Body b : bodys)
								resList.setValues(handleCallerEdgePara(defUnit, inv, b.getMethod(), depth).getValues());
						} else if (val instanceof JNewExpr) {
							if (val.toString().contains("new java.lang.StringBuilder")
									|| val.toString().contains("new java.lang.String")) {
								List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(method_name, defUnit);
								String sbString = "";
								for (int i = 0; i < use_var_list.size(); i++) {
									Unit useUnit = use_var_list.get(i).getUnit();
									if (useUnit.toString().contains("<init>") || useUnit.toString().contains("append")) {
										InvokeExpr invokeExpr = SootUtils.getInvokeExp(useUnit);
										if (invokeExpr != null && invokeExpr.getArgCount() > 0) {
											Value initSb = invokeExpr.getArg(0);
											List<String> tempVals = getValueofVar(initSb, useUnit, depth + 1)
													.getValues();
											if (tempVals.size() > 0)
												sbString += tempVals.get(0);
										}
									}
								}
								resList.addValue(sbString);// signature,
							} else {
								// Utils.printInfo("other new expr " +val);
								// new object -- unkonwn
								resList.addValue(val.toString());// signature,
							}
						} else if (val instanceof JNewArrayExpr) {
							resList.addValue(val.toString());// signature,
						} else if (val instanceof SPhiExpr) {
							SPhiExpr sp = (SPhiExpr) val;
							for (ValueUnitPair arg : sp.getArgs()) {
								if (dependentMap.containsKey(arg.getValue())
										&& dependentMap.get(arg.getValue()).contains(strVal))
									continue;
								if (!dependentMap.containsKey(strVal))
									dependentMap.put(strVal, new ArrayList<Value>());
								dependentMap.get(strVal).add(arg.getValue());
								resList.setValues(getValueofVar(arg.getValue(), defUnit, depth + 1).getValues());
							}
						} else if (val instanceof JimpleLocal) {
							try {
								resList.setValues(getValueofVar(val, defUnit, depth + 1).getValues());
							} catch (StackOverflowError e) {
								System.err.println("StackOverflowError");
							}
						} else if (val instanceof JCastExpr) {
							JCastExpr jc = (JCastExpr) val;
							resList.setValues(getValueofVar(jc.getOp(), defUnit, depth + 1).getValues());
						}
					}
					// para $r0 := @parameter0: java.lang.String
					else if (defUnit instanceof JIdentityStmt) {
						// the id number for innerclass is incorrect
						// TODO
						JIdentityStmt jid = (JIdentityStmt) defUnit;
						if (jid.getRightOp() instanceof ParameterRef) {
							ParameterRef pr = (ParameterRef) jid.getRightOp();
							if (contextsValue.getObjs().size() > pr.getIndex() + 1) {
								resList = contextsValue.getObjs().get(pr.getIndex() + 1);
								// System.out.println(method_name+" "+contextsValue.getObjs());

							}
							InvokeExpr exp = SootUtils.getInvokeExp(u);
							if (exp != null) {
								int useLocationId = 1;
								for (Value argValue : exp.getArgs()) {
									if (argValue.equals(strVal))
										break;
									useLocationId++;
								}
								if (contextsValue.getObjs().size() > pr.getIndex() + 1) {
									resList = contextsValue.getObjs().get(pr.getIndex() + 1);
								} else {
									// only primary or string can be analyzed
									if (isValidType(pr.getType())) {
										ParameterSource value = new ParameterSource(method_name, u, pr.getIndex() + 1,
												useLocationId);
										Global.v().getAppModel().addUnit2ParameterSource(u, value);
									}
								}
							}
						} else if (jid.getRightOp() instanceof ThisRef) {
							if (contextsValue.getObjs().size() > 0)
								resList = contextsValue.getObjs().get(0);
						}
					}
				}
			}
		}
		return resList;
	}

	private boolean isValidType(Type type) {
		if (type instanceof PrimType)
			return true;
		if (type.toString().contains("java.lang."))
			return true;
		if (type.toString().contains("android.content.Context"))
			return true;
		for (String str : ConstantUtils.fragmentClasses) {
			if (type.toString().equals(str))
				return true;
		}
		for (String str : ConstantUtils.dialogFragmentClasses) {
			if (type.toString().equals(str))
				return true;
		}
		for (String str : ConstantUtils.componentClasses) {
			if (type.toString().equals(str))
				return true;
		}
		// System.out.println(type);
		return false;
	}

	/**
	 * get value of strVal in unit u
	 * 
	 * @param strVal
	 * @param u
	 * @return
	 */
	public String getTypeofValue(Value strVal, Unit u) {
		String type = "";
		if (strVal instanceof Constant) {
			type = "String1";
		} else {
			if (!(strVal instanceof Local))
				return null;
			List<Unit> def_var_list = SootUtils.getDefOfLocal(method_name, strVal, u);
			if (def_var_list.size() > 0) {
				for (Unit defUnit : def_var_list) {
					if (defUnit instanceof JAssignStmt) {
						Value left = ((JAssignStmt) defUnit).getLeftOp();
						type = left.getType().toString();
					}
					// para $r0 := @parameter0: java.lang.String
					else if (defUnit instanceof JIdentityStmt) {
						type = defUnit.toString().split("\\.")[-1];
					}
				}
			}
		}
		return type;
	}

	/**
	 * get context of method invocation inv
	 * 
	 * @param u
	 * @param inv
	 * @param sm
	 * @param method_name
	 * @param depth
	 * @return
	 */
	public Context getContextValue(Unit u, InvokeExpr inv, SootMethod sm, String method_name, int depth) {
		Context cnx = new Context();
		ValueObtainer voc = new ValueObtainer(method_name, flag, contextsValue, counter);

		if (inv instanceof AbstractInstanceInvokeExpr) {
			AbstractInstanceInvokeExpr sp = (AbstractInstanceInvokeExpr) inv;
			NestableObj base = voc.getValueofVar(sp.getBase(), u, depth + 1);
			base.setName("base");
			cnx.addObj(base);
		} else {
			cnx.addObj(new NestableObj("base"));
		}
		for (Value arg : inv.getArgs()) {
			NestableObj o = new NestableObj(arg.toString());
			NestableObj res = voc.getValueofVar(arg, u, depth + 1);
			if (res != null) {
				o.setValues(res.getValues());
				cnx.addObj(o);
				getFiledOfObj(u, arg, voc, o, sm.getSignature());
			}
		}
		return cnx;
	}

	/**
	 * get value after string Apis Operation
	 * 
	 * @param u
	 * @param depth
	 * @return
	 */
	private NestableObj stringApisOperation(Unit u, int depth) {
		if (!MyConfig.getInstance().getMySwithch().isStringOpSwitch())
			return null;
		NestableObj resVal = new NestableObj(u.toString());
		if (u.toString().contains("toString")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			Value invoke = jas1.rightBox.getValue();
			if (invoke instanceof AbstractInstanceInvokeExpr || invoke instanceof JStaticInvokeExpr) {
				Value strVal = getValueFromInvokeExpr(invoke);
				resVal = getValueofVar(strVal, u, depth + 1);
			}
		} else if (u.toString().contains("append") || u.toString().contains("concat")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			if (jas1.rightBox.getValue() instanceof AbstractInstanceInvokeExpr) {
				AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.rightBox.getValue();
				Value lv = invokeStmt.getBase();
				if (invokeStmt.getArgCount() == 0)
					resVal = getValueofVar(lv, u, depth + 1);
				else {
					Value rv = invokeStmt.getArg(0);
					for (String l : getValueofVar(lv, u, depth + 1).getValues()) {
						if (u.toString().contains("append") &&lv == jas1.leftBox.getValue()) {
							resVal.addValue(l);
							continue;
						}
						for (String r : getValueofVar(rv, u, depth + 1).getValues())
							resVal.addValue(l + r);
					}
				}
			}
		} else if (u.toString().contains("valueOf") || u.toString().contains("copyValueOf")) {
			InvokeExpr InvokeExpr = SootUtils.getInvokeExp(u);
			if (InvokeExpr.getArgCount() > 0) {
				Value strVal = InvokeExpr.getArg(0);
				for (String val: getValueofVar(strVal, u, depth + 1).getValues())
					resVal.addValue(val);
			}
		} else if (u.toString().contains("substring")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) jas1.rightBox.getValue();
			Value strVal = invokeStmt.getBase();
			int b = 0;
			if (invokeStmt.getArgCount() > 0) {
				NestableObj obj = getValueofVar(invokeStmt.getArg(0), u, depth + 1);
				if (obj.getValues().size() > 0) {
					String str_b = obj.getValues().get(0);
					if (StringUtils.isInteger(str_b))
						try {
							b = Integer.parseInt(str_b);
						} catch (Exception NumberFormatException) {
						}
					if (b < 0)
						b = 0;
				}
			}
			int e = 0;
			if (invokeStmt.getArgCount() > 1) {
				NestableObj obj = getValueofVar(invokeStmt.getArg(1), u, depth + 1);
				if (obj.getValues().size() > 0) {
					String str_e = obj.getValues().get(0);
					if (StringUtils.isInteger(str_e))
						try {
							e = Integer.parseInt(str_e);
						} catch (Exception NumberFormatException) {
						}

					if (e < 0)
						e = 0;
				}
			}
			for (String old : getValueofVar(strVal, u, depth + 1).getValues()) {
				if (old.length() == 0)
					continue;
				if (invokeStmt.getArgCount() == 1) {
					if (b >= old.length())
						b = old.length() - 1;
					resVal.addValue(old.substring(b));
				}
				if (invokeStmt.getArgCount() == 2) {
					if (b > old.length())
						b = old.length();
					if (e == 0 || e > old.length())
						e = old.length();
					resVal.addValue(old.substring(b, e));
				}
			}
		} else if (u.toString().contains("toLowerCase")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			Value invoke = jas1.rightBox.getValue();
			if (invoke instanceof AbstractInstanceInvokeExpr || invoke instanceof JStaticInvokeExpr) {
				Value strVal = getValueFromInvokeExpr(invoke);
				for (String old : getValueofVar(strVal, u, depth + 1).getValues())
					resVal.addValue(old.toLowerCase());
			}
		} else if (u.toString().contains("toUpperCase")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			Value invoke = jas1.rightBox.getValue();
			if (invoke instanceof AbstractInstanceInvokeExpr || invoke instanceof JStaticInvokeExpr) {
				Value strVal = getValueFromInvokeExpr(invoke);
				for (String old : getValueofVar(strVal, u, depth + 1).getValues())
					resVal.addValue(old.toUpperCase());
			}
		} else if (u.toString().contains("trim")) {
			JAssignStmt jas1 = (JAssignStmt) u;
			Value invoke = jas1.rightBox.getValue();
			if (invoke instanceof AbstractInstanceInvokeExpr || invoke instanceof JStaticInvokeExpr) {
				Value strVal = getValueFromInvokeExpr(invoke);
				for (String old : getValueofVar(strVal, u, depth + 1).getValues())
					resVal.addValue(old.trim());
			}
		} else {
			return null;
		}
		return resVal;
	}

	/**
	 * get Value From InvokeExpr
	 * 
	 * @param invoke
	 * @return
	 */
	private Value getValueFromInvokeExpr(Value invoke) {
		if (invoke instanceof AbstractInstanceInvokeExpr) {
			AbstractInstanceInvokeExpr invokeStmt = (AbstractInstanceInvokeExpr) invoke;
			return invokeStmt.getBase();
		} else if (invoke instanceof JStaticInvokeExpr) {
			JStaticInvokeExpr invokeStmt = (JStaticInvokeExpr) invoke;
			if (invokeStmt.getArgCount() > 0) {
				return invokeStmt.getArg(0);
			}
		}
		return null;
	}

	/**
	 * handle caller edge para
	 * 
	 * @param u
	 * @param inv
	 * @param sm
	 * @param depth
	 * @return
	 */
	private NestableObj handleCallerEdgePara(Unit u, InvokeExpr inv, SootMethod sm, int depth) {
		NestableObj resSet = new NestableObj(sm.getSignature());
		// get value from return value
		Context contexts = getContextValue(u, inv, sm, method_name, depth + 1);
		List<Body> bodys = SootUtils.getBodySetofMethod(sm);
		for (Body b : bodys) {
			ValueObtainer vo = new ValueObtainer(sm.getSignature(), flag, contexts, counter);
			List<Unit> rets = SootUtils.getRetList(b.getMethod());
			if (rets != null) {
				for (Unit ret_u : rets) {
					ValueBox valbox = ((JReturnStmt) ret_u).getUseBoxes().get(0);
					NestableObj ress = vo.getValueofVar(valbox.getValue(), ret_u, depth + 1);
					resSet.setValues(ress.getValues());
				}
			}
		}
		return resSet;
	}

	/**
	 * get Filed Of Obj
	 * 
	 * @param argUnit
	 * @param arg
	 * @param voc
	 * @param father
	 * @param father_sig
	 */
	private void getFiledOfObj(Unit argUnit, Value arg, ValueObtainer voc, NestableObj father, String father_sig) {
		List<Unit> units = SootUtils.getDefOfLocal(method_name, arg, argUnit);
		for (Unit u : units) {
			List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(method_name, u);
			for (UnitValueBoxPair use : use_var_list) {
				Unit useUnit = use.getUnit();
				if (useUnit instanceof JAssignStmt) {
					JAssignStmt jas = (JAssignStmt) useUnit;
					if (jas.containsFieldRef()) {
						NestableObj o = null;
						if (father.getObjs().containsKey(jas.getFieldRef().getField().getSignature()))
							o = father.getObjs().get(jas.getFieldRef().getField().getSignature());
						else {
							o = new NestableObj(jas.getFieldRef().getField().getSignature());
							father.addObj(o);
						}
						NestableObj res = voc.getValueofVar(jas.getRightOp(), useUnit, 0);
						if (res != null)
							o.setValues(res.getValues());
					}
				}
			}
		}
	}
}
