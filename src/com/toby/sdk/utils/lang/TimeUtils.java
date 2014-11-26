package com.toby.sdk.utils.lang;
import android.content.Context;
import android.content.res.Resources;
import com.toby.sdk.utils.android.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

	/**
	 * 根据时间毫秒数格式化时间
	 *
	 * @param userId
	 * @return
	 */
	public static String formatItemTime(Context context,long time) {
		String strTime = "";
		Date endday = new Date(time);
		Calendar end = Calendar.getInstance();
		end.setTime(endday);
		int day =getDaysBetween(Calendar.getInstance(), end);
		switch (day) {
			case 0:
				strTime = formatMsShortTime(time);
				break;
			case 1:
//				strTime = context.getResources().getString(R.string.yesterday);
				break;
			case 2:
//				strTime = context.getResources().getString(R.string.the_day_before_yesterday);
				break;
			default:
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				strTime = df.format(endday);
				break;
		}
		return strTime;
	}

	public static String formatMsTime(long lt) {
		Date msgTime = new Date(lt);

		return msgTime.toString().substring(11, 19);
	}

	public static String formatMsShortTime(long lt) {
		Date msgTime = new Date(lt);

		return msgTime.toString().substring(11, 16);
	}

	public static String formatMsDate(long lt) {
		Date msgTime = new Date(lt);

		int year = msgTime.getYear() + 1900;
		int month = msgTime.getMonth();
		int day = msgTime.getDate();

		return year + "-" + (month + 1) + "-" + (day);
	}

	public static String formatMsDate(long lt, String separator) {
		Date msgTime = new Date(lt);

		int year = msgTime.getYear() + 1900;
		int month = msgTime.getMonth();
		int day = msgTime.getDate();

		return year + separator + (month + 1) + separator + (day);
	}
	
	public static String formatDate(long lt) {
		Date msgTime = new Date(lt);

		int year = msgTime.getYear() + 1900;
		int month = msgTime.getMonth() + 1;
		int day = msgTime.getDate();
		int h = msgTime.getHours();
		int m = msgTime.getMinutes();
		int s = msgTime.getSeconds();
		
		return year + "-" + (month) + "-" + (day) + " " + h + ":" + m + ":" + s;
	}
	
	/**
	 * 计算两个日期相隔的天数.
	 * 
	 * @param d1
	 * @param d2
	 * @return 返回两个日期相隔的天数,如果是同一天返回0.
	 */
	public static int getDaysBetween(Calendar d1,
			Calendar d2) {
		if (d1.after(d2)) {
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(Calendar.DAY_OF_YEAR)
				- d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}
	
	public static String getTimeFormNow(String since){
	    long formatSince = Long.parseLong(Tools.handleTime(since));
	    return getTimeFromNow(formatSince);
	}
	/**
	 * 返回指定时间到当前时间所经过的时间，并格式化其显示
	 * 
	 * @param since
	 * @return
	 */
	public static String getTimeFromNow(long since) {
	    
		long offset = System.currentTimeMillis() - since;
		if (offset < 0) {
			// System.out.println("offset=" + offset);
			return "刚刚";
		}
		long second = offset / 1000;
		if (second < 60) {
			// if (second == 0) {
			// second = 1;
			// }
			// return second + "秒钟前";
			return "刚刚";
		}
		long minutes = second / 60;
		if (minutes < 60) {
			return minutes + "分钟前";
		}
		long hours = minutes / 60;
		if (hours < 24) {
			return hours + "小时前";
		}
		long days = hours / 24;
		if (days < 30) {
			return days + "天前";
		}
		long months = days / 30;
		if (months < 12) {
			return months + "个月前";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(since);
	}
	
	
	public static boolean isInTheSameYear(long src,long dest){
		Calendar last = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		last.setTimeInMillis(src);
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		current.setTimeInMillis(dest);
		int lastYear = last.get(Calendar.YEAR);
		int currentYear = current.get(Calendar.YEAR);
		return (lastYear==currentYear);
	}
	
	
	/**
	 * 判断两个时间是否在同一天,用于导航获取域名对应IP及端口时使用
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean isInTheSameDay(long src,long dest){
		Calendar last = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		last.setTimeInMillis(src);
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		current.setTimeInMillis(dest);
		int lastDay = last.get(Calendar.DAY_OF_YEAR);
		int currentDay = current.get(Calendar.DAY_OF_YEAR);
		return (lastDay==currentDay);
	}

	public static boolean isBeforeYesterdayDate(long times) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if (times < c.getTimeInMillis()){
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/**
	 * HH:mm 上午/下午  
	 * @return
	 */
	public static String hh_mm_Format(long times,Resources mResouces){
		int afternootResId = 1;// R.string.utils_afternoon;
		int morningResId = 2;//R.string.utils_morning;
		
		String formatStr = "hh:mm";
		Date date = new Date(times);
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
		String result = dateFormat.format(date);
		/*if(date.getHours()>=12){
			result = result+" "+mResouces.getString(afternootResId);
		}else{
			result = result+" "+mResouces.getString(morningResId);
		}*/

		if(date.getHours()>=12){
			result = result+" "+"afternoon";
		}else{
			result = result+" "+"morning";
		}
		return result;
	}
	
	public static String hh_mm_yesterday_Format(long times,Resources mResouces){
		int yesterResId = 3;//R.string.utils_yesterday;
		String formatStr = "HH:mm";
		Date date = new Date(times);
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
		String result = dateFormat.format(date);
		//result = result+" "+mResouces.getString("");
		result = result+" "+"yesterday";
		return result;
	}
	
	
	/**
	 * 08-07 12:36
	 * @return
	 */
	public static String MM_DD_HH_mm_Format(long times){
		String formatString = "MM-dd HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}
	
	public static String yyyy_MM_DD_HH_mm_format(long times){
		String formatString = "yyyy-MM-dd HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}
	
	public static String yyyy_MM_DD_format(long times){
		String formatString = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}
	public static String MM_DD_format(long times){
		String formatString = "MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}
	
	
	
	
	public static String formatCloudDate(long lt) {
		String year = "";
		String month = "";
		String day = "";
		String h = "";
		String m = "";
		String s = "";
		try {
			String data = String.valueOf(lt);
			year = data.substring(0, 4);
			month = data.substring(4, 6);
			day = data.substring(6, 8);
			h = data.substring(8, 10);
			m = data.substring(10, 12);
			s = data.substring(12, 14);
		}
		catch (Exception e) {
			return "";
		}
		return year + "-" + (month) + "-" + (day) + " " + h + ":" + m + ":" + s;
	}
	
	public static String yyyy_MM_DD_Format(long times){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(times));
	}
	
	/**
	 * 获取date零晨时间
	 * @param date
	 * @return
	 */
	public static long getDateTimes(Date date){
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return date.getTime();
	}
	
	
}
