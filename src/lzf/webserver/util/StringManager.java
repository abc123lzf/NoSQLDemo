package lzf.webserver.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����2:21:33
* @Description ������Ϣ�����࣬ÿ������Ӧһ��StringManagerʵ��
*/
public class StringManager {
	
	private static final Map<Package, StringManager> map = new ConcurrentHashMap<>();
	
	private final Locale locale;
	private final ResourceBundle bundle;
	
	private StringManager(Package p) {
		locale = Locale.getDefault();
		String bundleName = p.getName() + ".Strings";
		bundle = ResourceBundle.getBundle(bundleName, locale);
	}
	
	public String getString(String key) {
		return bundle.getString(key);
	}
	
	public static StringManager getManager(Class<?> clazz) {
		Package p = clazz.getPackage();
		if(!map.containsKey(p)) {
			StringManager sm = new StringManager(p);
			return sm;
		}
		return map.get(p);
	}
}
