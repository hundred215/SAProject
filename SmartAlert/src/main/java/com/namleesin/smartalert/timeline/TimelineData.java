package com.namleesin.smartalert.timeline;


public class TimelineData
{

	public String pkgName;
	public String appName;
	public String content;
	public String filter;
	public int likeStatus;

	public String date;

	public String getPkgName() {
		return pkgName;
	}
	
	public TimelineData setPkgName(String pkgName) {
		this.pkgName = pkgName;
		return this;
	}
	
	public String getAppName() {
		return appName;
	}

	public TimelineData setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	public int getLikeStatus() {
		return likeStatus;
	}
	public TimelineData setLikeStatus(int likeCnt) {
		this.likeStatus = likeCnt;
		return this;
	}
	public String getContent() {
		return content;
	}

	public TimelineData setContent(String content) {
		this.content = content;
		return this;
	}

	public String getDate() {
		return date;
	}

	public TimelineData setDate(String date) {
		this.date = date;
		return this;
	}

	public TimelineData setFilter(String filter) {
		this.filter = filter;
		return this;
	}

	public String getFilter() {
		return filter;
	}

}
