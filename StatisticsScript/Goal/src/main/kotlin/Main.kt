import android.goal.explorer.cmdline.CmdLineParser

import org.slf4j.LoggerFactory

// entrypoint to make our testing simpler
class Main {
    //private val flowDroidAnalysis: SetupApplication = SetupApplication(android)
    companion object {
         private val logger = LoggerFactory.getLogger(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val config = CmdLineParser.parse(args)

            //TODO, if xml file given as parameter, preanalysis runner loads the outstg in memory
             //and initialize flowdroid + widget provider

             //then do the analysis with the stg

            val preRunner = PreAnalysisRunner(config, CmdLineParser.parseArgForBackstage(config))
            preRunner.run()

            //Get targets to mark
            val targets = config.targets
            val type: String? = config.targetType

            logger.debug("Targets to check $targets")
            val runner = AnalysisRunner(preRunner, targets, type)
            runner.run()
        }
    }
}
