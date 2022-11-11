package android.goal.explorer.analysis.value.identifiers;

import soot.Unit;

import java.util.Objects;

/**
 * An identifier for an argument value. It is composed of a program statement and an
 * {@link Argument}.
 */
public class ArgumentValueIdentifier {
    private final Unit stmt;
    private final Argument argument;

    public ArgumentValueIdentifier(Unit stmt, Argument argument) {
        this.stmt = stmt;
        this.argument = argument;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stmt, this.argument);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ArgumentValueIdentifier) {
            ArgumentValueIdentifier argumentValueIdentifier = (ArgumentValueIdentifier) other;
            return Objects.equals(this.stmt, argumentValueIdentifier.stmt)
                    && Objects.equals(this.argument, argumentValueIdentifier.argument);
        }
        return false;
    }
}
