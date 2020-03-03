package com.foloke.memgenactivity;
import android.os.*;
import com.foloke.memgenactivity.Requests.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.*;
import java.util.*;
import com.foloke.memgenactivity.Entities.*;
import android.widget.*;
import android.view.*;

public class RestController
{
	private MGActivity context;
	private boolean memesUpdating;
	private boolean ratingUpdating;
	
	public RestController(MGActivity context) {
		this.context = context;
	}
	
	public void updateMemes(List<Meme> memes) {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		LinearLayout lentList = lentInclude.findViewById(R.id.contentLinearLayout);
		
		if(memes.size() > 0) {
			for(Meme image : memes) {
				Content content = (Content)context.createContent(context);
				content.initContent(image);
				lentList.addView(content);
			}
		} else {
			Toast.makeText(context, "no response", Toast.LENGTH_SHORT).show();
		}
		updateIcon.setY(-updateIcon.getHeight());
		memesUpdating = false;
	}
	
	public void getMemes(int last) {
		if(!memesUpdating) {
			memesUpdating = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			String[] params = {"last=" + last};
			new MemesRequestTask(this).execute(params);
		} else {
			Toast.makeText(context, "wait", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void postRating(int id, boolean type) {
		if(!ratingUpdating) {
			ratingUpdating = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			String[] params = {"id=" + id + "&type=" + type};
			new RatingRequestTask(this).execute(params);
		}
	}
	
	public void updateRating(PostInfo info) {
		if(info != null) {
			View lentInclude = context.findViewById(R.id.mainLentInclude);
			
			LinearLayout lentList = lentInclude.findViewById(R.id.contentLinearLayout);
		
			for(int i = 0; i < lentList.getChildCount(); i++) {
				Content content = (Content)lentList.getChildAt(i);
				if(content.getId() == info.getMeme().getId()) {
						content.setLikes(info.getLikes(), info.isLikeState());
						content.setDislikes(info.getDislikes(), info.isDislikeState());
				}
			}
		}
	}
}
