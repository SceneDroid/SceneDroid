package android.goal.explorer.analysis;

import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.entity.Dialog;
import org.apache.commons.lang3.NotImplementedException;
import org.pmw.tinylog.Logger;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DialogFinder {
    private SootMethod sootMethod;
    private GlobalConfig config;
    private Set<List<Edge>> edges;
    private List<Unit> transactions;
    private Set<Dialog> dialogs;
    //private Map<Value, List<Unit>> transactionsToCommit;
    private int depth;


    public DialogFinder(GlobalConfig config, SootMethod tmpMethod, Set<List<Edge>> edges, int depth){
        this.sootMethod = tmpMethod;
        this.config = config;
        this.edges = edges;
        this.depth = depth;
    }
    public DialogFinder(GlobalConfig config, SootMethod tmpMethod, Set<List<Edge>> edges) {
        this(config, tmpMethod, edges, 1);
    }

    /**
     *  AlertDialog dialog = new AlertDialog.Builder(mContext)
     *             .setTitle(title)
     *             .setMessage(message)
     *             .setPositiveButton(buttonTxt, listener)
     *             .create();
     *             dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
     *             dialog.show();
     */


    public Set<Dialog> collectVisibleDialogs(){
        Logger.debug("Starting dialog collections on {}", sootMethod);
        //Iterate through all the units inside the method

        for(Unit unit: sootMethod.getActiveBody().getUnits()) {
            processStatement(unit);
        }
        Logger.debug("Done with dialog collections on {}", sootMethod);
        throw new NotImplementedException("Not implemented");
    }

    public void processStatement(Unit unit){
    }

    public List<List<Unit>> calculateDialogTransactions(){
        Logger.debug("Starting dialog builder analysis on {}", sootMethod);
        //Iterate through all the units inside the method
        //Need to keep a map of found values for dialog transactions, and add
        throw new NotImplementedException("Not implemented");
    }
}
