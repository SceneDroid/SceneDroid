package main.java.analyze.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.java.MyConfig;

public class AndroidUtils {

	/**
	 * sign apk on apkPath
	 * 
	 * @param apkPath
	 */
	public static void signAPK(String apkPath) {
		Runtime r = Runtime.getRuntime();
		String keystoreUrl = "template" + File.separator + "debug.keystore";

		String signCmd = String.format("jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -storepass android "
				+ "-keystore %s %s androiddebugkey", keystoreUrl, apkPath);
		try {
			Process p = r.exec(signCmd);

			ReadStream s1 = new ReadStream("stdin", p.getInputStream());
			ReadStream s2 = new ReadStream("stderr", p.getErrorStream());
			s1.start();
			s2.start();

			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finish Sign Apk.");
	}

	/**
	 * create android project projectName with pkg name on path param_result_dir
	 * 
	 * @param oldpkg
	 * @param param_result_dir
	 * @param projectName
	 */
	public static void creatProject(String pkg, String param_result_dir, String projectName) {
		ExecuteUtils.exec("android create project --name " + projectName + " --target "
				+ MyConfig.getInstance().getAndroidVersion() + " --path " + param_result_dir + " --package "
				+ ConstantUtils.APKFLAGPREFIX + pkg + ConstantUtils.APKFLAGSUFFIX + " --activity "
				+ ConstantUtils.ACTIVITY + "_");
		System.out.println("android create project --name " + projectName + " --target "
				+ MyConfig.getInstance().getAndroidVersion() + " --path " + param_result_dir + " --package "
				+ ConstantUtils.APKFLAGPREFIX + pkg + ConstantUtils.APKFLAGSUFFIX + " --activity "
				+ ConstantUtils.ACTIVITY + "_");
	}

	/**
	 * buildApp on path projectPath
	 * 
	 * @param projectPath
	 */
	public static void buildApp(String projectPath) {
		System.out.println("ant debug -buildfile " + projectPath + "/build.xml");
		ExecuteUtils.exec("ant debug -buildfile " + projectPath + "/build.xml");
	}

}

/**
 * signAPK IO
 * 
 * @author 79940
 *
 */
class ReadStream implements Runnable {
	String name;
	InputStream is;
	Thread thread;

	public ReadStream(String name, InputStream is) {
		this.name = name;
		this.is = is;
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				System.out.println("[" + name + "] " + s);
			}
			is.close();
		} catch (Exception ex) {
			System.out.println("Problem reading stream " + name + "... :" + ex);
			ex.printStackTrace();
		}
	}
}
