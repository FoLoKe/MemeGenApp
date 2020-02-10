package com.foloke.memgenactivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.foloke.memgenactivity.Entities.Greeting;


import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import android.app.*;

public class ScrollingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		new HttpRequestTask().execute();
        
    }

  
    @Override
    protected void onStart() {
        super.onStart();
		
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Greeting> {
        @Override
        protected Greeting doInBackground(Void... params) {
            try {
                final String url = "http://31.42.45.42:10204/greeting?name=SUKA";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Greeting greeting = (Greeting)restTemplate.getForObject(url, Greeting.class);
                return greeting;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Greeting greeting) {
            TextView greetingIdText = (TextView) findViewById(R.id.one);
            TextView greetingContentText = (TextView) findViewById(R.id.two);
            if (greeting != null) {

                greetingIdText.setText(greeting.getId());
                greetingContentText.setText(greeting.getContent());
            } else {
                greetingIdText.setText("error: ");
                greetingContentText.setText("host is unreachable");
            }
        }

    }
}
