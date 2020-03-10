package com.foloke.memgenactivity.Requests;
import android.os.*;
import android.widget.*;
import java.util.*;
import com.foloke.memgenactivity.Entities.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.*;
import com.foloke.memgenactivity.*;

public class TemplatesRequestTask extends AsyncTask<String, Void, Template[]>
{
	RestController restController;
	
	public TemplatesRequestTask(RestController parent) {
		this.restController = parent;
	}

	@Override
	protected Template[] doInBackground(String params[])
	{
		try {

			String last = "";
			if(params.length > 0) {
				last = "?" + params[0];
			}

            String url = "http://31.42.45.42:10204/getTemplates"+last;
            MGRestTemplate restTemplate = new MGRestTemplate(1000);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<Template[]> response = null;
            
            response = restTemplate.getForEntity(url, Template[].class);
            
			return response.getBody();
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Template[] memesInfos) {
		if(memesInfos != null && memesInfos.length >0) {
			restController.updateTemplates(new ArrayList<>(Arrays.asList(memesInfos)));
		} else {
			restController.updateTemplates(null);
		}

    }

	
}
