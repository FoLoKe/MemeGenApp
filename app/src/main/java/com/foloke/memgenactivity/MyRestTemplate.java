package com.foloke.memgenactivity;

import android.util.Log;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class MyRestTemplate extends RestTemplate {
    public MyRestTemplate(int timeout) {
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
}