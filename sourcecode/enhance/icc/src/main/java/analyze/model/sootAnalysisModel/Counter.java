package main.java.analyze.model.sootAnalysisModel;

/**
 * counter of code during soot analysis
 * 
 * @author 79940
 *
 */
public class Counter {
	private int getVarDepth = 0;
	private int totallength = 0;

	private int insensi_invokeMethodNum = 0;

	public int getGetVarDepth() {
		return getVarDepth;
	}

	public void setGetVarDepth(int getVarDepth) {
		this.getVarDepth = getVarDepth;
	}

	public int getTotallength() {
		return totallength;
	}

	public void setTotallength(int totallength) {
		this.totallength = totallength;
	}

	public int getInsensi_invokeMethodNum() {
		return insensi_invokeMethodNum;
	}

	public void setInsensi_invokeMethodNum(int insensi_invokeMethodNum) {
		this.insensi_invokeMethodNum = insensi_invokeMethodNum;
	}
}
