package com.foloke.memgenactivity.Requests;

import android.util.Log;

import com.foloke.memgenactivity.MGActivity;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.apache.commons.codec.binary.*;

public class MGRestTemplate extends RestTemplate {
    public MGRestTemplate(int timeout) {
        if (getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
            Log.d("HTTP", "HttpUrlConnection is used");
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(timeout);
            ((SimpleClientHttpRequestFactory) getRequestFactory()).setReadTimeout(timeout);
        } else if (getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
            Log.d("HTTP", "HttpClient is used");
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setReadTimeout(timeout);
            ((HttpComponentsClientHttpRequestFactory) getRequestFactory()).setConnectTimeout(timeout);
        }
    }
	
	public HttpHeaders basicAuthHeader(){
		String plainCreds = MGActivity.login + ":" + MGActivity.password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		return headers;
	}
}
