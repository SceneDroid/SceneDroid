package main.java.client.manifest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import main.java.Global;
import main.java.analyze.utils.output.FileUtils;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class ManifestClientOutput {
	/**
	 * writeManifest
	 * 
	 * @param dir
	 * @param file
	 * @param AppModel
	 *            .getInstance()
	 */
	public static void writeManifest(String dir, String file) {
		FileUtils.createFolder(dir);
		FileUtils.createFile(dir + file);
		File f = new File(dir + file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			writer.write(Global.v().getAppModel().getManifestString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
