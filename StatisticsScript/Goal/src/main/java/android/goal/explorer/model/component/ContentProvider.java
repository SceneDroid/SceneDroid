package android.goal.explorer.model.component;

import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;

import static android.goal.explorer.utils.AxmlUtils.processAuthorities;

public class ContentProvider extends AbstractComponent {
    private String authorities;

    public ContentProvider(AXmlNode node, String packageName) {
        super(node, packageName);
        this.authorities = processAuthorities(node);
    }

    public ContentProvider(AXmlNode node, SootClass sc, String packageName) {
        super(node, sc, packageName);
        this.authorities = processAuthorities(node);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        ContentProvider other = (ContentProvider) obj;

        if (authorities == null) {
            return  other.authorities == null;
        } else if (other.authorities == null) {
            return false;
        } else {
            return authorities.equals(other.authorities);
        }
    }
}
