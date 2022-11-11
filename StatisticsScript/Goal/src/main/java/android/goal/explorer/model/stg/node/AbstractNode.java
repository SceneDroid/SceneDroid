package android.goal.explorer.model.stg.node;

import android.goal.explorer.model.component.AbstractComponent;
import org.pmw.tinylog.Logger;

public class AbstractNode {

    private AbstractComponent component;
    private String name;

    public AbstractNode(AbstractComponent component) {
        this.component = component;
        this.name = component.getName();
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

    /**
     * Gets the parse component of this node
     * @return The parse component
     */
    public AbstractComponent getComponent() {
        return this.component;
    }

    /**
     * Sets the parse component of this node
     * @param component The parse component
     */
    public void setComponent(AbstractComponent component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((component == null) ? 0 : component.hashCode());
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

        AbstractNode other = (AbstractNode) obj;
        if (component == null) {
            if (other.component != null)
                return false;
        } else if (!component.equals(other.component))
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public String toString() {
        return name;
    }
}
