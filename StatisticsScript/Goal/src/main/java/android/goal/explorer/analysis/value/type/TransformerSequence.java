package android.goal.explorer.analysis.value.type;

import android.goal.explorer.analysis.value.analysis.solvers.PropagationSolver;
import android.goal.explorer.analysis.value.transformers.field.FieldTransformer;
import android.goal.explorer.analysis.value.transformers.field.IdentityFieldTransformer;
import soot.Value;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A sequence of {@link SequenceElement} that represents the influence of several consecutive COAL
 * value compositions.
 */
public class TransformerSequence {

    private List<SequenceElement> transformerSequence = null;

    public TransformerSequence() {
    }

    public TransformerSequence(TransformerSequence otherTransformerSequence) {
        this.transformerSequence = new ArrayList<>(otherTransformerSequence.transformerSequence);
    }

    public TransformerSequence(List<SequenceElement> otherTransformerSequence) {
        this.transformerSequence = new ArrayList<>(otherTransformerSequence);
    }

    /**
     * Adds a transformer to the sequence. The transformer is added to the last element.
     *
     * @param newFieldTransformer A field transformer.
     */
    public void addTransformerToSequence(FieldTransformer newFieldTransformer) {
        this.transformerSequence.get(this.transformerSequence.size() - 1).composeWith(
                newFieldTransformer);
    }

    /**
     * Concatenate another sequence to this sequence.
     *
     * @param transformerSequence A transformer sequence.
     */
    public void addElementsToSequence(TransformerSequence transformerSequence) {
        if (this.transformerSequence == null) {
            this.transformerSequence = new ArrayList<>();
        }
        this.transformerSequence.addAll(transformerSequence.transformerSequence);
    }

    /**
     * Adds an element to the sequence.
     *
     * @param symbol A symbol (variable) whose type is modeled with COAL.
     * @param stmt A modifier that references a variable modeled with COAL.
     * @param op An operation.
     */
    public void addElementToSequence(Value symbol, Stmt stmt, String op) {
        if (this.transformerSequence == null) {
            this.transformerSequence = new ArrayList<>();
        }
        this.transformerSequence.add(new SequenceElement(symbol, stmt, op));
    }

    /**
     * Generates the field transformers that represent the influence of this sequence.
     *
     * @param field A field name.
     * @param solver A propagation solver.
     * @return The set of field transformers that represent the influence of this sequence.
     */
    public Set<FieldTransformer> makeFinalFieldTransformers(String field, PropagationSolver solver) {
        Set<FieldTransformer> result =
                Collections.singleton((FieldTransformer) IdentityFieldTransformer.v());

        for (SequenceElement sequenceElement : transformerSequence) {
            result = composeFieldTransformerSets(result, sequenceElement.makeFinalTransformers(field, solver));
        }

        return result;
    }

    /**
     * Composes two sets of field transformers.
     *
     * @param fieldTransformers1 A set of field transformers.
     * @param fieldTransformers2 Another set of field transformers.
     * @return The result of the composition.
     */
    private Set<FieldTransformer> composeFieldTransformerSets(
            Set<FieldTransformer> fieldTransformers1, Set<FieldTransformer> fieldTransformers2) {
        Set<FieldTransformer> result = new HashSet<>();
        for (FieldTransformer fieldTransformer1 : fieldTransformers1) {
            for (FieldTransformer fieldTransformer2 : fieldTransformers2) {
                if (fieldTransformer1 != null && fieldTransformer2 != null) {
                    result.add(fieldTransformer1.compose(fieldTransformer2));
                } else {
                    result.add(null);
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return this.transformerSequence.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.transformerSequence);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TransformerSequence)) {
            return false;
        }
        TransformerSequence secondTransformerSequence = (TransformerSequence) other;
        return Objects.equals(this.transformerSequence, secondTransformerSequence.transformerSequence);
    }
}
