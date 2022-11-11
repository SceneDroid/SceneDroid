package main.java.analyze.model.labeledOracleModel;

public class IccTagCount {
	public int isLifeCycle;
	public int isStaticCallBack;
	public int isDynamicCallBack;
	public int isImpliciyCallBack;
	public int isLifeCycleOnly;
	public int isCallBackInclude;

	public int isNormalSendICC;
	public int isWarpperSendICC;

	public int isExplicit;
	public int isImplicit;

	public int isActivity;
	public int isService;
	public int isBroadCast;
	public int isDynamicBroadCast;
	public int isNonActivity;

	public int isFragment;
	public int isAdapter;
	public int isWidget;
	public int isOtherClass;
	public int isNonComponent;

	public int isLibraryInvocation;
	public int isAsyncInvocation;
	public int isPolymorphic;

	public int isStaticVal;
	public int isStringOp;
	public int isNoExtra;

	public int isFlowSensitive;
	public int isPathSensitive;
	public int isContextSensitive;
	public int isFieldSensitive;
	public int isObjectSensitive;

	public int isStaticCallBackonly;
	public int isDynamicCallBackonly;
	public int isImplicitCallBackonly;
	public int isWarrperonly;
	public int isImplicitICConly;
	public int isNonActonly;
	public int isNonComponentonly;
	public int isFragmentonly;
	public int isAdapteronly;
	public int isWidgetonly;
	public int isOtherClassonly;
	public int isContextSensionly;
	public int isLibonly;
	public int isAsynconly;
	public int isPolymonly;
	public int isStaticValOnly;
	public int isStringOpOnly;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("isLifeCycle: " + isLifeCycle + "\t");
		sb.append("isStaticCallBack: " + isStaticCallBack + "\t");
		sb.append("isDynamicCallBack: " + isDynamicCallBack + "\t");
		sb.append("isImpliciyCallBack: " + isImpliciyCallBack + "\n");
		sb.append("isLifeCycleOnly: " + isLifeCycleOnly + "\n");
		sb.append("isCallBackInclude: " + isCallBackInclude + "\n");

		sb.append("isNormalSendICC: " + isNormalSendICC + "\t");
		sb.append("isWarpperSendICC: " + isWarpperSendICC + "\n");

		sb.append("isExplicit: " + isExplicit + "\t");
		sb.append("isImplicit: " + isImplicit + "\n");

		sb.append("isActivity: " + isActivity + "\t");
		sb.append("isService: " + isService + "\t");
		sb.append("isBroadCast: " + isBroadCast + "\t");
		sb.append("isDynamicBroadCast: " + isDynamicBroadCast + "\n");
		sb.append("isNonActivity: " + isNonActivity + "\n");

		sb.append("isFragment: " + isFragment + "\t");
		sb.append("isAdapter: " + isAdapter + "\t");
		sb.append("isWidget: " + isWidget + "\t");
		sb.append("isOtherClass: " + isOtherClass + "\n");
		sb.append("isNonComponent: " + isNonComponent + "\n");

		sb.append("isLibraryInvocation: " + isLibraryInvocation + "\t");
		sb.append("isAsyncInvocation: " + isAsyncInvocation + "\t");
		sb.append("isPolymorphic: " + isPolymorphic + "\n");

		sb.append("isStaticVal: " + isStaticVal + "\t");
		sb.append("isStringOp: " + isStringOp + "\t");
		sb.append("isNoExtra: " + isNoExtra + "\n");

		sb.append("isFlowSensitive: " + isFlowSensitive + "\t");
		sb.append("isPathSensitive: " + isPathSensitive + "\t");
		sb.append("isContextSensitive: " + isContextSensitive + "\t");
		sb.append("isObjectSensitive: " + isObjectSensitive + "\t");
		sb.append("isFieldSensitive: " + isFieldSensitive + "\n");

		sb.append("isStaticCallBackonly: " + isStaticCallBackonly + "\t");
		sb.append("isDynamicCallBackonly: " + isDynamicCallBackonly + "\t");
		sb.append("isImplicitCallBackonly: " + isImplicitCallBackonly + "\n");
		sb.append("isWarrperonly: " + isWarrperonly + "\t");
		sb.append("isImplicitICConly: " + isImplicitICConly + "\n");
		sb.append("isNonActonly: " + isNonActonly + "\t");
		sb.append("isNonComponentonly: " + isNonComponentonly + "\n");

