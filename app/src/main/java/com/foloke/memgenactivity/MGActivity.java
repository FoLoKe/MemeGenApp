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
		
		for(int i=0; i<1; i++) {
		
			View contentBox = LayoutInflater.from(this).inflate(R.layout.content, null);
			ImageView content = contentBox.findViewById(R.id.contentImage);
			Bitmap b = BitmapFactory.decodeResource( getResources(),R.drawable.no_image);
			content.setImageBitmap(b);
			
			list.addView(contentBox);
			
		}

		ScrollView scroll = findViewById(R.id.mainScrollView);
		
		scroll.setOnTouchListener( new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent m){
				switch(m.getAction()) {
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(Color.RED);
						ScrollView sv = (ScrollView)v;
						if(sv.getScrollY() ==sv.getHeight()) {
							sv.setBackgroundColor(Color.BLACK);
						}
				}
				return false;
			}
		});
		
		scroll.setOnScrollChangeListener(new OnScrollChangeListener() {
			@Override
			public void onScrollChange(View v, int x, int y, int ox, int oy) {
				float diff = oy - y;
				if(diff == 0) { 
					//v.setBackgroundColor(Color.BLACK);
				} else {
					//v.setBackgroundColor(Color.WHITE);
				}
			}
		});
		
		final Activity context = this;
		scroll.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event){
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//v.setBackgroundColor(Color.DKGRAY);
						break;
					case MotionEvent.ACTION_UP:
						
						
						TextView textView = new TextView(context);
						textView.setText("updating");
						new RequestTask(list).execute();
						list.addView(textView);
						break;
				}
				return false;
			}
		});

    }

  
    @Override
    protected void onStart() {
        super.onStart();
		
    }
	
	public static View createContent(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.content, null);
	} 


}
