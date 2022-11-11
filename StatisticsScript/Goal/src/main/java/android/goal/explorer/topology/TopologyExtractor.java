package android.goal.explorer.topology;

import android.goal.explorer.data.android.AndroidClass;
import android.goal.explorer.data.android.constants.MethodConstants;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.AbstractComponent;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.BroadcastReceiver;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.component.Service;
import android.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.memory.IMemoryBoundedSolver;
import soot.jimple.infoflow.memory.ISolverTerminationReason;
import soot.jimple.infoflow.util.SystemClassHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.goal.explorer.utils.SootUtils.*;

public class TopologyExtractor implements IMemoryBoundedSolver {

    private static final String TAG = "Topology";
    private static Logger logger = LoggerFactory.getLogger(TAG);

    private App app;

    private final int timeout;
    private final int numThread;

    private Set<IMemoryBoundedSolverStatusNotification> notificationListeners = new HashSet<>();
    private ISolverTerminationReason isKilled = null;

    // UI elements
//    private final Set<DialogEntity> dialogs = Collections.synchronizedSet(new HashSet<>());
//    private final Map<Integer, LayoutEntity> layouts = Collections.synchronizedMap(new HashMap<>());

    public TopologyExtractor(App app, int timeout, int numThread) {

        this.app = app;

        this.timeout = timeout;
        this.numThread = numThread;
    }

