package main.java.analyze.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConstantUtils {
	public static final Object VERSION = "1.0";

	public static final String ACTIVITY = "Activity";
	public static final String SERVICE = "Service";
	public static final String RECEIVER = "Receiver";
	public static final String PROVIDER = "Provider";
	// soot config
	public static final String android_jar_path = "lib" + File.separator;
	public static final String SOOTOUTPUT = "SootIRInfo";

	// output files info
	public static final String GATORFOLDER = "GatorInfo" + File.separator;
	public static final String DUMMYFOLDETR = "DummyMainInfo" + File.separator;
	public static final String CGFOLDETR = "CallGraphInfo" + File.separator;
	public static final String ICTGFOLDETR = "CTGResult" + File.separator;
	public static final String ICTGSPEC = "ICCSpecification" + File.separator;
	public static final String IC3TMPFOLDETR = "relatedTools" + File.separator + "IC3_tmp" + File.separator;
	public static final String IC3FOLDETR = "relatedTools" + File.separator + "IC3" + File.separator;
	public static final String A3EFOLDETR = "relatedTools" + File.separator + "A3E" + File.separator;
	public static final String STORYFOLDETR = "relatedTools" + File.separator + "StoryDistiller" + File.separator;
	public static final String IC3DIALDROIDTMPFOLDETR = "relatedTools" + File.separator + "IC3-Dial_tmp" + File.separator;
	public static final String IC3DIALDROIDFOLDETR = "relatedTools" + File.separator + "IC3-Dial" + File.separator;
	public static final String GATORFOLDETR = "relatedTools" + File.separator + "GATOR" + File.separator;
	public static final String ICCINFOFOLDETR = "iccInfo" + File.separator;
	public static final String MANIFOLDETR = "ManifestInfo" + File.separator;
	public static final String STATFOLDETR = "statistic" + File.separator;
	public static final String FRAGFOLDETR = "FragmentInfo" + File.separator;
	public static final String FRAGTMPFOLDETR = "FragmentInfo" + File.separator;
	public static final String ORACLEFOLDETR = "CTGEvaluate" + File.separator;
	public static final String LABELEDORACLEFOLDETR = "labeledOracle" + File.separator;

	public static final String EXPORTEDLIST = "ExportedComponentList.txt";
	public static final String COMPONENTLIST = "ComponentList.txt";
	public static final String RECEICEDINTENTMODEL = "receivedIntentModel.txt";
	public static final String SENDINTENTMODEL = "sendIntentModel.txt";
	public static final String AUPFILE = "paths.txt";
	public static final String ICTGMERGE = "CTGwithFragment";
	public static final String ICTGOPT = "CTG";
	public static final String ICTGOPTFRAG = "CTG_Fragment";
	public static final String ICTGDYNAMIC = "CTGDynamic";
	public static final String ICTGDMANUAL = "CTGManual";
	public static final String ICTGORACLE = "CTGOracle";
	public static final String ICTG = "CTG";
	public static final String ICTGFRAGWITHCLASS = "fragLoadWithClass";
	public static final String ICTGFRAG = "fragLoad";
	public static final String ATGDOT_IC3 = "atg@ic3";
	public static final String ATGDOT_A3E = "atg@a3e";
	public static final String ATGDOT_STORY = "atg@story";
	public static final String ATGDOT_IC3DIAL = "atg@ic3-dial";
	public static final String ATGDOT_GATOR = "atg@gator";
	public static final String CG = "cg.txt";
	public static final String CGDOT = "cg.dot";
	public static final String TOPO = "topology.txt";
	public static final String ATGREACHABLE = "atgReachable.txt";
	public static final String ICCMSGFILE = "testCase.iccmsg";
	public static final String LINKFILE = "IccLinksConfigFile.txt";

	public static final String SINGLEMETHOD_ALL = "methodSummary_all.xml";
	public static final String SINGLEMETHOD_ENTRY = "methodSummary_frag_entry.xml";
	public static final String SINGLEMETHODFRAG_ENTRY = "methodSummary_frag_entry.xml";
	public static final String SINGLEPATH_ENTRY = "pathSummary_entry.xml";
	public static final String SINGLEPATHFRAG_ENTRY = "pathSummary_entry.xml";
	public static final String SINGLEPATH_ALL = "pathSummary_all.xml";
	public static final String SINGLEOBJECT_ENTRY = "objectSummary_entry.xml";
	public static final String SINGLEOBJECTFRAG_ENTRY = "objectSummary_frag_entry.xml";
	public static final String SINGLEOBJECT_ALL = "objectSummary_all.xml";
	public static final String COMPONENTMODEL = "componentInfo.xml";
	public static final String STATISTIC = "statisticResult.xml";
	public static final String ORACLETEXT = "_oracle.xml";
	public static final String ORACLEMANU = "_oracle_manual.txt";
	public static final String ORACLEDYNA = "_oracle_dynamic.txt";
	public static final String SCORERECORD = "_scores.txt";

	public static final String DUMMYMAIN = "dummyMain";
	public static final String ENTRYID = "on";

	public static final String RECEIVE = "Receive";
	public static final String SEND = "Send";
	public static final String ACTDEC = "DeclaredModel.txt";
	
	public static final String COMPONENTMODELJSON = "ComponentModel";

	// output files folder
	public static final String TEAMPLATEFOLDER = "template" + File.separator;
	public static final String INSTRUFOLDER = "instrument" + File.separator;
	public static final String INSTRUFILE = "instrument.txt";
	public static final String SYSTEMSERVICE = "SystemService.txt";

	// test case generation
	public static final String TESTCASEFOLDER = "testcases" + File.separator;
	public static final String APKTESTCASEFOLDER = "apkTestCases" + File.separator;
	public static final String GENERATEDAPPFOLDER = "generatedApp" + File.separator;

	// Constant string
	public static final String UNKOWN = "key_unkown";
	public static final String FLAGATTRI = "attributes";
	public static final String FLAGSTATIC = "staticValues";
	public static final String FLAGEXTRA = "extras";
	public static final String FLAGPARAM = "param";

	// Constant number
	public static final int UPLIMIT = 500;
	public static final int INTERCALLLIMIT = 1000;
	public static final int GETVALUELIMIT = 1000;

	// Android project build
	public static final String SRC = "src";
	public static final String APKFLAGPREFIX = "icc.";
	public static final String APKFLAGSUFFIX = "";
	public static final String LAYOUTMAIN = "main.xml";
	public static final String MANIFEST = "AndroidManifest.txt";
	public static final String MYMANIFEST = "AndroidManifest.txt";
	public static final String DEFAULTCALLBACKFILE = "AndroidCallbacks.txt";
	public static final String SERJAVA = "MySerializable";
	public static final String PARJAVA = "MyParcelable";
	public static final String CREATEPROJECT = "create project";

	// Android APIs
	public static final String[] implicitExecutes = { "onPreExecute(", "doInBackground(", "onPostExecute(" };
	public static final String[] implicitStart = { "start(" };

	public static final String ACTIVITY_ONCREATE = "void onCreate(android.os.Bundle)";
	public static final String ACTIVITY_ONSTART = "void onStart()";
	public static final String ACTIVITY_ONRESTOREINSTANCESTATE = "void onRestoreInstanceState(android.os.Bundle)";
	public static final String ACTIVITY_ONPOSTCREATE = "void onPostCreate(android.os.Bundle)";
	public static final String ACTIVITY_ONRESUME = "void onResume()";
	public static final String ACTIVITY_ONPOSTRESUME = "void onPostResume()";
	public static final String ACTIVITY_ONCREATEDESCRIPTION = "java.lang.CharSequence onCreateDescription()";
	public static final String ACTIVITY_ONSAVEINSTANCESTATE = "void onSaveInstanceState(android.os.Bundle)";
	public static final String ACTIVITY_ONPAUSE = "void onPause()";
	public static final String ACTIVITY_ONSTOP = "void onStop()";
	public static final String ACTIVITY_ONRESTART = "void onRestart()";
	public static final String ACTIVITY_ONDESTROY = "void onDestroy()";

	public static final String SERVICE_ONCREATE = "void onCreate()";
	public static final String SERVICE_ONSTART1 = "void onStart(android.content.Intent,int)";
	public static final String SERVICE_ONSTART2 = "int onStartCommand(android.content.Intent,int,int)";
	public static final String SERVICE_ONBIND = "android.os.IBinder onBind(android.content.Intent)";
	public static final String SERVICE_ONREBIND = "void onRebind(android.content.Intent)";
	public static final String SERVICE_ONUNBIND = "boolean onUnbind(android.content.Intent)";
	public static final String SERVICE_ONDESTROY = "void onDestroy()";

	public static final String FRAGMENT_ONCREATE = "void onCreate(android.os.Bundle)";
	public static final String FRAGMENT_ONATTACH = "void onAttach(android.app.Activity)";
	public static final String FRAGMENT_ONCREATEVIEW = "android.view.View onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)";
	public static final String FRAGMENT_ONVIEWCREATED = "void onViewCreated(android.view.View,android.os.Bundle)";
	public static final String FRAGMENT_ONSTART = "void onStart()";
	public static final String FRAGMENT_ONACTIVITYCREATED = "void onActivityCreated(android.os.Bundle)";
	public static final String FRAGMENT_ONVIEWSTATERESTORED = "void onViewStateRestored(android.app.Activity)";
	public static final String FRAGMENT_ONRESUME = "void onResume()";
	public static final String FRAGMENT_ONPAUSE = "void onPause()";
	public static final String FRAGMENT_ONSTOP = "void onStop()";
	public static final String FRAGMENT_ONDESTROYVIEW = "void onDestroyView()";
	public static final String FRAGMENT_ONDESTROY = "void onDestroy()";
	public static final String FRAGMENT_ONDETACH = "void onDetach()";
	public static final String FRAGMENT_ONSAVEINSTANCESTATE = "void onSaveInstanceState(android.os.Bundle)";

	public static final String BROADCAST_ONRECEIVE = "void onReceive(android.content.Context,android.content.Intent)";

	public static final String CONTENTPROVIDER_ONCREATE = "boolean onCreate()";

	public static final String onCreateOptionsMenu = "boolean onCreateOptionsMenu(android.view.Menu)";
	public static final String onOptionsItemSelected = "boolean onOptionsItemSelected(android.view.MenuItem)";

	private static final String[] activityMethods = { ACTIVITY_ONCREATE, ACTIVITY_ONDESTROY, ACTIVITY_ONPAUSE,
			ACTIVITY_ONRESTART, ACTIVITY_ONRESUME, ACTIVITY_ONSTART, ACTIVITY_ONSTOP, ACTIVITY_ONSAVEINSTANCESTATE,
			ACTIVITY_ONRESTOREINSTANCESTATE, ACTIVITY_ONCREATEDESCRIPTION, ACTIVITY_ONPOSTCREATE, ACTIVITY_ONPOSTRESUME };
	private static final String[] serviceMethods = { SERVICE_ONCREATE, SERVICE_ONDESTROY, SERVICE_ONSTART1,
			SERVICE_ONSTART2, SERVICE_ONBIND, SERVICE_ONREBIND, SERVICE_ONUNBIND };
	private static final String[] fragmentMethods = { FRAGMENT_ONCREATE, FRAGMENT_ONDESTROY, FRAGMENT_ONPAUSE,
			FRAGMENT_ONATTACH, FRAGMENT_ONDESTROYVIEW, FRAGMENT_ONRESUME, FRAGMENT_ONSTART, FRAGMENT_ONSTOP,
			FRAGMENT_ONCREATEVIEW, FRAGMENT_ONACTIVITYCREATED, FRAGMENT_ONVIEWSTATERESTORED, FRAGMENT_ONDETACH,
			FRAGMENT_ONSAVEINSTANCESTATE };
	private static final String[] broadcastMethods = { BROADCAST_ONRECEIVE };
	private static final String[] contentproviderMethods = { CONTENTPROVIDER_ONCREATE };

	private static final String[] selfEntryMethods = { onCreateOptionsMenu, onOptionsItemSelected };

	private static final String[] lifeCycleMethods = { ACTIVITY_ONCREATE, ACTIVITY_ONDESTROY, ACTIVITY_ONPAUSE,
			ACTIVITY_ONRESTART, ACTIVITY_ONRESUME, ACTIVITY_ONSTART, ACTIVITY_ONSTOP, ACTIVITY_ONSAVEINSTANCESTATE,
			ACTIVITY_ONRESTOREINSTANCESTATE, ACTIVITY_ONCREATEDESCRIPTION, ACTIVITY_ONPOSTCREATE,
			ACTIVITY_ONPOSTRESUME,

			SERVICE_ONCREATE, SERVICE_ONDESTROY, SERVICE_ONSTART1, SERVICE_ONSTART2, SERVICE_ONBIND, SERVICE_ONREBIND,
			SERVICE_ONUNBIND,

			BROADCAST_ONRECEIVE,

			CONTENTPROVIDER_ONCREATE,

			FRAGMENT_ONCREATE, FRAGMENT_ONDESTROY, FRAGMENT_ONPAUSE, FRAGMENT_ONATTACH, FRAGMENT_ONDESTROYVIEW,
			FRAGMENT_ONRESUME, FRAGMENT_ONSTART, FRAGMENT_ONSTOP, FRAGMENT_ONCREATEVIEW, FRAGMENT_ONACTIVITYCREATED,
			FRAGMENT_ONVIEWSTATERESTORED, FRAGMENT_ONDETACH };
	public static final List<String> lifeCycleMethodsSet = Arrays.asList(lifeCycleMethods);
	public static final List<String> selfEntryMethodsSet = Arrays.asList(selfEntryMethods);

	// send intent method
	public static final String[] sendIntent2ActivityMethods = { "void startActivities(", "void startActivity(",
			"void startActivityForResult(", "void startActivityFromChild(", "void startActivityFromFragment(",
			"void startActivityIfNeeded(", "android.content.pm.ResolveInfo resolveActivity(" };
	// send intent method
	public static final String[] sendIntent2ServiceMethods = { "android.content.ComponentName startService(",
			"void startForegroundService(", "boolean bindService(", };
	// send intent method
	public static final String[] sendIntent2ReceiverMethods = { "void sendBroadcast(", "void sendBroadcastAsUser(",
			"void sendOrderedBroadcast(", "void sendOrderedBroadcastAsUser(", "void sendStickyBroadcast(",
			"void sendStickyBroadcastAsUser(", "void sendStickyOrderedBroadcast(",
			"void sendStickyOrderedBroadcastAsUser(", };
	// send intent method
	public static final String[] sendIntent2ProviderMethods = {
	// "int update(",
	// "android.database.Cursor query(",
	// "android.net.Uri insert(",
	// "void notifyChange(",
	// "void registerContentObserver(",
	// "int delete(",
	};
	// component op methods
	public static final String[] componentOpMethods = { "void finish()" };

	// get intent extra method
	public static final String[] getIntnetExtraMethods = { "getDoubleArrayExtra", "getDoubleExtra",
			"getFloatArrayExtra", "getFloatExtra", "getIntArrayExtra", "getIntExtra", "getIntegerArrayListExtra",
			"getLongArrayExtra", "getLongExtra", "getParcelableArrayExtra", "getParcelableArrayListExtra",
			"getParcelableExtra", "getSerializableExtra", "getShortExtra", "getShortArrayExtra", "getStringArrayExtra",
			"getStringArrayListExtra", "getStringExtra", "getBooleanArrayExtra", "getBooleanExtra",
			"getByteArrayExtra", "getByteExtra", "getCharArrayExtra", "getCharExtra", "getCharSequenceArrayExtra",
			"getCharSequenceArrayListExtra", "getCharSequenceExtra", "getBundleExtra", "getExtras",
			"android.os.Bundle: java.lang.Object get" };

	// get intent extra method
	public static final String[] setIntnetExtraMethods = { "putExtra", "putIntegerArrayListExtra",
			"putCharSequenceArrayListExtra", "putExtras", "putParcelableArrayListExtra", "putStringArrayListExtra" };

	// extra intent method types
	public static final String[] intentExtraMethodTypes = { "doubleArray", "double", "floatArray", "float", "intArray",
			"int", "IntegerArrayList", "longArray", "long", "ParcelableArray", "ParcelableArrayList", "Parcelable",
			"Serializable", "short", "shortArray", "StringArray", "StringArrayList", "String", "booleanArray",
			"boolean", "byteArray", "byte", "charArray", "char", "CharSequenceArray", "CharSequenceArrayList",
			"CharSequence", "Bundle", "Extras", "String" };

	// put extra method
	public static final String[] putBundlleExtraMethods = {
		"putDoubleArray(", "putDouble(", "putFloatArray(", "putFloat(", "putIntArray(", "putInt(", "putIntegerArrayList(",
		"putLongArray(", "putLong(", "putParcelableArray(", "putParcelableArrayList(", "putParcelable(",
		"putSerializable(", "putShort(", "putShortArray(", "putStringArray(", "putStringArrayList(", "putString(",
		"putBooleanArray(", "putBoolean(", "putByteArray(", "putByte(", "putCharArray(", "putChar(",
		"putCharSequenceArray(", "putCharSequenceArrayList(", "putCharSequence", "putBundle(", "putExtras",
		"android.os.Bundle: java.lang.Object put" };
	
	// get extra method
	public static final String[] getBundlleExtraMethods = {

	"getDoubleArray(", "getDouble(", "getFloatArray(", "getFloat(", "getIntArray(", "getInt(", "getIntegerArrayList(",
			"getLongArray(", "getLong(", "getParcelableArray(", "getParcelableArrayList(", "getParcelable(",
			"getSerializable(", "getShort(", "getShortArray(", "getStringArray(", "getStringArrayList(", "getString(",
			"getBooleanArray(", "getBoolean(", "getByteArray(", "getByte(", "getCharArray(", "getChar(",
			"getCharSequenceArray(", "getCharSequenceArrayList(", "getCharSequence", "getBundle(", "getExtras",
			"android.os.Bundle: java.lang.Object get" };

	// extra method types
	public static final String[] bundleExtraMethodTypes = {

	"doubleArray", "double", "floatArray", "dloat", "intArray", "int", "IntegerArrayList", "longArray", "long",
			"ParcelableArray", "ParcelableArrayList", "Parcelable", "Serializable", "short", "shortArray",
			"StringArray", "StringArrayList", "String", "booleanArray", "boolean", "byteArray", "byte", "charArray",
			"char", "CharSequenceArray", "CharSequenceArrayList", "CharSequence", "Bundle", "Extras", "String" };


	public static final String[] comparedMethods = { "contains", "equals", "contentEquals", "equalsIgnoreCase",
			"startsWith", "endsWith", "!= null", "== null", "isEmpty", "valueOf", "copyValueOf", "==", "!=" };

	// get attribute method
	public static final String[] getAttributeMethods = { "android.content.Intent: java.lang.String getAction(",
			"android.content.Intent: java.util.Set getCategories(",
			"android.content.Intent: java.lang.String getDataString(",
			"android.content.Intent: android.net.Uri getData(",
			"android.content.Intent: java.lang.String getDataString(",
			"android.content.Intent: java.lang.String getType(" };

	// add attribute method
	public static final String[] setAttributeMethods = { "android.content.Intent: android.content.Intent setAction(",
			"android.content.Intent: android.content.Intent addCategory(",
			"android.content.Intent: android.content.Intent setData(",
			"android.content.Intent: android.content.Intent setDataAndNormalize(",
			"android.content.Intent: android.content.Intent setDataAndType(",
			"android.content.Intent: android.content.Intent setDataAndTypeAndNormalize(",
			"android.content.Intent: android.content.Intent setType(",
			"android.content.Intent: android.content.Intent setFlags(",
			"android.content.Intent: android.content.Intent addFlags(",
			"android.content.Intent: void <init>(java.lang.String)",
			"android.content.Intent: void <init>(android.content.Context,java.lang.Class)",
			"void <init>(java.lang.String,android.net.Uri,android.content.Context,java.lang.Class)",
			"android.content.Intent setClass(android.content.Context,java.lang.Class)", "setClassName(",
			"setComponent(", };

	
	public static final String[] unsafePrefix = {
			"<android.content.Context: java.lang.Object getSystemService(java.lang.String)>(\"activity\")",
			"<android.content.SharedPreferences", "<android.content.ContentProvider", "<android.app.Application",
			"<android.content.ContextWrapper", "<java.io.File", "android.content.ComponentName getCallingActivity()" };

	public static final String[] safePrefix = { "<android.content.Intent",
			"<android.content.Context: java.lang.Object getSystemService(java.lang.String)>" };
	public static final String[]  exitpoint= { "finish()", "throw " };

	public static String[] fragmentClasses = { "android.app.Fragment",
			"com.actionbarsherlock.app.SherlockListFragment", "android.support.v4.app.Fragment",
			"android.support.v4.app.ListFragment", "androidx.fragment.app.Fragment" };

	public static String[] dialogFragmentClasses = { "android.support.v4.app.DialogFragment",
			"androidx.fragment.app.DialogFragment" };

	public static String[] componentClasses = { "android.app.Activity", "android.app.Service",
			"android.content.BroadcastReceiver", "android.content.ContentProvider" };

}
