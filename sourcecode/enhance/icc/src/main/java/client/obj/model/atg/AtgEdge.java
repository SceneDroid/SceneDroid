package main.java.client.obj.model.atg;

import java.util.Set;

import main.java.Global;
import main.java.analyze.utils.SootUtils;
import main.java.client.obj.model.ctg.IntentSummaryModel;

public class AtgEdge {
	private AtgNode source;
	private AtgNode destnation;
	private IntentSummaryModel intentSummary;
	private AtgType type;
	private String methodSig;
	private int instructionId;
	private String iCCkind;

	public AtgEdge(AtgNode source, AtgNode destnation, String methodSig, int instructionId, String iCCkindId) {
		this.source = source;
		this.destnation = destnation;
		this.type = getTypeofATG(SootUtils.getNameofClass(source.getName()),
		SootUtils.getNameofClass(destnation.getName()));
		this.methodSig = methodSig;
		this.instructionId = instructionId;
		getICCKindById(iCCkindId);
	}

	public AtgEdge(AtgEdge temp) {
		setDestnation(temp.destnation);
		setSource(temp.source);
		setiCCkind(temp.iCCkind);
		setInstructionId(temp.instructionId);
		setMethodSig(temp.methodSig);
		setType(AtgType.Act2Act);
	}

	public String getDescribtion() {
		String res = "";
		res += source.getClassName();
		res += " --> " + destnation.getClassName();
		return res;
	}

	public String getReverseDescribtion() {
		String res = "";
		res += destnation.getClassName();
		res += " --> " + source.getClassName();
		return res;
	}

	@Override
	public String toString() {
		String res = "";
		res += source.getName();
		res += " --> " + destnation.getName();
		res += ", type:" + type;
		res += ", methodSig:" + methodSig;
		return res;
	}

	private void getICCKindById(String iCCkindId) {
		if (iCCkindId.equals("0") || iCCkindId.equals("ACTIVITY")) {
			setiCCkind("a");
		} else if (iCCkindId.equals("1") || iCCkindId.equals("SERVICE")) {
			setiCCkind("s");
		} else if (iCCkindId.equals("2") || iCCkindId.equals("3") || iCCkindId.equals("RECEIVER")
				|| iCCkindId.equals("DYNAMIC_RECEIVER")) {
			setiCCkind("r");
		} else if (iCCkindId.equals("4") || iCCkindId.equals("PROVIDER")) {
			setiCCkind("p");
		} else {
			setiCCkind(iCCkindId);
		}
	}

	private AtgType getTypeofATG(String src, String des) {
		AtgType type = null;
		Set<String> componentSet = Global.v().getAppModel().getComponentMap().keySet();
		Set<String> activitySet = Global.v().getAppModel().getActivityMap().keySet();
		Set<String> fragmentSet = Global.v().getAppModel().getFragmentClasses();
		boolean desIsFragment = fragmentSet.contains(des) 
				| SootUtils.isFragmentClass(SootUtils.getSootClassByName(des));
		boolean srcIsFragment = fragmentSet.contains(src)
			| SootUtils.isFragmentClass(SootUtils.getSootClassByName(src));
		boolean desIsActivity = activitySet.contains(des);
		boolean scrIsActivity = activitySet.contains(src);
		boolean desIsNonActivity = componentSet.contains(des);
		boolean srcIsNonActivity = componentSet.contains(src);
		if (srcIsFragment) {
			if (desIsFragment)
				type = AtgType.Frag2Frag;
			else if (desIsActivity)
				type = AtgType.Frag2Act;
			else if (desIsNonActivity)
				type = AtgType.Frag2NonAct;
			else
				type = AtgType.Frag2Class;
		} else if (scrIsActivity) {
			if (desIsFragment)
				type = AtgType.Act2Frag;
			else if (desIsActivity)
				type = AtgType.Act2Act;
			else if (desIsNonActivity)
				type = AtgType.Act2NonAct;
			else
				type = AtgType.Act2Class;
		} else if (srcIsNonActivity) {
			if (desIsFragment)
				type = AtgType.NonAct2Frag;
			else if (desIsActivity)
				type = AtgType.NonAct2Act;
			else if (desIsNonActivity)
				type = AtgType.NonAct2NonAct;
			else
				type = AtgType.NonAct2Class;
		} else {
			type = AtgType.Class2Any;
		}
		return type;
	}

	public AtgNode getSource() {
		return source;
	}

	public void setSource(AtgNode source) {
		this.source = source;
	}

	public AtgNode getDestnation() {
		return destnation;
	}

	public void setDestnation(AtgNode destnation) {
		this.destnation = destnation;
	}

	public IntentSummaryModel getIntentSummary() {
		return intentSummary;
	}

	public void setIntentSummary(IntentSummaryModel intentSummary) {
		this.intentSummary = intentSummary;
	}

	public AtgType getType() {
		return type;
	}

	public void setType(AtgType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof AtgEdge))
			return false;
		AtgEdge edge = (AtgEdge) obj;
		if (this.source == edge.source && this.destnation == edge.destnation && this.type == edge.type
				&& this.intentSummary == edge.intentSummary) {
			return true;
		}
		return false;
	}

	/**
	 * @return the instructionId
	 */
	public int getInstructionId() {
		return instructionId;
	}

	/**
	 * @param instructionId
	 *            the instructionId to set
	 */
	public void setInstructionId(int instructionId) {
		this.instructionId = instructionId;
	}

	/**
	 * @return the methodSig
	 */
	public String getMethodSig() {
		return methodSig;
	}

	/**
	 * @param methodSig
	 *            the methodSig to set
	 */
	public void setMethodSig(String methodSig) {
		this.methodSig = methodSig;
	}

	/**
	 * @return the iCCkind
	 */
	public String getiCCkind() {
		return iCCkind;
	}

	/**
	 * @param iCCkind
	 *            the iCCkind to set
	 */
	public void setiCCkind(String iCCkind) {
		this.iCCkind = iCCkind;
	}

}
