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

import java.io.IOException;
import java.util.*;
import android.os.*;

public class UIController
{
	private static MGActivity context;
	private Dialog openedDialog;
	private Editor editor;
	//TODO: separate loading and ui;
	//TODO: sending dialogs;
	
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
		View mainLayout = context.findViewById(R.id.mainLayout);
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
				
				View uploadLayout = context.findViewById(R.id.mainUploadInclude);
				GridLayout grid = uploadLayout.findViewById(R.id.uploadGridLayout);
				grid.setColumnCount(3);
				grid.removeAllViews();
				
				viewFlipper.setDisplayedChild(2);
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
					int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
					long id = cursor.getLong(idColumn);
					Uri contentUri = ContentUris.withAppendedId(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

					final Bitmap bitmap;
					try {
						bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentUri);
						Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
								context.getContentResolver(), id,
								MediaStore.Images.Thumbnails.MINI_KIND, null);
						ImageView image = new ImageView(context);
						image.setImageBitmap(thumbnail);
						image.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {

								final Dialog dialog = new Dialog(context);
								dialog.setContentView(R.layout.upload_dialog);
								ImageView imageView = dialog.findViewById(R.id.upload_dialogImageView);
								imageView.setImageBitmap(bitmap);

								dialog.findViewById(R.id.upload_dialogOkButton).setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										EditText editText = dialog.findViewById(R.id.upload_dialogTagsEditText);
										String[] tags = editText.getText().toString().split(", ");
										context.postTemplate(bitmap, tags);
										dialog.cancel();
									}
								});

								dialog.show();
							}
						});

						grid.addView(image);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		});

		editor = editorInclude.findViewById(R.id.editorcom);
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

		Button uploadMemeButton = editorInclude.findViewById(R.id.editorUploadImageButton);
		uploadMemeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final Bitmap bitmap = editor.getBitmap();
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.upload_dialog);
					ImageView imageView = dialog.findViewById(R.id.upload_dialogImageView);
					imageView.setImageBitmap(bitmap);
					
					dialog.findViewById(R.id.upload_dialogOkButton).setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							EditText editText = dialog.findViewById(R.id.upload_dialogTagsEditText);
							String[] tags = editText.getText().toString().split(", ");
							context.postMeme(bitmap, tags);	
							dialog.cancel();
						}
					});
					
					dialog.show();
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
					openedDialog = new Dialog(context);
					openedDialog.setContentView(R.layout.image_dialog);
					initLocal();

					RadioGroup radioGroup = openedDialog.findViewById(R.id.imageDialogRadioGroup);
					RadioButton radioButtonLocal = radioGroup.findViewById(R.id.imageDialogRadioButtonLocal);
					RadioButton radioButtonInternet = radioGroup.findViewById(R.id.imageDialogRadioButtonInternet);

					radioButtonLocal.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							ViewFlipper viewFlipper = openedDialog.findViewById(R.id.imageDialogViewFlipper);
							viewFlipper.setDisplayedChild(0);
							initLocal();
						}
					});

					radioButtonInternet.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							ViewFlipper viewFlipper = openedDialog.findViewById(R.id.imageDialogViewFlipper);
							viewFlipper.setDisplayedChild(1);
							initInternet();
						}
					});


					Button okButton = openedDialog.findViewById(R.id.image_dialogOkButton);
					okButton.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							openedDialog.cancel();
						}
					});
					openedDialog.show();
				}

			}
		});
		
		TextView loginTextView = context.findViewById(R.id.mainNicknameTextView);
		loginTextView.setText(MGActivity.login);
		
		loginTextView.setOnClickListener( new OnClickListener() {
				public void onClick(View v) {
					openLoginDialog();
				}
			});
		
		View loginButton = context.findViewById(R.id.mainLoginImageView);
		loginButton.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				openLoginDialog();
			}
		});
	}
	
	public void openLoginDialog() {
		final Dialog dialog = new Dialog(UIController.this.context);
		dialog.setContentView(R.layout.login_dialog);
		
		((TextView)dialog.findViewById(R.id.login_dialogLoginEditText)).setText(MGActivity.login);
		((TextView)dialog.findViewById(R.id.login_dialogPasswordEditText)).setText("password");
		
		dialog.findViewById(R.id.login_dialogOkButton).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String login = ((TextView)dialog.findViewById(R.id.login_dialogLoginEditText)).getText().toString();
					String password = ((TextView)dialog.findViewById(R.id.login_dialogPasswordEditText)).getText().toString();
					if(login == null || login.length() < 3) {
						Toast.makeText(UIController.this.context, "login to short", Toast.LENGTH_LONG).show();
						return;
					}
					if(password == null || password.length() < 8) {
						Toast.makeText(UIController.this.context, "password to short", Toast.LENGTH_LONG).show();
						return;
					}

					//TODO: Try to connect;

					MGActivity.password = password;
					MGActivity.login = login;
					dialog.cancel();
					TextView loginTextView = context.findViewById(R.id.mainNicknameTextView);
					loginTextView.setText(MGActivity.login);
				}
			});
		dialog.show();
		
	}
	
	public void clearContent() {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		LinearLayout lentList = lentInclude.findViewById(R.id.lentContentList);	
		lentList.removeAllViews();
	}
	
	public void likeRequest(int id, boolean action) {
		context.memeLike(id, action);
	}
	
	public void putMemes(List<MemeInfo> memesInfos) {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		LinearLayout lentList = lentInclude.findViewById(R.id.lentContentList);
		
		for(MemeInfo memeInfo : memesInfos) {
			Content content = (Content)context.createContent(context);
			content.initContent(memeInfo);
			lentList.addView(content);
		}
	}
	
	public void putTemplates(List<Template> templates) {
		GridLayout intrenetGrid = openedDialog.findViewById(R.id.imageDialogInternetGridLayout);
		for(Template template : templates) {
			ImageView image = new ImageView(context);
			final Bitmap bitmap = BitmapFactory.decodeByteArray(template.getImage(), 0, template.getImage().length);
			image.setImageBitmap(bitmap);
			intrenetGrid.addView(image);
			
			image.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Editor.Layer layer = editor.selectedLayer;
					layer.bitmap = bitmap;
				}
			});
		}
	}
	
	public void putRating(MemeInfo info) {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		LinearLayout lentList = lentInclude.findViewById(R.id.lentContentList);

		for(int i = 0; i < lentList.getChildCount(); i++) {
			Content content = (Content)lentList.getChildAt(i);
			if(content.meme.getId() == info.getMeme().getId()) {
				content.setLikes(info.getLikes(), info.isLikeState());
				content.setDislikes(info.getDislikes(), info.isDislikeState());
			}
		}
	}
	
	private void initLocal() {
		final Editor.Layer layer = editor.selectedLayer;
		final GridLayout grid = openedDialog.findViewById(R.id.imageDialogGridLayout);
		grid.removeAllViews();
		
		new AsyncTask<Void,Void,Void>() {
			public Void doInBackground(Void... params) {
				
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
						int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
						long id = cursor.getLong(idColumn);
						Uri contentUri = ContentUris.withAppendedId(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

						final Bitmap bitmap = MediaStore.Images.Media.getBitmap(UIController.this.context.getContentResolver(), contentUri);
						final Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
							context.getContentResolver(), id,
							MediaStore.Images.Thumbnails.MINI_KIND, null);

						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final ImageView image = new ImageView(context);
								image.setImageBitmap(thumbnail);

								image.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										layer.bitmap = bitmap;
										openedDialog.cancel();
									}
								});


								grid.addView(image);
							}
						});
						
					} catch (Exception e) {
						System.out.println(e);
					}
				}
				
				return null;
			}
		}.execute();
		
		
	}
	
	public void initInternet() {
		((MGActivity)context).getTemplates(-1);
	}
	
	public void clearInternetTemplates() {
		GridLayout intrenetGrid = openedDialog.findViewById(R.id.imageDialogInternetGridLayout);
		intrenetGrid.removeAllViews();
	}
	
	public void updatig() {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		updateIcon.setVisibility(View.VISIBLE);
	}
	
	public void updatingEnded() {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		updateIcon.setVisibility(View.GONE);
	}
}
