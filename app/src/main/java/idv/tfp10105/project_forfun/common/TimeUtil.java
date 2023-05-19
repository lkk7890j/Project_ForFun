package idv.tfp10105.project_forfun.common;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 24小時制轉化成12小時制
     *
     * @param strDay
     */
    public static String timeFormatStr(Calendar calendar, String strDay) {
        String tempStr = "";
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 11) {
            tempStr = "下午" + " " + strDay;
        } else {
            tempStr = "上午" + " " + strDay;
        }
        return tempStr;
    }

    /**
     * 時間轉化為星期
     *
     * @param indexOfWeek
     *            星期的第幾天
     */
    public static String getWeekDayStr(int indexOfWeek) {
        String weekDayStr = "";
        switch (indexOfWeek) {
            case 1:
                weekDayStr = "星期日";
                break;
            case 2:
                weekDayStr = "星期一";
                break;
            case 3:
                weekDayStr = "星期二";
                break;
            case 4:
                weekDayStr = "星期三";
                break;
            case 5:
                weekDayStr = "星期四";
                break;
            case 6:
                weekDayStr = "星期五";
                break;
            case 7:
                weekDayStr = "星期六";
                break;
        }
        return weekDayStr;
    }

    /**
     * 將時間戳格式化，13位的轉為10位
     *
     * @param timestamp
     * @return
     */
    public static long timestampFormate(long timestamp) {
        String timestampStr = timestamp + "";
        if (timestampStr.length() == 13) {
            timestamp = timestamp / 1000;
        }
        return timestamp;
    }

    /**
     * 獲取日起時間秒差
     *
     * @param time
     *            需要格式化的時間 如"2014-07-14 19:01:45"
     * @param pattern
     *            輸入引數time的時間格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果為空則預設使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time為null，或者時間格式不匹配，輸出空字元""
     * @throws ParseException
     */
    public static long formatLongTime(String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                if (tDate != null) {
                    dTime = (today.getTime() - tDate.getTime()) / 1000;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dTime;
    }

    /**
     * 獲取日期時間戳
     *
     * @param time
     *            需要格式化的時間 如"2014-07-14 19:01:45"
     * @param pattern
     *            輸入引數time的時間格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果為空則預設使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time為null，或者時間格式不匹配，輸出空字元""
     * @throws ParseException
     */
    public static long timeStamp(String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                if (tDate != null) {
                    dTime = tDate.getTime() / 1000;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dTime;
    }

    /**
     * 時間轉化為顯示字串
     *
     * @param timeStamp
     *            單位為秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            if (calendar.before(inputTime)) {
                return getWeekDayStr(inputTime.get(Calendar.DAY_OF_WEEK));
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                int year = inputTime.get(Calendar.YEAR);
                int month = inputTime.get(Calendar.MONTH);
                int day = inputTime.get(Calendar.DAY_OF_MONTH);
                return year + "/" + month + "/" + day;
            }

        }

    }


    /**
     * 時間轉化為聊天介面顯示字串
     *
     * @param timeStamp
     *            單位為秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        String timeStr = timeStamp + "";
        if (timeStr.length() == 10) {
            timeStamp = timeStamp * 1000;
        }
        inputTime.setTimeInMillis(timeStamp);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        // if (calendar.before(inputTime)){
        // //當前時間在輸入時間之前
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy" +
        // "年"+"MM"+"月"+"dd"+"日");
        // return sdf.format(currenTimeZone);
        // }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
            return timeFormatStr(inputTime, sdf.format(currenTimeZone));
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
            return "昨天" + " " + timeFormatStr(inputTime, sdf.format(currenTimeZone));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "月" + "d" + "日");
                String temp1 = sdf.format(currenTimeZone);
                SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm");
                String temp2 = timeFormatStr(inputTime, sdf1.format(currenTimeZone));
                return temp1 + temp2;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "/" + "M" + "/" + "d" + " ");
                String temp1 = sdf.format(currenTimeZone);
                SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm");
                String temp2 = timeFormatStr(inputTime, sdf1.format(currenTimeZone));
                return temp1 + temp2;
            }

        }

    }

    /**
     * 群發使用的時間轉換
     */
    public static String multiSendTimeToStr(long timeStamp) {

        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        String timeStr = timeStamp + "";
        if (timeStr.length() == 10) {
            timeStamp = timeStamp * 1000;
        }
        inputTime.setTimeInMillis(timeStamp);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            @SuppressWarnings("unused")
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            if (calendar.before(inputTime)) {
                return getWeekDayStr(inputTime.get(Calendar.DAY_OF_WEEK));
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                if (calendar.before(inputTime)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("M" + "/" + "d" + " ");
                    String temp1 = sdf.format(currenTimeZone);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String temp2 = sdf1.format(currenTimeZone);
                    return temp1 + temp2;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "/" + "M" + "/" + "d" + " ");
                    String temp1 = sdf.format(currenTimeZone);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String temp2 = sdf1.format(currenTimeZone);
                    return temp1 + temp2;
                }
            }
        }
    }

    /**
     * 格式化時間（輸出類似於 剛剛, 4分鐘前, 一小時前, 昨天這樣的時間）
     *
     * @param time
     *            需要格式化的時間 如"2014-07-14 19:01:45"
     * @param pattern
     *            輸入引數time的時間格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果為空則預設使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time為null，或者時間格式不匹配，輸出空字元""
     */
    public static String formatDisplayTime(String time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    @SuppressWarnings("unused")
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "剛剛";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分鐘前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小時前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天  " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = multiSendTimeToStr(tDate.getTime() / 1000);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return display;
    }

    public static void main(String[] args) {
        //TODO 當前時間
        System.out.println(sf.format(new Date()));
        // TODO 字傳入時間日期字串轉換群發使用時間輸出
        System.out.println(formatDisplayTime("2017-06-30 10:34:00", null));
        // TODO 先 轉換成時間戳 轉換成對應的時間輸出格式
        long timeStamp = timeStamp("2017-06-29 10:34:00", null);
        System.out.println(multiSendTimeToStr(timeStamp));//群發使用時間
        System.out.println(getChatTimeStr(timeStamp));//時間轉化為聊天介面顯示字串
        System.out.println(getTimeStr(timeStamp));//時間轉化為顯示字串

    }

}
