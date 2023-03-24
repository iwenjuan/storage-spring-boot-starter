package cn.iwenjuan.storage.utils;

import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/1/31 11:50
 */
public class DateUtils {

    public static final Pattern REGEX_NORM = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}(\\s\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?(.\\d{1,6})?");

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    public static Date today() {
        return beginOfDay(now());
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long current() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间秒数
     *
     * @return
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * 获取日历对象
     *
     * @param date
     * @return
     */
    public static Calendar calendar(Date date) {
        Assert.notNull(date, "date can not be null !");
        return calendar(date.getTime());
    }

    /**
     * 获取日历对象
     *
     * @param millis
     * @return
     */
    public static Calendar calendar(long millis) {
        return calendar(millis, TimeZone.getDefault());
    }

    /**
     * 获取日历对象
     *
     * @param millis
     * @param timeZone
     * @return
     */
    public static Calendar calendar(long millis, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static int getField(Date date, DateField dateField) {
        Calendar calendar = calendar(date);
        return calendar.get(dateField.value);
    }

    public static Date setField(Date date, DateField dateField, int value) {
        return setField(calendar(date), dateField, value);
    }

    public static Date setField(Calendar calendar, DateField dateField, int value) {
        calendar.set(dateField.value, value);
        return calendar.getTime();
    }

    public static int year(Date date) {
        return getField(date, DateField.YEAR);
    }

    public static int month(Date date) {
        return getField(date, DateField.MONTH);
    }

    public static int weekOfYear(Date date) {
        return getField(date, DateField.WEEK_OF_YEAR);
    }

    public static int weekOfMonth(Date date) {
        return getField(date, DateField.WEEK_OF_MONTH);
    }

    public static int dayOfMonth(Date date) {
        return getField(date, DateField.DAY_OF_MONTH);
    }

    public static int dayOfYear(Date date) {
        return getField(date, DateField.DAY_OF_YEAR);
    }

    public static int dayOfWeek(Date date) {
        return getField(date, DateField.DAY_OF_WEEK);
    }

    public static int dayOfWeekInMonth(Date date) {
        return getField(date, DateField.DAY_OF_WEEK_IN_MONTH);
    }

    public static int hour(Date date) {
        return getField(date, DateField.HOUR);
    }

    public static int hourOfDay(Date date) {
        return getField(date, DateField.HOUR_OF_DAY);
    }

    public static int minute(Date date) {
        return getField(date, DateField.MINUTE);
    }

    public static int second(Date date) {
        return getField(date, DateField.SECOND);
    }

    public static int millisecond(Date date) {
        return getField(date, DateField.MILLISECOND);
    }

    public static int thisYear() {
        return year(now());
    }

    public static int thisMonth() {
        return month(now());
    }

    public static int thisWeekOfYear() {
        return weekOfYear(now());
    }

    public static int thisWeekOfMonth() {
        return weekOfMonth(now());
    }

    public static int thisDayOfMonth() {
        return dayOfMonth(now());
    }

    public static int thisDayOfWeek() {
        return dayOfWeek(now());
    }

    public static int thisHour() {
        return hour(now());
    }

    public static int thisHourOfDay() {
        return hourOfDay(now());
    }

    public static int thisMinute() {
        return minute(now());
    }

    public static int thisSecond() {
        return second(now());
    }

    public static int thisMillisecond() {
        return millisecond(now());
    }

    /**
     * 判断是否是上午
     *
     * @param date
     * @return
     */
    public static boolean isAM(Date date) {
        return 0 == getField(date, DateField.AM_PM);
    }

    /**
     * 判断是否是下午
     *
     * @param date
     * @return
     */
    public static boolean isPM(Date date) {
        return 1 == getField(date, DateField.AM_PM);
    }

    /**
     * 判断是否是周末（周六或周日）
     *
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {
        int dayOfWeek = dayOfWeek(date);
        return 7 == dayOfWeek || 1 == dayOfWeek;
    }

    /**
     * 判断是否是闰年
     *
     * @param date
     * @return
     */
    public static boolean isLeapYear(Date date) {
        int year = year(date);
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, DateFormat format) {
        return format(date, format.getFormat());
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        Assert.notNull(date, "date can not be null !");
        Assert.notNull(format, "format can not be null !");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 解析日期
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr) throws ParseException {
        Assert.notNull(dateStr, "date string can not be null !");
        DateParsePattern dateParsePattern = DateParsePattern.of(dateStr);
        Assert.notNull(dateParsePattern, String.format("No format fit for date String [%s] !", dateStr));
        return parse(dateStr, dateParsePattern.getFormat().getFormat());
    }

    /**
     * 解析日期
     *
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateStr, String format) throws ParseException {
        Assert.notNull(dateStr, "date string can not be null !");
        Assert.notNull(format, "format can not be null !");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(dateStr);
    }

    public static int getBeginValue(Calendar calendar, DateField dateField) {
        return calendar.getActualMinimum(dateField.value);
    }

    public static int getEndValue(Calendar calendar, DateField dateField) {
        return calendar.getActualMaximum(dateField.value);
    }

    /**
     * 将时间设置为当前秒的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfSecond(Date date) {
        Calendar calendar = calendar(date);
        return setField(calendar, DateField.MILLISECOND, getBeginValue(calendar, DateField.MILLISECOND));
    }

    /**
     * 将时间设置为当前秒的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfSecond(Calendar calendar) {
        setField(calendar, DateField.MILLISECOND, getBeginValue(calendar, DateField.MILLISECOND));
        return calendar;
    }

    /**
     * 将时间设置为当前秒的结束
     *
     * @param date
     * @return
     */
    public static Date endOfSecond(Date date) {
        Calendar calendar = calendar(date);
        return setField(calendar, DateField.MILLISECOND, getEndValue(calendar, DateField.MILLISECOND));
    }

    /**
     * 将时间设置为当前秒的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfSecond(Calendar calendar) {
        setField(calendar, DateField.MILLISECOND, getEndValue(calendar, DateField.MILLISECOND));
        return calendar;
    }

    /**
     * 将时间设置为当前分钟的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfMinute(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.SECOND, getBeginValue(calendar, DateField.SECOND));
        return beginOfSecond(calendar).getTime();
    }

    /**
     * 将时间设置为当前分钟的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfMinute(Calendar calendar) {
        setField(calendar, DateField.SECOND, getBeginValue(calendar, DateField.SECOND));
        return beginOfSecond(calendar);
    }

    /**
     * 将时间设置为当前分钟的结束
     *
     * @param date
     * @return
     */
    public static Date endOfMinute(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.SECOND, getEndValue(calendar, DateField.SECOND));
        return endOfSecond(calendar).getTime();
    }

    /**
     * 将时间设置为当前分钟的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfMinute(Calendar calendar) {
        setField(calendar, DateField.SECOND, getEndValue(calendar, DateField.SECOND));
        return endOfSecond(calendar);
    }

    /**
     * 将时间设置为当前小时的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfHour(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.MINUTE, getBeginValue(calendar, DateField.MINUTE));
        return beginOfMinute(calendar).getTime();
    }

    /**
     * 将时间设置为当前小时的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfHour(Calendar calendar) {
        setField(calendar, DateField.MINUTE, getBeginValue(calendar, DateField.MINUTE));
        return beginOfMinute(calendar);
    }

    /**
     * 将时间设置为当前小时的结束
     *
     * @param date
     * @return
     */
    public static Date endOfHour(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.MINUTE, getEndValue(calendar, DateField.MINUTE));
        return endOfMinute(calendar).getTime();
    }

    /**
     * 将时间设置为当前小时的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfHour(Calendar calendar) {
        setField(calendar, DateField.MINUTE, getEndValue(calendar, DateField.MINUTE));
        return endOfMinute(calendar);
    }

    /**
     * 将时间设置为当前天的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfDay(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.HOUR_OF_DAY, getBeginValue(calendar, DateField.HOUR_OF_DAY));
        return beginOfHour(calendar).getTime();
    }

    /**
     * 将时间设置为当前天的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfDay(Calendar calendar) {
        setField(calendar, DateField.HOUR_OF_DAY, getBeginValue(calendar, DateField.HOUR_OF_DAY));
        return beginOfHour(calendar);
    }

    /**
     * 将时间设置为当前天的结束
     *
     * @param date
     * @return
     */
    public static Date endOfDay(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.HOUR_OF_DAY, getEndValue(calendar, DateField.HOUR_OF_DAY));
        return endOfHour(calendar).getTime();
    }

    /**
     * 将时间设置为当前天的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfDay(Calendar calendar) {
        setField(calendar, DateField.HOUR_OF_DAY, getEndValue(calendar, DateField.HOUR_OF_DAY));
        return endOfHour(calendar);
    }

    /**
     * 将时间设置为当前月的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfMonth(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.DAY_OF_MONTH, getBeginValue(calendar, DateField.DAY_OF_MONTH));
        return beginOfDay(calendar).getTime();
    }

    /**
     * 将时间设置为当前月的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfMonth(Calendar calendar) {
        setField(calendar, DateField.DAY_OF_MONTH, getBeginValue(calendar, DateField.DAY_OF_MONTH));
        return beginOfDay(calendar);
    }

    /**
     * 将时间设置为当前月的结束
     *
     * @param date
     * @return
     */
    public static Date endOfMonth(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.DAY_OF_MONTH, getEndValue(calendar, DateField.DAY_OF_MONTH));
        return endOfDay(calendar).getTime();
    }

    /**
     * 将时间设置为当前月的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfMonth(Calendar calendar) {
        setField(calendar, DateField.DAY_OF_MONTH, getEndValue(calendar, DateField.DAY_OF_MONTH));
        return endOfDay(calendar);
    }

    /**
     * 将时间设置为当前年的开始
     *
     * @param date
     * @return
     */
    public static Date beginOfYear(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.MONTH, getBeginValue(calendar, DateField.MONTH));
        return beginOfMonth(calendar).getTime();
    }

    /**
     * 将时间设置为当前年的开始
     *
     * @param calendar
     * @return
     */
    public static Calendar beginOfYear(Calendar calendar) {
        setField(calendar, DateField.MONTH, getBeginValue(calendar, DateField.MONTH));
        return beginOfMonth(calendar);
    }

    /**
     * 将时间设置为当前年的结束
     *
     * @param date
     * @return
     */
    public static Date endOfYear(Date date) {
        Calendar calendar = calendar(date);
        setField(calendar, DateField.MONTH, getEndValue(calendar, DateField.MONTH));
        return endOfMonth(calendar).getTime();
    }

    /**
     * 将时间设置为当前年的结束
     *
     * @param calendar
     * @return
     */
    public static Calendar endOfYear(Calendar calendar) {
        setField(calendar, DateField.MONTH, getEndValue(calendar, DateField.MONTH));
        return endOfMonth(calendar);
    }

    /**
     * 对日期进行加减
     *
     * @param date
     * @param dateField
     * @param offset
     * @return
     */
    public static Date offset(Date date, DateField dateField, int offset) {
        Calendar calendar = calendar(date);
        calendar.add(dateField.value, offset);
        return calendar.getTime();
    }

    /**
     * 对日期的毫秒数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetMillisecond(Date date, int offset) {
        return offset(date, DateField.MILLISECOND, offset);
    }

    /**
     * 对日期的秒数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetSecond(Date date, int offset) {
        return offset(date, DateField.SECOND, offset);
    }

    /**
     * 对日期的分钟数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetMinute(Date date, int offset) {
        return offset(date, DateField.MINUTE, offset);
    }

    /**
     * 对日期的小时数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetHour(Date date, int offset) {
        return offset(date, DateField.HOUR_OF_DAY, offset);
    }

    /**
     * 对日期的天数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }

    /**
     * 对日期的周数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetWeek(Date date, int offset) {
        return offset(date, DateField.WEEK_OF_YEAR, offset);
    }

    /**
     * 对日期的月数进行加减
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date offsetMonth(Date date, int offset) {
        return offset(date, DateField.MONTH, offset);
    }

    /**
     * 计算两个日期相差的毫秒数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long between(Date begin, Date end) {
        if (begin.after(end)) {
            return begin.getTime() - end.getTime();
        } else {
            return end.getTime() - begin.getTime();
        }
    }

    /**
     * 计算两个日期相差的秒数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long betweenSecond(Date begin, Date end) {
        return between(begin, end) / 1000;
    }

    /**
     * 计算两个日期相差的分钟数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long betweenMinute(Date begin, Date end) {
        return between(begin, end) / (1000 * 60);
    }

    /**
     * 计算两个日期相差的小时数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long betweenHour(Date begin, Date end) {
        return between(begin, end) / (1000 * 60 * 60);
    }

    /**
     * 计算两个日期相差的天数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long betweenDay(Date begin, Date end) {
        return between(begin, end) / (1000 * 60 * 60 * 24);
    }

    /**
     * 计算两个日期相差的月份
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @param reset 是否重置到月初
     * @return
     */
    public static long betweenMonth(Date begin, Date end, boolean reset) {
        Date beginDate;
        Date endDate;
        if (begin.after(end)) {
            beginDate = end;
            endDate = begin;
        } else {
            beginDate = begin;
            endDate = end;
        }
        if (reset) {
            beginDate = beginOfMonth(beginDate);
            endDate = beginOfMonth(endDate);
        }
        Calendar beginCal = calendar(beginDate);
        Calendar endCal = calendar(endDate);
        int betweenYear = endCal.get(DateField.YEAR.value) - beginCal.get(DateField.YEAR.value);
        int betweenMonthOfYear = endCal.get(DateField.MONTH.value) - beginCal.get(DateField.MONTH.value);
        int result = betweenYear * 12 + betweenMonthOfYear;
        if (!reset) {
            // 不重置日期，对跨月情况进行处理，例如：begin = 2023-01-31，end = 2023-02-01，此时计算出的结果为1，但实际两个日期相差不到1个月
            endCal.set(DateField.YEAR.value, beginCal.get(DateField.YEAR.value));
            endCal.set(DateField.MONTH.value, beginCal.get(DateField.MONTH.value));
            long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
            if (between < 0L) {
                return result - 1;
            }
        }
        return result;
    }

    /**
     * 计算两个日期相差的年份
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @param reset 是否重置到月初
     * @return
     */
    public static long betweenYear(Date begin, Date end, boolean reset) {
        Date beginDate;
        Date endDate;
        if (begin.after(end)) {
            beginDate = end;
            endDate = begin;
        } else {
            beginDate = begin;
            endDate = end;
        }
        if (reset) {
            beginDate = beginOfYear(beginDate);
            endDate = beginOfYear(endDate);
        }
        Calendar beginCal = calendar(beginDate);
        Calendar endCal = calendar(endDate);
        int result = endCal.get(DateField.YEAR.value) - beginCal.get(DateField.YEAR.value);
        if (!reset) {
            // 二月特殊处理
            int beginMonth = beginCal.get(DateField.MONTH.value);
            int endMonth = endCal.get(DateField.MONTH.value);
            int beginDay = beginCal.get(DateField.DAY_OF_MONTH.value);
            int endDay = endCal.get(DateField.DAY_OF_MONTH.value);
            if (1 == beginMonth && beginDay == getEndValue(beginCal, DateField.DAY_OF_MONTH)
                    && 1 == endMonth && endDay == getEndValue(endCal, DateField.DAY_OF_MONTH)) {
                beginCal.set(DateField.DAY_OF_MONTH.value, 1);
                endCal.set(DateField.DAY_OF_MONTH.value, 1);
            }
            endCal.set(DateField.YEAR.value, beginCal.get(DateField.YEAR.value));
            long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
            if (between < 0L) {
                return result - 1;
            }
        }
        return result;
    }

    /**
     * 日期格式化枚举
     */
    public enum DateFormat {

        NORM_YEAR_PATTERN("yyyy"),
        NORM_MONTH_PATTERN("yyyy-MM"),
        NORM_DATE_PATTERN("yyyy-MM-dd"),
        NORM_TIME_PATTERN("HH:mm:ss"),
        NORM_DATETIME_MINUTE_PATTERN("yyyy-MM-dd HH:mm"),
        NORM_DATETIME_PATTERN("yyyy-MM-dd HH:mm:ss"),
        NORM_DATETIME_MS_PATTERN("yyyy-MM-dd HH:mm:ss.SSS"),
        ISO8601_PATTERN("yyyy-MM-dd HH:mm:ss,SSS"),
        CHINESE_DATE_PATTERN("yyyy年MM月dd日"),
        CHINESE_DATE_TIME_PATTERN("yyyy年MM月dd日HH时mm分ss秒"),
        PURE_DATE_PATTERN("yyyyMMdd"),
        PURE_TIME_PATTERN("HHmmss"),
        PURE_DATETIME_PATTERN("yyyyMMddHHmmss"),
        PURE_DATETIME_MS_PATTERN("yyyyMMddHHmmssSSS"),
        UTC_SIMPLE_MINUTE_PATTERN("yyyy-MM-dd'T'HH:mm"),
        UTC_SIMPLE_PATTERN("yyyy-MM-dd'T'HH:mm:ss"),
        UTC_SIMPLE_MS_PATTERN("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        UTC_PATTERN("yyyy-MM-dd'T'HH:mm:ss'Z'");

        private String format;

        DateFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    /**
     * 日期解析枚举
     */
    public enum DateParsePattern {

        NORM_YEAR_PATTERN(DateFormat.NORM_YEAR_PATTERN, Pattern.compile("\\d{4}")),
        NORM_MONTH_PATTERN(DateFormat.NORM_MONTH_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}")),
        NORM_DATE_PATTERN(DateFormat.NORM_DATE_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}")),
        NORM_TIME_PATTERN(DateFormat.NORM_TIME_PATTERN, Pattern.compile("\\d{1,2}:\\d{1,2}:\\d{1,2}")),
        NORM_DATETIME_MINUTE_PATTERN(DateFormat.NORM_DATETIME_MINUTE_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}")),
        NORM_DATETIME_PATTERN(DateFormat.NORM_DATETIME_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")),
        NORM_DATETIME_MS_PATTERN(DateFormat.NORM_DATETIME_MS_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?")),
        ISO8601_PATTERN(DateFormat.ISO8601_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}(,\\d{1,6})?")),
        CHINESE_DATE_PATTERN(DateFormat.CHINESE_DATE_PATTERN, Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日")),
        CHINESE_DATE_TIME_PATTERN(DateFormat.CHINESE_DATE_TIME_PATTERN, Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒")),
        PURE_DATE_PATTERN(DateFormat.PURE_DATE_PATTERN, Pattern.compile("\\d{4}\\d{2}\\d{2}")),
        PURE_TIME_PATTERN(DateFormat.PURE_TIME_PATTERN, Pattern.compile("\\d{2}\\d{2}\\d{2}")),
        PURE_DATETIME_PATTERN(DateFormat.PURE_DATETIME_PATTERN, Pattern.compile("\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}")),
        PURE_DATETIME_MS_PATTERN(DateFormat.PURE_DATETIME_MS_PATTERN, Pattern.compile("\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}\\d{3}")),
        UTC_SIMPLE_MINUTE_PATTERN(DateFormat.UTC_SIMPLE_MINUTE_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}")),
        UTC_SIMPLE_PATTERN(DateFormat.UTC_SIMPLE_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}")),
        UTC_SIMPLE_MS_PATTERN(DateFormat.UTC_SIMPLE_MS_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}(.\\d{1,6})?")),
        UTC_PATTERN(DateFormat.UTC_PATTERN, Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}Z"));

        private DateFormat format;

        private Pattern pattern;

        DateParsePattern(DateFormat format, Pattern pattern) {
            this.format = format;
            this.pattern = pattern;
        }

        public static DateParsePattern of(String dateStr) {
            for (DateParsePattern dateParsePattern : values()) {
                if (dateParsePattern.pattern.matcher(dateStr).matches()) {
                    return dateParsePattern;
                }
            }
            return null;
        }

        public DateFormat getFormat() {
            return format;
        }
    }

    public enum DateField {

        ERA(Calendar.ERA),
        YEAR(Calendar.YEAR),
        MONTH(Calendar.MONTH),
        WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),
        WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
        DAY_OF_MONTH(Calendar.DAY_OF_MONTH),
        DAY_OF_YEAR(Calendar.DAY_OF_YEAR),
        DAY_OF_WEEK(Calendar.DAY_OF_WEEK),
        DAY_OF_WEEK_IN_MONTH(Calendar.DAY_OF_WEEK_IN_MONTH),
        AM_PM(Calendar.AM_PM),
        HOUR(Calendar.HOUR),
        HOUR_OF_DAY(Calendar.HOUR_OF_DAY),
        MINUTE(Calendar.MINUTE),
        SECOND(Calendar.SECOND),
        MILLISECOND(Calendar.MILLISECOND);

        private int value;

        DateField(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static DateField of(int calendarPartIntValue) {
            switch (calendarPartIntValue) {
                case 0:
                    return ERA;
                case 1:
                    return YEAR;
                case 2:
                    return MONTH;
                case 3:
                    return WEEK_OF_YEAR;
                case 4:
                    return WEEK_OF_MONTH;
                case 5:
                    return DAY_OF_MONTH;
                case 6:
                    return DAY_OF_YEAR;
                case 7:
                    return DAY_OF_WEEK;
                case 8:
                    return DAY_OF_WEEK_IN_MONTH;
                case 9:
                    return AM_PM;
                case 10:
                    return HOUR;
                case 11:
                    return HOUR_OF_DAY;
                case 12:
                    return MINUTE;
                case 13:
                    return SECOND;
                case 14:
                    return MILLISECOND;
                default:
                    return null;
            }
        }
    }
}
