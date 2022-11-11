package android.goal.explorer.analysis.components;

import soot.SootMethod;

public class MenuVisibilityAnalyzer {
    private SootMethod sootMethod;
    public MenuVisibilityAnalyzer(SootMethod method){



    }

    public void getRegisteredMenus(){
        //if method is onCreateOptionsMenu
    }

    /**
     * Parses sootMethod for instance of methods opening menus e.g showOptionsMenu
     * @return name for the menu ?
     */
    public void getVisibleMenus(){


    }


    /**
     * Maps menu item to set of statements to analyze for fragment changes
     */
    public void /*Map<MenuItem, Set<Unit>>*/ parseMenuItems() {
        //need to parse xml for onClick registration
        //can also use addMenuItem method
        //last case if we are currently parsing onOptionsItemSelected

        //what do we need,
        //list of items (obtained from the xml)
        //need to map them to the screens that are created afterwards
        //this should use a switch statement analyzer, and return a map from elementId to desired output, searching for a specific function

        //transitions need the callback, the id and the destination
    }
}