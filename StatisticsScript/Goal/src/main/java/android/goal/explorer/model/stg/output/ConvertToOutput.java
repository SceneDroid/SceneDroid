package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.node.AbstractNode;
import android.goal.explorer.model.stg.node.BroadcastReceiverNode;
import android.goal.explorer.model.stg.node.ScreenNode;
import android.goal.explorer.model.stg.node.ServiceNode;

public class ConvertToOutput {
    public static OutAbstractNode convertNode(AbstractNode abstractNode) {
        if (abstractNode instanceof ScreenNode) {
            return new OutScreenNode((ScreenNode) abstractNode);
        } else if (abstractNode instanceof ServiceNode) {
            return new OutServiceNode((ServiceNode) abstractNode);
        } else if (abstractNode instanceof BroadcastReceiverNode) {
            return new OutBroadcastReceiverNode((BroadcastReceiverNode) abstractNode);
        }
        return new OutAbstractNode(abstractNode.getName());
    }
}
