package com.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * 时间格式化
 * @author
 *
 */
public class DateFormatUtil {

    private static final String PATTERNSTR = "yyyy-MM-dd";
	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
	static Logger log = LoggerFactory.getLogger(DateFormatUtil.class);

    private DateFormatUtil(){}
    
    /**
     * 格式化为solr搜索的日期格式
     * @param date
     * @return
     */
    public static String formatSolrDate(LocalDateTime date) {
        if (date ==null) {
            return null;
        }
        return date.toInstant(ZoneOffset.UTC).toString();
    }
    
    

    /**
     * 
     * @param datestr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime string2LocalDateTime(String datestr){
        if (StringUtils.isEmpty(datestr)) {
            return null;
        }
        return LocalDateTime.parse(datestr,DateTimeFormatter.ofPattern(PATTERN));
    }
    
    /**
     * date1 是否比 date2 大
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isBiger(LocalDateTime date1,LocalDateTime date2){
        int result = date1.compareTo(date2);
        if(result >0){
            return true;
        }
        return false;
    }
    
    /**
     * date1 是否比 date2 小
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSmaller(LocalDateTime date1,LocalDateTime date2){
        int result = date1.compareTo(date2);
        if(result < 0){
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param date1
     * @param date2
     * @param date3
     * @return
     */
    public static boolean isBetween(LocalDateTime date1,LocalDateTime date2,LocalDateTime date3){
        if(isBiger(date1, date2) && isSmaller(date1, date3)){
            return true;
        }
        return false;
    }
    
    /**
     * localDateTime 转换成字符串类型
     * 
     * @param date localDateTime
     * @param pattern 例如： yyyy-MM-dd'T'HH:mm:ss 或 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2Str(LocalDateTime date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN;
        }
        String format = date.format(DateTimeFormatter.ofPattern(pattern));
//        log.debug("pattern:[{}],datestr:[{}]", pattern, format);
        return format;
    }

    /**
     * LocalDateTime TO Format LocalDateTime
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDateTime ldt2FormatLdt(LocalDateTime date, String pattern){
        String d = date2Str(date, pattern);
        return str2LocalDateTime(d,pattern);
    }
    /**
     * LocalDate TO Format LocalDate
     * @param date
     * @return
     */
    public static LocalDate ld2FormatLd(LocalDate date){
        return LocalDate.of(date.getYear(),date.getMonth(),1);
    }

    /**
     * localDateTime 转换成为 yyyy-MM-dd HH:mm:ss 格式日期字符串
     * 
     * @param date
     *            localDateTime
     * @return
     */
    public static String localDateTime2Str(LocalDateTime date) {
        return date2Str(date, null);
    }
    /**
     * localDate 转换成字符串类型
     * 
     * @param date localDate
     * @param pattern 例如： yyyy-MM-dd
     * @return
     */
    public static String localDate2Str(LocalDate date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERNSTR;
        }
        String format = date.format(DateTimeFormatter.ofPattern(pattern));
//        log.debug("pattern:[{}],datestr:[{}]", pattern, format);
        return format;
    }
    
    /**
     * localDate 转换成为 yyyy-MM-dd 格式日期字符串
     * 
     * @param date
     *            localDate
     * @return
     */
    public static String localDate2Str(LocalDate date) {
        return localDate2Str(date, null);
    }

    /**
     * 日期字符串转换成 localDateTime
     * 
     * @param datestr
     * @param pattern
     *            默认 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime str2LocalDateTime(String datestr, String pattern) {
        if (StringUtils.isEmpty(datestr)) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN;
        }
//        log.debug("datestr:[{}],pattern:[{}]", datestr, pattern);
        return LocalDateTime.parse(datestr,
                DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期字符串转换成 localDateTime
     * 
     * @param datestr
     *            默认 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime str2LocalDateTime(String datestr) {
        return str2LocalDateTime(datestr, null);
    }
    
    /**
     * 日期字符串转换成 localDate
     * @param datestr 
     * @param pattern  默认 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDate str2LocalDate(String datestr, String pattern){
        if (StringUtils.isEmpty(datestr)) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERNSTR;
        }
//        log.debug("datestr:[{}],pattern:[{}]", datestr, pattern);
        return LocalDate.parse(datestr,DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 日期字符串转换成 localDate
     * @param datestr 默认 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDate str2LocalDate(String datestr){
        return str2LocalDate(datestr,null);
    }

    /**
     * 时间戳转换为localDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime getDateTimeFromTimestamp(long timestamp) {
        if (timestamp == 0)
            return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 时间戳转换为localDate
     * @param timestamp
     * @return
     */
    public static LocalDate getDateFromTimestamp(long timestamp) {
        LocalDateTime date = getDateTimeFromTimestamp(timestamp);
        return date == null ? null : date.toLocalDate();
    }

    /**
     * 获取传入日期为当前年得第几周
     * @param date
     * @return
     */
    public static int getWeekOfYear(LocalDate date){
        return date.get(WeekFields.of(DayOfWeek.MONDAY,1).weekOfYear());
    }
    
    /**
     * 获取年周
     * @param date
     * @return
     */
    public static int getYearWeek(LocalDate date) {
    	int week = date.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfYear());
		Integer year = date.getYear();
		String yearWeek = week < 10 ? year + "0" + week : year + "" + week;
		return Integer.parseInt(yearWeek);
    }

    /**
     * 获取日期的时间戳
     * @param localDateTime
     * @return eg:1468484873640   时间戳
     */
    public static long getLongFromLocalDateTime(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return 0l;
        } else {
            Instant instant = localDateTime.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("CTT"))).toInstant();
            return  instant.toEpochMilli();
        }
    }
