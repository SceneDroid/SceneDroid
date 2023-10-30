package main.java.client.obj.unitHnadler.ictg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.analyze.model.analyzeModel.Attribute;
import main.java.analyze.model.analyzeModel.ObjectSummaryModel;
import main.java.analyze.model.sootAnalysisModel.Context;
import main.java.analyze.model.sootAnalysisModel.Counter;
import main.java.analyze.utils.CollectionUtils;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.ValueObtainer;
import main.java.client.obj.model.component.BundleType;
import main.java.client.obj.model.component.ExtraData;
import main.java.client.obj.model.ctg.IntentSummaryModel;
import main.java.client.obj.target.ctg.CTGAnalyzerHelper;
import main.java.client.obj.unitHnadler.UnitHandler;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.toolkits.scalar.UnitValueBoxPair;

public class SetIntentExtraHandler extends UnitHandler {

	IntentSummaryModel intentSummary;

	@Override
	public void handleSingleObject(ObjectSummaryModel singleObject) {
		this.intentSummary = (IntentSummaryModel) singleObject;
		this.intentSummary.getDataHandleList().add(unit);
		setExtraAPIAnalyze(unit); // extra
	}

	@Override
	public void handleSingleObject(Context context, ObjectSummaryModel singleObject) {
		this.context = context;
		this.intentSummary = (IntentSummaryModel) singleObject;
		this.intentSummary.getDataHandleList().add(unit);
		setExtraAPIAnalyze(unit); // extra
	}

	@Override
	public void handleSingleObject(Context oldContextwithRealValue, ObjectSummaryModel singleObject, Unit targetUnit) {
		this.oldContextwithRealValue = oldContextwithRealValue;
		this.intentSummary = (IntentSummaryModel) singleObject;
		this.intentSummary.getDataHandleList().add(unit);
		this.targetUnit = targetUnit;
		setExtraAPIAnalyze(unit); // extra
	}

