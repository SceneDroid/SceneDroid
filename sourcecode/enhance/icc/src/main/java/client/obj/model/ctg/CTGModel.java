package main.java.client.obj.model.ctg;

import main.java.client.obj.model.atg.ATGModel;

public class CTGModel {
	private ATGModel optModel;
	private ATGModel optModelwithoutFrag;
	private ATGModel dynamicModel;
	private ATGModel mannualModel;
	private ATGModel oracleModel;

	public CTGModel() {
		setOptModel(new ATGModel());
		setOptModelwithoutFrag(new ATGModel());
		setDynamicModel(new ATGModel());
		setMannualModel(new ATGModel());
		setOracleModel(new ATGModel());
	}

	/**
	 * @return the atgModel
	 */
	public ATGModel getOptModel() {
		return optModel;
	}

	/**
	 * @param model
	 *            the atgModel to set
	 */
	public void setOptModel(ATGModel model) {
		this.optModel = model;
	}

	/**
	 * @return the optModelwithoutFrag
	 */
	public ATGModel getOptModelwithoutFrag() {
		return optModelwithoutFrag;
	}

	/**
	 * @param optModelwithoutFrag
	 *            the optModelwithoutFrag to set
	 */
	public void setOptModelwithoutFrag(ATGModel optModelwithoutFrag) {
		this.optModelwithoutFrag = optModelwithoutFrag;
	}

	/**
	 * @return the dynamicModel
	 */
	public ATGModel getDynamicModel() {
		return dynamicModel;
	}

	/**
	 * @param dynamicModel
	 *            the dynamicModel to set
	 */
	public void setDynamicModel(ATGModel dynamicModel) {
		this.dynamicModel = dynamicModel;
	}

	/**
	 * @return the mannualModel
	 */
	public ATGModel getMannualModel() {
		return mannualModel;
	}

	/**
	 * @param mannualModel
	 *            the mannualModel to set
	 */
	public void setMannualModel(ATGModel mannualModel) {
		this.mannualModel = mannualModel;
	}

	/**
	 * @return the oracleModel
	 */
	public ATGModel getOracleModel() {
		return oracleModel;
	}

	/**
	 * @param oracleModel
	 *            the oracleModel to set
	 */
	public void setOracleModel(ATGModel oracleModel) {
		this.oracleModel = oracleModel;
	}
}
