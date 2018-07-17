package lzf.webserver.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��16�� ����8:48:57
* @Description ��ȡʱ�乤����
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
