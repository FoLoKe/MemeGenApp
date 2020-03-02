package com.foloke.memgenactivity.Requests;

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
import com.foloke.memgenactivity.*;
import org.apache.http.HttpStatus;
import android.widget.*;
import org.springframework.http.*;



public class RatingRequestTask extends AsyncTask<Boolean, Void, Boolean> {
    public int id;
    public Content content;

    public RatingRequestTask(Content content, int id) {
        super();
        this.content = content;
        this.id = id;
    }

    @Override
    protected Boolean doInBackground(Boolean params[]) {
        try {
			boolean rating = params[0];
			String destination = "";
			
			if(rating) {
				destination = "/ratingUp";
			} else {
				destination = "ratingDown";
			}
				
            final String url = "http://31.42.45.42:10204" + destination;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
			
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
            Image image = new Image();
            image.setId(id);
			HttpEntity<Image> request = new HttpEntity<Image>(image, restTemplate.basicAuthHeader());
            ResponseEntity result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			
			if(result.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
				String response = (String)result.getBody();
				String[] values = response.split(" ");
				int actual = Integer.parseInt(values[0]);
				boolean posted = Boolean.parseBoolean(values[1]);
		
				if(rating) {
					content.setLikes(actual, posted);
				} else {
         	  	 	content.setDislikes(actual, posted);
				}
				return true;
			}
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean response) {
		if(!response){
			Toast.makeText(content.getContext(),"bad reaponse",Toast.LENGTH_SHORT);
		}
    }

    
}
