package com.foloke.memgenactivity;
import android.widget.*;
import android.util.*;
import android.content.*;
import com.foloke.memgenactivity.Entities.*;
import android.view.*;
import com.foloke.memgenactivity.Requests.*;
import android.graphics.*;

public class Content extends LinearLayout
{
	public Meme meme;
	private UIController uIController;
	
	public Content(Context context, AttributeSet attributes){
		super(context, attributes);
		this.uIController = ((MGActivity)context).uiController;
	}
	
	public void initContent(Meme image) {
		TextView nickName = ((TextView) this.findViewById(R.id.contentNickname));
		ImageView imageView = ((ImageView) this.findViewById(R.id.contentImage));
		TextView ratingUpTextView = ((TextView) this.findViewById(R.id.contentUpRating));
		ratingUpTextView.setText("" + image.getRatingUp());
		
		TextView ratingDownTextView = ((TextView) this.findViewById(R.id.contentUpRating));
		ratingDownTextView.setText("" + image.getRatingDown());

		Button ratingUpButton = ((Button) this.findViewById(R.id.contentUpButton));
		ratingUpButton.setOnClickListener(new LikeButtonListener(true));
		ratingUpButton.setBackgroundColor(Color.GRAY);
		
		Button ratingDownButton = ((Button) this.findViewById(R.id.contentDownButton));
		ratingDownButton.setOnClickListener(new LikeButtonListener(false));
		ratingDownButton.setBackgroundColor(Color.GRAY);
		
		

		imageView.setImageBitmap(BitmapFactory.decodeByteArray(image.getImage(), 0, image.getImage().length));
		nickName.setText("" + image.id);
	}
	
	private class LikeButtonListener implements View.OnClickListener {
		
		private boolean action;
		public LikeButtonListener(boolean action) {
			super();
			this.action = action;
		}
		
		@Override
		public void onClick(View v) {
			uIController.likeRequest(Content.this.meme.id ,action);
		}
	}
	
	public void setLikes(int likes, boolean posted) {
		TextView ratingUpTextView = ((TextView) this.findViewById(R.id.contentUpRating));
		ratingUpTextView.setText(Integer.toString(likes));
		
		Button ratingUpButton = ((Button) this.findViewById(R.id.contentUpButton));
		Button ratingDownButton = ((Button) this.findViewById(R.id.contentDownButton));
		
		ratingDownButton.setBackgroundColor(Color.GRAY);
		
		if(posted) {
			ratingUpButton.setBackgroundColor(Color.GREEN);
		} else {
			ratingUpButton.setBackgroundColor(Color.GRAY);
		}
	}
	
	public void setDislikes(int dislikes, boolean posted) {
		TextView ratingDownTextView = ((TextView) this.findViewById(R.id.contentUpRating));
		ratingDownTextView.setText(Integer.toString(dislikes));
		
		
		Button ratingUpButton = ((Button) this.findViewById(R.id.contentUpButton));
		Button ratingDownButton = ((Button) this.findViewById(R.id.contentDownButton));
		
		ratingUpButton.setBackgroundColor(Color.GRAY);

		if(posted) {
			ratingDownButton.setBackgroundColor(Color.GREEN);
		} else {
			ratingDownButton.setBackgroundColor(Color.GRAY);
		}
	}
}