    /**
     * Collects the lifecycle methods
     */
    public void extractTopopology() {
        // multi-thread analysis -> each thread analyze a component
        ExecutorService classExecutor = Executors.newFixedThreadPool(numThread);
        Set<Future<Void>> classTasks = new HashSet<>();

        logger.debug(String.format("Submitting %d activity analysis tasks", app.getActivities().size()));
        for (Activity activity : app.getActivities()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(activity);
                return null;
            }));
        }

        logger.debug(String.format("Submitting %d service analysis tasks", app.getServices().size()));
        for (Service service : app.getServices()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(service);
                return null;
            }));
        }

        logger.debug(String.format("Submitting %d broadcast receiver analysis tasks", app.getBroadcastReceivers().size()));
        for (BroadcastReceiver receiver : app.getBroadcastReceivers()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(receiver);
                return null;
            }));
        }

        // Execute the task
        for (Future<Void> classTask : classTasks) {
            try {
                classTask.get(timeout, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                logger.warn("[{}] Interrupted analyzer from a parent thread", TAG);
                classTask.cancel(true);
            } catch (TimeoutException e) {
                logger.warn("[{}] Timeout for analysis task: {}", TAG, classTask);
                classTask.cancel(true);
            } catch (Exception e) {
                logger.error("[{}] Unknown error happened: {}", TAG, e.getMessage());
                e.printStackTrace();
                classTask.cancel(true);
            }
        }
        logger.debug("Finished all analysis tasks");
        classExecutor.shutdown();
    }

    /**
     * Submit the analysis as a task for multi-thread
     * @param comp The activity class to analyze
     */
    private void submitTopologyAnalysisTask(AbstractComponent comp) {
        logger.debug("[{}] Collecting topology for: {}...", TAG, comp.getMainClass());

        // collect extended classes
        collectExtendedClasses(comp);


        // Collect lifecycle methods
        collectLifecycleMethods(comp);

        // Collect callback methods
        /*for(SootClass superClass: comp.getAddedClasses())
            collectCallbackMethods(comp, app.getCallbacksInSootClass(superClass));
        */
        collectCallbackMethods(comp, app.getCallbacksInSootClass(comp.getMainClass()));

        logger.debug("[{}] DONE - Collected topology for: {}...", TAG, comp.getMainClass());

        // Collect reachable methods from lifecycle methods
//        for (MethodOrMethodContext method : comp.getLifecycleMethods()) {
//            ComponentReachableMethods rm = new ComponentReachableMethods(comp.getMainClass(), Collections.singletonList(method));
//            rm.update();
//            comp.addLifecycleReachableMethods(method, rm);
//        }
//
//        for (MethodOrMethodContext lifecycleMethod : comp.getLifecycleMethods()) {
//            ComponentReachableMethods rm = comp.getLifecycleReachableMethodsFrom(lifecycleMethod);
//            QueueReader<MethodOrMethodContext> reachableMethods = rm.listener();
//
//            // Analyze each reachable methods
//            while (reachableMethods.hasNext()) {
//                // Get the next reachable method
//                SootMethod method = reachableMethods.next().method();
//                // Do not analyze system classes
//                if (SystemClassHandler.isClassInSystemPackage(method.getDeclaringClass().getName()))
//                    continue;
//                if (!method.isConcrete())
//                    continue;
//
//                // Edges
//                Set<List<Edge>> edges = rm.getContextEdges(method);
//
//                // Analyze layout
//                if (lifecycleMethod.method().getName().contains("onCreate")) {
//                    // Analyze layout
//                    analyzeLayout(method, comp, edges);
//                }
//
//                // Analyze fragment transaction
//                Set<SootClass> fragmentClasses = analyzeFragmentTransaction(method, edges, comp);
//
//                // Create the fragments
//                for (SootClass sc : fragmentClasses) {
//                    App.v().createFragment(sc, comp);
//
//                    // process the fragment lifecycle methods
//                    Fragment fragment = App.v().getFragmentByName(sc.getName());
//
//                    // collect extended classes
//                    collectExtendedClassesForFragment(fragment);
//
//                    // collect lifecycle methods
//                    collectFragmentLifecycleMethods(fragment);
//
//                    // collect menu methods
//                    collectMenuAndDrawerMethods(fragment);
//
//                    // collect reachable methods
//                    LinkedList<MethodOrMethodContext> lifecycleMethods = new LinkedList<>(fragment.getLifecycleMethods());
//                    for (MethodOrMethodContext fragmentLifecycleMethod : lifecycleMethods) {
//                        ComponentReachableMethods rmFragment = new ComponentReachableMethods(fragment.getMainClass(),
//                                Collections.singletonList(fragmentLifecycleMethod));
//                        rmFragment.update();
//                        fragment.addLifecycleReachableMethods(fragmentLifecycleMethod, rmFragment);
//                    }
//                }
//            }
//        }
//
//        // Collect nodes with fragments
//        Screen screen = SSTG.v().getScreenNodeByName(comp.getName());
//        screen.addFragments(comp.getFragments());

        // Analyze drawer methods
//        analyzeMenuDrawerMethods(screen);
    }


    /**
     * Collects all extended classes of the component declared in the manifest
     */
    public static void collectExtendedClasses(AbstractComponent comp){
        // Find the main SootClass if not found
        SootClass sc = comp.getMainClass();
        if (sc == null) {
            sc = Scene.v().getSootClassUnsafe(comp.getName(), false);
            if (sc != null) {
                System.out.println("Adding soot class "+ sc);
                comp.setMainClass(sc);
            }
            else
                logger.error("Failed to find class for component: {}", comp.getName());
        }

        if (comp instanceof Activity) {
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (isSubclassOf(x, AndroidClass.v().osClassActivity) ||
                        isSubclassOf(x, AndroidClass.v().scSupportV7Activity) ||
                        isSubclassOf(x, AndroidClass.v().scSupportV4Activity)) {
                    if (!SystemClassHandler.v().isClassInSystemPackage(x.getName())) {
                        //logger.debug("Added super class")
                        comp.addAddedClass(x);
                    }
                }});
        } else if (comp instanceof Service) {
            // Services
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (isSubclassOf(x, AndroidClass.v().osClassService)){
                    if (!SystemClassHandler.v().isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }
            });
        } else if (comp instanceof BroadcastReceiver) {
            // Broadcast receivers
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (isSubclassOf(x, AndroidClass.v().osClassBroadcastReceiver)) {
                    if (!SystemClassHandler.v().isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }
            });
        } else if (comp instanceof Fragment) {
            // Fragments
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (isSubclassOf(x, AndroidClass.v().osClassFragment) ||
                        isSubclassOf(x, AndroidClass.v().scFragment)) {
                    if (!SystemClassHandler.v().isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }
            });
        }
    }

    /**
     * collects the lifecycle methods for a component
     * @param comp The component to collect lifecycle methods
     */
    public static void collectLifecycleMethods(AbstractComponent comp) {
        // lifecycle methods
        List<String> lifecycleMethods = Collections.emptyList();

        if (comp instanceof Activity) {
            lifecycleMethods = MethodConstants.Activity.getLifecycleMethods();
        } else if (comp instanceof Service) {
            lifecycleMethods = MethodConstants.Service.getLifecycleMethods();
        } else if (comp instanceof BroadcastReceiver) {
            lifecycleMethods = MethodConstants.BroadcastReceiver.getLifecycleMethods();
        } else if (comp instanceof Fragment) {
            lifecycleMethods = MethodConstants.Fragment.getLifecycleMethods();
        }

        // Collect the list of lifecycle methods (in order)
        lifecycleMethods.iterator().forEachRemaining(x -> {
            MethodOrMethodContext method = findAndAddMethod(x, comp);
            if (method!=null)
                comp.addLifecycleMethod(method);
        });
    }

    /**
     * Collects all callback methods of the component
     * @param comp The given component to collect callback methods
     */
    public static void collectCallbackMethods(AbstractComponent comp, Set<AndroidCallbackDefinition> callbacks) {
        comp.addCallbacks(callbacks);

        // Menu callbacks
        collectMenuAndDrawerMethods(comp); //TODO callbacks is empty for fragments?
        //Dialog callbacks
        collectDialogMethods(comp);
    }

    /**
     * Finds all menu creation and callback methods in a given component
     * @param comp The component to look for menu creation and callback methods
     */
    private static void collectMenuAndDrawerMethods(AbstractComponent comp) {
        // Get the list of menu methods
        List<String> menuCreateMethods;
        List<String> menuCallbackMethods = MethodConstants.Menu.getOptionMenuCallbackMethodList();
        List<String> contextMenuOnCreateMethods = MethodConstants.Menu.getContextMenuCreateMethodList(),
                contextMenuCallbackMethods = MethodConstants.Menu.getContextMenuCallbackMethodList();
        List<String> drawerCallbacksMethods = MethodConstants.Drawer.getDrawerCallbackMethods();
        if (comp instanceof Activity) {
            menuCreateMethods = MethodConstants.Menu.getOptionMenuCreateForActivity();
            ((Activity)comp).addMenuOnCreateMethods(menuCreateMethods);
            ((Activity)comp).addMenuCallbackMethods(menuCallbackMethods);
            ((Activity)comp).addContextMenuOnCreateMethods(contextMenuOnCreateMethods);
            ((Activity)comp).addContextMenuCallbackMethods(contextMenuCallbackMethods);
            ((Activity)comp).addDrawerMenuCallbackMethods(drawerCallbacksMethods);
        } else if (comp instanceof Fragment) {
            menuCreateMethods = MethodConstants.Menu.getOptionMenuCreateForFragment();
            // The list of menu methods (in order of create and callback)
            menuCreateMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Fragment)comp).addMenuRegistrationMethod(method);
                }
            });

            contextMenuOnCreateMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Fragment)comp).addContextMenuRegistrationMethod(method);
                }
            });

            menuCallbackMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Fragment)comp).addMenuCallbackMethod(method);
                    comp.addCallback(new AndroidCallbackDefinition(method.method(), method.method(), AndroidCallbackDefinition.CallbackType.Widget));
                }
            });

            contextMenuCallbackMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Fragment)comp).addMenuCallbackMethod(method);
                    comp.addCallback(new AndroidCallbackDefinition(method.method(), method.method(), AndroidCallbackDefinition.CallbackType.Widget));
                }
            });
        }
    }

    private static void collectDialogMethods(AbstractComponent comp){
        List<String> dialogCreateMethods; //onCreateDialog within a fragment
        List<String> dialogCallbackMethods = MethodConstants.Dialog.getDialogCallbackMethods();
        if(comp instanceof Activity){ //the callback is probably within an inner class?
            ((Activity)comp).addDialogCallbackMethods(dialogCallbackMethods);
        }
        else if (comp instanceof Fragment){
            //TODO
        }
    }

    @Override
    public void forceTerminate(ISolverTerminationReason reason) {
        this.isKilled = reason;
    }

    @Override
    public boolean isTerminated() {
        return isKilled != null;
    }

    @Override
    public boolean isKilled() {
        return isKilled != null;
    }

    @Override
    public void reset() {
        this.isKilled = null;
    }

    @Override
    public void addStatusListener(IMemoryBoundedSolverStatusNotification listener) {
        this.notificationListeners.add(listener);
    }

    @Override
    public ISolverTerminationReason getTerminationReason() {
        return isKilled;
    }
}
