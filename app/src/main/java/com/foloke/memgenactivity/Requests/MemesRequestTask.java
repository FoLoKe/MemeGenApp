package com.foloke.memgenactivity.Requests;

import android.os.AsyncTask;
import com.foloke.memgenactivity.Entities.Meme;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.ArrayList;
import java.util.Arrays;
import com.foloke.memgenactivity.*;
import com.foloke.memgenactivity.Entities.MemeInfo;

import org.springframework.http.*;

public class MemesRequestTask extends AsyncTask<Object, Void, MemeInfo[]> {

    private RestController restController;

	public MemesRequestTask(RestController parent){
		this.restController = parent;
	}
	
    @Override
    protected MemeInfo[] doInBackground(Object... params) {
        try {
			
			String last = "";

			last = "?" + (String)params[0];
			String[] tags = (String[])params[1];
			
            String url = "http://31.42.45.42:10204/get"+last;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
			
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<MemeInfo[]> response = null;
            if(MGActivity.password == null) {
                HttpEntity<String[]> request = new HttpEntity<String[]>(tags);
                response = restTemplate.exchange(url, HttpMethod.GET, request, MemeInfo[].class);
            } else {
                HttpEntity<String[]> request = new HttpEntity<String[]>(tags, restTemplate.basicAuthHeader());
                response = restTemplate.exchange(url, HttpMethod.GET, request, MemeInfo[].class);
            }
			return response.getBody();
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(MemeInfo[] memesInfos) {
		if(memesInfos != null && memesInfos.length >0) {
			restController.updateMemes(new ArrayList<>(Arrays.asList(memesInfos)));
		} else {
			restController.updateMemes(new ArrayList<MemeInfo>());
		}
		
    }
}
