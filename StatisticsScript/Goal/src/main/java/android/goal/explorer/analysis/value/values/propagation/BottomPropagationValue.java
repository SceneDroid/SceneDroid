package android.goal.explorer.analysis.value.values.propagation;

/**
 * A "bottom" COAL propagation value.
 */
public final class BottomPropagationValue implements BasePropagationValue {
  private static final BottomPropagationValue instance = new BottomPropagationValue();

  private BottomPropagationValue() {
  }

  /**
   * Returns the singleton instance for this class.
   * 
   * @return The singleton instance for this class.
   */
  public static BottomPropagationValue v() {
    return instance;
  }

  @Override
  public String toString() {
    return "bottom";
  }
}
