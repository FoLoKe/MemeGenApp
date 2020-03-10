package com.foloke.memgenactivity;

import android.os.Bundle;
import android.view.View;

import android.app.*;
import android.view.*;
import android.content.*;
import android.*;
import android.content.pm.*;
import com.foloke.memgenactivity.Entities.*;
import java.util.*;
import android.graphics.*;
import java.io.*;


public class MGActivity extends Activity {

	private RestController restController;
	public UIController uiController;
	public static String password = "password";
	public static String login = "login";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
			   != PackageManager.PERMISSION_GRANTED) {
				this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			restController = new RestController(this);
			uiController = new UIController(this);
			refreshMemes(-1);
			
		} catch (Exception e) {
			System.out.println(e);
		}
    }
	
    @Override
    protected void onStart() {
        super.onStart();
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		Editor editor = findViewById(R.id.editorcom);
		editor.resume();
	}

	public static View createContent(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.content, null);
	} 
	
	public void refreshMemes(int id) {
		if(id == -1) {
			uiController.clearContent();
		}
		restController.getMemes(id);
	}
	
	public void postMeme(Bitmap bitmap, String[] tags) {
		Meme meme = new Meme();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		meme.setImage(stream.toByteArray());
		for(String stringTag : tags) {
			Tag tag = new Tag();
			tag.setName(stringTag);
			meme.getTags().add(tag);

		}
		restController.postMeme(meme);
	}
	
	public void postTemplate(Bitmap bitmap, String[] tags) {
		Template template = new Template();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		template.setImage(stream.toByteArray());
		
		for(String stringTag : tags) {
			Tag tag = new Tag();
			tag.setName(stringTag);
			template.getTags().add(tag);
			
		}
		restController.postTemplate(template);
	}
	
	public void memeLike(int id, boolean type) {
		restController.postRating(id, type);
	}
	
	public void getTemplates(int id) {
		if(id == -1) {
			uiController.clearInternetTemplates();
		}
		restController.getTemplates(id);
	}
	
	public void putTemplates(List<Template> templates) {
		uiController.putTemplates(templates);
	}
	
	public void putMemes(List<MemeInfo> memesInfos) {
		uiController.putMemes(memesInfos);
	}
	
	public void putRating(MemeInfo memeInfo) {
		uiController.putRating(memeInfo);
	}
}
