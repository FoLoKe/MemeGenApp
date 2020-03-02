package com.foloke.memgenactivity.Requests;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foloke.memgenactivity.Entities.Image;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.widget.*;
import com.foloke.memgenactivity.*;
import org.springframework.http.*;

public class MemesRequestTask extends AsyncTask<String, Void, Image[]> {

    private RestController restContrloler;

	public MemesRequestTask(RestController parent){
		this.restContrloler = parent;
	}
	
    @Override
    protected Image[] doInBackground(String... params) {
        try {
			
			String last = "";
			if(params.length > 0) {
				last = "?" + params[0];
			}
			
            String url = "http://31.42.45.42:10204/get?last="+last;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);
			
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			HttpEntity<String> request = new HttpEntity<String>(restTemplate.basicAuthHeader());
            ResponseEntity<Image[]> response = (ResponseEntity<Image[]>)restTemplate.exchange(url, HttpMethod.GET, request, Image[].class);
			return response.getBody();
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Image[] images) {
		if(images != null && images.length >0) {
			restContrloler.updateMemes(new ArrayList<Image>(Arrays.asList(images)));
		} else {
			restContrloler.updateMemes( new ArrayList<Image>());
		}
		
    }
}
