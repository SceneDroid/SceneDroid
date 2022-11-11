package android.hierarchy

import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants

class BaseClassNames {
    enum class BaseActivity(val className: String) {
        LEGACY(AndroidEntryPointConstants.ACTIVITYCLASS),
        COMPATIBILITY_SUPPORT_V4(AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V4),
        COMPATIBILITY_SUPPORT_V7(AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V7),
    }

    enum class BaseFragment(val className: String) {
        LEGACY(AndroidEntryPointConstants.FRAGMENTCLASS),
        COMPATIBILITY_SUPPORT_V4(AndroidEntryPointConstants.SUPPORTFRAGMENTCLASS),
    }

    companion object {
        @JvmStatic
        fun getBaseActivityClassNames() = BaseActivity.values().map { it.className }

        @JvmStatic
        fun isBaseActivityClassName(className: String) = BaseActivity.values().any { it.className == className }

        @JvmStatic
        fun getBaseFragmentClassNames() = BaseFragment.values().map { it.className }

        @JvmStatic
        fun isBaseFragmentClassName(className: String) = BaseFragment.values().any { it.className == className }
    }
}