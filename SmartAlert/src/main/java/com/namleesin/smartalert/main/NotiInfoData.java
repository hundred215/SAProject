package com.namleesin.smartalert.main;


import java.util.ArrayList;

public class NotiInfoData
{
	public String pkgName;
	public String appName;
	public int likeCnt;
	public int totalCnt;

	public String getPkgName() {
		return pkgName;
	}
	
	public NotiInfoData setPkgName(String pkgName) {
		this.pkgName = pkgName;
		return this;
	}
	
	public String getAppName() {
		return appName;
	}

	public NotiInfoData setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	public int getLikeCnt() {
		return likeCnt;
	}
	public NotiInfoData setLikeCnt(int likeCnt) {
		this.likeCnt = likeCnt;
		return this;
	}
	public int getTotalCnt() {
		return totalCnt;
	}
	public NotiInfoData setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
		return this;
	}
}
