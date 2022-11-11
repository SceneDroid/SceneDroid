package main.java;

import java.io.File;

import main.java.analyze.utils.TimeUtilsofProject;
import main.java.client.BaseClient;
import main.java.client.cg.CallGraphClient;
import main.java.client.gator.GatorClient;
import main.java.client.manifest.ManifestClient;
import main.java.client.obj.checker.MisEACheckerClient;
import main.java.client.obj.target.ctg.CTGClient;
import main.java.client.obj.target.ctg.ICCSpecClient;
import main.java.client.obj.target.fragment.FragmentClient;
import main.java.client.obj.target.fragment.FragmentRemoveClient;
import main.java.client.related.gator.GatorATGResultEvaluateClient;
import main.java.client.related.ic3.IC3ResultEvaluateClient;
import main.java.client.related.ic3dial.IC3DIALDroidResultEvaluateClient;
import main.java.client.soot.IROutputClient;
import main.java.client.testcase.TestGenerationClient;
import main.java.client.toolEvaluate.ToolEvaluateClient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main Class of Android ICC Resolution Tool ICCBot
 * 
 * @author hanada
 * @version 2.0 
 */
public class MainClass {

	/**
	 * get commands from args
	 * @param args
	 */
	public static void main(String[] args) {
		/** analyze args**/
		CommandLine mCmd = getCommandLine(args);
		analyzeArgs(mCmd);
		/** start ICCBot**/
		startAnalyze();
		
		System.out.println("ICC Resolution Finish...\n");
		System.exit(0);
	}

