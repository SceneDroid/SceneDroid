package android.goal.explorer.model.entity;

import java.util.HashSet;
import java.util.Set;

public class Dialog extends AbstractEntity {

    private String parentActivity = "";
    private Set<Listener> posListeners = new HashSet<>();
    private Set<Listener> negListeners = new HashSet<>();
    private Set<Listener> neutralListeners = new HashSet<>();
    private Set<Listener> itemListeners = new HashSet<>();

    public Dialog() {
        super(Type.DIALOG);
    }

    @Override
    public AbstractEntity clone() {
        Dialog newEntity = new Dialog();
        newEntity.addPosListeners(posListeners);
        newEntity.addNegListeners(negListeners);
        newEntity.addItemListeners(itemListeners);
        return newEntity;
    }

    /*
    Getters and setters
     */

    /**
     * Gets the parant activity of this dialog
     * @return The parent activity of this dialog
     */
    public String getParentActivity() {
        return parentActivity;
    }

    /**
     * Sets the parent activity of the dialog
     * @param parentActivity The parent activity
     */
    public void setParentActivity(String parentActivity) {
        this.parentActivity = parentActivity;
    }

    /**
     * Gets the positive listeners
     * @return The positive listeners
     */
    public Set<Listener> getPosListeners() {
        return posListeners;
    }

    /**
     * Adds a positive listener
     * @param posListener The positive listener to be added
     */
    public void addPosListener(Listener posListener) {
        this.posListeners.add(posListener);
    }

    /**
     * Adds positive listeners
     * @param posListeners The positive listeners to be added
     */
    public void addPosListeners(Set<Listener> posListeners) {
        this.posListeners.addAll(posListeners);
    }

    /**
     * Gets the negative listeners
     * @return The negative listeners
     */
    public Set<Listener> getNegListeners() {
        return negListeners;
    }

    /**
     * Adds a negative listener
     * @param negListener The negative listener to be added
     */
    public void addNegListener(Listener negListener) {
        this.negListeners.add(negListener);
    }

    /**
     * Adds negative listeners
     * @param negListeners The negative listeners to be added
     */
    public void addNegListeners(Set<Listener> negListeners) {
        this.negListeners.addAll(negListeners);
    }

    /**
     * Gets the neutral listeners
     * @return The neutral listeners
     */
    public Set<Listener> getNeutralListeners() {
        return neutralListeners;
    }

    /**
     * Adds a neutral listener
     * @param neutralListener The negative listener to be added
     */
    public void addNeutralListener(Listener neutralListener) {
        this.neutralListeners.add(neutralListener);
    }

    /**
     * Adds neutral listeners
     * @param neutralListeners The negative listeners to be added
     */
    public void addNeutralListeners(Set<Listener> neutralListeners) {
        this.neutralListeners.addAll(neutralListeners);
    }

    /**
     * Gets the item listeners
     * @return The item listeners
     */
    public Set<Listener> getItemListeners() {
        return itemListeners;
    }

    /**
     * Adds an item listener
     * @param itemListener The item listener to be added
     */
    public void addItemListener(Listener itemListener) {
        this.itemListeners.add(itemListener);
    }

    /**
     * Adds item listeners
     * @param itemListeners The item listeners to be added
     */
    public void addItemListeners(Set<Listener> itemListeners) {
        this.itemListeners.addAll(itemListeners);
    }
}
