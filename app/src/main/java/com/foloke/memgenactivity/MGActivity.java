package com.foloke.memgenactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.app.*;
import android.widget.*;
import android.view.View.*;
import android.graphics.*;
import android.view.*;
import android.content.*;


public class MGActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		final LinearLayout list = findViewById(R.id.content);
		final Activity context = this;
		
		View content = LayoutInflater.from(this).inflate(R.layout.content, null);
		list.addView(content);

		final ScrollView scroll = findViewById(R.id.mainScrollView);
		
		
		
		scroll.setOnScrollChangeListener(new OnScrollChangeListener() {
			@Override
			public void onScrollChange(View v, int x, int y, int ox, int oy) {
				View view = scroll.getChildAt(scroll.getChildCount() - 1);
				int diff = (view.getBottom() - (scroll.getHeight() + scroll.getScrollY()));

				// if diff is zero, then the bottom has been reached
				if (diff == 0) {
					// do stuff
					getRequest(context, list);
				}
			}
		});
		
		
		scroll.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//v.setBackgroundColor(Color.DKGRAY);
						break;
					case MotionEvent.ACTION_UP:
						
						
						
						break;
				}
				return false;
			}
		});
		
		final ViewFlipper viewFlipper = findViewById(R.id.mainViewFlipper);
		
		Button menuLentButton = (Button)findViewById(R.id.menuLentButton);
		
		menuLentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewFlipper.setDisplayedChild(1);
				v.setBackgroundColor(Color.BLACK);
				
			}
		});
		
		Button menuEditorButton = (Button)findViewById(R.id.menuEditorButton);

		menuEditorButton.setOnClickListener(new View.OnClickListener() {
			@Override
				public void onClick(View v) {
					viewFlipper.setDisplayedChild(2);
					
				}
				
			});
			
		Button menuUploadButton = findViewById(R.id.menuUploadButton);

		menuUploadButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					viewFlipper.setDisplayedChild(3);
				}
			});
		
		getRequest(this, list);
		
		

    }

  
    @Override
    protected void onStart() {
        super.onStart();
		
    }
	
	public static View createContent(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.content, null);
	} 
	
	public static void getRequest(Context context, LinearLayout container) {
		Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();

		new RequestTask().execute(container);
	}


}
