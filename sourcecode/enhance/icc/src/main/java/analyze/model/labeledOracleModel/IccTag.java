package main.java.analyze.model.labeledOracleModel;

import java.util.ArrayList;
import java.util.List;

import fj.P;
import main.java.analyze.utils.output.PrintUtils;

public class IccTag {
	private String source;
	private String destination;

	private List<String> methodTrace;
	private List<String> callLineTrace;

	private boolean isLifeCycle;
	private boolean isStaticCallBack;
	private boolean isDynamicCallBack;
	private boolean isImpliciyCallBack;

	private boolean isLifeCycleOnly;
	private boolean isCallBackInclude;

	private boolean isNormalSendICC;
	private boolean isWarpperSendICC;

	private boolean isExplicit;
	private boolean isImplicit;

	private boolean isActivity;
	private boolean isService;
	private boolean isBroadCast;
	private boolean isDynamicBroadCast;
	private boolean isNonActivity;

	private boolean isFragment;
	private boolean isAdapter;
	private boolean isWidget;
	private boolean isOtherClass;
	private boolean isNonComponent;

	private boolean isLibraryInvocation;
	private boolean isAsyncInvocation;
	private boolean isPolymorphic;

	private boolean isStaticVal;
	private boolean isStringOp;
	private boolean isNoExtra;

	private boolean isFlowSensitive;
	private boolean isPathSensitive;
	private boolean isContextSensitive;
	private boolean isFieldSensitive;
	private boolean isObjectSensitive;
	private boolean isSomeSensitive;

	private boolean isStaticCallBackonly;
	private boolean isDynamicCallBackonly;
	private boolean isImplicitCallBackonly;
	private boolean isWarrperonly;
	private boolean isImplicitICConly;
	private boolean isNonActonly;
	private boolean isNonComponentonly;
	private boolean isFragmentonly;
	private boolean isAdapteronly;
	private boolean isWidgetonly;
	private boolean isOtherClassonly;
	private boolean isContextSensionly;
	private boolean isLibonly;
	private boolean isAsynconly;
	private boolean isPolymonly;
	private boolean isStaticValOnly;
	private boolean isStringOpOnly;
	
	public IccTag(String source, String destination) {
		this.source = source;
		this.destination = destination;
		methodTrace = new ArrayList<String>();
		callLineTrace = new ArrayList<String>();
	}