	/**
	 * 
	 * @param mCmdArgs
	 * @return
	 */
	private static CommandLine getCommandLine(String[] mCmdArgs) {
		CommandLineParser parser = new DefaultParser();
		try {
			return parser.parse(getOptions(), mCmdArgs, false);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * start the analyze of app with a given client
	 */
	public static void startAnalyze() {
		System.out.println("Analyzing " + MyConfig.getInstance().getAppName());
		BaseClient client = getClient();

		TimeUtilsofProject.setTotalTimer(client);
		long startTime = System.currentTimeMillis();

		client.start();

		long endTime = System.currentTimeMillis();
		System.out.println("---------------------------------------");
		System.out.println("Analyzing " + MyConfig.getInstance().getAppName() + " Finish...\n");
		System.out.println(MyConfig.getInstance().getClient() + " time = " + (endTime - startTime) / 1000 + " seconds");
	}
	
	

	
	
	/**
	 * get the client to be analyzed
	 * the default client is used for ICC resolution
	 * @return
	 */
	private static BaseClient getClient() {
		System.out.println("using client " + MyConfig.getInstance().getClient());
		BaseClient client;
		switch (MyConfig.getInstance().getClient()) {
			case "CTGClient":
				client = new CTGClient();
				break;
			case "ICCSpecClient":
				client = new ICCSpecClient();
				break;
			case "MainClient":
				client = new CTGClient();
				break;
			case "IROutputClient":
				client = new IROutputClient();
				break;
			case "ManifestClient":
				client = new ManifestClient();
				break;
			case "CallGraphClient":
				client = new CallGraphClient();
				break;
			case "FragmentClient":
				client = new FragmentClient();
				break;
//			case "StoryResultEvaluateClient":
//				client = new IC3ResultEvaluateClient();
//				break;
//			case "IC3DIALDroidResultEvaluateClient":
//				client = new IC3DIALDroidResultEvaluateClient();
//				break;
//			case "GatorATGResultEvaluateClient":
//				client = new GatorATGResultEvaluateClient();
//				break;
//			case "ToolEvaluateClient":
//				client = new ToolEvaluateClient();
//				break;
//			case "TestGenerationClient":
//				client = new TestGenerationClient();
//				break;
//			case "MisEACheckerClient":
//				client = new MisEACheckerClient();
//				break;
//			case "GatorClient":
//				client = new GatorClient();
//				break;
//			case "FragmentRemoveClient":
//				client = new FragmentRemoveClient();
//				break;
			default:
				client = new CTGClient();
				break;
		}
		return client;
	}
	

	/**
	 * construct the structure of options
	 * 
	 * @return
	 */
	private static Options getOptions() {
		Options options = new Options();

		options.addOption("h", false, "-h: Show the help information.");

		/** input **/
		options.addOption("name", true, "-name: Set the name of the apk under analysis.");
		options.addOption("path", true, "-path: Set the path to the apk under analysis.");
		options.addOption("androidJar", true, "-androidJar: Set the path of android.jar.");
//		options.addOption("version", true, "-version [default:23]: Version of Android SDK.");
		
		/** analysis config **/
		options.addOption("client", true, "-client "
			
				+ "CallGraphClient: Output call graph files.\n"
				+ "ManifestClient: Output manifest.xml file.\n"
				+ "IROutputClient: Output soot IR files.\n"
				+ "FragmentClient: Output the fragment loading results.\n"
				+ "CTGClient/MainClient: Resolve ICC and generate CTG.\n"
				+ "ICCSpecClient: Report ICC specification for each component.\n"

//				+ "ICCBotResultEvaluateClient: Evaluate the results generated by ICCBot.\n"
//				+ "StoryResultEvaluateClient: Evaluate the results generated by IC3.\n"
//				+ "IC3DIALDroidResultEvaluateClient: Evaluate the results generated by IC3DIALDroid.\n"
//				+ "GatorATGResultEvaluateClient: Evaluate the results generated by Gator.\n"
//				+ "ToolEvaluateClient: Evaluate the results of four tools.\n"
//				+ "TestGenerationClient: Generate test cases for components based on the static results.\n"
//				+ "MisEACheckerClient: Report the mis-exported activities.\n"
//				+ "GatorClient: Invoke the client in Gator tool.\n"
			);
		/** analysis config **/
		options.addOption("time", true, "-time [default:90]: Set the max running time (min).");
		options.addOption("maxPathNumber", true, "-maxPathNumber [default:100]: Set the max number of paths.");
		options.addOption("maxFunctionExpandNumber", true, "-maxFunctionExpandNumber [default:10]: Set the max number of expanded functions when perform inter-precedural analysis.");
		options.addOption("maxObjectSummarySize", true, "-maxObjectSummarySize [default:1000]: Set the max number of units in an object summary.");
		options.addOption("callgraphAlgorithm", true, "-callgraphAlgorithm [default:SPARK]: Set algorithm for CG, CHA or SPARK.");
		
		/** output **/
		options.addOption("outputDir", true, "-outputDir: Set the output folder of the apk.");

		/** Switch **/

		options.addOption("onlyDummyMain", false, "-onlyDummyMain: limit the entry scope");
		options.addOption("noCallBackEntry", false, "-noCallBackEntry: exclude the call back methods");
		options.addOption("noFunctionExpand", false, "-noFunctionExpand: do not inline function with useful contexts");
		options.addOption("noAsyncMethod", false, "-noAsyncMethod: exclude async method call edge");
		options.addOption("noPolym", false, "-noPolym: exclude polymorphism methods");
		options.addOption("noAdapter", false, "-noAdapter: exclude super simple adapter model");
		options.addOption("noStringOp", false, "-noStringOp: exclude string operation model");
		options.addOption("noStaticField", false, "-noStaticField: exclude static field analysis");
		options.addOption("noFragment", false, "-noFragment: exclude fragment operation model");
		options.addOption("noLibCode", false, "-noLibCode: exclude the activities not declared in app's package");
		options.addOption("noWrapperAPI", false, "-noWrapperAPI: exclude RAICC model");
		options.addOption("noImplicit", false, "-noImplicit: exclude implict matching");
		options.addOption("noDynamicBC", false, "-noDynamicBC: exclude dynamic broadcast receiver matching");
		options.addOption("sootOutput", false, "-sootOutput: Output the sootOutput");
		
//		 /** Strategy **/
//		 options.addOption("summaryStrategy", true,
//		 "-summaryStrategy: choose the type of summary model from object/path/none");
//		 options.addOption("noVfgStrategy", false,
//		 "-vfgStrategy: do not use vfg model");
//		 options.addOption("cgAnalyzeGroup", false,
//		 "-cgAnalyzeGroup: group cg edges into several groups");
//		 options.addOption("getAttributeStrategy", false,
//		 "-getAttributeStrategy: include the analyze of intent data receiveing.");
//		 options.addOption("setAttributeStrategy", false,
//				 "-setAttributeStrategy: include the analyze of intent data sending.");
//				
//		 options.addOption("scenarioStack", false,
//		 "-scenarioStack: for stack related bug analysis.");

		return options;
	}

	/**
	 * analyze args and store information to MyConfig
	 * @param mCmd 
	 * 
	 */
	private static void analyzeArgs(CommandLine mCmd) {
		if (null == mCmd)
			System.exit(-1);

		if (mCmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.setWidth(120);
			
			formatter.printHelp("java -jar [jarFile] [options] [-path] [-name] [-outputDir] [-client]", getOptions());
			System.out.println("E.g., -path apk\\ -name test.apk -outputDir result -client MainClient");
			System.exit(0);
		}

		/** run config **/
		MyConfig.getInstance().setJimple(true);
		MyConfig.getInstance().setAppName(mCmd.getOptionValue("name", ""));
		MyConfig.getInstance().setAppPath(mCmd.getOptionValue("path", System.getProperty("user.dir")) + File.separator);
		MyConfig.getInstance().setAndroidJar(mCmd.getOptionValue("androidJar", "lib/platforms") + File.separator);
//		MyConfig.getInstance().setAndroidVersion("android-" + mCmd.getOptionValue("version", "23"));
		if (mCmd.hasOption("sootOutput"))
			MyConfig.getInstance().setWriteSootOutput(true);
		
		int timeLimit = Integer.valueOf(mCmd.getOptionValue("time", "90"));
		MyConfig.getInstance().setTimeLimit(timeLimit);
		MyConfig.getInstance().setMaxPathNumber(Integer.valueOf(mCmd.getOptionValue("maxPathNumber", "100")));
		MyConfig.getInstance().setMaxFunctionExpandNumber(Integer.valueOf(mCmd.getOptionValue("maxFunctionExpandNumber", "10")));
		MyConfig.getInstance().setMaxObjectSummarySize(Integer.valueOf(mCmd.getOptionValue("maxObjectSummarySize", "1000")));
		MyConfig.getInstance().setCallgraphAlgorithm(mCmd.getOptionValue("callgraphAlgorithm", "SPARK"));

		String client = mCmd.getOptionValue("client", "MainClient");
		MyConfig.getInstance().setClient(mCmd.getOptionValue("client", client));
		
		String gatorClient = mCmd.getOptionValue("gatorClient", "GUIHierarchyPrinterClient");
		MyConfig.getInstance().setGatorClient(mCmd.getOptionValue("gatorClient", gatorClient));

		MyConfig.getInstance().setResultFolder(mCmd.getOptionValue("outputDir", "outputDir") + File.separator);
		String resFolder = mCmd.getOptionValue("outputDir", "results/outputDir");
		if(resFolder.contains("/")){
			resFolder = resFolder.substring(0,resFolder.lastIndexOf("/"));
			MyConfig.getInstance().setResultWarpperFolder(resFolder+ File.separator);
		}else if(resFolder.contains("\\")){
			resFolder = resFolder.substring(0,resFolder.lastIndexOf("\\"));
			MyConfig.getInstance().setResultWarpperFolder(resFolder+ File.separator);
		}

		if (!mCmd.hasOption("name")) {
			printHelp("Please input the apk name use -name.");
		}
		
		/** analysis config **/
		if (mCmd.hasOption("onlyDummyMain"))
			MyConfig.getInstance().getMySwithch().setDummyMainSwitch(true);
		if (mCmd.hasOption("noCallBackEntry"))
			MyConfig.getInstance().getMySwithch().setCallBackSwitch(false);
		if (mCmd.hasOption("noFunctionExpand"))
			MyConfig.getInstance().getMySwithch().setFunctionExpandSwitch(false);
		if (mCmd.hasOption("noAsyncMethod"))
			MyConfig.getInstance().getMySwithch().setAsyncMethodSwitch(false);
		if (mCmd.hasOption("noPolym"))
			MyConfig.getInstance().getMySwithch().setPolymSwitch(false);

		if (mCmd.hasOption("noAdapter"))
			MyConfig.getInstance().getMySwithch().setAdapterSwitch(false);
		if (mCmd.hasOption("noStringOp"))
			MyConfig.getInstance().getMySwithch().setStringOpSwitch(false);
		if (mCmd.hasOption("noStaticField"))
			MyConfig.getInstance().getMySwithch().setStaticFieldSwitch(false);

		if (mCmd.hasOption("noFragment"))
			MyConfig.getInstance().getMySwithch().setFragmentSwitch(false);
		if (mCmd.hasOption("noLibCode"))
			MyConfig.getInstance().getMySwithch().setLibCodeSwitch(false);
		if (mCmd.hasOption("noWrapperAPI"))
			MyConfig.getInstance().getMySwithch().setWrapperAPISwitch(false);

		if (mCmd.hasOption("noImplicit"))
			MyConfig.getInstance().getMySwithch().setImplicitLaunchSwitch(false);
		if (mCmd.hasOption("noDynamicBC"))
			MyConfig.getInstance().getMySwithch().setDynamicBCSwitch(false);
		if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("none"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.none);
		else if (mCmd.hasOption("summaryStrategy") && mCmd.getOptionValue("summaryStrategy").equals("path"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.path);
		else if (mCmd.hasOption("summaryStrategy"))
			MyConfig.getInstance().getMySwithch().setSummaryStrategy(SummaryLevel.object);

		if (mCmd.hasOption("noVfgStrategy"))
			MyConfig.getInstance().getMySwithch().setVfgStrategy(false);
		if (mCmd.hasOption("cgAnalyzeGroup"))
			MyConfig.getInstance().getMySwithch().setCgAnalyzeGroupedStrategy(true);
		if (mCmd.hasOption("getAttributeStrategy"))
			MyConfig.getInstance().getMySwithch().setGetAttributeStrategy(true);
		if (mCmd.hasOption("setAttributeStrategy"))
			MyConfig.getInstance().getMySwithch().setSetAttributeStrategy(true);
		if (mCmd.hasOption("scenarioStack")) {
			MyConfig.getInstance().getMySwithch().setScenario_stack(true);
			if (MyConfig.getInstance().getMySwithch().isScenario_stack()) {
				MyConfig.getInstance().getMySwithch().setFunctionExpandAllSwitch(true);
			}
		}
	}

	private static void printHelp(String string) {
		System.out.println(string);
		HelpFormatter formatter = new HelpFormatter();
		System.out.println("Please check the help inforamtion");
		formatter.printHelp("java -jar ICCBot.jar [options]", getOptions());
		System.exit(0);
	}


}