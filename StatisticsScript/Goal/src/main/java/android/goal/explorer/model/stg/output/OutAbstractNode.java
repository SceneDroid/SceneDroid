package android.goal.explorer.model.stg.output;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Node")
public class OutAbstractNode {
    private String name;
    protected boolean target = false;
    protected OutTargetActionTag targetAction;

    public OutAbstractNode(String name) {
        this.name = name;
    }

    public OutAbstractNode(String name, boolean target, OutTargetActionTag targetAction){
        this.name = name;
        this.target = target;
        this.targetAction = targetAction;
    }

    /* =======================================
              Getters and setters
     =========================================*/
    /**
     * Gets the name of this component
     * @return The name of this component
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component
     * @param name The component name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        OutAbstractNode other = (OutAbstractNode) obj;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public String toString() {
        return name;
    }
}