		sb.append("isFragmentonly: " + isFragmentonly + "\t");
		sb.append("isAdapteronly: " + isAdapteronly + "\t");
		sb.append("isWidgetonly: " + isWidgetonly + "\t");
		sb.append("isOtherClassonly: " + isOtherClassonly + "\n");

		sb.append("isContextSensionly: " + isContextSensionly + "\t");
		sb.append("isLibonly: " + isLibonly + "\t");
		sb.append("isAsynconly: " + isAsynconly + "\t");
		sb.append("isPolymonly: " + isPolymonly + "\n");
		return sb.toString();
	}

	public String getTitle() {
		StringBuilder sb = new StringBuilder();
		// callback
		sb.append("callBack\t");
		sb.append("staticCB\t");
		sb.append("dynamicCB\t");
		sb.append("implicitCB\t");

		sb.append("atypicalICC\t");
		sb.append("implicitICC\t");
		sb.append("stringOp\t");
		sb.append("staticVal\t");

		sb.append("callContext\t");
		sb.append("async\t");
		sb.append("polym\t");
		
		// component
		sb.append("nonAct\t");
		sb.append("dynamicBC\t");
		sb.append("fragment\t");
		sb.append("adapter\t");
		sb.append("widget\t");
		sb.append("otherClass\t");

		// library
		sb.append("isLibraryInvocation\t");

		

		sb.append("staticCBOnly\t");
		sb.append("dynamicCBOnly\t");
		sb.append("implicitCBonly\t");
		sb.append("atypicalICCOnly\t");
		sb.append("implicitICCOnly\t");
		
		sb.append("stringOpOnly\t");
		sb.append("staticValOnly\t");
		
		sb.append("callContextOnly\t");
		sb.append("asyncOnly\t");
		sb.append("polymOnly\t");
		
		sb.append("nonActonly\t");
		sb.append("dynamicBCOnly\t");

		sb.append("fragmentOnly\t");
		sb.append("adapterOnly\t");
		sb.append("widgetOnly\t");
		sb.append("otherClassOnly\t");
		sb.append("libraryonly");


		return sb.toString();
	}

	public String toSimpleString() {
		StringBuilder sb = new StringBuilder();

		sb.append(isCallBackInclude + "\t");
		sb.append(isStaticCallBack + "\t");
		sb.append(isDynamicCallBack + "\t");
		sb.append(isImpliciyCallBack + "\t");
		
		sb.append(isWarpperSendICC + "\t");
		sb.append(isImplicit + "\t");
		sb.append(isStringOp + "\t");
		sb.append(isStaticVal + "\t");
		
		sb.append(isContextSensitive + "\t");
		sb.append(isAsyncInvocation + "\t");
		sb.append(isPolymorphic + "\t");
		
		sb.append(isNonActivity + "\t");
		sb.append(isDynamicBroadCast + "\t");
		sb.append(isFragment + "\t");
		sb.append(isAdapter + "\t");
		sb.append(isWidget + "\t");
		sb.append(isOtherClass + "\t");
		sb.append(isLibraryInvocation + "\t");

		
		sb.append(isStaticCallBackonly + "\t");
		sb.append(isDynamicCallBackonly + "\t");
		sb.append(isImplicitCallBackonly + "\t");
		sb.append(isWarrperonly + "\t");
		sb.append(isImplicitICConly + "\t");
		sb.append(isStringOpOnly + "\t");
		sb.append(isStaticValOnly + "\t");
		
		sb.append(isContextSensionly + "\t");
		sb.append(isAsynconly + "\t");
		sb.append(isPolymonly + "\t");
		
		sb.append(isNonActonly + "\t");
		sb.append(isDynamicCallBackonly + "\t");
		sb.append(isFragmentonly + "\t");
		sb.append(isAdapteronly + "\t");
		sb.append(isWidgetonly + "\t");
		sb.append(isOtherClassonly + "\t");
		sb.append(isLibonly );


		
		return sb.toString();
	}
}