	public void postAnalyzeTags() {
		if (isStaticCallBack && !isDynamicCallBack && !isImpliciyCallBack && !isWarpperSendICC && !isImplicit && !isStaticVal && !isStringOp
				&& !isNonActivity && !isNonComponent && !isContextSensitive && !isLibraryInvocation&& !isAsyncInvocation && !isPolymorphic)
			isStaticCallBackonly = true;
		if (!isStaticCallBack && isDynamicCallBack && !isImpliciyCallBack && !isWarpperSendICC && !isImplicit && !isStaticVal && !isStringOp
				&& !isNonActivity && !isNonComponent && !isContextSensitive && !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isDynamicCallBackonly = true;
		if (!isStaticCallBack && !isDynamicCallBack && isImpliciyCallBack && !isWarpperSendICC && !isImplicit && !isStaticVal && !isStringOp
				&& !isNonActivity && !isNonComponent && !isContextSensitive && !isLibraryInvocation&& !isAsyncInvocation && !isPolymorphic)
			isImplicitCallBackonly = true;

		if (!isWarpperSendICC && !isImplicit && isNonActivity && !isNonComponent && !isContextSensitive && !isStaticVal && !isStringOp
				&& !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isNonActonly = true;

		if (isWarpperSendICC && !isImplicit && !isNonComponent && !isContextSensitive && !isLibraryInvocation && !isStaticVal && !isStringOp
				&& !isAsyncInvocation && !isPolymorphic)
			isWarrperonly = true;

		if (!isWarpperSendICC && isImplicit &&  !isContextSensitive   && !isStaticVal && !isStringOp
				&& !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isImplicitICConly = true;
		
		if (!isWarpperSendICC && !isImplicit && isNonComponent && !isContextSensitive && !isNonActivity && !isStaticVal && !isStringOp
				&& !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isNonComponentonly = true;
		if (!isWarpperSendICC && !isImplicit && !isNonComponent && !isContextSensitive && !isNonActivity && !isStaticVal && !isStringOp
				&& isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isLibonly = true;
		if (!isWarpperSendICC && !isImplicit && !isNonComponent && !isContextSensitive && !isNonActivity && !isStaticVal && !isStringOp
				&& !isLibraryInvocation && isAsyncInvocation && !isPolymorphic)
			isAsynconly = true;
		if (!isWarpperSendICC && !isImplicit && !isNonComponent && !isContextSensitive && !isNonActivity && !isStaticVal && !isStringOp
				&& !isLibraryInvocation && !isAsyncInvocation && isPolymorphic)
			isPolymonly = true;

		if (!isWarpperSendICC && !isImplicit && !isNonComponent && isContextSensitive && !isNonActivity && !isStaticVal && !isStringOp
				&& !isFieldSensitive && !isFlowSensitive && !isPathSensitive && !isObjectSensitive
				&& !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isContextSensionly = true;

		if (!isWarpperSendICC && !isImplicit && isFragment && !isAdapter && !isWidget && !isContextSensitive && !isStaticVal && !isStringOp
				&& !isNonActivity && !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isFragmentonly = true;
		if (!isWarpperSendICC && !isImplicit && !isFragment && isAdapter && !isWidget && !isContextSensitive && !isStaticVal && !isStringOp
				&& !isNonActivity && !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isAdapteronly = true;
		if (!isWarpperSendICC && !isImplicit && !isFragment && !isAdapter && isWidget && !isContextSensitive && !isStaticVal && !isStringOp
				&& !isNonActivity && !isLibraryInvocation && !isAsyncInvocation && !isPolymorphic)
			isWidgetonly = true;
		if (!isWarpperSendICC && !isImplicit && !isFragment && !isAdapter && isWidget && isOtherClass && !isStaticVal && !isStringOp
				&& !isContextSensitive && !isNonActivity && !isLibraryInvocation && !isAsyncInvocation
				&& !isPolymorphic)
			isOtherClassonly = true;
		
		if (!isWarpperSendICC && !isImplicit && !isFragment && !isAdapter && isWidget && isOtherClass  && !isStaticVal && !isStringOp
				&& !isContextSensitive && !isNonActivity && !isLibraryInvocation && !isAsyncInvocation
				&& !isPolymorphic)
			isOtherClassonly = true;
		
		if (!isWarpperSendICC && !isImplicit && !isFragment && !isAdapter && isWidget && !isOtherClass  && isStaticVal && !isStringOp
				&& !isContextSensitive && !isNonActivity && !isLibraryInvocation && !isAsyncInvocation
				&& !isPolymorphic)
			isStaticValOnly = true;
		
		if (!isWarpperSendICC  && !isFragment && !isAdapter && isWidget && !isOtherClass  && !isStaticVal && isStringOp
				&& !isContextSensitive && !isNonActivity && !isLibraryInvocation && !isAsyncInvocation
				&& !isPolymorphic)
			isStringOpOnly = true;	
		
//		if(isAsynconly){
//			System.err.println("!!! "+source+"   " +destination);
//		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("source: " + source + "\t");
		sb.append("destination: " + destination + "\n");

		sb.append("methodTrace: " + PrintUtils.printList(methodTrace) + "\n");
		sb.append("callLineTrace: " + PrintUtils.printList(callLineTrace) + "\n");

		sb.append("isLifeCycle: " + isLifeCycle + "\t");
		sb.append("isStaticCallBack: " + isStaticCallBack + "\t");
		sb.append("isDynamicCallBack: " + isDynamicCallBack + "\t");
		sb.append("isImpliciyCallBack: " + isImpliciyCallBack + "\n");

		sb.append("isNormalSendICC: " + isNormalSendICC + "\t");
		sb.append("isWarpperSendICC: " + isWarpperSendICC + "\n");

		sb.append("isExplicit: " + isExplicit + "\t");
		sb.append("isImplicit: " + isImplicit + "\n");

		sb.append("isActivity: " + isActivity + "\t");
		sb.append("isService: " + isService + "\t");
		sb.append("isBroadCast: " + isBroadCast + "\t");
		sb.append("isDynamicBroadCast: " + isDynamicBroadCast + "\n");

		sb.append("isFragment: " + isFragment + "\t");
		sb.append("isAdapter: " + isAdapter + "\t");
		sb.append("isWidget: " + isWidget + "\t");
		sb.append("isOtherClass: " + isOtherClass + "\n");

		sb.append("isLibraryInvocation: " + isLibraryInvocation + "\t");
		sb.append("isAsyncInvocation: " + isAsyncInvocation + "\t");
		sb.append("isPolymorphic: " + isPolymorphic + "\n");

		sb.append("isStaticVal: " + isStaticVal + "\t");
		sb.append("isStringOp: " + isStringOp + "\n");
		sb.append("isNoExtra: " + isNoExtra + "\n");

		sb.append("isFlowSensitive: " + isFlowSensitive + "\t");
		sb.append("isPathSensitive: " + isPathSensitive + "\t");
		sb.append("isContextSensitive: " + isContextSensitive + "\t");
		sb.append("isObjectSensitive: " + isObjectSensitive + "\t");
		sb.append("isFieldSensitive: " + isFieldSensitive + "\n");

		sb.append("isStaticCallBackonly: " + isStaticCallBackonly + "\t");
		sb.append("isDynamicCallBackonly: " + isDynamicCallBackonly + "\t");
		sb.append("isImplicitCallBackonly: " + isImplicitCallBackonly + "\n");
		sb.append("isWarrperonly: " + isWarrperonly + "\nt");
		sb.append("isImplicitICConly: " + isImplicitICConly + "\n");
		sb.append("isNonActonly: " + isNonActonly + "\t");
		sb.append("isNonComponentonly: " + isNonComponentonly + "\n");
		sb.append("isFragmentonly: " + isFragmentonly + "\t");
		sb.append("isAdapteronly: " + isAdapteronly + "\t");
		sb.append("isWidgetonly: " + isWidgetonly + "\t");
		sb.append("isOtherClassonly: " + isOtherClassonly + "\t");
		sb.append("isContextSensionly: " + isContextSensionly + "\t");
		sb.append("isLibonly: " + isLibonly + "\t");
		sb.append("isAsynconly: " + isAsynconly + "\t");
		sb.append("isPolymonly: " + isPolymonly + "\n");

		return sb.toString();
	}

	public boolean isLifeCycle() {
		return isLifeCycle;
	}

	public void setLifeCycle(boolean isLifeCycle) {
		this.isLifeCycle = isLifeCycle;
	}

	public boolean isStaticCallBack() {
		return isStaticCallBack;
	}

	public void setStaticCallBack(boolean isStaticCallBack) {
		this.isStaticCallBack = isStaticCallBack;
	}

	public boolean isDynamicCallBack() {
		return isDynamicCallBack;
	}

	public void setDynamicCallBack(boolean isDynamicCallBack) {
		this.isDynamicCallBack = isDynamicCallBack;
	}

	public boolean isNormalSendICC() {
		return isNormalSendICC;
	}

	public void setNormalSendICC(boolean isNormalSendICC) {
		this.isNormalSendICC = isNormalSendICC;
	}

	public boolean isWarpperSendICC() {
		return isWarpperSendICC;
	}

	public void setWarpperSendICC(boolean isWarpperSendICC) {
		this.isWarpperSendICC = isWarpperSendICC;
	}

	public boolean isExplicit() {
		return isExplicit;
	}

	public void setExplicit(boolean isExplicit) {
		this.isExplicit = isExplicit;
	}

	public boolean isImplicit() {
		return isImplicit;
	}

	public void setImplicit(boolean isImplicit) {
		this.isImplicit = isImplicit;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}

	public boolean isService() {
		return isService;
	}

	public void setService(boolean isService) {
		this.isService = isService;
	}

	public boolean isBroadCast() {
		return isBroadCast;
	}

	public void setBroadCast(boolean isBroadCast) {
		this.isBroadCast = isBroadCast;
	}

	public boolean isDynamicBroadCast() {
		return isDynamicBroadCast;
	}

	public void setDynamicBroadCast(boolean isDynamicBroadCast) {
		this.isDynamicBroadCast = isDynamicBroadCast;
	}

	public boolean isFragment() {
		return isFragment;
	}

	public void setFragment(boolean isFragment) {
		this.isFragment = isFragment;
	}

	public boolean isAdapter() {
		return isAdapter;
	}

	public void setAdapter(boolean isAdapter) {
		this.isAdapter = isAdapter;
	}

	public boolean isWidget() {
		return isWidget;
	}

	public void setWidget(boolean isWidget) {
		this.isWidget = isWidget;
	}

	public boolean isOtherClass() {
		return isOtherClass;
	}

	public void setOtherClass(boolean isOtherClass) {
		this.isOtherClass = isOtherClass;
	}

	public boolean isLibraryInvocation() {
		return isLibraryInvocation;
	}

	public void setLibraryInvocation(boolean isLibraryInvocation) {
		this.isLibraryInvocation = isLibraryInvocation;
	}

	public boolean isAsyncInvocation() {
		return isAsyncInvocation;
	}

	public void setAsyncInvocation(boolean isAsyncInvocation) {
		this.isAsyncInvocation = isAsyncInvocation;
	}

	public boolean isPolymorphic() {
		return isPolymorphic;
	}

	public void setPolymorphic(boolean isPolymorphic) {
		this.isPolymorphic = isPolymorphic;
	}

	public boolean isStaticVal() {
		return isStaticVal;
	}

	public void setStaticVal(boolean isStaticVal) {
		this.isStaticVal = isStaticVal;
	}

	public boolean isStringOp() {
		return isStringOp;
	}

	public void setStringOp(boolean isStringOp) {
		this.isStringOp = isStringOp;
	}

	public boolean isFlowSensitive() {
		return isFlowSensitive;
	}

	public void setFlowSensitive(boolean isFlowSensitive) {
		this.isFlowSensitive = isFlowSensitive;
	}

	public boolean isPathSensitive() {
		return isPathSensitive;
	}

	public void setPathSensitive(boolean isPathSensitive) {
		this.isPathSensitive = isPathSensitive;
	}

	public boolean isContextSensitive() {
		return isContextSensitive;
	}

	public void setContextSensitive(boolean isContextSensitive) {
		this.isContextSensitive = isContextSensitive;
	}

	public boolean isFieldSensitive() {
		return isFieldSensitive;
	}

	public void setFieldSensitive(boolean isFieldSensitive) {
		this.isFieldSensitive = isFieldSensitive;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the methodTrace
	 */
	public List<String> getMethodTrace() {
		return methodTrace;
	}

	/**
	 * @param methodTrace
	 *            the methodTrace to set
	 */
	public void setMethodTrace(List<String> methodTrace) {
		this.methodTrace = methodTrace;
	}

	/**
	 * @return the callLineTrace
	 */
	public List<String> getCallLineTrace() {
		return callLineTrace;
	}

	/**
	 * @param callLineTrace
	 *            the callLineTrace to set
	 */
	public void setCallLineTrace(List<String> callLineTrace) {
		this.callLineTrace = callLineTrace;
	}

	/**
	 * @return the isImpliciyCallBack
	 */
	public boolean isImpliciyCallBack() {
		return isImpliciyCallBack;
	}

	/**
	 * @param isImpliciyCallBack
	 *            the isImpliciyCallBack to set
	 */
	public void setImpliciyCallBack(boolean isImpliciyCallBack) {
		this.isImpliciyCallBack = isImpliciyCallBack;
	}

	/**
	 * @return the isObjectSensitive
	 */
	public boolean isObjectSensitive() {
		return isObjectSensitive;
	}

	/**
	 * @param isObjectSensitive
	 *            the isObjectSensitive to set
	 */
	public void setObjectSensitive(boolean isObjectSensitive) {
		this.isObjectSensitive = isObjectSensitive;
	}

	/**
	 * @return the isNoExtra
	 */
	public boolean isNoExtra() {
		return isNoExtra;
	}

	/**
	 * @param isNoExtra
	 *            the isNoExtra to set
	 */
	public void setNoExtra(boolean isNoExtra) {
		this.isNoExtra = isNoExtra;
	}

	/**
	 * @return the isLifeCycleOnly
	 */
	public boolean isLifeCycleOnly() {
		return isLifeCycleOnly;
	}

	/**
	 * @param isLifeCycleOnly
	 *            the isLifeCycleOnly to set
	 */
	public void setLifeCycleOnly(boolean isLifeCycleOnly) {
		this.isLifeCycleOnly = isLifeCycleOnly;
	}

	/**
	 * @return the isCallBackInclude
	 */
	public boolean isCallBackInclude() {
		return isCallBackInclude;
	}

	/**
	 * @param isCallBackInclude
	 *            the isCallBackInclude to set
	 */
	public void setCallBackInclude(boolean isCallBackInclude) {
		this.isCallBackInclude = isCallBackInclude;
	}

	/**
	 * @return the isNonActivy
	 */
	public boolean isNonActivity() {
		return isNonActivity;
	}

	/**
	 * @param isNonActivy
	 *            the isNonActivy to set
	 */
	public void setNonActivity(boolean isNonActivity) {
		this.isNonActivity = isNonActivity;
	}

	/**
	 * @return the isNonComponent
	 */
	public boolean isNonComponent() {
		return isNonComponent;
	}

	/**
	 * @param isNonComponent
	 *            the isNonComponent to set
	 */
	public void setNonComponent(boolean isNonComponent) {
		this.isNonComponent = isNonComponent;
	}

	public boolean isStaticCallBackonly() {
		return isStaticCallBackonly;
	}

	public void setStaticCallBackonly(boolean isStaticCallBackonly) {
		this.isStaticCallBackonly = isStaticCallBackonly;
	}

	public boolean isDynamicCallBackonly() {
		return isDynamicCallBackonly;
	}

	public void setDynamicCallBackonly(boolean isDynamicCallBackonly) {
		this.isDynamicCallBackonly = isDynamicCallBackonly;
	}

	public boolean isImplicitCallBackonly() {
		return isImplicitCallBackonly;
	}

	public void setImplicitCallBackonly(boolean isImplicitCallBackonly) {
		this.isImplicitCallBackonly = isImplicitCallBackonly;
	}

	public boolean isWarrperonly() {
		return isWarrperonly;
	}

	public void setWarrperonly(boolean isWarrperonly) {
		this.isWarrperonly = isWarrperonly;
	}

	public boolean isImplicitICConly() {
		return isImplicitICConly;
	}

	public void setImplicitICConly(boolean isImplicitICConly) {
		this.isImplicitICConly = isImplicitICConly;
	}

	public boolean isNonActonly() {
		return isNonActonly;
	}

	public void setNonActonly(boolean isNonActonly) {
		this.isNonActonly = isNonActonly;
	}

	public boolean isNonComponentonly() {
		return isNonComponentonly;
	}

	public void setNonComponentonly(boolean isNonComponentonly) {
		this.isNonComponentonly = isNonComponentonly;
	}

	public boolean isContextSensionly() {
		return isContextSensionly;
	}

	public void setContextSensionly(boolean isContextSensionly) {
		this.isContextSensionly = isContextSensionly;
	}

	public boolean isLibonly() {
		return isLibonly;
	}

	public void setLibonly(boolean isLibonly) {
		this.isLibonly = isLibonly;
	}

	public boolean isAsynconly() {
		return isAsynconly;
	}

	public void setAsynconly(boolean isAsynconly) {
		this.isAsynconly = isAsynconly;
	}

	public boolean isPolymonly() {
		return isPolymonly;
	}

	public void setPolymonly(boolean isPolymonly) {
		this.isPolymonly = isPolymonly;
	}

	/**
	 * @return the isFragmentonly
	 */
	public boolean isFragmentonly() {
		return isFragmentonly;
	}

	/**
	 * @param isFragmentonly
	 *            the isFragmentonly to set
	 */
	public void setFragmentonly(boolean isFragmentonly) {
		this.isFragmentonly = isFragmentonly;
	}

	/**
	 * @return the isAdapteronly
	 */
	public boolean isAdapteronly() {
		return isAdapteronly;
	}

	/**
	 * @param isAdapteronly
	 *            the isAdapteronly to set
	 */
	public void setAdapteronly(boolean isAdapteronly) {
		this.isAdapteronly = isAdapteronly;
	}

	/**
	 * @return the isWidgetonly
	 */
	public boolean isWidgetonly() {
		return isWidgetonly;
	}

	/**
	 * @param isWidgetonly
	 *            the isWidgetonly to set
	 */
	public void setWidgetonly(boolean isWidgetonly) {
		this.isWidgetonly = isWidgetonly;
	}

	/**
	 * @return the isOtherClassonly
	 */
	public boolean isOtherClassonly() {
		return isOtherClassonly;
	}

	/**
	 * @param isOtherClassonly
	 *            the isOtherClassonly to set
	 */
	public void setOtherClassonly(boolean isOtherClassonly) {
		this.isOtherClassonly = isOtherClassonly;
	}

	/**
	 * @return the isSomeSensitive
	 */
	public boolean isSomeSensitive() {
		return isSomeSensitive;
	}

	/**
	 * @param isSomeSensitive
	 *            the isSomeSensitive to set
	 */
	public void setSomeSensitive(boolean isSomeSensitive) {
		this.isSomeSensitive = isSomeSensitive;
	}
}
