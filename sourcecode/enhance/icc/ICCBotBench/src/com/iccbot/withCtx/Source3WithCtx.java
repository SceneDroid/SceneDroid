package com.iccbot.withCtx;
import com.iccbot.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Source3WithCtx to Des1WithCtx
 * 
 */
public class Source3WithCtx extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){
			//Callback Entry of Component
			@Override
			public void onClick(View v){
				Intent i = new Intent();
				i.setAction("action.first.ctx");
		        i.addCategory("category.ctx");
		        Utils.startIntent(getBaseContext(), i);
			}
		});
	}
}
