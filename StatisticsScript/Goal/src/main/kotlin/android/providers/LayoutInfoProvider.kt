package android.providers

import org.slf4j.LoggerFactory
import soot.jimple.infoflow.android.resources.LayoutFileParser

/**
 * Provides info about elements statically declared within res/layout files
 */
class LayoutInfoProvider(private val layoutFileParser: LayoutFileParser) {

    init {
        layoutFileParser.userControlsByID
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LayoutInfoProvider::class.java)
    }
}
