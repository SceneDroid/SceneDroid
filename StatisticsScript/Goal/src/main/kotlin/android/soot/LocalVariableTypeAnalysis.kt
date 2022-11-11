package android.soot

import soot.Local
import soot.Scene
import soot.Type

class LocalVariableTypeAnalysis {
    companion object {
        @JvmStatic
        fun calculatePossibleTypes(local: Local): Set<Type> {
            assert(Scene.v().hasCallGraph()) { "Scene call graph must be constructed first" }
            return Scene.v().pointsToAnalysis
                .reachingObjects(local)
                .possibleTypes()
        }
    }
}
