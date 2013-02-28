package com.androidapp.notice.board;

public class Item 
{
	private String id;
	private String title;
	private String desc;
	private String pubdate;
	private String link;
	private String venue;
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
	public String getVenue()
	{
		return venue;
	}

	public void setVenue(String id)
	{
		this.venue = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getDesc() 
	{
		return desc;
	}

	public void setDesc(String desc) 
	{
		this.desc = desc;
	}

	public String getPubdate() 
	{
		return pubdate;
	}

	public void setPubdate(String pubdate) 
	{
		this.pubdate = pubdate;
	}

	public String getLink() 
	{
		return link;
	}

	public void setLink(String link) 
	{
		this.link = link;
	}
}
