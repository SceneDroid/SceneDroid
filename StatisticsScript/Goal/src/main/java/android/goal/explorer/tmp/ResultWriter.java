package android.goal.explorer.tmp;

import org.pmw.tinylog.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ResultWriter {

    private static FileWriter logFileWriter;
    private static BufferedWriter bufferedWriter;

    public static void initialize(String logDirString, String apkNameString) {
        logFileWriter = null;
        logFileWriter = createLogDirIfNotExist(logDirString, apkNameString);
        bufferedWriter = new BufferedWriter(logFileWriter);
    }

    /**
     * Creates the logging directory if it does not exist
     * @param logDir The logging direct
     * @param apkName The name of the apk
     */
    private static FileWriter createLogDirIfNotExist(String logDir, String apkName) {
        File dir = new File(logDir);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (success)
                Logger.info("[{}] Successfully created the logging directory!");
            else
                Logger.warn("[WARN] Failed to create the loggin directory!");
        }

        File file = new File(dir + File.separator + apkName + "_log.txt");
        try{
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            return fileWriter;
        } catch (IOException e) {
            Logger.error("[ERROR] Failed to create new log file: {}", e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    /**
     * Append the string to the end of the file
     * @param message The string to append to the file
     */
    public static void writeToFile(String message) {

        try {
            bufferedWriter.append(message).append("\n");
        } catch (IOException e) {
            Logger.error("[ERROR] Failed to write to log file: {}", e.getMessage());
        }
    }

    /**
     * done with result writing, flush and close the file
     */
    public static void done() {
        try {
            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedWriter = null;
            logFileWriter.close();
            logFileWriter = null;
        } catch (IOException e) {
            Logger.error("[ERROR] Failed to write to log file: {}", e.getMessage());
        }
    }

    /**
     * Append a string to a file
     * @param outputPath The output file path
     * @param packageName The package name which is placed at the beginning
     * @param fileName The filename
     * @param toAppend The string to append
     * @throws IOException
     */
    public static void appendStringToResultFile(String outputPath, String packageName,
                                                String fileName, String toAppend) {
        try {
            FileWriter fw = new FileWriter(outputPath + "/" + fileName, true);
            BufferedWriter resultWriter = new BufferedWriter(fw);

            resultWriter.write(packageName + " : " + toAppend);
            resultWriter.newLine();
            resultWriter.close();
        } catch (IOException e) {
            Logger.error("Cannot write to file: {}", e.getMessage());
        }
    }

    /**
     * write a list of string to file
     * @param stringList The list of string
     * @param packageName The packagename
     * @param filename The filename
     * @param outputPath The output path
     */
    public static void writeStringListToFile(List<String> stringList, String packageName,
                                             String filename, String outputPath) {
        try {
            FileWriter fw = new FileWriter(outputPath + "/" + filename, true);
            BufferedWriter resultWriter = new BufferedWriter(fw);
            resultWriter.write(packageName + ": ");
            for (String stringItem : stringList) {
                resultWriter.write(stringItem + ", ");
            }
            resultWriter.newLine();
            resultWriter.close();
        } catch (IOException e) {
            Logger.error("Cannot write to file: {}", e.getMessage());
        }
    }
}
