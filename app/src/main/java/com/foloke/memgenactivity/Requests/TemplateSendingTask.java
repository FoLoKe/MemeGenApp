package com.foloke.memgenactivity.Requests;
import android.os.*;
import com.foloke.memgenactivity.Entities.*;
import com.foloke.memgenactivity.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.*;

public class TemplateSendingTask extends AsyncTask<Template, Void, String>
{

	private RestController restController;

	public TemplateSendingTask(RestController parent){
		this.restController = parent;
	}

	@Override
	protected String doInBackground(Template[] params)
	{
		if(params != null && params.length >= 1) {
			Template template = params[0];
			String url = "http://31.42.45.42:10204/postTemplate";
            MGRestTemplate restTemplate = new MGRestTemplate(1000);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<String> response = null;
            if(MGActivity.password == null) {
                return null;
            } else {
                HttpEntity<Template> request = new HttpEntity<>(template, restTemplate.basicAuthHeader());
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
			restController.templatePosted(false);
		}
		restController.templatePosted(true);
	}
}
