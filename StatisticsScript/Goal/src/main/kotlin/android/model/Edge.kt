package android.model

import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl

data class Edge(
    /**
     * Source screen - application on this screen before callback is invoked
     */
    val src: Screen,
    /**
     * Target screen - application on this screen after callback is invoked
     */
    val tgt: Screen,
    /**
     * Callback responsible for screen transition
     */
    val callback: AndroidCallbackDefinition,
    /**
     * The widget that is responsible for the callback
     */
    val widget: AndroidLayoutControl?
)
