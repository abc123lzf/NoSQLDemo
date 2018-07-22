package lzf.webserver.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月22日 下午4:34:33
* @Description 类说明
*/
public final class ContentType {

	private static final Map<String, String> content = new ConcurrentHashMap<String, String>();
	
	public static String getContentBySuffix(String suffix) {
		return content.get(suffix);
	}
	
	static {
		content.put("", "application/octet-stream");
		content.put("pdf", "application/pdf");
		content.put("ai", "application/postscript");
		content.put("xml", "text/xml");
		content.put("json", "application/json");
		content.put("js", "application/javascript");
		content.put("html", "text/html");
		content.put("jpg", "image/jpg");
		content.put("jpeg", "image/jpeg");
		content.put("css", "text/css");
		content.put("psd", "image/vnd.adobe.photoshop");
		content.put("png", "image/png");
	}
	
	private ContentType() {
	}
}
