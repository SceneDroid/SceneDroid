package android.goal.explorer.model.stg.node;

import android.goal.explorer.model.component.AbstractComponent;
import android.goal.explorer.model.component.BroadcastReceiver;

public class BroadcastReceiverNode extends AbstractNode {

    public BroadcastReceiverNode(BroadcastReceiver receiver) {
        super(receiver);
    }

    /* ==================================================
                    Getters and setters
       ==================================================*/
    /**
     * Gets the broadcast receiver component
     * @return The broadcast receiver component
     */
    public AbstractComponent getReceiver() {
        return getComponent();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        BroadcastReceiverNode other = (BroadcastReceiverNode) obj;
        if (getComponent() == null) {
            return other.getComponent() == null;
        } else return getComponent().equals(other.getComponent());
    }
}
