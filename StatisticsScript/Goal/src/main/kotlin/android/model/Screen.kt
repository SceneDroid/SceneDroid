package android.model

import soot.SootClass

data class Screen(
    /**
     * Activity running on the screen
     */
    val activity: SootClass,
    /**
     * The resource ID and the fragment contained at resource ID
     */
    val dynamicFragments: Map<Int, SootClass>,
    /**
     * Classes of static fragments associated with screen
     */
    val staticFragments: Set<SootClass> = setOf()
)
