package com.mobile.timeselectlibrary.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final String DEFAULT_TEMPLATE_DAY = "yyyy-MM-dd";
	public static final String DEFAULT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";

	private DateUtils() {
	}

	/**
	 * -日期控件 * / /** 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 */

	public static Date parse(String strDate, String pattern) {
		if (TextUtils.isEmpty(strDate)) {
			return null;
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			return df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用用户格式格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return
	 */

	public static String format(Date date, String pattern) {
		String returnValue = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return (returnValue);
	}

	/*
     * 将时间戳转换为时间
     */
	public static String stampToDate(String strDate, String pattern){
		if (TextUtils.isEmpty(strDate)) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Long lt = new Long(strDate+"000");
		return simpleDateFormat.format(lt);
	}

}