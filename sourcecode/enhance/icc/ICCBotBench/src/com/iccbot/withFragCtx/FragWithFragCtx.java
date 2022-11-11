package com.iccbot.withFragCtx;

import android.app.Activity;
import android.content.Intent;

import android.support.v4.app.Fragment;

public class FragWithFragCtx extends Fragment {
    @Override
    public void onAttach(Activity activity) {
        //Callback Entry of Fragment
    	
        if(getActivity().getComponentName().equals("Main")){ 
        	doWithAction("action.first.fragctx");
        }  
        else{ 
        	doWithAction("action.second.fragctx");
        }
    }
    
    private void doWithAction(String mAction){
        //Call Context Related ICC
        Intent i = new Intent(mAction);
        addCategory(i);
        getActivity().startActivity(i); 
    }
    
    private void addCategory(Intent i){
        i.addCategory("category.fragctx");
    }
}
