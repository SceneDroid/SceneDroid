package android.goal.explorer.analysis.value.transformers.field;


import android.goal.explorer.analysis.value.values.fields.FieldValue;

/**
 * The identity field transformer, which does not modify anything.
 */
public class IdentityFieldTransformer extends FieldTransformer{
    private static final IdentityFieldTransformer instance = new IdentityFieldTransformer();

    private IdentityFieldTransformer() {
    }

    @Override
    public FieldValue apply(FieldValue fieldValue) {
        return fieldValue;
    }

    @Override
    public FieldTransformer compose(FieldTransformer secondFieldOperation) {
        return secondFieldOperation;
    }

    @Override
    public String toString() {
        return "identity";
    }

    public static IdentityFieldTransformer v() {
        return instance;
    }
}
