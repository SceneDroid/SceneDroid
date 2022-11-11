import android.analysis.*
import android.model.LayoutComponent
import android.model.Screen
import android.providers.ARSCInfoProvider
import org.slf4j.LoggerFactory
import soot.SootClass
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl

class ScreenTransitionGraphAnalysis(private val flowDroidAnalysis: SetupApplication) : Runnable {
    override fun run() {
        TODO("Not yet implemented")
    }
    /*lateinit var baseScreens: Map<SootClass, Set<Screen>>
    lateinit var intraActivityScreenTransitionsProvider: IntraActivityScreenTransitionsProvider
    lateinit var lifecycleScreenTransitionsProvider: LifecycleScreenTransitionsProvider
    /**
     * performs the analysis and stores the result
     */
    override fun run() {
        // get the screens for each activity when started
        // only dynamic fragments are contained within screens, add in static fragments as well
        val baseScreenProvider = BaseScreenProvider(flowDroidAnalysis)
        baseScreens = baseScreenProvider.baseScreens

        // find each widget associated with the callbacks
        val callbackWidgetProvider = CallbackWidgetProvider(flowDroidAnalysis)

        // collect fragment changes within an activity
        intraActivityScreenTransitionsProvider = IntraActivityScreenTransitionsProvider(flowDroidAnalysis, baseScreenProvider, callbackWidgetProvider)
        lifecycleScreenTransitionsProvider = LifecycleScreenTransitionsProvider(flowDroidAnalysis)
    }

   /* private fun <T: LayoutComponent> getStaticLayoutInfoForComponents(components: List<T>): Map<T, StaticLayoutInfo> {
        val androidResourceInfoProvider = ARSCInfoProvider(flowDroidAnalysis.)

        return components.map { component ->
            val layoutFileName = androidResourceInfoProvider.getFileNameForLayoutId(component.layoutId)
            component to flowDroidAnalysis.layoutFileParser.let { StaticLayoutInfo(
                it.fragments.get(layoutFileName),
                it.userControls.get(layoutFileName)
            ) }
        }.toMap()
    }*/

    /**
     * @param staticFragments fragments declared in XML layout and cannot be changed dynamically
     * @param layoutControls callbacks and other possible user controls present in XML layout
     */
    private data class StaticLayoutInfo(
        val staticFragments: Set<SootClass>,
        val layoutControls: Set<AndroidLayoutControl>
    )

    companion object {
        private val logger = LoggerFactory.getLogger(ScreenTransitionGraphAnalysis::class.java)
    }*/
}
