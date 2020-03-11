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
	private boolean templatesUpdating;
	private boolean memeSending;
	private boolean templateSending;
	
	public RestController(MGActivity context) {
		this.context = context;
	}
	
	public void getMemes(int last, String[] tags) {
		if(!memesUpdating) {
			memesUpdating = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			Object[] params = {"last=" + last + "&type=" + MGActivity.goodContent, tags};
			
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
		} else {
			Toast.makeText(context, "wait", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void updateRating(MemeInfo info) {
		if(info != null) {
			context.putRating(info);
		}
		ratingUpdating = false;
	}
	
	public void getTemplates(int id) {
		if(!templatesUpdating) {
			templatesUpdating = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			String[] params = {"id=" + id};
			new TemplatesRequestTask(this).execute(params);
			
		} else {
			Toast.makeText(context, "wait", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void updateMemes(List<MemeInfo> memesInfos) {
		if(memesInfos != null) {
			context.putMemes(memesInfos);
		} else {
			Toast.makeText(context, "no response", Toast.LENGTH_SHORT).show();
		}	
		memesUpdating = false;
	}
	
	public void updateTemplates(List<Template> templates) {
		
		if(templates != null) {
			context.putTemplates(templates);
		} else {
			Toast.makeText(context, "no response", Toast.LENGTH_SHORT).show();
		}	
		templatesUpdating = false;
	}
	
	public void postMeme(Meme meme) {

		if(!memeSending) {
			memeSending = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			new MemeSendingTask(this).execute(meme);

		} else {
			Toast.makeText(context, "wait", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void memePosted(boolean posted) {
		if(posted) {
			Toast.makeText(context, "posted successfully", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "no response", Toast.LENGTH_LONG).show();
		}	
		memeSending = false;
	}
	
	public void postTemplate(Template template) {

		if(!templateSending) {
			templateSending = true;
			Toast.makeText(context,"updating",Toast.LENGTH_SHORT).show();
			new TemplateSendingTask(this).execute(template);
		} else {
			Toast.makeText(context, "wait", Toast.LENGTH_SHORT).show();
		}
	}

	public void templatePosted(boolean posted) {
		if(posted) {
			Toast.makeText(context, "posted successfully", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "no response", Toast.LENGTH_LONG).show();
		}	
		templateSending = false;
	}
}
