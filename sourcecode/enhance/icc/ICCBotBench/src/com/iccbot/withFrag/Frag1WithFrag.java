package com.iccbot.withFrag;

import android.app.Activity;
import android.content.Intent;

import android.support.v4.app.Fragment;

public class Frag1WithFrag extends Fragment {
    @Override
    public void onAttach(Activity activity) {
        //Callback Entry of Fragment
    	Intent i = new Intent(getActivity().getBaseContext(), Des1WithFrag.class );
        getActivity().startActivity(i); 
    }
}
