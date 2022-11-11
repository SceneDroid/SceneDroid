package android.goal.explorer.analysis.value.identifiers;

import java.io.Serializable;

/**
 * A method descriptor that includes the base class that declares this method and all the arguments
 * of interest. The purpose of declaring the base class is that we want to detect all overridden
 * versions of the method.
 */
public class MethodDescription implements Serializable {
    private static final long serialVersionUID = 1L;
    private String baseClass = null;
    private Argument[] arguments;

    public MethodDescription(String baseClass, Argument[] arguments) {
        this.baseClass = baseClass;
        this.arguments = arguments;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(baseClass);
        for (Argument argument : arguments) {
            result.append("    ").append(argument.toString());
        }

        return result.toString();
    }

}
