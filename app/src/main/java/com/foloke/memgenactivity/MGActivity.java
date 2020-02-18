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


public class MGActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
			   != PackageManager.PERMISSION_GRANTED)
				this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
			//
        setContentView(R.layout.main);
		
		final LinearLayout list = findViewById(R.id.content);
		final Activity context = this;
		
		View content = LayoutInflater.from(this).inflate(R.layout.content, null);
		list.addView(content);

		final ScrollView scroll = findViewById(R.id.mainScrollView);
		
		
		
		scroll.setOnScrollChangeListener(new OnScrollChangeListener() {
			@Override
			public void onScrollChange(View v, int x, int y, int ox, int oy) {
				View view = scroll.getChildAt(scroll.getChildCount() - 1);
				int diff = (view.getBottom() - (scroll.getHeight() + scroll.getScrollY()));

				// if diff is zero, then the bottom has been reached
				if (diff == 0) {
					// do stuff
					getRequest(context, list);
				}
			}
		});
		
		
		final ViewFlipper viewFlipper = findViewById(R.id.mainViewFlipper);
		
		
		FrameLayout mainLayout = findViewById(R.id.mainFrameLayout);
		View mainMenu = mainLayout.findViewById(R.id.mainMenu);
		View editorInclude  = mainLayout.findViewById(R.id.mainEditorInclude);
		Button menuLentButton = mainMenu.findViewById(R.id.menuLentButton);
		
		menuLentButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v,MotionEvent m) {
				viewFlipper.setDisplayedChild(0);
				return true;
			}
		});
		
		Button menuEditorButton = mainMenu.findViewById(R.id.menuEditorButton);

		menuEditorButton.setOnClickListener(new View.OnClickListener() {
			@Override
				public void onClick(View v) {
					viewFlipper.setDisplayedChild(1);
					
				}
				
			});
			
		Button menuUploadButton = mainMenu.findViewById(R.id.menuUploadButton);

		menuUploadButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					viewFlipper.setDisplayedChild(2);
				}
			});
			
			View uploadLayout = findViewById(R.id.mainUploadInclude);
			GridLayout grid = uploadLayout.findViewById(R.id.uploadGridLayout);
			grid.setColumnCount(3);
			
			String[] projection = new String[] {
				MediaStore.Images.Media._ID
			};
			

			Cursor cursor = getApplicationContext().getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection,
				null,
				null,
				null
			);

			while (cursor.moveToNext()) {
				// Use an ID column from the projection to get
				// a URI representing the media item itself.
				int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
				long id = cursor.getLong(idColumn);
				Uri contentUri = ContentUris.withAppendedId(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
				
				Bitmap thumbnail = BitmapFactory.decodeFile(contentUri.getPath());
				Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
					getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND,null);
				ImageView image = new ImageView(this);
				image.setImageBitmap(bitmap);
				
				ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
				image.setLayoutParams(layoutParams);
				
				grid.addView(image);
			}
			
			final Editor editor = editorInclude.findViewById(R.id.editorcom);
			final LinearLayout editorLayersLayout = editorInclude.findViewById(R.id.editorLayersLayout);
			
			Button newLayerButton  = editorInclude.findViewById(R.id.editorNewlayerButton);
			newLayerButton.setOnClickListener( new OnClickListener() {
				public void onClick(View v) {
					//editor.newLayer();
					Button layerButton = new Button(MGActivity.this);
					editorLayersLayout.addView(layerButton);
					final Editor.Layer layer = editor.newLayer();
					layerButton.setOnClickListener(new OnClickListener() {
						public void onClick(View v){
							editor.selectLayer(layer);
						}
					});
				}
			});
			
			Button selectCanvasButton = editorInclude.findViewById(R.id.editorCanvasButton);
			selectCanvasButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					editor.selectLayer(null);
				}
			});
		
			getRequest(this, list);
		} catch (Exception e) {
			System.out.println(e);
		}
		

    }

  
    @Override
    protected void onStart() {
        super.onStart();
		
    }
	
	public static View createContent(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.content, null);
	} 
	
	public static void getRequest(Context context, LinearLayout container) {
		Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();

		new RequestTask().execute(container);
	}


	
}
