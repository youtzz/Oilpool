package org.shichuangnet.jojo.oilpool.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DateUtils {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取系统时间戳
     * @return 系统时间戳
     */
    public static long getCurTimeLong(){
        return System.currentTimeMillis();
    }
    /**
     * 获取当前时间
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    public static String getCurDate(String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(new java.util.Date());
    }

    /**
     *
     * @param time  1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    public static String getDate2String(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    public static long getDate2Date(long time, String pattern) {
        String date = getDate2String(time, pattern);
        return getString2Date(date, pattern);
    }
    /**
     *
     * @param dateString 2018-11-07 13:42:03,
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 1541569323000
     */
    public static long getString2Date(String dateString, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(date).getTime();
    }

    /**
     *
     * 时间戳转化为星期
     * */
    public static String getWeek(long time) {

        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));

        int year  = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day   = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week  = cd.get(Calendar.DAY_OF_WEEK); //获取星期

        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "周日";
                break;
            case Calendar.MONDAY:
                weekString = "周一";
                break;
            case Calendar.TUESDAY:
                weekString = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "周三";
                break;
            case Calendar.THURSDAY:
                weekString = "周四";
                break;
            case Calendar.FRIDAY:
                weekString = "周五";
                break;
            default:
                weekString = "周六";
                break;

        }

        return weekString;
    }

    /**
     * 计算两个时间差的天数
     * @return 返回时间差天数
     * */
    public static long getTimeDiffDays(long startTime, long endTime) {
        if (startTime > endTime) {
            return -1;
        } else {
            long timediff = endTime - startTime;
            long remain;
            long days = timediff / 86400000;
            //计算小时数
            remain = timediff % 86400000;
            long hours = remain / 3600000;
            //计算分钟数
            remain = remain % 3600000;
            long mins = remain / 60000;
            //计算秒数
            long secs = remain % 60000 / 1000;

            return days;
        }
    }

    /**
     * 计算两个时间差
     * @return 时间差的字符形式
     * */
    public static String getTimeDiffString(long startTime, long endTime) {
        if (startTime > endTime) {
            return "";
        } else {
            long timediff = endTime - startTime;
            long remain;
            long days = timediff / 86400000;
            //计算小时数
            remain = timediff % 86400000;
            long hours = remain / 3600000;
            //计算分钟数
            remain = remain % 3600000;
            long mins = remain / 60000;
            //计算秒数
            long secs = remain % 60000 / 1000;

            if (days != 0) {
                return days + "天"+hours + "小时"+mins + "分钟" + secs + "秒";
            } else if (hours != 0) {
                return hours + "小时"+ mins + "分钟" + secs + "秒";
            } else if (mins != 0) {
                return mins + "分钟" + secs + "秒";
            } else if (secs != 0) {
                return secs + "秒" ;
            }
        }
        return "";
    }


}
