package android.goal.explorer.utils;

import java.security.Permission;

public class CustomSecurityManager extends SecurityManager{
    @Override
    public void checkExit(int status){
        throw new SecurityException();
    }

    @Override
    public void checkPermission(Permission permission){
        //Allow other activites by default
    }
}
