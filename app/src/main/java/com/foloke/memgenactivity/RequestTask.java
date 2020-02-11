package com.foloke.memgenactivity;

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

public class RequestTask extends AsyncTask<Void, Void, List<Image>> {

    public LinearLayout contentList;

    public RequestTask(LinearLayout contentList) {
        super();

        this.contentList = contentList;
    }

    @Override
    protected List<Image> doInBackground(Void... params) {
        try {
            final String url = "http://31.42.45.42:10204/get?name=SUKA";
            MyRestTemplate restTemplate = new MyRestTemplate(1000);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Image[] images = restTemplate.getForObject(url, Image[].class);
            return new ArrayList<Image>(Arrays.asList(images));
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Image> replies) {
        for (final Image reply : replies) {
            final View content = ((MGActivity)contentList.getContext()).createContent(contentList.getContext());
            TextView nickName = ((TextView) content.findViewById(R.id.contentNickname));
            final ImageView imageView = ((ImageView) content.findViewById(R.id.contentImage));
            final TextView ratingUp = ((TextView) content.findViewById(R.id.contentUpRating));
            ratingUp.setText("" + reply.getRatingUp());

            Button ratingUpButton = ((Button) content.findViewById(R.id.contentUpButton));
            ratingUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RatingUpTask(ratingUp, reply.getId()).execute();
                }
            });

            contentList.addView(content);
            if (reply != null) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(reply.getImage(), 0, reply.getImage().length));
                nickName.setText("" + reply.id);
            } else {
                nickName.setText("error: host is unreachable");

            }
        }
    }



}
