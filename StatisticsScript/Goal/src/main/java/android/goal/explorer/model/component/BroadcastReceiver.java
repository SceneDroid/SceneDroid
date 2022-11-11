package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.IntentFilter;
import android.goal.explorer.utils.AxmlUtils;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;

import java.util.Set;

public class BroadcastReceiver extends AbstractComponent {

    private Set<IntentFilter> intentFilters;

    public BroadcastReceiver(AXmlNode node, String packageName) {
        super(node, packageName);
        this.intentFilters = createIntentFilters(AxmlUtils.processIntentFilter(node, IntentFilter.Type.Action),
                AxmlUtils.processIntentFilter(node, IntentFilter.Type.Category));
    }

    public BroadcastReceiver(AXmlNode node, SootClass sc, String packageName) {
        super(node, sc, packageName);
        this.intentFilters = createIntentFilters(AxmlUtils.processIntentFilter(node, IntentFilter.Type.Action),
                AxmlUtils.processIntentFilter(node, IntentFilter.Type.Category));
    }

    /**
     * Gets the intent filters declared by this broadcast receiver
     * @return The set of IntentFilters
     */
    public Set<IntentFilter> getIntentFilters() {
        return intentFilters;
    }

    /**
     * Adds an intent filter to this broadcast receiver
     * @param intentFilter The intent filter to be added
     */
    public void addIntentFilter(IntentFilter intentFilter) {
        intentFilters.add(intentFilter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((intentFilters == null) ? 0 : intentFilters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() == obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        BroadcastReceiver other = (BroadcastReceiver) obj;

        if (this.getIntentFilters() == null) {
            return  other.getIntentFilters() == null;
        } else if (other.getIntentFilters() == null) {
            return false;
        } else {
            return this.getIntentFilters().equals(other.getIntentFilters());
        }
    }
}
