package com.foloke.memgenactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.app.*;
import android.widget.*;
import android.view.View.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import android.provider.*;
import android.database.*;
import java.net.*;
import android.util.*;
import android.net.*;
import android.*;
import android.content.pm.*;
import android.widget.TableRow.*;
import android.widget.SearchView.*;
import com.foloke.memgenactivity.Requests.*;
import com.foloke.memgenactivity.Entities.*;


public class MGActivity extends Activity {

	private RestController restController;
	public UIController uiController;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
			   != PackageManager.PERMISSION_GRANTED) {
				this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
	
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
		if(!editor.thread.running) {
			editor.thread.running = true;
			editor.thread.start();
		}
	}

	public static View createContent(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.content, null);
	} 
	
	public void refreshMemes(int id) {
		restController.getMemes(id);
	}
	
	public void memeLike(int id, boolean type) {
		restController.postRating(id, type);
	}
}
