package com.foloke.memgenactivity.Requests;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foloke.memgenactivity.Entities.Meme;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.foloke.memgenactivity.*;
import org.apache.http.HttpStatus;
import android.widget.*;
import org.springframework.http.*;
import com.foloke.memgenactivity.Entities.*;



public class RatingRequestTask extends AsyncTask<String, Void, MemeInfo> {
    
	private RestController restController;
	
	
    public RatingRequestTask(RestController parent) {
        super();
        
		this.restController = parent;
    }

    @Override
    protected MemeInfo doInBackground(String params[]) {
        try {
			int id = Integer.parseInt(params[0]);
			boolean type = Boolean.parseBoolean(params[1]);
			
			String destination = "/postRating?id=" + id + "&type=" + type;
				
            final String url = "http://31.42.45.42:10204" + destination;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
			
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
            Meme image = new Meme();
            image.setId(id);
			HttpEntity<String> request = new HttpEntity<String>(restTemplate.basicAuthHeader());
            ResponseEntity<MemeInfo> result = restTemplate.exchange(url, HttpMethod.POST, request, MemeInfo.class);
			
			
			
			if(result.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
				MemeInfo response = result.getBody();
				
				return response;
			}
            return null;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(MemeInfo info) {
		restController.updateRating(info);
    }

    
}
