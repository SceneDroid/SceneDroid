package com.iccbot.withFrag;
import com.iccbot.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Source1WithFrag to Des1WithFrag
 *  * 
 */

public class Source1WithFrag extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){
			//Callback Entry of Component
			@Override
			public void onClick(View v){
				getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_one, new Frag1WithFrag())
				.commit(); //Fragment Loading
				}
			}
		);
	}
}
