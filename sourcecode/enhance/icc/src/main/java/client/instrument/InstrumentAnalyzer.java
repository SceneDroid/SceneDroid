package main.java.client.instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

//import jymbolic.android.resources.controls.ProcessManifest;
//import jymbolic.android.resources.xml.AXmlAttribute;
//import jymbolic.android.resources.xml.AXmlNode;
//import jymbolic.android.resources.xml.parser.AXmlHandler;
import main.java.Analyzer;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.SootUtils;
import main.java.analyze.utils.output.FileUtils;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlHandler;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.options.Options;

public class InstrumentAnalyzer extends Analyzer {
	private String appName;
	private String pkgName;
	private String appPath;
	private String instrumentedApkPath;
	private String instrumentedApkFolder;

	public InstrumentAnalyzer() {
		super();
		this.appName = appModel.getAppName();
		this.pkgName = appModel.getPackageName();
		this.appPath = appModel.getAppPath();
		this.instrumentedApkFolder = MyConfig.getInstance().getResultFolder() + appName + File.separator
				+ ConstantUtils.INSTRUFOLDER;
		this.instrumentedApkPath = instrumentedApkFolder + appName + ".apk";
	}

	@Override
	public void analyze() {
		System.out.println("Start Instrument...");

		try {
			FileUtils.createFolder(instrumentedApkFolder);
			configSoot();
			// setMiniSdk();
			instrument();// soot requires manifestManager.getMinSdkVersion()>=22

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("End Instrument...");
	}

	/**
	 * config soot before analyze
	 * 
	 * @throws IOException
	 */
	public void configSoot() throws IOException {
		soot.G.reset();
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_dir(instrumentedApkFolder);
		Options.v().set_debug(true);
		Options.v().set_validate(true);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_process_multiple_dex(true);

		List<String> process_dirs = new ArrayList<>();
		process_dirs.add(appPath);

		Options.v().set_process_dir(process_dirs);
		Options.v().set_android_jars(MyConfig.getInstance().getAndroidJar());
		Options.v().set_force_overwrite(true);

	}

	/**
	 * instrument pay attention to the location of the inserted stmt. should not
	 * exclude any package, or else the app will crash
	 */
	public void instrument() {
		Scene.v().loadNecessaryClasses();
		PackManager.v().getPack("jtp").add(new Transform("jtp.androcov", new BodyTransformer() {
			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
				final PatchingChain<Unit> units = b.getUnits();
				// important to use snapshotIterator here
				String methodSig = b.getMethod().getSignature();
				String className = SootUtils.getNameofClass(b.getMethod().getDeclaringClass());
				String classType = SootUtils.getTypeofClassName(b.getMethod().getDeclaringClass());
				if (classType.equals("other"))
					return;
				if (!MyConfig.getInstance().getMySwithch().allowLibCodeSwitch()) {
					if (!methodSig.contains(pkgName))
						return;
				}
				// String methodName = b.getMethod().getName();
				// if (!methodName.startsWith(ConstantUtils.ENTRYIDON))
				// return;
				if (b.getMethod().getName().equals("<init>") || b.getMethod().getName().equals("<clinit>"))
					return;
				String tag = "ICCTAG";

				// perform instrumentation here
				for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
					final Unit u = iter.next();
					// insert before the first non-identity statement
					if (u instanceof JIdentityStmt)
						continue;
					if (u instanceof JInvokeStmt) {
						InvokeStmt invokeStmt = (InvokeStmt) u;
						if (invokeStmt.getInvokeExpr() instanceof JSpecialInvokeExpr) {
							JSpecialInvokeExpr expr = (JSpecialInvokeExpr) invokeStmt.getInvokeExpr();
							if (expr.getBase().toString().equals("$r0"))
								continue;
						}
						if (invokeStmt.getInvokeExpr() instanceof JVirtualInvokeExpr) {
							JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeStmt.getInvokeExpr();
							if (expr.getBase().toString().equals("$r0"))
								continue;
						}
					}

					SootMethod toCall = Scene.v().getSootClass("android.util.Log")
							.getMethod("int i(java.lang.String,java.lang.String)");
					InvokeStmt logStatement;
					logStatement = Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(toCall.makeRef(), StringConstant.v(tag),
									StringConstant.v(classType + "    " + className + "    " + methodSig)));
					units.insertBefore(logStatement, u);
					Global.v().addInstrumentList(logStatement);
					break;

				}
				try {

					b.validate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
		PackManager.v().runPacks();
		PackManager.v().writeOutput();
	}

	/**
	 * set the minSdkVersion>22
	 * 
	 */
	private void setMiniSdk() {
		ProcessManifest manifestManager = null;
		try {
			manifestManager = new ProcessManifest(this.appPath);
			AXmlAttribute<String> attr = new AXmlAttribute<String>("minSdkVersion", "27", AXmlHandler.ANDROID_NAMESPACE);
			List<AXmlNode> nodes = manifestManager.getAXml().getNodesWithTag("uses-sdk");
			nodes.get(0).addAttribute(attr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}finally{
			manifestManager.close();
		}
	}

	/**
	 * modify manifest file export all EAs into IAs
	 */
	public void export() {
		ProcessManifest manifestManager = null;
		try {
			File instrumentedApk = new File(instrumentedApkPath);
			if (!instrumentedApk.exists())
				FileUtils.copyFile(appPath, instrumentedApkPath);
			manifestManager = new ProcessManifest(instrumentedApkPath);
			List<AXmlNode> acts = manifestManager.getActivities();
			for (AXmlNode actNode : acts) {
				// get the attributes of the activity element
				AXmlAttribute<String> attr = new AXmlAttribute<String>("exported", "true",
						AXmlHandler.ANDROID_NAMESPACE);
				actNode.addAttribute(attr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		try {
			File manifestFile = null;
			manifestFile = File.createTempFile("AndroidManifest.xml", null);
			FileOutputStream fos = new FileOutputStream(manifestFile.getPath());
			byte[] output = manifestManager.getOutput();
			fos.write(output);
			fos.close();

			ArrayList<File> files = new ArrayList<File>();
			files.add(manifestFile);
			HashMap<String, String> paths = new HashMap<String, String>();
			paths.put(manifestFile.getAbsolutePath(), "AndroidManifest.xml");
			// add the modified AndroidManifest into the original APK

			manifestManager.getApk().addFilesToApk(files, paths);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			manifestManager.close();
		}
	}
}
