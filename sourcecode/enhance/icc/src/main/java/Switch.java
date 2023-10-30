package main.java;

/**
 * switches for the analysis
 * @author hanada
 *
 */
public class Switch {
	// control flow
	private boolean dummyMainSwitch;
	private boolean callBackSwitch;
	private boolean asyncMethodSwitch;
	private boolean polymSwitch;
	private boolean functionExpandSwitch;
	private boolean functionExpandAllSwitch;
	
	// data flow
	private boolean adapterSwitch;
	private boolean stringOpSwitch;
	private boolean staticFieldSwitch;

	// target code
	private boolean libCodeSwitch;
	private boolean fragmentSwitch;
	private boolean wrapperAPISwitch;

	// launching

	private boolean implicitLaunchSwitch;
	private boolean dynamicBCSwitch;

	// efficiency
	private boolean vfgStrategy;
	private SummaryLevel summaryStrategy;
	private boolean cgAnalyzeGroupedStrategy;
	
	//other
	private boolean getAttributeStrategy;
	private boolean setAttributeStrategy;
	private boolean setDesRelatedAttributeOnlyStrategy;
	private boolean scenario_stack;

	public Switch() {
		dummyMainSwitch = false;
		callBackSwitch = true;
		asyncMethodSwitch = true;
		polymSwitch = true;
		functionExpandSwitch = true;

		adapterSwitch = true;
		stringOpSwitch = true;
		staticFieldSwitch = true;

		libCodeSwitch = true;
		fragmentSwitch = true;
		wrapperAPISwitch = true;

		implicitLaunchSwitch = true;
		dynamicBCSwitch = true;

		summaryStrategy = SummaryLevel.object;
		vfgStrategy = true;
		cgAnalyzeGroupedStrategy = false;
		getAttributeStrategy = false;
		setAttributeStrategy = false;

		setScenario_stack(false);
		functionExpandAllSwitch = false;
	}

	@Override
	public String toString() {
		String res = "dummyMain=" + isDummyMainSwitch() + "\t";
		res += "callBack=" + isCallBackSwitch() + "\t";
		res += "asyncMethod=" + isAsyncMethodSwitch() + "\t";

		res += "adapter=" + isAdapterSwitch() + "\t";
		res += "stringOp=" + isStringOpSwitch() + "\t";
		res += "contextSensi=" + isFunctionExpandSwitch() + "\t";

		res += "libCode=" + allowLibCodeSwitch() + "\t";
		res += "fragment=" + isFragmentSwitch() + "\t";
		res += "wrapperAPI=" + isWrapperAPISwitch() + "\t";

		res += "vfg=" + isVfgStrategy() + "\t";
		res += "summary=" + getSummaryStrategy() + "\t";
		res += "cgGrouped=" + isCgAnalyzeGroupedStrategy() + "\n";

		return res;
	}

	/**
	 * @return the fragmentSwitch
	 */
	public boolean isFragmentSwitch() {
		return fragmentSwitch;
	}

	/**
	 * @param fragmentSwitch
	 *            the fragmentSwitch to set
	 */
	public void setFragmentSwitch(boolean fragmentSwitch) {
		this.fragmentSwitch = fragmentSwitch;
	}

	/**
	 * @return the adapterSwitch
	 */
	public boolean isAdapterSwitch() {
		return adapterSwitch;
	}

	/**
	 * @param adapterSwitch
	 *            the adapterSwitch to set
	 */
	public void setAdapterSwitch(boolean adapterSwitch) {
		this.adapterSwitch = adapterSwitch;
	}

	/**
	 * @return the asyncMethodSwitch
	 */
	public boolean isAsyncMethodSwitch() {
		return asyncMethodSwitch;
	}

	/**
	 * @param asyncMethodSwitch
	 *            the asyncMethodSwitch to set
	 */
	public void setAsyncMethodSwitch(boolean asyncMethodSwitch) {
		this.asyncMethodSwitch = asyncMethodSwitch;
	}

	/**
	 * @return the wrapperAPISwitch
	 */
	public boolean isWrapperAPISwitch() {
		return wrapperAPISwitch;
	}

	/**
	 * @param wrapperAPISwitch
	 *            the wrapperAPISwitch to set
	 */
	public void setWrapperAPISwitch(boolean wrapperAPISwitch) {
		this.wrapperAPISwitch = wrapperAPISwitch;
	}

	/**
	 * @return the stringOpSwitch
	 */
	public boolean isStringOpSwitch() {
		return stringOpSwitch;
	}

	/**
	 * @param stringOpSwitch
	 *            the stringOpSwitch to set
	 */
	public void setStringOpSwitch(boolean stringOpSwitch) {
		this.stringOpSwitch = stringOpSwitch;
	}

	/**
	 * @return the callBackSwitch
	 */
	public boolean isCallBackSwitch() {
		return callBackSwitch;
	}

	/**
	 * @param callBackSwitch
	 *            the callBackSwitch to set
	 */
	public void setCallBackSwitch(boolean callBackSwitch) {
		this.callBackSwitch = callBackSwitch;
	}

	/**
	 * @return the dummyMainSwitch
	 */
	public boolean isDummyMainSwitch() {
		return dummyMainSwitch;
	}

	/**
	 * @param dummyMainSwitch
	 *            the dummyMainSwitch to set
	 */
	public void setDummyMainSwitch(boolean dummyMainSwitch) {
		this.dummyMainSwitch = dummyMainSwitch;
	}

	/**
	 * @return the functionExpandSwitch
	 */
	public boolean isFunctionExpandSwitch() {
		return functionExpandSwitch;
	}

