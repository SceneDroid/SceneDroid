package android.goal.explorer.analysis.value.values.fields;

import android.goal.explorer.analysis.value.type.Internable;
import android.goal.explorer.analysis.value.type.Pool;

/**
 * A COAL field value. It is modeled as a set of objects that could be anything, depending on the
 * problem.
 */
public abstract class FieldValue implements Internable<FieldValue> {
    private static final Pool<FieldValue> POOL = new Pool<>();

    /**
     * Returns the value represented by this field value.
     *
     * @return The value represented by this field value.
     */
    public abstract Object getValue();

    /**
     * Determines if the field value makes a reference to another COAL value. In other words, this
     * determines if the field value is not completely resolved.
     *
     * @return True if the field value makes a reference to another COAL value.
     */
    public boolean hasTransformerSequence() {
        return false;
    }

    @Override
    public FieldValue intern() {
        return POOL.intern(this);
    }
}
