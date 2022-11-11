package main.java.client.obj.model.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.client.obj.model.atg.ATGModel;
import soot.SootClass;

public class FragmentModel {
	private ATGModel atgModel;
	private Map<String, List<SootClass>> xmlFragmentMap;

	public FragmentModel() {
		setAtgModel(new ATGModel());
		xmlFragmentMap = new HashMap<String, List<SootClass>>();
	}

	/**
	 * @return the atgModel
	 */
	public ATGModel getAtgModel() {
		return atgModel;
	}

	/**
	 * @param atgModel
	 *            the atgModel to set
	 */
	public void setAtgModel(ATGModel atgModel) {
		this.atgModel = atgModel;
	}

	/**
	 * @return the xmlFragmentMap
	 */
	public Map<String, List<SootClass>> getXmlFragmentMap() {
		return xmlFragmentMap;
	}

	/**
	 * @param xmlFragmentMap
	 *            the xmlFragmentMap to set
	 */
	public void setXmlFragmentMap(Map<String, List<SootClass>> xmlFragmentMap) {
		this.xmlFragmentMap = xmlFragmentMap;
	}
}
