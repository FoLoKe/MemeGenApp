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



public class RatingRequestTask extends AsyncTask<String, Void, Meme> {
    
	private RestController restController;
	private boolean type;
	private boolean posted;
	
    public RatingRequestTask(RestController parent) {
        super();
        
		this.restController = parent;
    }

    @Override
    protected Meme doInBackground(String params[]) {
        try {
			int id = Integer.parseInt(params[0]);
			type = Boolean.parseBoolean(params[1]);
			
			String destination = "/postRating?id=" + id + "&type=" + type;
				
            final String url = "http://31.42.45.42:10204" + destination;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
			
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
            Meme image = new Meme();
            image.setId(id);
			HttpEntity<Meme> request = new HttpEntity<Meme>(image, restTemplate.basicAuthHeader());
            ResponseEntity<Object[]> result = restTemplate.exchange(url, HttpMethod.POST, request, Object[].class);
			
			
			
			if(result.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
				Object[] response = result.getBody();
				posted = Boolean.parseBoolean((String)response[0]);
				return (Meme)response[1];
			}
            return null;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Meme meme) {
		if(meme != null) {
			restController.updateRating(meme,type, posted);
		} else {
			restController.updateRating(null, false, false);
		}
    }

    
}
