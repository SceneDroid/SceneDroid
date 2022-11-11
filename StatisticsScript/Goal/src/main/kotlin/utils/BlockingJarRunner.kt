package utils

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Error

/**
 * Runs a jar file with the given arguments
 */
open class BlockingJarRunner(jarPath: String,
                             private var verbose: Boolean = false,
                             vararg args: String): Runnable {
    private val pb = ProcessBuilder("java", "-jar", jarPath, *args)

    override fun run() {
        try {
            val p = pb.start()
            val stdout = p.inputStream
            val reader = BufferedReader(InputStreamReader(stdout))

            while (true) {
                val line = reader.readLine() ?: break
                if (verbose) {
                    logger.info(line)
                }
            }
        } catch (e : Error) {
            logger.error(e.toString())
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BlockingJarRunner::class.java)
    }
}
