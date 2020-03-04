package com.foloke.memgenactivity;
import com.foloke.memgenactivity.Requests.*;
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
	
	public void updateMemes(List<MemeInfo> memesInfos) {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		LinearLayout lentList = lentInclude.findViewById(R.id.lentContentList);
		
		if(memesInfos.size() > 0) {
			for(MemeInfo memeInfo : memesInfos) {
				Content content = (Content)context.createContent(context);
				content.initContent(memeInfo);
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
	
	public void updateRating(MemeInfo info) {
		if(info != null) {
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
		ratingUpdating = false;
	}
}
