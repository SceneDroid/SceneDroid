package main.java.client.related.ic3.model;

import main.java.client.obj.model.atg.ATGModel;

public class IC3Model {
	private ATGModel IC3AtgModel;
	private String IC3FilePath;

	public IC3Model() {
		setIC3AtgModel(new ATGModel());
	}

	/**
	 * @return the iC3AtgModel
	 */
	public ATGModel getIC3AtgModel() {
		return IC3AtgModel;
	}

	/**
	 * @param iC3AtgModel
	 *            the iC3AtgModel to set
	 */
	public void setIC3AtgModel(ATGModel iC3AtgModel) {
		IC3AtgModel = iC3AtgModel;
	}

	/**
	 * @return the iC3FilePath
	 */
	public String getIC3FilePath() {
		return IC3FilePath;
	}

	/**
	 * @param iC3FilePath
	 *            the iC3FilePath to set
	 */
	public void setIC3FilePath(String iC3FilePath) {
		IC3FilePath = iC3FilePath;
	}
}
