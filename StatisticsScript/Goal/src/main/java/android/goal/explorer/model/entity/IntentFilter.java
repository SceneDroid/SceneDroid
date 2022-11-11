package android.goal.explorer.model.entity;

public class IntentFilter {

    public enum Type {
        Action, Category
    }

    private String string;
    private Type type;

    public IntentFilter(String string, Type type) {
        this.string = string;
        this.type = type;
    }

    public String getString() {
        return string;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString(){
        String typeString;
        switch (type) {
            case Category: typeString = "category";
            case Action: typeString = "action";
            default: typeString = "other";
        }
        return typeString + ": " + string;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((string == null) ? 0 : string.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        IntentFilter other = (IntentFilter) obj;

        if (string == null) {
            if (other.string != null)
                return false;
        } else if (!string.equals(other.string))
            return false;
        if (type == null) {
            return other.type == null;
        } else return type.equals(other.type);
    }
}
