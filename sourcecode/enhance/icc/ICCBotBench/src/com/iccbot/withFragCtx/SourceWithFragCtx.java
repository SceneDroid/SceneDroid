package com.iccbot.withFragCtx;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.iccbot.R;


/**
 * SourceWithFragCtx to Des1WithFrag
 * SourceWithFragCtx to Des2WithFrag
 */
public class SourceWithFragCtx extends ActionBarActivity {
	
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
				.add(R.id.fragment_one, new FragWithFragCtx())
				.commit(); //Fragment Loading
				}
			}
		);
	}
}
