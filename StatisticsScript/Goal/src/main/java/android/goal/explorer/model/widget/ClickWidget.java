package android.goal.explorer.model.widget;

public class ClickWidget extends AbstractWidget {
    public enum EventType {
        Click, ContextClick, LongClick, Key, Touch, None
    }

    private EventType eventType = EventType.None;
    private String clickListener;

//    public ClickWidgetNode(int resourceId, String text) { super(resourceId, text); }
//
//    public ClickWidgetNode(int resourceId, String resString, String text) { super(resourceId,resString,text); }

    public ClickWidget(int resourceId, String text, EventType eventType, String clickListener) {
        super(resourceId, text);
        this.eventType = eventType;
        this.clickListener = clickListener;
    }

    public ClickWidget(int resourceId, String resString, String text, EventType eventType, String clickListener) {
        super(resourceId, resString, text);
        this.eventType = eventType;
        this.clickListener = clickListener;
    }

    public EventType getInputType() {
        return eventType;
    }

    public void setInputType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getClickListener() {
        return clickListener;
    }

    @Override
    public String toString(){
        return getResourceId() + "-" + clickListener;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((clickListener == null) ? 0 : clickListener.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        ClickWidget other = (ClickWidget) obj;

        if (eventType == null) {
            if (other.eventType != null)
                return false;
        } else if (!eventType.equals(other.eventType))
            return false;
        if (clickListener == null) {
            return other.clickListener == null;
        } else {
            return clickListener.equals(other.clickListener);
        }
    }
}
