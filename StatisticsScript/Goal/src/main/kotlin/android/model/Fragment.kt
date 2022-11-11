package android.model

import soot.SootClass
import soot.SootMethod

class Fragment(
        sootClass: SootClass,
        lifecycleMethods: List<SootMethod>,
        layoutId: Int
) : LayoutComponent(sootClass, lifecycleMethods, layoutId)