	/**
	 * @param functionExpandSwitch
	 *            the functionExpandSwitch to set
	 */
	public void setFunctionExpandSwitch(boolean contextSensiSwitch) {
		this.functionExpandSwitch = contextSensiSwitch;
	}

	/**
	 * @return the libCodeSwitch
	 */
	public boolean allowLibCodeSwitch() {
		return libCodeSwitch;
	}

	/**
	 * @param libCodeSwitch
	 *            the libCodeSwitch to set
	 */
	public void setLibCodeSwitch(boolean libCodeSwitch) {
		this.libCodeSwitch = libCodeSwitch;
	}

	/**
	 * @return the vfgStrategy
	 */
	public boolean isVfgStrategy() {
		return vfgStrategy;
	}

	/**
	 * @param vfgStrategy
	 *            the vfgStrategy to set
	 */
	public void setVfgStrategy(boolean vfgStrategy) {
		this.vfgStrategy = vfgStrategy;
	}

	/**
	 * @return the cgAnalyzeGroupedStrategy
	 */
	public boolean isCgAnalyzeGroupedStrategy() {
		return cgAnalyzeGroupedStrategy;
	}

	/**
	 * @param cgAnalyzeGroupedStrategy
	 *            the cgAnalyzeGroupedStrategy to set
	 */
	public void setCgAnalyzeGroupedStrategy(boolean cgAnalyzeGroupedStrategy) {
		this.cgAnalyzeGroupedStrategy = cgAnalyzeGroupedStrategy;
	}

	/**
	 * @return the summaryStrategy
	 */
	public SummaryLevel getSummaryStrategy() {
		return summaryStrategy;
	}

	/**
	 * @param summaryStrategy
	 *            the summaryStrategy to set
	 */
	public void setSummaryStrategy(SummaryLevel summaryStrategy) {
		this.summaryStrategy = summaryStrategy;
	}

	/**
	 * @return the staticFieldSwitch
	 */
	public boolean isStaticFieldSwitch() {
		return staticFieldSwitch;
	}

	/**
	 * @param staticFieldSwitch
	 *            the staticFieldSwitch to set
	 */
	public void setStaticFieldSwitch(boolean staticFieldSwitch) {
		this.staticFieldSwitch = staticFieldSwitch;
	}

	/**
	 * @return the implicitLaunchSwitch
	 */
	public boolean isImplicitLaunchSwitch() {
		return implicitLaunchSwitch;
	}

	/**
	 * @param implicitLaunchSwitch
	 *            the implicitLaunchSwitch to set
	 */
	public void setImplicitLaunchSwitch(boolean implicitLaunchSwitch) {
		this.implicitLaunchSwitch = implicitLaunchSwitch;
	}

	/**
	 * @return the dynamicBCSwitch
	 */
	public boolean isDynamicBCSwitch() {
		return dynamicBCSwitch;
	}

	/**
	 * @param dynamicBCSwitch
	 *            the dynamicBCSwitch to set
	 */
	public void setDynamicBCSwitch(boolean dynamicBCSwitch) {
		this.dynamicBCSwitch = dynamicBCSwitch;
	}

	/**
	 * @return the getAttributeStrategy
	 */
	public boolean isGetAttributeStrategy() {
		return getAttributeStrategy;
	}

	/**
	 * @param getAttributeStrategy
	 *            the getAttributeStrategy to set
	 */
	public void setGetAttributeStrategy(boolean getAttributeStrategy) {
		this.getAttributeStrategy = getAttributeStrategy;
	}

	/**
	 * @return the polymSwitch
	 */
	public boolean isPolymSwitch() {
		return polymSwitch;
	}

	/**
	 * @param polymSwitch
	 *            the polymSwitch to set
	 */
	public void setPolymSwitch(boolean polymSwitch) {
		this.polymSwitch = polymSwitch;
	}

	/**
	 * @return the functionExpandAllSwitch
	 */
	public boolean isFunctionExpandAllSwitch() {
		return functionExpandAllSwitch;
	}

	/**
	 * @param functionExpandAllSwitch
	 *            the functionExpandAllSwitch to set
	 */
	public void setFunctionExpandAllSwitch(boolean functionExpandAllSwitch) {
		this.functionExpandAllSwitch = functionExpandAllSwitch;
	}

	/**
	 * @return the scenario_stack
	 */
	public boolean isScenario_stack() {
		return scenario_stack;
	}

	/**
	 * @param scenario_stack
	 *            the scenario_stack to set
	 */
	public void setScenario_stack(boolean scenario_stack) {
		this.scenario_stack = scenario_stack;
	}

	/**
	 * @return the setAttributeStrategy
	 */
	public boolean isSetAttributeStrategy() {
		return setAttributeStrategy;
	}

	/**
	 * @param setAttributeStrategy the setAttributeStrategy to set
	 */
	public void setSetAttributeStrategy(boolean setAttributeStrategy) {
		this.setAttributeStrategy = setAttributeStrategy;
	}

	/**
	 * @return the setDesRelatedAttributeOnlyStrategy
	 */
	public boolean isSetDesRelatedAttributeOnlyStrategy() {
		return setDesRelatedAttributeOnlyStrategy;
	}

	/**
	 * @param setDesRelatedAttributeOnlyStrategy the setDesRelatedAttributeOnlyStrategy to set
	 */
	public void setSetDesRelatedAttributeOnlyStrategy(boolean setDesRelatedAttributeOnlyStrategy) {
		this.setDesRelatedAttributeOnlyStrategy = setDesRelatedAttributeOnlyStrategy;
	}
}
