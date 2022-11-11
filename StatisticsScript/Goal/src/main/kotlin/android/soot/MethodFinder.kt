package android.soot

import soot.Scene
import soot.SootClass
import soot.SootMethod

class MethodFinder {
    companion object {
        /**
         * Finds the implementation for an existing method of a SootClass.
         * Will traverse the class hierarchy until an implementation is found.
         *
         * @param sootClass [SootClass] class to find method implementation for
         * @param methodSignature [String] method signature to find implementation of
         * @return [SootMethod] containing implementation of method
         *
         * @throws NoSuchElementException when no [SootMethod] for implementation is found
         */
        fun findMethodImplementation(sootClass: SootClass, methodSignature: String): SootMethod {
            val superClasses = Scene.v().activeHierarchy.getSuperclassesOfIncluding(sootClass)
            superClasses.forEach {
                val methodImpl = it.getMethodUnsafe(methodSignature)
                if (methodImpl != null) return methodImpl
            }

            throw NoSuchElementException(
                "Cannot find the SootMethod that provides a implementation for function " +
                    "$methodSignature in ${sootClass.name}"
            )
        }
    }
}
