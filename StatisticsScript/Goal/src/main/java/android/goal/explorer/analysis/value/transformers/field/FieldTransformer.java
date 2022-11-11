package android.goal.explorer.analysis.value.transformers.field;

import android.goal.explorer.analysis.value.type.Internable;
import android.goal.explorer.analysis.value.type.Pool;
import android.goal.explorer.analysis.value.values.fields.FieldValue;

/**
 * A field transformer, which models the influence of a statement of a single field.
 */
public abstract class FieldTransformer implements Internable<FieldTransformer> {
    private static final Pool<FieldTransformer> POOL = new Pool<>();

    /**
     * Applies this field transformer to a {@link FieldValue}.
     *
     * @param fieldValue A field value.
     * @return A field value.
     */
    public abstract FieldValue apply(FieldValue fieldValue);

    /**
     * Composes this field transformer with another one.
     *
     * @param secondFieldOperation A field transformer.
     * @return The result of composing this field transformer with the argument transformer.
     */
    public abstract FieldTransformer compose(FieldTransformer secondFieldOperation);

    @Override
    public FieldTransformer intern() {
        return POOL.intern(this);
    }
}
