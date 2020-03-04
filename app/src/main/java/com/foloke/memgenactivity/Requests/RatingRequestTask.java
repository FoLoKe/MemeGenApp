package com.foloke.memgenactivity.Requests;

import android.os.AsyncTask;
import com.foloke.memgenactivity.Entities.Meme;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.foloke.memgenactivity.*;
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
			String destination = "/rating?" + params[0];
				
            final String url = "http://31.42.45.42:10204" + destination;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			HttpEntity<String> request = new HttpEntity<String>(restTemplate.basicAuthHeader());
            ResponseEntity<MemeInfo> result = restTemplate.exchange(url, HttpMethod.POST, request, MemeInfo.class);
			
			if(result.getStatusCode() == HttpStatus.ACCEPTED) {
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
