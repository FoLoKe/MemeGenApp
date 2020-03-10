package com.foloke.memgenactivity.Entities;
import java.util.*;

public class Template
{
	public int id;

    private byte[] image;
	
	private Set<Tag> tags;

	public void setTags(Set<Tag> tags)
	{
		this.tags = tags;
	}

	public Set<Tag> getTags()
	{
		return tags;
	}


	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setImage(byte[] image)
	{
		this.image = image;
	}

	public byte[] getImage()
	{
		return image;
	}
	
}
