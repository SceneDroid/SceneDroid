package android.goal.explorer.utils;

import android.goal.explorer.cmdline.GlobalConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.pmw.tinylog.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class FileUtil {
    // print the debug info to a text file
    public static void printFile(String fileName, String content){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true))) {
            bw.write(content);
            // no need to close it.
            //bw.close();
            //System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // run command line apk tool to decompile the apk
//    public static void decompile_apk(String apk_path) throws IOException {
//        ProcessBuilder pb = new ProcessBuilder("java", "-jar", AteConfig.APKTOOL_JAR,"d",apk_path,"-o", AteConfig.TEMP_FOLDER,"-f","-s");
//        Process p = pb.start();
//    }

    /**
     * Checks if the given file exists
     * @param filePath The path to the directory or file
     * @return True if the given path is valid
     */
    public static boolean validateFile(String filePath) {
        File f = new File(filePath);
        return (f.exists()&&f.isFile());
    }

    /**
     * Checks if the given directory exists
     * @param filePath The path to the directory or file
     * @return True if the given path is valid
     */
    public static boolean validatePath(String filePath) {
        File f = new File(filePath);
        return f.exists()&&f.isDirectory();
    }

    /**
     * Checks if the given APK exists
     * @param filePath The path to the APK file
     * @return True if the given APK is valid
     */
    public static boolean validateAPK(String filePath) {
        if (!validateFile(filePath))
            return false;
        return filePath.endsWith(".apk");
    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    /**
     * Loads the configuration parameters from file
     * @param configFilePath The path to the config file
     * @param config The config object
     */
    public static void loadConfigFile(String configFilePath, GlobalConfig config){
        // load properties from the config file
        try {
            PropertiesConfiguration configProp = new PropertiesConfiguration(configFilePath);

            // get the output path
            String outputPathFile = configProp.getString("outputPath");
            String outputPathConfig = config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir();
            // use the android sdk path in the config file if the user does not specifies a path for androidSdk
            if (outputPathConfig == null || outputPathConfig.isEmpty()){
                if (outputPathFile!=null)
                    config.getFlowdroidConfig().getAnalysisFileConfig().setOutputFile(outputPathFile);
                else {
                    Logger.error("*** ERROR: Output path needs to be specified either in config.properties or through command line options!");
                    System.exit(2);
                }
            } else {
                if (!outputPathFile.equals(outputPathConfig)) {
                    configProp.setProperty("outputPath", outputPathConfig);
                    configProp.save();
                }
            }

            // get the property values
            String androidSdkFile = configProp.getString("androidSdk");
            String androidSdkConfig = config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir();
            // use the android sdk path in the config file if the user does not specifies a path for androidSdk
            if (androidSdkConfig == null || androidSdkConfig.isEmpty()){
                if (androidSdkFile!=null)
                    config.getFlowdroidConfig().getAnalysisFileConfig().setAndroidPlatformDir(androidSdkFile);
                else {
                    Logger.error("*** ERROR: Android SDK path needs to be specified either in config.properties or through command line options!");
                    System.exit(2);
                }
            } else {
                if (!androidSdkFile.equals(androidSdkConfig)) {
                    configProp.setProperty("androidSdk", androidSdkConfig);
                    configProp.save();
                }
            }

            // api level
            String androidApiFile = configProp.getString("androidApi");
            int androidApiConfig = config.getTargetApi();
            // use the android sdk path in the config file if the user does not specifies a path for androidSdk
            if (androidApiFile==null) {
                if (androidApiConfig!=-1) {
                    configProp.setProperty("androidApi", androidApiConfig);
                    configProp.save();
                }
            } else if (androidApiConfig != Integer.parseInt(androidApiFile)) {
                    config.setTargetApi(Integer.parseInt(androidApiFile));
            }

        } catch (ConfigurationException ex){
            Logger.error(ex.getMessage());
        }
    }

    /**
     * Gets the key by value in a one-to-one map
     * @param map The map to iterate
     * @param value The value to look for
     * @param <T> Any type key
     * @param <E> Any type entry
     * @return The corresponding key
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
