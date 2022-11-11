package android.goal.explorer.analysis.value;

import org.pmw.tinylog.Logger;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton with global analysis parameters.
 */
public class AnalysisParameters {
    private static AnalysisParameters instance = new AnalysisParameters();

    private Set<String> analysisClasses;
    private boolean iterative = true;
    private JimpleBasedInterproceduralCFG icfg;
    private boolean inferNonModeledTypes = true;
    private int threadCount;

    /**
     * Adds classes to the set of analysis classes. The analysis classes are the set of classes
     * through which constant propagation should take place.
     *
     * @param classes A collection of classes.
     */
    public void addAnalysisClasses(Collection<String> classes) {
        if (analysisClasses == null) {
            analysisClasses = new HashSet<>();
        }

        analysisClasses.addAll(classes);
    }

    /**
     * Determines if a class is an analysis class.
     *
     * @param clazz A fully-qualified class name.
     * @return True if the argument class is an analysis class.
     */
    public boolean isAnalysisClass(String clazz) {
        if (analysisClasses == null) {
            Logger.warn("No analysis classes set. To change this, use "
                        + "Analysis.v().addAnalysisClasses()");
            return false;
        }

        return analysisClasses.contains(clazz);
    }

    /**
     * Gets the set of analysis classes.
     *
     * @return The set of analysis classes.
     */
    public Set<String> getAnalysisClasses() {
        return analysisClasses;
    }

    /**
     * Determines if the analysis is iterative. Currently all analyses should be iterative. The
     * default value is true.
     *
     * @return True if the analysis is iterative.
     */
    public boolean isIterative() {
        return iterative;
    }

    /**
     * Sets the interprocedural control flow graph for the analysis.
     *
     * @param icfg The interprocedural CFG.
     */
    public void setIcfg(JimpleBasedInterproceduralCFG icfg) {
        this.icfg = icfg;
    }

    /**
     * Gets the interprocedural control flow graph for the analysis.
     *
     * @return The interprocedural control flow graph.
     */
    public JimpleBasedInterproceduralCFG getIcfg() {
        return icfg;
    }

    /**
     * Sets whether hotspot values should be inferred for values that are not modeled using COAL. The
     * COAL language allows method arguments with primitive values (string, int, etc.) to be specified
     * as hotspots. This flags specifies if such values should be inferred. Note that in order to
     * infer these values, argument analyses have to be specified for them using
     * registerArgumentValueAnalysis}. The default value is true.
     *
     * @param inferNonModeledTypes The value of the flag.
     */
    public void setInferNonModeledTypes(boolean inferNonModeledTypes) {
        this.inferNonModeledTypes = inferNonModeledTypes;
    }

    /**
     * Sets the maximum number of threads to be used by the IDE solver.
     *
     * @param threadCount The maximum number of threads.
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Determines whether hotspot values should be inferred for values that are not modeled using
     * COAL.
     *
     * @return True if hotspot values should be inferred for values that are not modeled using COAL.
     */
    public boolean inferNonModeledTypes() {
        return inferNonModeledTypes;
    }

    /**
     * Determines the maximum number of threads that should be used by the program.
     *
     * @return The maximum thread count.
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Returns the singleton instance for this class.
     *
     * @return The singleton instance for this class.
     */
    public static AnalysisParameters v() {
        return instance;
    }

    private AnalysisParameters() {
    }
}
