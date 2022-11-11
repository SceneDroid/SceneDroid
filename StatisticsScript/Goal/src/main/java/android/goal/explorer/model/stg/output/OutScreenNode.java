package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.stg.node.ScreenNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import st.cs.uni.saarland.de.entities.Dialog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@XStreamAlias("ScreenNode")
public class OutScreenNode extends OutAbstractNode {

    private Set<String> fragments;
    private String menu, contextMenu;
    private String tab;

    @XStreamAlias("drawer")
    private String drawerMenu; //maybe make an out object for drawers

    private Set<String> dialogs;
    boolean baseScreen = false;

    public OutScreenNode(ScreenNode screenNode) {
        super(screenNode.getName());
        Set<String> fragmentStrings = new HashSet<>();
        Set<String> dialogStrings = new HashSet<>();
        for (Fragment fragment : screenNode.getFragments()) {
            fragmentStrings.add(fragment.getName());
        }
        for (Dialog dialog : screenNode.getDialogs()) {
            dialogStrings.add(dialog.getTitleText());
        }
        if (screenNode.getVisibleMenu() == null) {
            if(screenNode.getVisibleContextMenu() == null) {
                menu = null;
                contextMenu = null;
            }
            else contextMenu = screenNode.getVisibleContextMenu().getName();
        } else {
            menu = screenNode.getVisibleMenu().getName();
        }
        if (screenNode.getDrawer() == null) {
            drawerMenu = null;
        }
        else {
            drawerMenu = screenNode.getDrawer().getMenu().getName();
        }
        baseScreen = screenNode.isBaseScreenNode();
        if (screenNode.getTab() == null) {
            tab = null;
        } else {
            tab = screenNode.getTab().getContentActivityName();
        }
        dialogs = dialogStrings;
        fragments = fragmentStrings;
    }

    public String getMenu() {
        return menu;
    }

    public String getDrawerMenu() {
        return drawerMenu;
    }

    public String getContextMenu() {return contextMenu; }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String toString(){
        return getName() + " fragments: " + fragments + " menu: " + menu + " drawer: "+ drawerMenu + " dialogs" + dialogs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fragments == null) ? 0 : fragments.hashCode());
        result = prime * result + ((menu == null) ? 0 : menu.hashCode());
        result = prime * result + ((contextMenu == null) ? 0 : contextMenu.hashCode());
        result = prime * result + ((drawerMenu == null) ? 0 : drawerMenu.hashCode());
        result = prime * result + ((dialogs.isEmpty()) ? 0 : dialogs.hashCode());
        result = prime * result + ((tab == null) ? 0 : tab.hashCode());
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

        OutScreenNode other = (OutScreenNode) obj;

        if(!baseScreen == other.baseScreen)
            return false;
        if (fragments == null) {
            if (other.fragments != null)
                return false;
        } else if (!fragments.equals(other.fragments))
            return false;

        if (tab == null) {
            if(other.tab != null)
                return false;
        } else if (!tab.equals(other.tab))
            return false;
        if(dialogs == null){
            if(other.dialogs != null)
                return false;
        }
        if(!dialogs.equals(other.dialogs))
            return false;
        if (drawerMenu == null){
            if (other.drawerMenu != null)
                return false;
        }
        else if (!drawerMenu.equals(other.drawerMenu))
            return false;
        if(contextMenu == null) {
            if (other.contextMenu != null)
                return false;
        }
        else if(!contextMenu.equals(other.contextMenu))
            return false;

        if (menu == null) {
            return other.menu == null;
        } else return menu.equals(other.menu);
    }
}
