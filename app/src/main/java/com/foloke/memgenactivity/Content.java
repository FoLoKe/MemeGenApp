package com.foloke.memgenactivity;
import android.widget.*;
import android.util.*;
import android.content.*;
import com.foloke.memgenactivity.Entities.*;
import android.view.*;
import android.graphics.*;

public class Content extends LinearLayout
{
	public Meme meme;
	private UIController uIController;
	
	public Content(Context context, AttributeSet attributes){
		super(context, attributes);
		this.uIController = ((MGActivity)context).uiController;
	}
	
	public void initContent(MemeInfo memeInfo) {
		this.meme = memeInfo.getMeme();
		TextView nickName = this.findViewById(R.id.contentNickname);
		ImageView imageView = this.findViewById(R.id.contentImage);

		Button ratingUpButton = this.findViewById(R.id.contentUpButton);
		ratingUpButton.setOnClickListener(new LikeButtonListener(true));

		setLikes(memeInfo.getLikes(), memeInfo.isLikeState());
		setDislikes(memeInfo.getDislikes(), memeInfo.isDislikeState());

		Button ratingDownButton = this.findViewById(R.id.contentDownButton);
		ratingDownButton.setOnClickListener(new LikeButtonListener(false));

		imageView.setImageBitmap(BitmapFactory.decodeByteArray(meme.getImage(), 0, meme.getImage().length));
		if(meme.getUser() != null) {
			nickName.setText(meme.getUser().getName());
		}
		ViewGroup tagsContainer = this.findViewById(R.id.contentTagsContainer);

		for(final Tag tag : meme.getTags()) {
			View tagView = LayoutInflater.from(getContext()).inflate(R.layout.tag, null);
			TextView text = tagView.findViewById(R.id.tagTextView);
			text.setText(tag.getName());
			tagsContainer.addView(tagView);

			tagView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						View lentInclude = ((MGActivity)v.getContext()).findViewById(R.id.mainLentInclude);
						EditText tagsEditText = lentInclude.findViewById(R.id.lentTagsEditText);
						String editTextTags = tagsEditText.getText().toString();
						StringBuilder sb = new StringBuilder();
						sb.append(editTextTags);
						sb.append(tag.getName());

						tagsEditText.setText(sb.toString());
					}
				});

		}
	}
	
	private class LikeButtonListener implements View.OnClickListener {
		
		private boolean action;
		LikeButtonListener(boolean action) {
			super();
			this.action = action;
		}
		
		@Override
		public void onClick(View v) {
			uIController.likeRequest(Content.this.meme.id ,action);
		}
	}
	
	public void setLikes(int likes, boolean posted) {
		TextView ratingUpTextView = this.findViewById(R.id.contentUpRating);
		ratingUpTextView.setText(Integer.toString(likes));
		
		Button ratingUpButton = this.findViewById(R.id.contentUpButton);
		
		if(posted) {
			ratingUpButton.setBackgroundColor(Color.GREEN);
		} else {
			ratingUpButton.setBackgroundColor(Color.GRAY);
		}
	}
	
	public void setDislikes(int dislikes, boolean posted) {
		TextView ratingDownTextView = this.findViewById(R.id.contentDownRating);
		ratingDownTextView.setText(Integer.toString(dislikes));

		Button ratingDownButton = this.findViewById(R.id.contentDownButton);

		if(posted) {
			ratingDownButton.setBackgroundColor(Color.RED);
		} else {
			ratingDownButton.setBackgroundColor(Color.GRAY);
		}
	}
}
