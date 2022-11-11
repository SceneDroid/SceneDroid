package main.java.client.cg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * output analyze result
 * 
 * @author 79940
 *
 */
public class CgClientOutput {
	/**
	 * write call graph
	 * 
	 * @param dir
	 * @param file
	 * @param cg
	 * @param AppModel
	 *            .getInstance()
	 */
	public static void writeCG(String dir, String file, CallGraph cg) {
		FileUtils.createFolder(dir);
		File f = new File(dir + file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			if (cg == null)
				return;
			Iterator<Edge> it = cg.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				if (SootUtils.hasSootActiveBody(edge.getTgt().method())) {
					String caller = edge.getSrc().method().getSignature();
					String callee = edge.getTgt().method().getSignature();
					writer.write(caller + " -> " + callee + "\n");
				}
			}
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


	public static void writeCGToString(String dir, String file, CallGraph cg) {
		FileUtils.createFolder(dir);
		File f = new File(dir + file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			if (cg == null)
				return;
			Iterator<Edge> it = cg.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				if (SootUtils.hasSootActiveBody(edge.getTgt().method())) {
					writer.write(edge.toString() + "\n");
				}
			}
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
	
	/**
	 * write the methods in cg according to the topology order
	 * 
	 * @param dir
	 * @param file
	 * @param topoMethodQueue
	 * @param AppModel
	 *            .getInstance()
	 */
	public static void writeTopoMethodFile(String dir, String file, List<SootMethod> topoMethodQueue) {
		File f = new File(dir + file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			for (SootMethod m : topoMethodQueue) {
				writer.write(m.getSignature() + "\n");
			}
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
