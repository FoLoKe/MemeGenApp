package com.foloke.memgenactivity;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foloke.memgenactivity.Entities.Image;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RatingUpTask extends AsyncTask<Void, Void, Boolean> {
    public int id;
    public TextView content;

    public RatingUpTask(TextView content, int id) {
        super();
        this.content = content;
        this.id = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            final String url = "http://31.42.45.42:10204/ratingup";
            MyRestTemplate restTemplate = new MyRestTemplate(1000);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Image image = new Image();
            image.setId(id);
            ResponseEntity<String> result = restTemplate.postForEntity(url, image, String.class);
            content.setText("" + result.getBody());
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean response) {

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
