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

	boolean updating = false;
	
	private LinearLayout list;
	private View updateIcon;;
	private RestController restController;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
			   != PackageManager.PERMISSION_GRANTED)
				this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			//
        setContentView(R.layout.main);
		restController = new RestController(this);
		
		final ScrollView scroll = findViewById(R.id.mainScrollView);
			list = findViewById(R.id.content);
			updateIcon = findViewById(R.id.lentUpdateIcon);
			updateIcon.setY(-updateIcon.getHeight());
		
		scroll.setOnTouchListener(new OnTouchListener() {
			PointF start = new PointF();
			int offset;
			
			
			
			public boolean onTouch(View v, MotionEvent m) {
				try {
					View updateIcon = findViewById(R.id.lentUpdateIcon);
					View view = scroll.getChildAt(scroll.getChildCount() - 1);
					int bottomDiff = (view.getBottom() - (scroll.getHeight() + scroll.getScrollY()));
					int topDiff = (view.getTop() - scroll.getScrollY());
					
				switch (m.getAction()) {
					case MotionEvent.ACTION_DOWN:
						start.set(m.getX(), m.getY()); 
						break;
					case MotionEvent.ACTION_MOVE:
						offset =  (int)( m.getY() - start.y);
						
						if(topDiff == 0 || bottomDiff <= 0) {
							int updateIconOffset = offset;
							if(updateIconOffset < 0) {
								updateIconOffset = -updateIconOffset;
							}
							
							updateIconOffset = Integer.min(250, updateIconOffset);
							updateIcon.setY(updateIconOffset);
						}
						
						break;
					case MotionEvent.ACTION_UP: 
						
						if (offset < 0 && bottomDiff <= 0) {
							if(list.getChildCount() > 0) {
								Content lastContent = (Content)list.getChildAt(list.getChildCount() - 1);
								Image lastMeme = lastContent.image;
								if(lastMeme != null) {
									restController.getMemes(((Content)list.getChildAt(list.getChildCount() - 1)).image.getId());
								}
							} else {
								restController.getMemes(-1);
							}
						} else if (offset > 0 && topDiff == 0) {
							restController.getMemes(-1);
						}
						break;
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				
				return true;
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
				
				Bitmap bitmap = BitmapFactory.decodeFile(contentUri.getPath());
				Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
					getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND,null);
				ImageView image = new ImageView(this);
				image.setImageBitmap(thumbnail);
				
				//ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
				//LayoutParams.FILL_PARENT);
				//image.setLayoutParams(layoutParams);
				
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
			
			Button saveCanvasButton = editorInclude.findViewById(R.id.editorSaveButton);
			saveCanvasButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						editor.thread.running = false;
						editor.thread.join();
						Canvas c = editor.getHolder().lockCanvas();
						synchronized(editor.getHolder()) {
							editor.toBitmap(c);
							editor.getHolder().unlockCanvasAndPost(c);
						}
						editor.thread.running = true;
						editor.thread.start();
					} catch (Exception e) {
							System.out.println(e);
					}
				}
			});
			
			Button editorChangeTextButton = editorInclude.findViewById(R.id.editorChangeTextButton);
			editorChangeTextButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					if(editor.selectedLayer != null) {
						final Editor.Layer layer = editor.selectedLayer;
						final Dialog dialog = new Dialog(MGActivity.this);
						dialog.setContentView(R.layout.text_dialog);
						final EditText editText = dialog.findViewById(R.id.text_dialogEditText);
						editText.setText(layer.text);
						
						Button okButton = dialog.findViewById(R.id.text_dialogOkButton);
						okButton.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								layer.text = editText.getText().toString();
								dialog.cancel();
							}
						});
						dialog.show();
					}
				}
			});
			
			Button editorChangeImageButton = editorInclude.findViewById(R.id.editorChangeImageButton);
			editorChangeImageButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					if(editor.selectedLayer!=null) {
					final Editor.Layer layer = editor.selectedLayer;
					final Dialog dialog = new Dialog(MGActivity.this);
					dialog.setContentView(R.layout.image_dialog);
					
					GridLayout grid = dialog.findViewById(R.id.imageDialogGridLayout);
					
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
						try {
						// Use an ID column from the projection to get
						// a URI representing the media item itself.
						int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
						long id = cursor.getLong(idColumn);
						Uri contentUri = ContentUris.withAppendedId(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
							
						
						final Bitmap bitmap = MediaStore.Images.Media.getBitmap(MGActivity.this.getContentResolver(), contentUri);
						
						Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
							getContentResolver(), id,
							MediaStore.Images.Thumbnails.MINI_KIND,null);
						ImageView image = new ImageView(MGActivity.this);
						image.setImageBitmap(thumbnail);
						
						image.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								layer.bitmap = bitmap;
								dialog.cancel();
							}
						});

						//ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
						//LayoutParams.FILL_PARENT);
						//image.setLayoutParams(layoutParams);

						grid.addView(image);
						} catch (Exception e) {
							System.out.println(e);
						}
					}
					
					
					Button okButton = dialog.findViewById(R.id.image_dialogOkButton);
					okButton.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								
								dialog.cancel();
							}
					});
					
					dialog.show();
				}
				}
			});
			
			LinearLayout list = findViewById(R.id.content);
			restController.getMemes(-1);
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
		// TODO: Implement this method
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
	
	
	
	
	

	
}
