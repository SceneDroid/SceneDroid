package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.node.BroadcastReceiverNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("BroadcastReceiverNode")
public class OutBroadcastReceiverNode extends OutAbstractNode {

    public OutBroadcastReceiverNode(BroadcastReceiverNode receiver) {
        super(receiver.getName());
    }

    /* ==================================================
                    Getters and setters
       ==================================================*/
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
