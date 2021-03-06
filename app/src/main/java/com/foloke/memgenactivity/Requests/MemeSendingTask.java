package com.foloke.memgenactivity.Requests;
import com.foloke.memgenactivity.*;
import com.foloke.memgenactivity.Entities.*;
import android.os.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.*;

public class MemeSendingTask extends AsyncTask<Meme, Void, String>
{
	private RestController restController;

	public MemeSendingTask(RestController parent){
		this.restController = parent;
	}

	@Override
	protected String doInBackground(Meme[] params)
	{
		if(params != null && params.length >= 1) {
			Meme template = params[0];
			String url = "http://31.42.45.42:10204/postMeme";
            MGRestTemplate restTemplate = new MGRestTemplate(1000);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<String> response = null;
            if(MGActivity.password == null) {
                return null;
            } else {
                HttpEntity<Meme> request = new HttpEntity<Meme>(template, restTemplate.basicAuthHeader());
                try {
					response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
				} catch (Exception e) {
                	return null;
				}
            }
			return response.getBody();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result)
	{
		if(result == null) {
			restController.memePosted(false);
		}
		restController.memePosted(true);
	}
}
