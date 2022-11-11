package main.java.analyze.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteUtils {

	/**
	 * execute commandStr (any os)
	 * 
	 * @param commandStr
	 * @param timeout 
	 * @return
	 */
	public static Object exec(String commandStr) {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("win")) {
			exeCmd(commandStr);
		} else {
			exeShell(commandStr);
		}
		return null;
	}
	
	public static Object exec(String commandStr, int timeout) {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("win")) {
			exeCmd(commandStr);
		} else {
			exeShell("timeout " +timeout+"s " +commandStr);
		}
		return null;
	}

	/**
	 * execute commandStr (linux)
	 * 
	 * @param commandStr
	 * @return
	 */
	public static String exeShell(String commandStr) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String[] cmdA = { "/bin/sh", "-c", commandStr };
			Process process = Runtime.getRuntime().exec(cmdA);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println("exeShell" + line);
				sb.append(line).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * execute commandStr (windows)
	 * 
	 * @param commandStr
	 * @return
	 */
	public static String exeCmd(String commandStr) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			commandStr = "cmd /c " + commandStr;
			Process p = Runtime.getRuntime().exec(commandStr);
			// p.waitFor();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("java.io.FileNotFoundException"))
					System.out.println("exeShell " + line);
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}