/**
     * 获取日期的时间戳
     * @param localDate
     * @return eg:1468484873640   时间戳
     */
    public static long getLongFromLocalDate(LocalDate localDate) {
        if(localDate == null) {
            return 0l;
        } else {
            LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
            Instant instant = localDateTime.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("CTT"))).toInstant();
            return  instant.toEpochMilli();
        }
    }

    /**
     * 获取所在年 第一天   时间戳
     * @param date
     * @return
     */
    public static LocalDateTime getFirstDayOfYear(LocalDateTime date) {
        if(date == null) {
            return null;
        } else {
            return LocalDate.of(date.getYear(),1,1).atStartOfDay();
        }
    }

    /**
     * 获取所在年 最后一天   时间戳
     * @param date
     * @return
     */
    public static LocalDateTime getLastDayOfYear(LocalDateTime date) {
        if(date == null) {
            return null;
        } else {
            return  LocalDate.of(date.getYear(),1,1).atStartOfDay().plusYears(1).minusNanos(1);
        }
    }

    /**
     * 获取月第一天
     * @param year
     * @param month
     * @return
     */
    public static LocalDateTime getFirstDayOfMonth(int year, int month) {
        return LocalDate.of(year,month,1).atStartOfDay();
    }

    /**
     * 获取月最后一天
     * @param year
     * @param month
     * @return
     */
    public static LocalDateTime getLastDayOfMonth(int year, int month) {
        return LocalDate.of(year,month,1).atStartOfDay().plusMonths(1).minusNanos(1);
    }

    /**
     * 计算两个日期相差多少天
     * @param sdate
     * @param edate
     * @return
     */
    public static int daysBetween(LocalDate sdate, LocalDate edate) {
        int count = 0;
        while (sdate.isBefore(edate) || sdate.equals(edate)) {
            sdate = sdate.plusDays(1);
            count++;
        }
        return count;
    }

    /**
     * java.util.Date 类型 转换为java.time.LocalDateTime类型
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date){
        if (date == null){
            return null;
        }
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant,ZoneOffset.ofHours(8));
    }

    /**
     * java.util.Date 类型 转换为java.time.LocalDate类型
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date){
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        if (localDateTime == null){
            return null;
        }
        return LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(),
                localDateTime.getDayOfMonth());
    }


    public static String dateToStr(Date date){
        if (date == null){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static LocalDateTime format(LocalDateTime date){
        return format(date,"yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static LocalDateTime format(LocalDateTime date,String pattern){
        if (date == null){
            return null;
        }
        DateTimeFormatter patterns = DateTimeFormatter.ofPattern(pattern);
        String format = date.format(patterns);
        return LocalDateTime.parse(format,patterns);
    }




}
