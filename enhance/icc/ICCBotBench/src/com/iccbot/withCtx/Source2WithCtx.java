package com.iccbot.withCtx;
import com.iccbot.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Source2WithCtx to Des1WithCtx
 * Source2WithCtx to Des2WithCtx
 * 
 */
public class Source2WithCtx extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){
			//Callback Entry of Component
			@Override
			public void onClick(View v){
				if(getComponentName().equals("Main")){ 
		        	doWithAction("action.first.ctx");
		        }  
		        else{ 
		        	doWithAction("action.second.ctx");
		        }
			}
		});
	}
	
	  private void doWithAction(String mAction){
	        //Call Context Related ICC
	        Intent i = new Intent(mAction);
	        i.addCategory("category.ctx");
	        startActivity(i); 
	    }
}
