import android.goal.explorer.analysis.CallbackWidgetProvider
import android.goal.explorer.model.component.Activity
import android.goal.explorer.model.stg.STG
import android.goal.explorer.model.stg.edge.TransitionEdge
import android.goal.explorer.model.stg.node.AbstractNode
import android.goal.explorer.model.stg.node.BroadcastReceiverNode
import android.goal.explorer.model.stg.node.ScreenNode
import android.goal.explorer.model.stg.node.ServiceNode
import org.slf4j.LoggerFactory
import soot.Scene
import soot.SootMethod
import soot.VoidType
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition
import soot.jimple.infoflow.util.SystemClassHandler
import soot.util.HashMultiMap
import soot.util.MultiMap
import st.cs.uni.saarland.de.helpClasses.Helper
import st.cs.uni.saarland.de.helpClasses.SootHelper
import st.cs.uni.saarland.de.reachabilityAnalysis.ApiInfoForForward
import st.cs.uni.saarland.de.reachabilityAnalysis.CallbackToApiMapper
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class TargetMarker {
    companion object {
        private val logger = LoggerFactory.getLogger(TargetMarker::class.java)
        private val maxMethodDepth = 30
        private var numFoundTargets = 0

        /**
         * Marks the screen nodes that match the evaluation criteria
         */

        fun markNodesByCriteria(
            stg: STG,
            criteriae: Set<String>,
            targetType: String?
        ): Map<AbstractNode, Pair<Boolean,String>> {
            //Initialization for reachability analysis
            //TODO move in class construction
            val newClass = SootHelper.createSootClass("CustomIntercomponentClass")
            SootHelper.createSootMethod(newClass, "newActivity", ArrayList(), VoidType.v(), false)

            if(targetType == null || targetType.isEmpty()) //no target
                return stg.allNodes.associateWith { Pair(false, "") }
            if(targetType.equals("act"))
                return markNodesIfNameMatch(
                    stg,
                    stg.allBaseNodes,
                    criteriae
                )
            else if(targetType.equals("api"))
                return markNodesIfRecursivelySatisfiesCriteria(
                    stg,
                    stg.allNodes,
                    criteriae,
                    HashMultiMap()
                ) { method, methodsList ->
                    methodsList.contains(method.signature)
                }
            else //statement as target
                return markNodesIfRecursivelySatisfiesCriteria(
                    stg,
                    stg.allNodes,
                    criteriae,
                    HashMultiMap()
                ) { method, stmtList ->
                    stmtList.any { method.activeBody.units.any { stmt -> stmt.toString() == it }}
                }
        }


        private fun markNodesIfNameMatch(
            stg: STG,
            nodes: Set<AbstractNode>,
            names: Set<String>
        ): Map<AbstractNode, Pair<Boolean, String>> {

            return nodes.associateWith { Pair(names.contains(it.name),"")  }.toMutableMap()
        }


        //TO-DO
        /**
         * Parse through nodes of the stg
         * Parse callbacks of each activity (with memo)
         * If callbacks invokes target
         * Search for edge from that src screen with callback target or parent method
         * Mark the tgt node
         * If no edge (then the callback doesn't lead to a new screen, mark current node)
         * If no callback,  (or if current not marked already), parse lifecycle and mark all screens of that activity
         */



            private fun <T : Any> markNodesIfRecursivelySatisfiesCriteria(
            stg: STG,
            nodes: Set<AbstractNode>,
            criteriae: Set<T>,
            memo: MultiMap<Activity, AndroidCallbackDefinition>,
            match: (SootMethod, Set<T>) -> Boolean
        ): Map<AbstractNode, Pair<Boolean,String>> {
            logger.debug("Searching for targets in stg from $criteriae")
            var marks = nodes.associateWith { _ -> Pair(false, "") }.toMutableMap()
            //var marks = nodes.associateWith { _ -> false }.toMutableMap()
            for (node in nodes) {
                logger.debug("Checking node $node")
                if (node is ServiceNode || node is BroadcastReceiverNode) {
                    node.component.lifecycleMethods.forEach { lifecycle ->
                        logger.debug("Checking lifecycle $lifecycle")
                        if (recursiveCheck(lifecycle.method()) { match(it, criteriae) }) {
                            logger.debug("Found method of interest, marking node $node")
                            numFoundTargets += 1
                            marks[node] = Pair(true, "")
                            //marks.put(node, true)
                        }
                    }

                    node.component.callbacks.forEach {callback ->
                        //TODO check how triggered?
                        logger.debug("Checking callback $callback")
                        if (recursiveCheck(callback.targetMethod) { match(it, criteriae) }) {
                            logger.debug("Found method of interest, marking node $node")
                            numFoundTargets += 1
                            marks[node] = Pair(true, "")
                            //marks.put(node, true)
                        }
                    }
                } else {
                    val activity = (node.component as Activity)
                    //TODO deal with tabs?
                    //Parse callbacks of the current node
                    activity.getLifecycleMethodsPreRun().forEach { lifecycle ->
                        logger.debug("Checking lifecycle $lifecycle")
                        if (recursiveCheck(lifecycle.method()) { match(it, criteriae) }) {
                            logger.debug("Found method of interest, marking node $node")
                            numFoundTargets += 1
                            marks[node] = Pair(true, "")
                            //marks.put(node, true)
                        }
                    }
                    //TODO: add some consistent filter so that only feasible callbacks are parsed for each node
                    val allCallbacks = activity.getCallbacks()
                    if(node is ScreenNode && node.hasTab()) //callbacks of content activity, add callbacks of parent activity as well
                        stg.getBaseScreenNodesByName(node.name).forEach { screen -> allCallbacks.addAll((screen.component as Activity).callbacks) }

                    activity.getCallbacks().filter { callback -> callback.getTargetMethod().hasActiveBody() }
                        .forEach { callback ->
                            logger.debug("Checking callback $callback")
                            //Get all nodes reachables through callbacks
                            if (memo.get(activity).contains(callback)) {
                                //callback invokes method of interest
                                logger.debug("Loading from memoized $callback ...")
                                val uiTrigger = getTriggerOfCallbackPrecise(callback, activity, criteriae) { match(it, criteriae) }
                                if (uiTrigger != null) {
                                    logger.debug("Found ui trigger $uiTrigger for $callback")
                                    if (!foundEdgeForTarget(
                                            stg.getEdgesWithSrcNode(node),
                                            callback,
                                            uiTrigger,
                                            marks
                                        )
                                    ) {
                                        numFoundTargets += 1
                                        //marks[node] = true //mark target node
                                        marks[node] = Pair(true, formatTrigger(callback, uiTrigger))
                                    }
                                }
                                //todo, add the action + trigger in the mark
                            } else if (recursiveCheck(callback.targetMethod) { match(it, criteriae) }) {
                                //TODO exclude lifecycle from callbacks?
                                logger.debug("Found method of interest in $callback ")
                                memo.put(activity, callback)
                                val uiTrigger = getTriggerOfCallbackPrecise(callback, activity, criteriae) { match(it, criteriae) }
                                if (uiTrigger != null) {
                                    logger.debug("Found ui trigger $uiTrigger")
                                    if (!foundEdgeForTarget(
                                            stg.getEdgesWithSrcNode(node),
                                            callback,
                                            uiTrigger,
                                            marks
                                        )
                                    ) {
                                        numFoundTargets += 1
                                        //marks[node] = true
                                        marks[node] = Pair(true, formatTrigger(callback, uiTrigger))
                                        logger.debug("Adding mark in stg ${marks[node]} for $node")
                                    }
                                }
                                //else if it's a callback, but no edge, mark the current screen and add the action to trigger
                            }
                        }
                }
            }
            logger.debug("Done with target marking, collected {} target statements", numFoundTargets)
            return marks;
        }

        /**
         * Checks if STG contains edge with desired action
         * @param edges the set of edges of the STG
         * @param callback to look for on edge
         * @param uiTrigger the id of the ui trigger
         * @param marks the target screens
         */
        private fun foundEdgeForTarget(
            edges: Set<TransitionEdge>,
            callback: AndroidCallbackDefinition,
            uiTrigger: UiElement,
            marks: MutableMap<AbstractNode, Pair<Boolean,String>>
        ): Boolean = //TODO change type
            edges.any { edge ->
                logger.debug("Checking edge $edge")
                if (isTriggerOfEdge(edge, callback, uiTrigger.globalId)) {
                    logger.debug("Marking target node ${edge.tgtNode}")
                    numFoundTargets += 1
                    //marks.put((edge.tgtNode as ScreenNode), true)
                    marks.put(edge.tgtNode as ScreenNode, Pair(true, formatTrigger(callback, uiTrigger)))
                    true
                } else false
            }

        /**
         * Converts pair callback, ui id to output
         */
        private fun formatTrigger(callback: AndroidCallbackDefinition, uiTrigger: UiElement): String =
            callback.parentMethod.subSignature + ";" + uiTrigger.globalId + ";" + uiTrigger.text["default_value"]


        /**
         * Checks if transition is started by given ui element and callback
         */
        private fun isTriggerOfEdge(edge: TransitionEdge, callback: AndroidCallbackDefinition, uiTrigger: Int): Boolean {
            val tag = edge.edgeTag
            val resId = tag.resId
            val handlerMethod = tag.handlerMethod
            val targetMethod = callback.targetMethod
            val parentMethod = callback.parentMethod
            logger.debug("Checking edge $handlerMethod with $targetMethod $parentMethod")
            return (parentMethod.equals(handlerMethod) || targetMethod.equals(handlerMethod)) && resId == uiTrigger
        }

        private fun <T: Any> getTriggerOfCallbackPrecise(
            callback: AndroidCallbackDefinition,
            activity: Activity,
            methods: Set<T>,
            match: (SootMethod) -> Boolean
        ): UiElement? {
            val potentialUiElements = activity.uiElements.filter { callback.targetMethod.subSignature.equals(it.handlerMethod.subSignature) }
            if (potentialUiElements.isEmpty())
                return CallbackWidgetProvider.v().findWidget(callback.parentMethod)
            if (potentialUiElements.size == 1)
                return potentialUiElements[0]
            val counter = AtomicInteger(0)
            return potentialUiElements.find {
                val elementId = if (it.hasIdInCode()) it.idInCode.toString() else it.elementId
                val apisFound = ArrayList<ApiInfoForForward>()
                val forwardFounder = CallbackToApiMapper(
                    it.handlerMethod, elementId, counter.getAndIncrement(), potentialUiElements.size,
                    maxMethodDepth, true, apisFound, false
                )
                val sourcesAndSinks: Set<String> = methods.map { it.toString() }.toSet()
                forwardFounder.setSourcesAndSinks(sourcesAndSinks)
                forwardFounder.call()
                logger.debug("All apis found {}", apisFound);
                apisFound.any { api -> api != null && match(api.api) }
            }
        }


        /**
         * Checks if the root method or any methods called recursively by the root method satisfied the criteria
         */
        private fun recursiveCheck(rootMethod: SootMethod, func: (SootMethod) -> Boolean): Boolean {
            try {
                val callGraph = Scene.v().callGraph

                // BFS to check if the method is reachable from the rootMethod
                val visited = HashSet<SootMethod>()
                val toCheck = LinkedList<SootMethod>()

                visited.add(rootMethod)
                toCheck.add(rootMethod)

                while (toCheck.isNotEmpty()) {
                    val cur = toCheck.poll()

                    //checked.add(cur)
                    logger.debug("Checking method ${cur.getSignature()}")
                    if (func(cur)) {
                        return true
                    }

                    for (edge in callGraph.edgesOutOf(cur)) {
                        val mCtxt = edge.tgt
                        logger.debug("Edge out of ${cur.name} ${mCtxt}")
                        //retrieve body?
                        if(mCtxt.method() != null && !visited.contains(mCtxt.method())){
                            if (func(mCtxt.method())){
                                return true
                            }
                            if(mCtxt.method().hasActiveBody()){//&& Helper.isClassInAppNameSpace(mCtxt.method().declaringClass.name)) {
                                //if (!checked.contains(edge.tgt()) && edge.tgt().hasActiveBody() && SystemClassHandler.isClassInSystemPackage(edge.tgt().declaringClass.name);
                                visited.add(mCtxt.method())
                                toCheck.add(mCtxt.method())
                            }
                        }
                    }
                }

                return false
            } catch (e: Exception) {
                return false
            }

        }

    }


}
