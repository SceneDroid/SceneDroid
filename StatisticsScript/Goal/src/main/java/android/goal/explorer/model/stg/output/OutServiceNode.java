package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.node.ServiceNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ServiceNode")
public class OutServiceNode extends OutAbstractNode {

    public OutServiceNode(ServiceNode service) {
        super(service.getName());
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
