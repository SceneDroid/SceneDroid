package android.goal.explorer.tmp;

import android.goal.explorer.cmdline.GlobalConfig;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.SetupApplication;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.List;

public class JimpleAnalyzer {

    // Configuration
    private GlobalConfig config;


    // SetupApplication
    private SetupApplication setupApplication;

    // app
    private String packageName;

    /**
     * Default constructor
     * @param config The configuration file
     */
    public JimpleAnalyzer(GlobalConfig config) {
        // Setup analysis config
        this.config = config;

        // Setup application
        this.setupApplication = new SetupApplication(config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir(),
                config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile());
    }

    /**
     * Triggers the callgraph construction in FlowDroid
     */
    private void constructCallgraph() {
        setupApplication.constructCallgraph();
    }

    /**
     * count the number of methods in the app
     */
    public void countMethods(String logDir) {
        // initialize soot and construct the callgraph
        constructCallgraph();

        packageName = setupApplication.getManifest().getPackageName();

        // initialize result writer
        if (logDir!=null)
            ResultWriter.initialize(logDir, "test");

        int numOfMethods = 0;

        // search for the invocation
        for(SootClass sootClass : Scene.v().getApplicationClasses()) {
            numOfMethods = sootClass.getMethods().size() + numOfMethods;
        }

        ResultWriter.writeToFile(packageName + ", " + numOfMethods);

        ResultWriter.done();
    }

    /**
     * search for invocation in the app
     * @param invMethodSubsigs the subsignatures of the invoded method name of the invocation
     * @param logDir the result directory
     */
    public void searchForInv(List<String> invMethodSubsigs, String logDir) {
        // initialize soot and construct the callgraph
        constructCallgraph();

        packageName = setupApplication.getManifest().getPackageName();

        // initialize result writer
        if (logDir!=null)
        ResultWriter.initialize(logDir, "test");

        // search for the invocation
        for(SootClass sootClass : Scene.v().getClasses()) {
//            if (sootClass.getName().equals("net.zedge.android.player.BufferTask")) {
//                List<SootMethod> methods = sootClass.getMethods();
//                Logger.debug("Here");
//            }
//            if (sootClass.getName().equals("com.inmobi.ads.cache.AssetStore")) {
//                List<SootMethod> methods = sootClass.getMethods();
//                Logger.debug("Here");
//            }
            searchForInvInClass(invMethodSubsigs, sootClass);
        }

        ResultWriter.done();
    }

    /**
     * search for invocation in the app
     * @param annotationTypes the type of the annotations to check
     * @param logDir the result directory
     */
    public void searchForAnnotation(List<String> annotationTypes, String logDir) {
        // initialize soot and construct the callgraph
        constructCallgraph();

        packageName = setupApplication.getManifest().getPackageName();

        // initialize result writer
        if (logDir!=null)
            ResultWriter.initialize(logDir, "test");

        // search for the invocation
        for(SootClass sootClass : Scene.v().getApplicationClasses()) {
            searchForAnnotationInClass(annotationTypes, sootClass);
        }

        ResultWriter.done();
    }

    /**
     * Search for the invocation
     * @param annotationTypes the type of annotation to check
     * @param sootClass the soot class to search
     */
    private void searchForAnnotationInClass(List<String> annotationTypes, SootClass sootClass) {
        for (SootMethod sm : sootClass.getMethods()) {

            VisibilityAnnotationTag tag = (VisibilityAnnotationTag) sm.getTag("VisibilityAnnotationTag");
            if (tag != null) {
                for (AnnotationTag annotation : tag.getAnnotations()) {
                    for (String targetType : annotationTypes) {
                        if (annotation.getType().equals(targetType)) {
                            ResultWriter.writeToFile(packageName + ", " + sootClass.getName() + ", " +
                                    sm.getName() + ", " + annotation.getType());
                        }
                    }
                }
            }
        }
    }

    /**
     * Search for the invocation
     * @param invSubsigs the subsignatures of the inv method
     * @param sootClass the soot class to search
     */
    private void searchForInvInClass(List<String> invSubsigs, SootClass sootClass) {
        for (SootMethod sm : sootClass.getMethods()) {
            if (!sm.isConcrete())
                continue;
//            if (!sm.hasActiveBody())
//                continue;
            Body b = sm.retrieveActiveBody();
            if (b == null)
                continue;

            if (sm.getName().equals("com.facebook.login.widget.LoginButton")) {
                ResultWriter.writeToFile(packageName + ", " + sootClass.getName() + ", " +
                        sm.getName() + ", ");
            }

            for (Unit u : b.getUnits()) {
                if (u instanceof Stmt) {
                    Stmt stmt = (Stmt) u;
                    if (stmt.containsInvokeExpr()) {
                        InvokeExpr inv = stmt.getInvokeExpr();


                        if (inv.getMethodRef().getDeclaringClass().getName().equals("com.facebook.login.LoginManager")) {
                            ResultWriter.writeToFile(packageName + ", " + sootClass.getName() + ", " +
                                    sm.getName() + ", " + inv.toString());
                        }

                        for (String invMethodName : invSubsigs) {
                            if (inv.getMethodRef().getSignature().equals(invMethodName)) {
                                ResultWriter.writeToFile(packageName + ", " + sootClass.getName() + ", " +
                                        sm.getName() + ", " + inv.toString());
                            }

//                            if (inv.getMethodRef().getSubSignature().getString().equals(invMethodName)) {
//                                ResultWriter.writeToFile(packageName + ", " + sootClass.getName() + ", " +
//                                        sm.getName() + ", " + inv.toString());
//                            }
                        }
                    }
                }
            }
        }
    }
}
