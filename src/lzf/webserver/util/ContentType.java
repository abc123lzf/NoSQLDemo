package lzf.webserver.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��22�� ����4:34:33
* @Description ��˵��
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
