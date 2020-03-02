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
	
	public RestController(MGActivity context) {
		this.context = context;
	}
	
	public void updateMemes(List<Image> memes) {
		View lentInclude = context.findViewById(R.id.mainLentInclude);
		View updateIcon = lentInclude.findViewById(R.id.lentUpdateIcon);
		LinearLayout lentList = lentInclude.findViewById(R.id.contentLinearLayout);
		
		if(memes.size() > 0) {
			for(Image image : memes) {
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
}
