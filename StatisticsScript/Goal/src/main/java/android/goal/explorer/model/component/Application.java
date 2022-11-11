package android.goal.explorer.model.component;

import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;

public class Application extends AbstractComponent {
    public Application(AXmlNode node, SootClass sc, String packageName) {
        super(node, sc, packageName);
    }

    public Application(String name, SootClass sc, String packageName) { super(name, sc, packageName); }
}
