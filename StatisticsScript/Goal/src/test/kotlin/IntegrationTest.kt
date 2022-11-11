import android.goal.explorer.cmdline.CmdLineParser
import android.goal.explorer.cmdline.CmdLineParser.parseArgForBackstage
import android.goal.explorer.cmdline.GlobalConfig
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import soot.Scene
import soot.baf.Inst
import soot.jimple.infoflow.android.SetupApplication
import st.cs.uni.saarland.de.testApps.Settings

class IntegrationTest {
    @Test
    fun endToEndTest() {
        val preRunner = PreAnalysisRunner(getGlobalConfig(), getBackstageSettings())
        preRunner.run()

        // get the API to target mark
        val urlClass = Scene.v().getSootClass("java.net.URL")
        val openConnectionMethods = urlClass.methods.filter { it.name == "openConnection" }.map { it.signature }.toSet()

        val runner = AnalysisRunner(preRunner, openConnectionMethods, "api")
        runner.run()

        logger.info("done")
    }

    private fun getGlobalConfig(): GlobalConfig {
        return CmdLineParser.parse(arrayOf(
            "ge",
            "--debug",
            "--input", APK_FILE_PATH,
            "--output", OUTPUT_PATH,
            "--sdk", ANDROID_SDK,
            "--api", API_LEVEL,
        ))
    }

    private fun getBackstageSettings(): Settings {
        return parseArgForBackstage(getGlobalConfig())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(IntegrationTest::class.java)

        private const val APK_FILE_PATH = "src/test/resources/unob_apks/app-debug.apk"
        private const val ANDROID_SDK = "D:\\libs\\.android\\sdk"
        private const val OUTPUT_PATH = "src/test/resources/output"
        private const val API_LEVEL = "30"
    }
}
