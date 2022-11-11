package android.hierarchy

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONCREATE
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONDESTROY
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONPAUSE
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONRESTART
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONRESUME
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONSTART
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.ACTIVITY_ONSTOP
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONCREATE
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONCREATEVIEW
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONDESTROY
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONDESTROYVIEW
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONPAUSE
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONRESUME
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONSTART
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants.FRAGMENT_ONSTOP

class MethodSignatures {
    enum class ActivityLifecycleMethods(val sootMethodSubSignature: String) {
        ON_CREATE(ACTIVITY_ONCREATE),
        ON_START(ACTIVITY_ONSTART),
        ON_RESUME(ACTIVITY_ONRESUME),
        ON_PAUSE(ACTIVITY_ONPAUSE),
        ON_STOP(ACTIVITY_ONSTOP),
        ON_RESTART(ACTIVITY_ONRESTART),
        ON_DESTROY(ACTIVITY_ONDESTROY)
    }

    enum class FragmentLifecycleMethods(val sootMethodSubSignature: String) {
        ON_CREATE(FRAGMENT_ONCREATE),
        ON_CREATE_VIEW(FRAGMENT_ONCREATEVIEW),
        ON_START(FRAGMENT_ONSTART),
        ON_RESUME(FRAGMENT_ONRESUME),
        ON_PAUSE(FRAGMENT_ONPAUSE),
        ON_STOP(FRAGMENT_ONSTOP),
        ON_DESTROY_VIEW(FRAGMENT_ONDESTROYVIEW),
        ON_DESTROY(FRAGMENT_ONDESTROY)
    }

    enum class FragmentTransactionMethods(val sootMethodSubSignature: String) {
        REPLACE("android.support.v4.app.FragmentTransaction replace(int,android.support.v4.app.Fragment)"),
    }

    companion object {
        @JvmStatic
        fun getActivityLifecycleMethodSignatures() = ActivityLifecycleMethods.values().map { it.sootMethodSubSignature }

        @JvmStatic
        fun getFragmentLifecycleMethodSignatures() = FragmentLifecycleMethods.values().map { it.sootMethodSubSignature }
    }
}
