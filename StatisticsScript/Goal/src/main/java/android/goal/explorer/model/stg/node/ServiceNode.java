package android.goal.explorer.model.stg.node;

import android.goal.explorer.model.component.AbstractComponent;
import android.goal.explorer.model.component.Service;

public class ServiceNode extends AbstractNode {

    public ServiceNode(Service service) {
        super(service);
    }

    /* ==================================================
                    Getters and setters
       ==================================================*/
    /**
     * Gets the service component
     * @return The service component
     */
    public AbstractComponent getService() {
        return getComponent();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
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

        ServiceNode other = (ServiceNode) obj;
        if (getComponent() == null) {
            return other.getComponent() == null;
        } else return getComponent().equals(other.getComponent());
    }
}
