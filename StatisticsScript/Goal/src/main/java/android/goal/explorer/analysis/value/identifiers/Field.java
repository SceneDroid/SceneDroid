package android.goal.explorer.analysis.value.identifiers;

import java.io.Serializable;

/**
 * The representation of a field, which includes a name and a type.
 */
public class Field implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String type;

    /**
     * Constructor.
     *
     * @param name The field name.
     * @param type The field type, which should be one of the types registered using
     */
    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
