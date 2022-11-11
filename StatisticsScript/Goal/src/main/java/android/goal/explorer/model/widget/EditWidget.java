package android.goal.explorer.model.widget;

public class EditWidget extends AbstractWidget {
    // components classes and constructors
    private String contentDescription;
    private String hint;
    private int inputType;

    public EditWidget(int resourceId, String text) {
        super(resourceId, text);
    }

    public EditWidget(int resourceId, String text, String contentDescription, String hint, int inputType) {
        super(resourceId, text);
        this.contentDescription = contentDescription;
        this.hint = hint;
        this.inputType = inputType;
    }

    public EditWidget(int resourceId, String resourceIdString, String text, String contentDescription, String hint, int inputType) {
        super(resourceId, resourceIdString, text);
        this.contentDescription = contentDescription;
        this.hint = hint;
        this.inputType = inputType;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    @Override
    public String toString(){
        if (getText()!=null)
            return getResourceId() + "-" + getText();
        else
            return getResourceId() + "- EditText";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((hint == null) ? 0 : hint.hashCode());
        result = prime * result + ((contentDescription == null) ? 0 : contentDescription.hashCode());
        result = prime * result + inputType;
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

        EditWidget other = (EditWidget) obj;

        if (contentDescription == null) {
            if (other.contentDescription != null)
                return false;
        } else if (!contentDescription.equals(other.contentDescription))
            return false;
        if (hint == null) {
            if (other.hint != null)
                return false;
        } else if (!hint.equals(other.hint))
            return false;
        return inputType == other.inputType;
    }
}
