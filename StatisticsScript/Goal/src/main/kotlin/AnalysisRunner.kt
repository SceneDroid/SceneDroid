import android.goal.explorer.SaveData
import android.goal.explorer.model.stg.output.OutSTG

class AnalysisRunner(private val preAnalysisRunner: PreAnalysisRunner, private val targets: Set<String>, private val targetType: String?): Runnable {
    override fun run() {
        // get the constructed STG
        val stg = preAnalysisRunner.stgExtractor.stg

        //TODO retrieve active bodies

        // mark the target screen nodes that can call the passed methods
        val marks = TargetMarker.markNodesByCriteria(stg, targets, targetType)

        val outSTG = OutSTG(stg, marks)

        // print the results to XML file
        val saveData = SaveData(outSTG, preAnalysisRunner.config)
        saveData.saveSTG()
    }
}
