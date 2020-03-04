package com.foloke.memgenactivity;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.graphics.*;
import com.foloke.memgenactivity.Entities.*;
import android.provider.*;
import android.database.*;
import android.net.*;
import android.content.*;
import android.app.*;

public class UIController
{
	public static MGActivity context;
	
	public UIController(MGActivity parent) {
		context = parent;
		context.setContentView(R.layout.main);

		View lentInclude = context.findViewById(R.id.mainLentInclude);
		final ScrollView scroll = lentInclude.findViewById(R.id.mainScrollView);
		final LinearLayout lent = lentInclude.findViewById(R.id.lentContentList);
		final View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		updateIcon.setY(-updateIcon.getHeight());

		scroll.setOnTouchListener(new OnTouchListener() {
			PointF start = new PointF();
			int offset;

			public boolean onTouch(View v, MotionEvent m) {
				try {
					View view = scroll.getChildAt(scroll.getChildCount() - 1);
					int bottomDiff = (view.getBottom() - (scroll.getHeight() + scroll.getScrollY()));
					int topDiff = (view.getTop() - scroll.getScrollY());

					switch (m.getAction()) {
						case MotionEvent.ACTION_DOWN:
							start.set(m.getX(), m.getY());
							break;
						case MotionEvent.ACTION_MOVE:
							offset = (int) (m.getY() - start.y);

							if (topDiff == 0 || bottomDiff <= 0) {
								int updateIconOffset = offset;
								if (updateIconOffset < 0) {
									updateIconOffset = -updateIconOffset;
								}

								updateIconOffset = Integer.min(250, updateIconOffset);
								updateIcon.setY(updateIconOffset);
							}

							break;
						case MotionEvent.ACTION_UP:

							if (offset < 0 && bottomDiff <= 0) {
								if (lent.getChildCount() > 0) {
									Content lastContent = (Content) lent.getChildAt(lent.getChildCount() - 1);
									Meme lastMeme = lastContent.meme;
									if (lastMeme != null) {
										UIController.this.context.refreshMemes(((Content) lent.getChildAt(lent.getChildCount() - 1)).meme.getId());
									}
								} else {
									UIController.this.context.refreshMemes(-1);
								}
							} else if (offset > 0 && topDiff == 0) {
								UIController.this.context.refreshMemes(-1);
							}
							break;
					}
				} catch (Exception e) {
					System.out.println(e);
				}

				return false;
			}
		});

		final ViewFlipper viewFlipper = context.findViewById(R.id.mainViewFlipper);
		FrameLayout mainLayout = context.findViewById(R.id.mainFrameLayout);
		View mainMenu = mainLayout.findViewById(R.id.mainMenu);
		View editorInclude = mainLayout.findViewById(R.id.mainEditorInclude);
		Button menuLentButton = mainMenu.findViewById(R.id.menuLentButton);

		menuLentButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent m) {
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

		View uploadLayout = context.findViewById(R.id.mainUploadInclude);
		GridLayout grid = uploadLayout.findViewById(R.id.uploadGridLayout);
		grid.setColumnCount(3);

		String[] projection = new String[]{
				MediaStore.Images.Media._ID
		};

		Cursor cursor = context.getApplicationContext().getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection,
				null,
				null,
				null
		);

		while (cursor.moveToNext()) {
			int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
			long id = cursor.getLong(idColumn);
			Uri contentUri = ContentUris.withAppendedId(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

			Bitmap bitmap = BitmapFactory.decodeFile(contentUri.getPath());
			Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
					context.getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND, null);
			ImageView image = new ImageView(context);
			image.setImageBitmap(thumbnail);

			grid.addView(image);
		}

		final Editor editor = editorInclude.findViewById(R.id.editorcom);
		final LinearLayout editorLayersLayout = editorInclude.findViewById(R.id.editorLayersLayout);

		Button newLayerButton = editorInclude.findViewById(R.id.editorNewlayerButton);
		newLayerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button layerButton = new Button(UIController.this.context);
				editorLayersLayout.addView(layerButton);
				final Editor.Layer layer = editor.newLayer();
				layerButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						editor.selectLayer(layer);
					}
				});
			}
		});

		Button selectCanvasButton = editorInclude.findViewById(R.id.editorCanvasButton);
		selectCanvasButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editor.selectLayer(null);
			}
		});

		Button saveCanvasButton = editorInclude.findViewById(R.id.editorSaveButton);
		saveCanvasButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editor.save();
			}
		});

		Button editorChangeTextButton = editorInclude.findViewById(R.id.editorChangeTextButton);
		editorChangeTextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor.selectedLayer != null) {
					final Editor.Layer layer = editor.selectedLayer;
					final Dialog dialog = new Dialog(UIController.this.context);
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
			public void onClick(View v) {
				if (editor.selectedLayer != null) {
					final Editor.Layer layer = editor.selectedLayer;
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.image_dialog);

					GridLayout grid = dialog.findViewById(R.id.imageDialogGridLayout);

					String[] projection = new String[]{
							MediaStore.Images.Media._ID
					};


					Cursor cursor = UIController.this.context.getApplicationContext().getContentResolver().query(
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


							final Bitmap bitmap = MediaStore.Images.Media.getBitmap(UIController.this.context.getContentResolver(), contentUri);

							Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
									context.getContentResolver(), id,
									MediaStore.Images.Thumbnails.MINI_KIND, null);
							ImageView image = new ImageView(context);
							image.setImageBitmap(thumbnail);

							image.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									layer.bitmap = bitmap;
									dialog.cancel();
								}
							});

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
	}
	
	public void likeRequest(int id, boolean action) {
		context.memeLike(id, action);
	}
	
}