	/**
	 * calculate the value set of extra from set stmt
	 * 
	 * @param u
	 * @param singleFrag
	 * @throws Exception 
	 */
	void setExtraAPIAnalyze(Unit u) {
		// step1: get type of extra through the assignment
		String type = CTGAnalyzerHelper.getTypeOfSetBundleExtra(u.toString());
		if(type == null){
			InvokeExpr invokStmt = SootUtils.getInvokeExp(u);
			List<Type> types = invokStmt.getMethodRef().getParameterTypes();
			type = types.get(types.size() - 1).toString();
			type = type.split("\\.")[type.split("\\.").length - 1];
		}
		Map<String, List<ExtraData>> param_list = new HashMap<String, List<ExtraData>>();
		if (SootUtils.isBundleExtra(type)) {
			param_list = getParamListBundle(u);
			for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
				for (ExtraData ed : en.getValue()){
					BundleType bundle_type = genBundleType(u,ed,0);
					if (bundle_type == null) {
						param_list = null;
						return;
					}
					bundle_type.setType(type);
					ed.setType(bundle_type);
				}
			}
		} else if (SootUtils.isExtrasExtra(type)) {
			param_list = getParamListBundle(u);
			BundleType bundle_type = null;
			try {
				bundle_type = genBundleType(u,null,0);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (bundle_type == null) {
				param_list = null;
				return;
			}
			List<ExtraData> bundleList = new ArrayList<ExtraData>(bundle_type.getExtraDatas()); 
			param_list.put(u.toString(), bundleList);
		} else if (SootUtils.isParOrSerExtra(type)) {
			param_list = getParamListNormal(u);
			for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
				for (ExtraData ed : en.getValue()) {
					ed.setType(type);
					ed.setObjName(getObjectNameforReflection(u));
				}
			}
		} else {
			param_list = getParamListNormal(u);
			for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
				for (ExtraData ed : en.getValue())
					ed.setType(type);
			}
		}
		// entry class
		// appModel.ops.addSendExtraDataItemToMap(methodName, u, className,
		// param_list);
		addExtraValue2Set(methodUnderAnalyze, u, methodSig, param_list, intentSummary.getSetExtrasValueList());
	}
	/**
	 * get param list
	 * 
	 * @param u
	 * @return
	 */
	public Map<String, List<ExtraData>> getParamListNormal(Unit u) {
		Value key = null;
		Value val = null;
		Map<String, List<ExtraData>> param_list = new HashMap<String, List<ExtraData>>();
		
		int idKey = 0, idVal = 1;
		Context objContextInnerKey = new Context();
		if (oldContextwithRealValue != null) {
			objContextInnerKey = constructContextObj(idKey + 1, unit);
		}
		Context objContextInnerVal = new Context();
		if (oldContextwithRealValue != null) {
			objContextInnerVal = constructContextObj(idVal + 1, unit);
		}
		key = getVarInExtraStmt(u, idKey);
		val = getVarInExtraStmt(u, idVal);
		if(key ==null | val ==null ){
			return param_list;
		}
		
		try {

			ValueObtainer voKey = new ValueObtainer(methodSig, ConstantUtils.FLAGEXTRA, objContextInnerKey,
					new Counter());
			ValueObtainer voVal = new ValueObtainer(methodSig, ConstantUtils.FLAGEXTRA, objContextInnerVal,
					new Counter());
			param_list = new HashMap<String, List<ExtraData>>();
			List<ExtraData> eds = new ArrayList<ExtraData>();
			param_list.put(u.toString(), eds);
			if (key != null) {
				List<String> keylist = voKey.getValueofVar(key, u, 0).getValues();
				for (String res : keylist) {
					ExtraData ed = new ExtraData();
					ed.setName(res);
					List<String> vallist = voVal.getValueofVar(val, u, 0).getValues();
					ed.setValues(vallist);
					eds.add(ed);
				}
			} else {
				ExtraData ed = new ExtraData();
				eds.add(ed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (param_list == null || param_list.size() == 0) {
			return null;
		}
		return param_list;
	}
	public Map<String, List<ExtraData>> getParamListBundle(Unit u) {
		Value val = null;
		int id = 0;
		try {
			val = getVarInExtraStmt(u, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Map<String, List<ExtraData>> param_list = new HashMap<String, List<ExtraData>>();
		try {
			param_list = new HashMap<String, List<ExtraData>>();
			List<ExtraData> eds = new ArrayList<ExtraData>();
			param_list.put(u.toString(), eds);
			ExtraData ed = new ExtraData();
			Context objContextInner = new Context();
			if (oldContextwithRealValue != null) {
				objContextInner = constructContextObj(id + 1, unit);
			}
			ValueObtainer vo = new ValueObtainer(methodSig, ConstantUtils.FLAGEXTRA, objContextInner, new Counter());
			List<String> vallist = vo.getValueofVar(val, u, 0).getValues();
			if(vallist.size()>0)
				ed.setName(vallist.get(0)); 
//			ed.setValue(vallist);
			eds.add(ed);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (param_list == null || param_list.size() == 0) {
			return null;
		}
		return param_list;
	}
//	/**
//	 * get param list
//	 * 
//	 * @param u
//	 * @return
//	 */
//	public Map<String, List<ExtraData>> getParamList(Unit u) {
//		Value var = null;
//		try {
//			var = getVarInExtraStmt(u);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		Map<String, List<ExtraData>> param_list = new HashMap<String, List<ExtraData>>();
//		try {
//			int id = 0;
//			Context objContextInner = new Context();
//			if (oldContextwithRealValue != null) {
//				objContextInner = constructContextObj(id + 1, unit);
//			}
//			ValueObtainer vo = new ValueObtainer(methodSig, "", objContextInner, new Counter());
//
//			param_list = new HashMap<String, List<ExtraData>>();
//			// same u -->multiple str
//			List<ExtraData> eds = new ArrayList<ExtraData>();
//			param_list.put(u.toString(), eds);
//			if (var != null) {
//				List<String> reslist = vo.getValueofVar(var, u, 0).getValues();
//				for (String res : reslist) {
//					ExtraData ed = new ExtraData();
//					ed.setName(res);
//					eds.add(ed);
//				}
//			} else {
//				ExtraData ed = new ExtraData();
//				eds.add(ed);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		if (param_list == null || param_list.size() == 0) {
//			return null;
//		}
//		return param_list;
//	}
	/**
	 * gen_bundle_type
	 * 
	 * @param u
	 * @return
	 * @throws Exception
	 */
	public BundleType genBundleType(Unit bundleUnit, ExtraData parent, int depth) {
		BundleType bt = new BundleType();
		if(depth>10) 
			return bt;
		List<Unit> defs = new ArrayList<Unit>();
		InvokeExpr invokeExpr = SootUtils.getInvokeExp(bundleUnit);
		if(invokeExpr!=null && invokeExpr.getArgCount()>0){
			defs = SootUtils.getDefOfLocal(methodSig,SootUtils.getInvokeExp(bundleUnit).getArg(invokeExpr.getArgCount()-1), bundleUnit);
		}
		if(defs.size() ==0) return bt;
		
		Unit u = defs.get(0);
		List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);
		if (use_var_list == null)return bt;
		
		for (int i = 0; i < use_var_list.size(); i++) {
			Unit useUnit = use_var_list.get(i).getUnit();
			if(useUnit == bundleUnit) continue;
			if (!CTGAnalyzerHelper.isSetIntentExtraMethod(useUnit.toString()))
				continue;
			Map<String, List<ExtraData>> param_list = getParamListNormal(useUnit);
			if (param_list == null)
				continue;
			String type = CTGAnalyzerHelper.getTypeOfSetBundleExtra(useUnit.toString());
			if (!SootUtils.isBundleExtra(type) && !SootUtils.isExtrasExtra(type)) {
				for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
					for (ExtraData ed : en.getValue()) {
						ed.setType(type);
						ed.setParent(parent);
					}
					bt.put(en.getKey(), en.getValue());
				}
			} else {
				BundleType bundle_type = null;
				bundle_type = genBundleType(useUnit, parent, depth+1);
				if (bundle_type == null) {
					param_list = null;
					continue;
				}
				bundle_type.setType(type);
				for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
					for (ExtraData ed : en.getValue()) {
						ed.setType(bundle_type);
						ed.setParent(parent);
					}
					bt.put(en.getKey(), en.getValue());
				}
			}
		}
		return bt;
	}

	

	/**
	 * 
	 * @param u
	 * @return
	 * @throws Exception
	 */
	private Value getVarInExtraStmt(Unit u) throws Exception {
		Value res = null;
		if (u instanceof JAssignStmt) {
			JAssignStmt jas = (JAssignStmt) u;
			ValueBox ads = jas.rightBox;
			Value v = ads.getValue();
			if (v instanceof JVirtualInvokeExpr) {
				JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) v;
				if (jvie.getArgCount() == 0)
					res = null;
				else
					res = jvie.getArg(0);
			}
		} else if (u instanceof JVirtualInvokeExpr) {
			JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) u;
			if (jvie.getArgCount() == 0)
				res = null;
			else
				res = jvie.getArg(0);
		} else if (u instanceof JInvokeStmt) {
			JInvokeStmt jis = (JInvokeStmt) u;
			if (jis.getInvokeExpr().getArgCount() == 0)
				res = null;
			else
				res = jis.getInvokeExpr().getArg(0);
		}
		return res;
	}

	/**
	 * 
	 * @param u
	 * @return
	 * @throws Exception
	 */
	private Value getVarInExtraStmt(Unit u, int id) {
		Value res = null;
		if (u instanceof JAssignStmt) {
			JAssignStmt jas = (JAssignStmt) u;
			ValueBox ads = jas.rightBox;
			Value v = ads.getValue();
			if (v instanceof JVirtualInvokeExpr) {
				JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) v;
				if (jvie.getArgCount()<=id)
					res = null;
				else{
					res = jvie.getArg(id);
				}
			}
		} else if (u instanceof JVirtualInvokeExpr) {
			JVirtualInvokeExpr jvie = (JVirtualInvokeExpr) u;
			if (jvie.getArgCount() == 0)
				res = null;
			else
				res = jvie.getArg(id);
		} else if (u instanceof JInvokeStmt) {
			JInvokeStmt jis = (JInvokeStmt) u;
			if (jis.getInvokeExpr().getArgCount()<=id)
				res = null;
			else {
				res = jis.getInvokeExpr().getArg(id);
			}

		}
		return res;
	}


	/**
	 * get object name of the par and ser objs
	 * 
	 * @param u
	 * @return
	 */
	private String getObjectNameforReflection(Unit u) {
		String res = "";
		List<UnitValueBoxPair> use_var_list = SootUtils.getUseOfLocal(methodSig, u);

		for (UnitValueBoxPair vb : use_var_list) {
			if (vb.getUnit().getDefBoxes().size() > 0 && vb.getUnit().getUseBoxes().size() > 0) {
				if (vb.getUnit().getUseBoxes().get(0).getValue() instanceof JCastExpr) {
					JCastExpr cast = (JCastExpr) vb.getUnit().getUseBoxes().get(0).getValue();
					if (cast.getCastType().toString().endsWith("[]"))
						res = cast.getCastType().toString().replace("[]", "");
					else
						res = cast.getCastType().toString();
				}
			}
			// else{
			// System.err.println("no left/right value  "+u.getClass().getName());
			// }
		}
		return res;
	}

	public void addExtraValue2Set(SootMethod sm, Unit u, String act_name, Map<String, List<ExtraData>> param_list,
			BundleType bundleType) {
		String res = "";
		for (Entry<String, List<ExtraData>> en : param_list.entrySet()) {
			if (!bundleType.obtainBundle().containsKey(en.getKey())) {
				bundleType.put(en.getKey(), en.getValue());
				for (ExtraData ed : en.getValue()) {
					res += ed.toString().trim();
				}
			}
		}
		if (res.endsWith(","))
			res = res.substring(0, res.length() - 1);
		// key and value
		Attribute attr = new Attribute(sm.getSignature() + "," + u.toString() + "," + u.hashCode(), "extra", res,
				"equals");
		// send
		CollectionUtils.add_attribute_to_map(attr.getId(), attr, appModel.getUnit2Attribute());
	}

}
