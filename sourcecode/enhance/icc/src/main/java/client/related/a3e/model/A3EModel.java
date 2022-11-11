package main.java.client.related.a3e.model;

import main.java.client.obj.model.atg.ATGModel;

public class A3EModel {
	private ATGModel a3eAtgModel;
	private String a3eFilePath;

	public A3EModel() {
		seta3eAtgModel(new ATGModel());
	}

	/**
	 * @return the a3eAtgModel
	 */
	public ATGModel geta3eAtgModel() {
		return a3eAtgModel;
	}

	/**
	 * @param a3eAtgModel
	 *            the a3eAtgModel to set
	 */
	public void seta3eAtgModel(ATGModel a3eAtgModel) {
		this.a3eAtgModel = a3eAtgModel;
	}

	/**
	 * @return the a3eFilePath
	 */
	public String geta3eFilePath() {
		return a3eFilePath;
	}

	/**
	 * @param a3eFilePath
	 *            the a3eFilePath to set
	 */
	public void seta3eFilePath(String a3eFilePath) {
		this.a3eFilePath = a3eFilePath;
	}
}
