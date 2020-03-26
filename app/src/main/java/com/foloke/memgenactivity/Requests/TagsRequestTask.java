package com.foloke.memgenactivity.Requests;
import android.os.*;
import com.foloke.memgenactivity.Entities.*;
import com.foloke.memgenactivity.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.*;

public class TagsRequestTask extends AsyncTask<String, Void, Tag[]>
{
	private RestController restController;
	
	public TagsRequestTask(RestController parent) {
		restController = parent;
	}
	
	@Override
	protected Tag[] doInBackground(String[] params)
	{
		try {
			String tag = "?" + params[0];
			String url = "http://31.42.45.42:10204/getTags"+tag;
			MGRestTemplate restTemplate = new MGRestTemplate(1000);

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			ResponseEntity<Tag[]> response = null;
			
			response = restTemplate.getForEntity(url, Tag[].class);
		
			return response.getBody();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Tag[] result)
	{
		restController.putTags(result);
	}

	
}
