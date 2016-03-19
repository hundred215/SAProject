package com.namleesin.smartalert.dbmgr;

public class DBValue 
{
	public static final int SUCCESS								= 0;
	public static final int FAILURE								= -1;

	public static final int STATUS_NORMAL 	= 0;
	public static final int STATUS_LIKE 	= 1;
	public static final int STATUS_DISLIKE	= 2;

	public static final int CULNUM_PACKAGE 	= 0;
	public static final int CULNUM_TITLE 	= 1;
	public static final int CULNUM_SUBTXT 	= 2;
	public static final int CULNUM_DATE		= 5;
	public static final int CULNUM_STATUS 	= 6;
	public static final int CULNUM_FILTER	= 7;

	
	public static final int TYPE_INSERT_NOTIINFO				= 0;
	public static final int TYPE_INSERT_KEYWORDFILTER			= 1;
	public static final int TYPE_INSERT_PACKAGEFILTER			= 2;
	
	public static final int TYPE_SELECT_DAY_COUNT					= 10;
	public static final int TYPE_SELECT_PRE_DAY_COUNT			= 11;
	public static final int TYPE_SELECT_TOTAL_COUNT				= 12;
	public static final int TYPE_SELECT_URL_COUNT					= 13;
	public static final int TYPE_SELECT_DISLIKE_COUNT			= 14;
	public static final int TYPE_SELECT_LIKE_COUNT				= 15;
	public static final int TYPE_SELECT_DISLIKE_PACKAGE_COUNT	= 16;
	public static final int TYPE_SELECT_LIKE_PACKAGE_COUNT		= 17;
	public static final int TYPE_SELECT_PACKAGE_INFO_COUNT		= 18;
	public static final int TYPE_SELECT_PACKAGE_INFO				= 20;
	public static final int TYPE_SELECT_PKGINFO_LIKESTAT		= 21;
	public static final int TYPE_SELECT_FILTERWORD_INFO			= 22;
	public static final int TYPE_SELECT_FILTERPKG_INFO			= 23;
	public static final int TYPE_SELECT_DAILY_NOTI_INFO 			= 24;
	public static final int TYPE_SELECT_PACKAGE_NOTI_INFO		= 25;
	public static final int TYPE_SELECT_FILTER_PACKAGE			= 26;
	public static final int TYPE_DELETE_FILTER_APP				= 27;
	public static final int TYPE_DELETE_FILTER_KEYWORD			= 28;
	public static final int TYPE_SELECT_DISLIKE_PKG_INFO		= 29;
	public static final int TYPE_SELECT_LIKE_PKG_INFO			= 30;
	public static final int TYPE_SELECT_NOTI_INFO				= 31;

		
	public static final String SQL_INSRT_NOTIDATA				= "INSERT INTO noti_info_table (noti_package, noti_titletxt, noti_subtxt, noti_id, noti_key, noti_time, noti_status, noti_filter, noti_url_status) " +
																	"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_KEYWORDDATA			= "INSERT INTO keyword_filter_table (keyword, keyword_status) VALUES (?, ?)";
	public static final String SQL_INSERT_PACKDATA				= "INSERT INTO package_filter_table (package, pakcage_status) VALUES (?, ?)";
	
	public static final String SQL_SELECT_DAY_COUNT				= "SELECT count(*) FROM noti_info_table WHERE date(noti_time) = date(now())";
	public static final String SQL_SELECT_PRE_DAY_COUNT			= "SELECT count(*) FROM noti_info_table WHERE date(noti_time) >= date(subdate(now(), INTERVAL ? DAY) and date(noti_time) <= date(now())";
	public static final String SQL_SELECT_TOTAL_COUNT			= "SELECT count(*) FROM noti_info_table";
	public static final String SQL_SELECT_URL_COUNT				= "SELECT count(*) FROM noti_info_table WHERE noti_url_status=1";
	public static final String SQL_SELECT_DISLIKE_COUNT			= "SELECT count(*) FROM noti_info_table WHERE noti_status="+STATUS_DISLIKE +" or noti_package=?";
	public static final String SQL_SELECT_LIKE_COUNT			= "SELECT count(*) FROM noti_info_table WHERE noti_status="+STATUS_LIKE;
	public static final String SQL_SELECT_DISLIKE_PACKAGE_COUNT = "SELECT count(*) FROM noti_info_table WHERE noti_package=? and noti_status="+STATUS_DISLIKE;
	public static final String SQL_SELECT_PACKAGE_INFO_COUNT	= "SELECT count(*) FROM noti_info_table WHERE noti_package=?";
	public static final String SQL_SELECT_LIKE_PACKGAE_COUNT	= "SELECT count(*) FROM noti_info_table WHERE noti_package=? and noti_status = "+STATUS_LIKE;
	public static final String SQL_SELECT_PACKAGE_INFO			= "SELECT * FROM noti_info_table WHERE noti_package=? order by noti_time desc";
	public static final String SQL_SELECT_PKGINFO_LIKESTAT		= "SELECT * FROM noti_info_table WHERE noti_status=?";
	public static final String SQL_SELECT_FILTERWORD_INFO		= "select * from keyword_filter_table where keyword_status=?";
	public static final String SQL_SELECT_FILTERPKG_INFO		= "select * from package_filter_table";
	public static final String SQL_SELECT_FILTERPKG_PACKAGENAME = "select * from package_filter_table where package=?";
	public static final String SQL_SELECT_DISLIKE_PKG_INFO		= "select * from noti_info_table where noti_status="+STATUS_DISLIKE+" order by noti_time desc";
	public static final String SQL_SELECT_LIKE_PKG_INFO		= "select * from noti_info_table where noti_status="+STATUS_LIKE+" order by noti_time desc";
	public static final String SQL_SELECT_NOTI_INFO					= "select * from noti_info_table order by noti_time desc";

	public static final String SQL_SELECT_DAILY_NOTI_INFO = "select strftime('%Y-%m-%d', noti_time / 1000, 'unixepoch') as day, count(noti_time) as count from noti_info_table where strftime('%Y-%m-%d', noti_time / 1000, 'unixepoch') between date('now','start of day','0 month','-31 day') and date('now','start of day','0 month','0 day') group by strftime('%Y-%m-%d', noti_time / 1000, 'unixepoch') order by day desc";
	public static final String SQL_SELECT_PACKAGE_NOTI_COUNT = "select noti_package, count(noti_package) as count from noti_info_table where strftime('%Y-%m-%d', noti_time / 1000, 'unixepoch') between date('now','start of day','0 month','-31 day') and date('now','start of day','0 month','0 day') group by noti_package order by count desc";

	public static final String SQL_DELETE_FILTER_APP = "delete from package_filter_table where package=? and pakcage_status=?";
	public static final String SQL_DELETE_FILTER_KEYWORD = "delete from keyword_filter_table where keyword=? and keyword_status=?";
}