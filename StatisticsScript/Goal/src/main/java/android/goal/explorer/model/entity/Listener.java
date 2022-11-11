package android.goal.explorer.model.entity;

import soot.SootClass;
import soot.SootMethod;

import java.util.Set;

public class Listener {
    private SootClass sootClass;
    private String button;
    private Set<SootMethod> callbackMethods;

    public Listener(String button, Set<SootMethod> callbackMethods) {
        this.button = button;
        this.callbackMethods = callbackMethods;
    }

    public Listener(SootClass sootClass, String button, Set<SootMethod> callbackMethods) {
        this.sootClass = sootClass;
        this.button = button;
        this.callbackMethods = callbackMethods;
    }

    public String getButton(){
        return button;
    }

    public Set<SootMethod> getCallbackMethods(){
        return callbackMethods;
    }

    public SootClass getSootClass() {
        return sootClass;
    }
}
