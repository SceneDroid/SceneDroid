package android.soot

import soot.Unit
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ArraySparseSet
import soot.toolkits.scalar.FlowSet
import soot.toolkits.scalar.ForwardFlowAnalysis

/**
 * Computes all possible execution paths that can be taken when a method body is run
 * Includes the branching statements inside execution path but does not indicate which branch path was taken
 */
class MethodBodyExecutionPathAnalysis(graph: DirectedGraph<Unit>) :
    ForwardFlowAnalysis<Unit, FlowSet<List<Unit>>>(graph),
    Runnable {

    /**
     * Execution paths calculated for the [graph].
     * Each element in the [FlowSet] is the list of [Unit] present in the execution path
     */
    lateinit var executionPaths: FlowSet<List<Unit>>
        private set

    /**
     * Returns the flow object corresponding to the initial values for each graph node.
     */
    override fun newInitialFlow(): FlowSet<List<Unit>> {
        return ArraySparseSet()
    }

    /**
     * Returns the initial flow value for entry/exit graph nodes.
     */
    override fun entryInitialFlow(): FlowSet<List<Unit>> {
        return newInitialFlow().apply { add(listOf()) }
    }

    /**
     * Runs the analysis and computes all execution paths, storing result in [executionPaths]
     */
    public override fun doAnalysis() {
        super.doAnalysis()
        executionPaths = getFlowAfter(graph.last())
    }

    /**
     * Runs the analysis and computes all execution paths, storing result in [executionPaths]
     */
    override fun run() = doAnalysis()

    /**
     * Runs the analysis and computes all execution paths
     * @return execution paths calculated in analysis
     */
    fun runAndReturnResult(): FlowSet<List<Unit>> {
        run()
        return executionPaths
    }

    /**
     * Compute the merge of the `in1` and `in2` sets, putting the result into `out`. The
     * behavior of this function depends on the implementation ( it may be necessary to check whether `in1` and
     * `in2` are equal or aliased ). Used by the doAnalysis method.
     */
    override fun merge(in1: FlowSet<List<Unit>>, in2: FlowSet<List<Unit>>, out: FlowSet<List<Unit>>) {
        in1.union(in2, out)
    }

    /** Creates a copy of the `source` flow object in `dest`.  */
    override fun copy(source: FlowSet<List<Unit>>, dest: FlowSet<List<Unit>>) {
        source.copy(dest)
    }

    /**
     * Given the merge of the `out` sets, compute the `in` set for `s` (or in to out,
     * depending on direction).
     *
     * This function often causes confusion, because the same interface is used for both forward and backward flow analyses.
     * The first parameter is always the argument to the flow function (i.e. it is the "in" set in a forward analysis and the
     * "out" set in a backward analysis), and the third parameter is always the result of the flow function (i.e. it is the
     * "out" set in a forward analysis and the "in" set in a backward analysis).
     *
     * @param in
     * the input flow
     * @param d
     * the current node
     * @param out
     * the returned flow
     */
    override fun flowThrough(`in`: FlowSet<List<Unit>>, d: Unit, out: FlowSet<List<Unit>>) {
        `in`.forEach { out.add(it.plus(d)) }
    }
}
