package android.soot

import org.slf4j.LoggerFactory
import soot.Local
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.DefinitionStmt
import soot.jimple.InstanceInvokeExpr
import soot.jimple.IntConstant
import soot.jimple.InvokeStmt
import soot.jimple.Stmt
import soot.toolkits.graph.BriefUnitGraph
import soot.toolkits.scalar.ArraySparseSet
import soot.toolkits.scalar.FlowSet
import soot.toolkits.scalar.ForwardFlowAnalysis

/**
 * Calculate the changes in the fragments associated with R.ids when subjected to a method.
 * We only calculate one fragment per layout even thought its possible to have two on top of each other.
 * The fragment that is last initialized in the layout will be the one mapped to layout.
 *
 * The reasoning is that we only want to consider visible fragments so we assume the last inflated fragment is the
 * one visible.
 *
 * @param sootMethod the SootMethod to analyze
 * @param depth the analysis depth
 */
class FragmentChangeAnalysis private constructor(private val sootMethod: SootMethod, private val depth: Int) :
    ForwardFlowAnalysis<Unit, FlowSet<FragmentChangeAnalysis.TransactionState>>(BriefUnitGraph(sootMethod.retrieveActiveBody())) {

    constructor(sootMethod: SootMethod): this(sootMethod, 1)

    /**
     * Represent intermediate states as we move along execution paths
     * @property executed represents Fragment Transactions that have been executed
     *              The unit stmts in order of execution
     * @property stored represents FragmentTransactions that have been declared but not yet executed
     *              Maps the register value that started the chain of transactions to a list of statements that
     *              were found to be declared fragment transactions called on the register
     */
    data class TransactionState(val executed: List<Unit>, val stored: Map<Value, List<Unit>>)

    /**
     * Calculates the chain of fragment transactions that are possibly applied when callback is executed
     */
    fun calculateFragmentTransactions(): List<List<Unit>> {
        logger.debug("Starting fragment change analysis on $sootMethod")
        doAnalysis()
        return graph.tails.flatMap {
            // Find the transaction state at the end of the control flow
            // Return the list of executed fragment transactions
            getFlowAfter(graph.last()).map { it.executed }
        }.distinct()
    }

    /**
     * Calculates the different layouts that can result from the callback execution
     */
    fun calculateFragmentChanges(initialLayout: Map<Int, SootClass>, transactions: List<List<Unit>>): List<Map<Int, SootClass>> {
        return transactions.flatMap { initialLayout.applyTransactions(it) }.distinct()
    }

    /**
     * Returns the flow object corresponding to the initial values for each graph node
     */
    override fun newInitialFlow(): FlowSet<TransactionState> {
        return ArraySparseSet()
    }

    /**
     * Returns the flow object corresponding to the flow for entrypoints
     */
    override fun entryInitialFlow(): FlowSet<TransactionState> {
        val initFlow = ArraySparseSet<TransactionState>()
        // Add in an newly created transaction state to mark the state before any stmts are executed
        initFlow.add(TransactionState(listOf(), mapOf()))
        return initFlow
    }

    /**
     * Compute the merge of the `in1` and `in2` sets, putting the result into `out`. The
     * behavior of this function depends on the implementation ( it may be necessary to check whether `in1` and
     * `in2` are equal or aliased ). Used by the doAnalysis method.
     */
    override fun merge(in1: FlowSet<TransactionState>,
                       in2: FlowSet<TransactionState>,
                       out: FlowSet<TransactionState>) {

        in1.union(in2, out)
    }

    /** Creates a deep copy of the `source` flow object in `dest`.  */
    override fun copy(source: FlowSet<TransactionState>,
                      dest: FlowSet<TransactionState>) {

        source.copy(dest)
    }

    /**
     * Calculates the [out] set from the given [in] set.
     *
     * For our analysis the [out] set is only changed when some variation of commit or commitNow is called to execute
     * all pending FragmentTransactions
     *
     * @param in the input flow
     * @param d the current node
     * @param out the returned flow
     */
    override fun flowThrough(`in`: FlowSet<TransactionState>, d: Unit, out: FlowSet<TransactionState>) {
        when {
            // Check the statement in the current control flow and process accordingly
            isBeginTransaction(d) -> processBeginTransaction(`in`, d, out) // declares start for a chain of transactions
            isFragmentTransaction(d) -> processFragmentTransaction(`in`, d, out) // declares a transaction
            isCommitTransaction(d) -> processCommitTransaction(`in`, d, out) // executes transactions for a chain

            // ordering matters here, only process other function calls if there are no matches
            // analyze possible state changes from called function
            isMethodCall(d) -> processMethodCall(`in`, d, out)

            // shallow copy to save space
            else -> `in`.copy(out)
        }
    }

    /**
     * Looks for stmt similar to
     * $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()
     * afterwards, need to start keeping track of fragment transactions called on the register reference (r3)
     */
    private fun isBeginTransaction(stmt: Unit): Boolean = invokesMethod(stmt, MethodUtils.beginTransactionMethods)

    /**
     * Looks for stmt similar to
     * $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);
     */
    private fun isFragmentTransaction(stmt: Unit): Boolean =
        isAddTransaction(stmt) || isReplaceTransaction(stmt) || isRemoveTransaction(stmt)

    private fun isAddTransaction(stmt: Unit): Boolean = invokesMethod(stmt, MethodUtils.addFragmentMethods)
    private fun isReplaceTransaction(stmt: Unit): Boolean = invokesMethod(stmt, MethodUtils.replaceFragmentMethods)
    private fun isRemoveTransaction(stmt: Unit): Boolean = invokesMethod(stmt, MethodUtils.removeFragmentMethods)

    /**
     * Looks for stmt similar to virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>();
     */
    private fun isCommitTransaction(stmt: Unit): Boolean = invokesMethod(stmt, MethodUtils.commitTransactionMethods)

    /**
     * Checks if another function is being called within the analysis function
     */
    private fun isMethodCall(stmt: Unit): Boolean = (stmt as Stmt).containsInvokeExpr()

    /**
     * Checks that the stmt invokes a method present in methods
     * Makes sure of such by matching with function name and making sure that the declaring class is a subclass
     * and the return type and params match making it an override or the exact function
     */
    private fun invokesMethod(stmt: Unit, methods: List<SootMethod>): Boolean {
        return (stmt is Stmt)
            && stmt.containsInvokeExpr()
            && methods.contains(stmt.invokeExpr.method)
    }

    /**
     * Main work is to extract the jimple register that starts the chain of transactions
     */
    private fun processBeginTransaction(`in`: FlowSet<TransactionState>, d: Unit, out: FlowSet<TransactionState>) {
        logger.debug("$d found to start fragment transactions")
        // An example of the jimple instruction that starts the transaction is
        // $r3 = virtualinvoke $r2.<android.support.v4.app.FragmentManager: android.support.v4.app.FragmentTransaction beginTransaction()>()
        val leftOp = (d as DefinitionStmt).leftOp

        `in`.forEach {
            if (it.stored.containsKey(leftOp)) {
                logger.warn("Another set of transactions have already been started, discarding pending")
            }

            val updatedStored = it.stored.toMutableMap().plus(leftOp to listOf())
            out.add(TransactionState(it.executed, updatedStored))
        }
    }

    /**
     * Main work is all the instruction to the chain of transactions under the jimple register
     */
    private fun processFragmentTransaction(`in`: FlowSet<TransactionState>, d: Unit, out: FlowSet<TransactionState>) {
        logger.debug("$d found to be a fragment transaction")
        // Example jimple instruction
        // $r3 = virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)>(2131165396, $r6);

        try {
            val leftOp = (d as DefinitionStmt).leftOp

            `in`.forEach {
                if (!it.stored.containsKey(leftOp)) {
                    logger.error("Transaction set was not started, not registering $d as a declared transaction")
                } else {

                    val updatedStored = it.stored.toMutableMap()
                    val transactionList = updatedStored.getValue(leftOp).toMutableList().apply { add(d) }
                    updatedStored[leftOp] = transactionList
                    out.add(TransactionState(it.executed, updatedStored))
                }
            }
        } catch (e: Exception) {
            logger.error(e.toString())
            logger.error("Could not process fragment transaction $d")
        }
    }

    private fun processCommitTransaction(`in`: FlowSet<TransactionState>, d: Unit, out: FlowSet<TransactionState>) {
        logger.debug("$d found to execute fragment transactions")
        // breaks down virtualinvoke $r3.<android.support.v4.app.FragmentTransaction: void commitNow()>() to $r3
        val leftOp = ((d as InvokeStmt).invokeExprBox.value as InstanceInvokeExpr).baseBox.value

        `in`.forEach {
            if (!it.stored.containsKey(leftOp)) {
                logger.error("transactions not started before commit found")
            } else {
                val updatedExecuted = it.executed.toMutableList().apply { addAll(it.stored.getValue(leftOp)) }
                val updatedStored = it.stored.toMutableMap().minus(leftOp)
                out.add(TransactionState(updatedExecuted, updatedStored))
            }
        }
    }

    private fun processMethodCall(`in`: FlowSet<TransactionState>, d: Unit, out: FlowSet<TransactionState>) {
        val calledMethod = (d as Stmt).invokeExpr.method

        if (stopProcessing(calledMethod)) {
            `in`.copy(out)
            return
        }

        logger.debug("$d extends analysis to new function")
        logger.debug("${calledMethod.declaringClass}")

        // we assume that fragment changing transactions are not inter-procedural
        // so transaction sets are not shared across methods
        // we find the possible layouts that result from the method call and continue
        // TODO maybe work on some optimization to not have to call as much or multi-thread using coroutines
        val transSeqs = FragmentChangeAnalysis(calledMethod, depth + 1).calculateFragmentTransactions()

        `in`.forEach { state ->
            transSeqs.forEach { seq ->
                val updatedExecuted = state.executed.toMutableList().apply { addAll(seq) }
                out.add(TransactionState(updatedExecuted, state.stored))
            }
        }
    }

    private fun stopProcessing(sootMethod: SootMethod): Boolean {
        if (depth >= 10) {
            logger.debug("Analysis depth hit, now stopping")
            return true
        }

        return isExcludedMethod(sootMethod)
    }

    private fun isExcludedMethod(sootMethod: SootMethod): Boolean {
        return SystemClassInfoProvider.isClassInSystemPackage(sootMethod.declaringClass)
                || !sootMethod.hasActiveBody()
                || sootMethod.name == "<init>"
    }

    private fun Map<Int, SootClass>.applyTransactions(transactions: List<Unit>): List<Map<Int, SootClass>> {
        var possibleLayouts = listOf(this)
        transactions.forEach {
            possibleLayouts = when {
                isAddTransaction(it) -> possibleLayouts.applyAddTransaction(it)
                isReplaceTransaction(it) -> possibleLayouts.applyReplaceTransaction(it)
                isRemoveTransaction(it) -> possibleLayouts.applyRemoveTransaction(it)
                else -> throw RuntimeException("Found unknown transaction")
            }
        }
        return possibleLayouts
    }

    private fun List<Map<Int, SootClass>>.applyAddTransaction(transaction: Unit): List<Map<Int, SootClass>> {
        val methodArgs = (transaction as Stmt).invokeExpr.args
        val layoutId = if (methodArgs[0] is IntConstant) (methodArgs[0] as IntConstant).value else 0

        // TODO currently undecided whether to do anything special for fragments added to id 0
        val possibleFragmentTypes =
            if (layoutId == 0) LocalVariableTypeAnalysis.calculatePossibleTypes(methodArgs[0] as Local)
            else LocalVariableTypeAnalysis.calculatePossibleTypes(methodArgs[1] as Local)

        return this.flatMap { map ->
            possibleFragmentTypes.map { type ->
                map.toMutableMap().apply {
                    put(layoutId, type.toSootClass())
                }
            }
        }
    }

    private fun List<Map<Int, SootClass>>.applyReplaceTransaction(transaction: Unit): List<Map<Int, SootClass>> {
        val methodArgs = (transaction as Stmt).invokeExpr.args
        val layoutId = (methodArgs[0] as IntConstant).value
        val possibleFragmentTypes = LocalVariableTypeAnalysis.calculatePossibleTypes(methodArgs[1] as Local)

        return this.flatMap { map ->
            possibleFragmentTypes.map { type ->
                map.toMutableMap().apply {
                    put(layoutId, type.toSootClass())
                }
            }
        }
    }

    private fun List<Map<Int, SootClass>>.applyRemoveTransaction(transaction: Unit): List<Map<Int, SootClass>> {
        val methodArgs = (transaction as Stmt).invokeExpr.args
        val possibleFragmentTypes = LocalVariableTypeAnalysis.calculatePossibleTypes(methodArgs[0] as Local)
            .map { it.toSootClass() }

        return this.flatMap { map ->
            possibleFragmentTypes.map { type -> map.filterValues { it != type } }
        }
    }

    private fun Type.toSootClass(): SootClass {
        return Scene.v().getSootClass(this.toString())
    }

    // TODO refactor in the future
    private class MethodUtils {
        companion object {
            val beginTransactionMethodNames = setOf("beginTransaction", "openTransaction")
            val beginTransactionMethods: List<SootMethod> by lazy {
                val l = mutableListOf<SootMethod>()
                l.addAll(Scene.v().getSootClass("android.support.v4.app.FragmentManager")
                    .methods.filter { beginTransactionMethodNames.contains(it.name) })

                l.addAll( Scene.v().getSootClass("androidx.fragment.app.FragmentManager")
                    .methods.filter { beginTransactionMethodNames.contains(it.name) })
                l
            }


            val addFragmentMethodNames = setOf("add")
            val addFragmentMethods: List<SootMethod> by lazy {
                val l = mutableListOf<SootMethod>()
                l.addAll(Scene.v().getSootClass("android.support.v4.app.FragmentTransaction")
                    .methods.filter { addFragmentMethodNames.contains(it.name) })

                l.addAll(Scene.v().getSootClass("androidx.fragment.app.FragmentTransaction")
                    .methods.filter { addFragmentMethodNames.contains(it.name) })
                l
            }

            val removeFragmentMethodNames = setOf("remove")
            val removeFragmentMethods: List<SootMethod> by lazy {
                val l = mutableListOf<SootMethod>()
                l.addAll(Scene.v().getSootClass("android.support.v4.app.FragmentTransaction")
                    .methods.filter { removeFragmentMethodNames.contains(it.name) })
                l.addAll(Scene.v().getSootClass("androidx.fragment.app.FragmentTransaction")
                    .methods.filter { removeFragmentMethodNames.contains(it.name) })
                l
            }

            val replaceFragmentMethodNames = setOf("replace")
            val replaceFragmentMethods: List<SootMethod> by lazy {
                val l = mutableListOf<SootMethod>()
                l.addAll(Scene.v().getSootClass("android.support.v4.app.FragmentTransaction")
                    .methods.filter { replaceFragmentMethodNames.contains(it.name) })
                l.addAll(Scene.v().getSootClass("androidx.fragment.app.FragmentTransaction")
                    .methods.filter { replaceFragmentMethodNames.contains(it.name) })
                l
            }

            val commitTransactionMethodNames = setOf(
                "commit",
                "commitAllowingStateLoss",
                "commitNow",
                "commitNowAllowingStateLoss"
            )
            val commitTransactionMethods: List<SootMethod> by lazy {
                val l = mutableListOf<SootMethod>()
                l.addAll(Scene.v().getSootClass("android.support.v4.app.FragmentTransaction")
                    .methods.filter { commitTransactionMethodNames.contains(it.name) })
                l.addAll(Scene.v().getSootClass("androidx.fragment.app.FragmentTransaction")
                    .methods.filter { commitTransactionMethodNames.contains(it.name) })
                l
            }

        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FragmentChangeAnalysis::class.java)

        fun calculateFragmentChanges(methods: List<SootMethod>,
                                     initialLayout: Map<Int, SootClass> = mapOf()): List<Map<Int, SootClass>> {
            var layouts = listOf(initialLayout)
            methods.forEach { method ->
                if (!SystemClassInfoProvider.isClassInSystemPackage(method.declaringClass)) {
                    val analyzer = FragmentChangeAnalysis(method)
                    val transactions = analyzer.calculateFragmentTransactions()
                    layouts = layouts.flatMap { layout ->
                        analyzer.calculateFragmentChanges(layout, transactions)
                    }
                }
            }
            return layouts
        }
    }
}
