package android.providers

import org.slf4j.LoggerFactory
import soot.jimple.infoflow.android.resources.ARSCFileParser

/**
 * Android resources are assigned to a global id for an app.
 * This class can translate between IDs and values which are represented by "R" class.
 *
 * https://developer.android.com/guide/topics/resources/available-resources
 * https://stackoverflow.com/questions/6804053/understand-the-r-class-in-android
 *
 * This reflects the usage of the "R" class where a typical usage such as
 * R.layout.some_layout is mapped to R.{resourceType}.{resourceName}.
 */
class ARSCInfoProvider(private val resourceFileParser: ARSCFileParser) {
    /**
     * This documents some internal information about the ARSCFileParser
     *
     * anim -> represented by StringResource
     *      value = "res/anim/{resourceName}.xml"
     *
     * attr -> represented by ComplexResource
     *
     * bool -> represented by BooleanResource
     *
     * color -> represented by ReferenceResource, ColorResource, StringResource
     *
     * dimen -> represented by DimensionResource, FloatResource, ReferenceResource, FractionResource
     *
     * drawable -> represented by StringResource
     *      value = "res/drawable/{filepath + extension}"
     *
     * id -> represented by BooleanResource
     *      value = false
     *
     * integer -> represented by IntegerResource
     *
     * layout -> represented by StringResource
     *      value = "res/layout/{resourceName}.xml"
     *
     * mipmap -> represented by StringResource
     *      value = "res/mipmap ... /{resourceName}.*
     *
     * string -> represented by StringResource
     *
     * style -> represented by complex Resource
     */

    /**
     * @return id represented by R.layout.[resourceName]
     */
    fun getIdForLayout(resourceName: String) =
        resourceFileParser.findResourceByName("layout", resourceName).resourceID

    /**
     * @return the file associated with the layoutId (e.g. "/res/layout/activity_main.xml")
     */
    fun getFileNameForLayoutId(layoutId: Int): String {
        val candidates = resourceFileParser.findAllResources(layoutId)

        if (candidates.size > 1) {
            logger.warn("Multiple candidates for layout_id: $layoutId")
            logger.warn("Candidates: $candidates")
        }

        return (candidates.first() as ARSCFileParser.StringResource).value
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ARSCInfoProvider::class.java)
    }
}
