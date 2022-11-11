package android.goal.explorer.analysis.value.analysis.strings;

public interface switchLC {
    boolean setFieldMode(boolean mode);

    /***********************************************************/
    void caseTop(LanguageConstraints.Top lc);

    void caseBottom(LanguageConstraints.Bottom lc);

    void caseTerminal(LanguageConstraints.Terminal lc);

    void caseUnion(LanguageConstraints.Union lc);

    void caseConcatenate(LanguageConstraints.Concatenate lc);

    void caseEq(LanguageConstraints.Eq lc);

    void casePending(LanguageConstraints.Pending lc);

    void caseParameter(LanguageConstraints.Parameter lc);

    void caseCall(LanguageConstraints.Call lc);
}
