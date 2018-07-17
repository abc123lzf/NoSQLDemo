package lzf.webserver.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月16日 下午8:48:57
* @Description 获取时间工具类
*/
public final class TimeUtil {

	private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	public static Timestamp getTimestamp() {
		Date date = new Date();       
		Timestamp dateStamp = new Timestamp(date.getTime());
		return dateStamp;
	}
	
	public static String getLogTimeString() {
		Date date = new Date();
		return LOG_TIME_FORMAT.format(date);
	}
		
	private TimeUtil() {
	}
}
