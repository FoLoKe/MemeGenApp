package com.foloke.memgenactivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.foloke.memgenactivity.Entities.Content;


import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import android.app.*;
import android.widget.*;
import android.view.View.*;
import android.graphics.*;
import android.view.*;
import org.springframework.http.client.*;
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
					case m.ACTION_UP:
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
					case event.ACTION_DOWN:
						//v.setBackgroundColor(Color.DKGRAY);
						break;
					case event.ACTION_UP:
						
						
						TextView textView = new TextView(context);
						textView.setText("updating");
						new HttpRequestTask(list).execute();
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

    private class HttpRequestTask extends AsyncTask<Void, Void, Content> {
		
		public LinearLayout contentList;
		
		public HttpRequestTask(LinearLayout contentList) {
			super();
			
			this.contentList = contentList;
		}
		
        @Override
        protected Content doInBackground(Void... params) {
            try {
                final String url = "http://31.42.45.42:10204/get?name=SUKA";
                MyRestTemplate restTemplate = new MyRestTemplate(1000);
				
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Content greeting = (Content)restTemplate.getForObject(url, Content.class);
                return greeting;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Content greeting) {
            
			View content = createContent(contentList.getContext());
			TextView nickName = ((TextView)content.findViewById(R.id.contentNickname));
			contentList.addView(content);
            if (greeting != null) {
				
                nickName.setText(greeting.getId() + greeting.getContent());
            } else {
                nickName.setText("error: host is unreachable");
                
            }
        }
		
		protected class MyRestTemplate extends RestTemplate {
			public MyRestTemplate(int timeout) {
				if (getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
					Log.d("HTTP", "HttpUrlConnection is used");
					((SimpleClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(timeout);
					((SimpleClientHttpRequestFactory) getRequestFactory()).setReadTimeout(timeout);
				} else if (getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
					Log.d("HTTP", "HttpClient is used");
					((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setReadTimeout(timeout);
					((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(timeout);
				}
			}
		}
		
    }
}
