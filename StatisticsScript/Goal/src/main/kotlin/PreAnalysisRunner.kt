import android.goal.explorer.STGExtractor
import android.goal.explorer.cmdline.GlobalConfig
import org.apache.log4j.Logger
import st.cs.uni.saarland.de.testApps.Settings

class PreAnalysisRunner(val config: GlobalConfig, private val settings: Settings): Runnable {
    lateinit var stgExtractor: STGExtractor

    override fun run() {
        logger.info("Running old goal-explorer apk data collection involving backstage")
        if (!this::stgExtractor.isInitialized) {
            stgExtractor = STGExtractor(config, settings)
            
            stgExtractor.constructSTG()
        }
        logger.info("Done with STG construction")
    }

    fun runAndGetResult(): STGExtractor {
        run()
        return stgExtractor
    }

    companion object {
        private val logger = Logger.getLogger(PreAnalysisRunner::class.java)
    }
}
