package com.iccbot.withFrag;
import com.iccbot.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Frag2WithFrag extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	  Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment1, container, false);
		
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Intent i = new Intent(getActivity().getBaseContext(), Des2WithFrag.class );
        getActivity().startActivity(i); 
	}
}



