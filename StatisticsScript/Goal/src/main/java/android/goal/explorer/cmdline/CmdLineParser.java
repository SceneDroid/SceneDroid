package android.goal.explorer.cmdline;

//import android.goal.explorer.utils.FileUtil;
import android.goal.explorer.utils.FileUtil;
import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import st.cs.uni.saarland.de.helpClasses.Helper;
import st.cs.uni.saarland.de.reachabilityAnalysis.RAHelper;
import st.cs.uni.saarland.de.testApps.Settings;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class CmdLineParser {
    //TODO: Add a model, build (for stg construction) vs mark for target marking
    //TODO: Add --target target --target_type type for the options also (not mandatory)

    private final Options options = new Options();

    // Files
    private static final String OPTION_INPUT_APK_PATH = "i";
    private static final String OPTION_OUTPUT_PATH = "o";
    private static final String OPTION_CONFIG_FILE_PATH = "p";
    private static final String OPTION_STG_PATH = "stg";
    private static final String OPTION_PRE_PATH = "pre";
    private static final String OPTION_ICC_MODEL = "m";
    private static final String OPTION_IC3_RESULTS_FOLDER = "r";

    // Analysis Config
    private static final String OPTION_MAX_CALLBACK = "c";
    private static final String OPTION_MAX_TIMEOUT = "t";
    private static final String OPTION_NUM_THREAD = "n";
    private static final String OPTION_CONTEXT_POINTTO = "cp";
    private static final String OPTION_TARGET = "target";

    // Android
    private static final String OPTION_ANDROID_SDK_PATH = "s";
    private static final String OPTION_ANDROID_API_LEVEL = "l";

    // Program Config
    private static final String OPTION_DEBUG = "d";
    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERSION = "v";

    private CmdLineParser(){
        setupCmdOptions();
    }

    /**
     *  setup the command line parser
     */
    private void setupCmdOptions() {
        // command line options
        Option input = Option.builder(OPTION_INPUT_APK_PATH).required(true).longOpt("input").hasArg(true).desc("input apk path (required)").build();
        Option stg = Option.builder(OPTION_STG_PATH).required(false).longOpt("stg").hasArg(true).desc("input stg path (required for marking)").build();
        Option pre = Option.builder(OPTION_PRE_PATH).required(false).longOpt("precomputed").hasArg(true).desc("precomputed model folder").build();
        Option target = Option.builder(OPTION_TARGET).longOpt("target").hasArg(true).desc("target or list of targets").build();
        Option output = Option.builder(OPTION_OUTPUT_PATH).required(false).longOpt("output").hasArg(true).desc("output directory (default to \"sootOutput\")").build();
        Option config = Option.builder(OPTION_CONFIG_FILE_PATH).required(false).longOpt("config").hasArg(true).desc("the configuration file (optional)").build();
        Option sdkPath = Option.builder(OPTION_ANDROID_SDK_PATH).required(false).longOpt("sdk").hasArg(true).desc("path to android sdk (default value can be set in config file)").build();
        Option apiLevel = Option.builder(OPTION_ANDROID_API_LEVEL).required(false).type(Number.class).longOpt("api").hasArg(true).desc("api level (default to 23)").build();
        Option contextPointTo = new Option(OPTION_CONTEXT_POINTTO, "contextPt", false, "enable context-sensitive point-to analysis (default disabled)");
        Option iccModel = Option.builder(OPTION_ICC_MODEL).required(false).longOpt("model").hasArg(true).desc("icc model (default to match the package name").build();
        Option ic3ResultsFolder = Option.builder(OPTION_IC3_RESULTS_FOLDER).required(false).longOpt("icc_folder").hasArg(true).desc("icc models directory (default to \"iccbot\"").build();
        Option maxCallback = Option.builder(OPTION_MAX_CALLBACK).required(false).type(Number.class).hasArg(true).desc("the maximum number of callbacks modeled for each component (default to 100)").build();
        Option timeOut = Option.builder(OPTION_MAX_TIMEOUT).required(false).hasArg(true).desc("maximum timeout analyzing each component in seconds (default: 120)").build();
        Option numThread = Option.builder(OPTION_NUM_THREAD).required(false).hasArg(true).desc("the number of threads used for multi-threading analysis. Adjust to the number of CPU cores for better performance (default: 16)").build();
        Option debug = new Option(OPTION_DEBUG, "debug", false, "debug mode (default disabled)");
        Option help = new Option(OPTION_HELP, "help", false, "print the help message");
        Option version = new Option( OPTION_VERSION,"version", false,"print version info" );

        // add the options
        options.addOption(input).addOption(output).addOption(config).addOption(sdkPath).addOption(apiLevel).addOption(iccModel).addOption(stg).addOption(target).addOption(pre);
        options.addOption(timeOut).addOption(maxCallback).addOption(numThread).addOption(contextPointTo);
        options.addOption(debug).addOption(help).addOption(version);
    }

    /**
     * parse the command line arguments
     * @param args The command line arguments
     * @return the configuration
     */
    public static GlobalConfig parse(String[] args) {
        CmdLineParser mainClass = new CmdLineParser();
        return mainClass.run(args);
    }

    /**
     * Parse the command line options
     * @param cmd The command line arguments
     * @param config The configuration object ({@link GlobalConfig})
     */
    private void parseCommandLineArgs(CommandLine cmd, GlobalConfig config) throws Exception {
        // Set apk path and output path
        if (cmd.hasOption(OPTION_INPUT_APK_PATH) || cmd.hasOption("input")) {
            String apkFile = cmd.getOptionValue(OPTION_INPUT_APK_PATH);
            if (apkFile != null && !apkFile.isEmpty())
                config.getFlowdroidConfig().getAnalysisFileConfig().setTargetAPKFile(apkFile);
        }
        if (cmd.hasOption(OPTION_OUTPUT_PATH) || cmd.hasOption("output")) {
            String outputPath = cmd.getOptionValue(OPTION_OUTPUT_PATH);
            if (outputPath != null && !outputPath.isEmpty())
                config.getFlowdroidConfig().getAnalysisFileConfig().setOutputFile(outputPath);
        }
        if(cmd.hasOption(OPTION_STG_PATH) || cmd.hasOption("stg")){
            String stgFile = cmd.getOptionValue(OPTION_STG_PATH);
            if (stgFile != null && !stgFile.isEmpty())
                config.setPrecomputedSTG(stgFile);
        }
        if(cmd.hasOption(OPTION_TARGET) || cmd.hasOption("target")){
            String[] info = parseTupleOption(cmd, OPTION_TARGET);
            if(info != null){
                config.setTargetType(info[0]);
                Set<String> targets = parseListOption(info[1]);
                if(targets != null && !targets.isEmpty()) {
                    config.setTargets(targets);
                }
            }
        }
        if(cmd.hasOption(OPTION_PRE_PATH) || cmd.hasOption("precomputed")){
            String precomputedModel = cmd.getOptionValue(OPTION_PRE_PATH);
            if(precomputedModel != null && !precomputedModel.isEmpty())
                config.setPrecomputedModelFolder(precomputedModel);
        }
        if (cmd.hasOption(OPTION_ICC_MODEL) || cmd.hasOption("model")) {
            String iccModelPath = cmd.getOptionValue(OPTION_ICC_MODEL);
            if (iccModelPath != null && !iccModelPath.isEmpty())
                config.getFlowdroidConfig().getIccConfig().setIccModel(iccModelPath);
        }

        if (cmd.hasOption(OPTION_IC3_RESULTS_FOLDER) || cmd.hasOption("icc_folder")) {
            String ic3ResultsFolder = cmd.getOptionValue(OPTION_IC3_RESULTS_FOLDER);
            if (ic3ResultsFolder != null && !ic3ResultsFolder.isEmpty())
                config.setIc3ResultsFolder(ic3ResultsFolder);
        }

        // Setting android SDK path and target API level
        if (cmd.hasOption(OPTION_ANDROID_SDK_PATH) || cmd.hasOption("sdk")) {
            String adkPath = cmd.getOptionValue(OPTION_ANDROID_SDK_PATH);
            if (adkPath != null && !adkPath.isEmpty())
                config.getFlowdroidConfig().getAnalysisFileConfig().setAndroidPlatformDir(adkPath);
        }
        if (cmd.hasOption(OPTION_ANDROID_API_LEVEL) || cmd.hasOption("api")) {
            Integer targetApi = parseIntOption(cmd, OPTION_ANDROID_API_LEVEL);
            if (targetApi != null)
                config.setTargetApi(targetApi);
        }

        // callback analysis setting
        if (cmd.hasOption(OPTION_CONTEXT_POINTTO) || cmd.hasOption("contextPt"))
            config.setPointToType(GlobalConfig.PointToType.CONTEXT);
        else
            config.setPointToType(GlobalConfig.PointToType.DEFAULT);

        if (cmd.hasOption(OPTION_MAX_CALLBACK)) {
            Integer maxCallback = parseIntOption(cmd, OPTION_MAX_CALLBACK);
            config.getFlowdroidConfig().getCallbackConfig().setMaxCallbacksPerComponent(maxCallback);
        }
        if (cmd.hasOption(OPTION_MAX_TIMEOUT)) {
            Integer maxTimeout = parseIntOption(cmd, OPTION_MAX_TIMEOUT);
            config.setTimeout(maxTimeout);
        }
        if (cmd.hasOption(OPTION_NUM_THREAD)) {
            Integer numThread = parseIntOption(cmd, OPTION_NUM_THREAD);
            config.setNumThread(numThread);
        }

        // log level setting (debug/info/production)
        String level;
        if (cmd.hasOption(OPTION_DEBUG) || cmd.hasOption("debug")) {
            // Change the log level
            Configurator.currentConfig().formatPattern("[{level}] {class_name}.{method}(): {message}").level(Level.DEBUG).activate();
            level = org.apache.log4j.Level.DEBUG.toString();
        } else {
            Configurator.currentConfig().formatPattern("{level}: {message}").activate();
            level = org.apache.log4j.Level.OFF.toString();
        }
        configureLog4j(level);

        // load the config file
        if (cmd.hasOption(OPTION_CONFIG_FILE_PATH) || cmd.hasOption("config")) {
            String configPath = cmd.getOptionValue(OPTION_CONFIG_FILE_PATH);
            if (configPath != null && !configPath.isEmpty())
                FileUtil.loadConfigFile(configPath, config);
        }

        // validate command line options
        if (!FileUtil.validateAPK(config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile())){
            Logger.error("Input apk path does not exist: {}",
                    config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile());
            System.exit(1);
        }
        if (!FileUtil.validatePath(config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir())){
            Logger.error("Android SDK path does not exist or it is not a directory: {}",
                    config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir());
            System.exit(1);
        }



        if (!FileUtil.validatePath(config.getFlowdroidConfig().getAnalysisFileConfig().getOutputFile())){
            File outputFile = new File(config.getFlowdroidConfig().getAnalysisFileConfig().getOutputFile());
            if (!outputFile.mkdirs()) {
                Logger.error("Failed to create the output path: {}",
                        config.getFlowdroidConfig().getAnalysisFileConfig().getOutputFile());
                System.exit(1);
            }
        }

        // set the android JAR
        Integer targetApiLevel = config.getTargetApi();
        String sdkPath = config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir();
        if (targetApiLevel != -1) {
            config.getFlowdroidConfig().getAnalysisFileConfig().
                    setAndroidPlatformDir(sdkPath + "/platforms/android-" + targetApiLevel + "/android.jar");
        } else {
            config.getFlowdroidConfig().getAnalysisFileConfig().
                    setAndroidPlatformDir(sdkPath + "/platforms/");
        }
    }


    /**
     * Configure log4j level
     * @param level The level of logging
     */
    private void configureLog4j(String level) {
        Properties props = new Properties();
        props.put("log4j.rootLogger", level+", stdlog");
        props.put("log4j.appender.stdlog", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.stdlog.target", "System.out");
        props.put("log4j.appender.stdlog.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.stdlog.layout.ConversionPattern",
                "%d{HH:mm:ss} %-5p %-25c{1} :: %m%n");
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    /**
     * Parse the command line arguments.
     *
     * @param args the command line arguments passed from parse().
     * @return the configuration parsed from command line arguments
     */
    private GlobalConfig run(String[] args) {
        // Initial check for the number of arguments
        final HelpFormatter formatter = new HelpFormatter();
        if (args.length == 0) {
            formatter.printHelp("ge [OPTIONS]", options, true);
            System.exit(1);
        }

        // Use commons lib to parse the params
        CommandLineParser parser = new DefaultParser();
        GlobalConfig config = new GlobalConfig();
        try {
            CommandLine cmd = parser.parse(options, args);

            // display the help message if option is specified
            if (cmd.hasOption(OPTION_HELP) || cmd.hasOption("help")) {
                formatter.printHelp("ge [OPTIONS]", options, true);
                System.exit(1);
            }

            // display version info and exit
            if (cmd.hasOption(OPTION_VERSION) || cmd.hasOption("version")) {
                System.out.println("ge " + getClass().getPackage().getImplementationVersion());
                System.exit(1);
            }

            // the actual param parsing
            parseCommandLineArgs(cmd, config);

        } catch (ParseException e) {
            formatter.printHelp("ge [OPTIONS]", options, true);
            System.exit(1);
        } catch (Exception e) {
            Logger.error("GoalExplorer has failed. Error message: {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return config;
    }

    /**
     * parse the integer option
     * @param cmd The command line object
     * @param option The option to parse
     * @return Integer value of the option
     */
    private Integer parseIntOption(CommandLine cmd, String option){
        String str = cmd.getOptionValue(option);
        if (str == null || str.isEmpty())
            return null;
        else
            return Integer.parseInt(str);
    }

    private String[] parseTupleOption(CommandLine cmd, String option) {
        String str = cmd.getOptionValue(option);
        if(str == null || str.isEmpty())
            return null;
        //TODO if it's a path, then load the content
        String[] info = str.split(":", 2);
        if(info.length == 2 && (info[0].equals("act") || info[0].equals("api") || info[0].equals("stmt")) && !info[1].isEmpty())
            return info;
        return null;
    }
    private Set<String> parseListOption(String option) {
        if(option == null || option.isEmpty())
            return null;
        return Arrays.stream(option.split(";")).collect(Collectors.toSet());
    }

    /**
     * Process arguments for
     */
    public static Settings parseArgForBackstage(GlobalConfig config) {
        final IDefaultProvider DEFAULT_PROVIDER = optionName -> ("-rTimeoutUnit".equals(optionName) ||
                "-tTimeoutUnit".equals(optionName)) ? "MINUTES" : null;

        String[] args = new String[20];
        args[0] = "-apk";
        args[1] = config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile();
        args[2] = "-androidJar";
        args[3] = config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir();
        args[4] = "-apkToolOutput";
        args[5] = config.getFlowdroidConfig().getAnalysisFileConfig().getOutputFile();
        args[6] = "-rAnalysis";
        args[7] = "-uiTimeoutValue";
        args[8] = "120";
        args[9] = "-uiTimeoutUnit";
        args[10] = "SECONDS";
        args[11] = "-rTimeoutValue";
        args[12] = "120";
        args[13] = "-rTimeoutUnit";
        args[14] = "SECONDS";
        args[15] = "-maxDepthMethodLevel";
        args[16] = "15";
        args[17] = "-numThreads";
        args[18] = "24";
        args[19] = "-rLimitByPackageName";

        Settings settings = new Settings();
        JCommander jc = new JCommander(settings);
        jc.setDefaultProvider(DEFAULT_PROVIDER);

        try {
            jc.parse(args);
            Helper.setApkName(settings.apkPath);
            Helper.setApkPath(settings.apkPath);
            Helper.setLogsDir(settings.logsDir);
            Helper.setResultsDir(settings.resultsDir);
            Helper.setLOC(settings.maxLocInMethod);
            RAHelper.numThreads = settings.numThreads;
            Helper.initializeManifestInfo(settings.apkPath);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            jc.usage();
            System.exit(1);
        }

        return settings;
    }
}
