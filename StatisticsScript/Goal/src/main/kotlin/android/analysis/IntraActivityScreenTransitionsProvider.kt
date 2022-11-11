package android.analysis

import android.model.Edge
import android.model.Screen
import android.soot.FragmentChangeAnalysis
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition
import soot.util.HashMultiMap
import soot.util.MultiMap
import java.util.LinkedList
import kotlin.collections.HashSet

/**
 * Finds possible transitions between screens containing the same activity
 *
 * IC3 is unable to find exit-points within fragments
 * flow-droid does not detect callbacks for fragments
 * We currently only consider transitions due to callbacks within activity that triggers transitions
 */
class IntraActivityScreenTransitionsProvider(private val flowDroidAnalysis: SetupApplication,
                                             private val baseScreenProvider: BaseScreenProvider,
                                             private val callbackWidgetProvider: CallbackWidgetProvider) {
    /**
     * All screens within graph
     */
   /* val screens: Set<Screen> get() = transitions.screen
    /**
     * In edges for screen - edges where the screen is the tgt
     */
    val inEdges: MultiMap<Screen, Edge> get() = transitions.inEdges
    /**
     * In edges for screen - edges where the screen is the src
     */
    val outEdges: MultiMap<Screen, Edge> get() = transitions.outEdges

    /**
     * Class to store variables, used to expose variables in a lazy manner
     */
    private val transitions: Transitions by lazy { findTransitions() }
    private data class Transitions(val screen: Set<Screen>,
                                   val inEdges: MultiMap<Screen, Edge>,
                                   val outEdges: MultiMap<Screen, Edge>)

    private fun findTransitions(): Transitions {
        // Using base screens as a starting point, calculate new screens
        val toAnalyzeScreens = LinkedList(baseScreenProvider.baseScreens.values.flatten())

        val analyzedScreens = HashSet<Screen>()
        val inEdges = HashMultiMap<Screen, Edge>()
        val outEdges = HashMultiMap<Screen, Edge>()

        while (toAnalyzeScreens.isNotEmpty()) {
            val cur = toAnalyzeScreens.poll()
            val edges = findEdges(cur)

            for (edge in edges) {
                inEdges.put(edge.tgt, edge)
                outEdges.put(edge.src, edge)

                if (!analyzedScreens.contains(edge.tgt)) {
                    toAnalyzeScreens.add(edge.tgt)
                }
            }

            analyzedScreens.add(cur)
        }

        return Transitions(analyzedScreens, inEdges, outEdges)
    }

    private fun findEdges(screen: Screen): List<Edge> {
        val callbacks = flowDroidAnalysis.callbackMethods.get(screen.activity)
            // only consider callbacks launched by widget interactions
            .filter { it.callbackType == CallbackDefinition.CallbackType.Widget }

        return callbacks.flatMap { cb ->
            val analyzer = FragmentChangeAnalysis(cb.targetMethod)
            val transactions = analyzer.calculateFragmentTransactions()
            val layouts = analyzer.calculateFragmentChanges(screen.dynamicFragments, transactions)
            val widget = callbackWidgetProvider.findLayoutControl(cb.targetMethod)
            layouts.map { state ->
                val tgt = Screen(screen.activity, state)
                Edge(screen, tgt, cb, widget)
            }
        }
    }*/
}
