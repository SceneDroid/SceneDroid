package main.java.analyze.model.labeledOracleModel;

import java.util.HashMap;
import java.util.Map;

import main.java.analyze.utils.output.PrintUtils;

public class LabeledOracleModel {
	private Map<String, IccTag> LabeledOracle;

	public LabeledOracleModel() {
		LabeledOracle = new HashMap<String, IccTag>();
	}

	@Override
	public String toString() {
		return PrintUtils.printMap(LabeledOracle);
	}

	/**
	 * @return the labeledOracle
	 */
	public Map<String, IccTag> getLabeledOracle() {
		return LabeledOracle;
	}

	/**
	 * @param labeledOracle
	 *            the labeledOracle to set
	 */
	public void setLabeledOracle(Map<String, IccTag> labeledOracle) {
		LabeledOracle = labeledOracle;
	}

	public void addLabeledOracle(IccTag iccTag) {
		String key = iccTag.getSource() + " --> " + iccTag.getDestination();
		if (!LabeledOracle.containsKey(key)) {
			LabeledOracle.put(key, iccTag);
		}
	}

}
