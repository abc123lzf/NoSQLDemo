package lzf.webserver.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����2:21:33
* @Description ������Ϣ�����࣬ÿ������Ӧһ��StringManagerʵ��
*/
public final class StringManager {
	
	private static final Map<Package, StringManager> map = new ConcurrentHashMap<>();
	
	private final Locale locale;
	private ResourceBundle bundle;
	
	private StringManager(Package p) {
		
		locale = Locale.getDefault();
		String bundleName = p.getName() + ".Strings";
		
		try {
			bundle = ResourceBundle.getBundle(bundleName + "_" + locale.toString(), locale);
		} catch(MissingResourceException e) {
			//Ĭ�϶�ȡStrings_zh_CN.properties�ļ�
			bundle = ResourceBundle.getBundle(bundleName + "_zh_CN");
		}
	}
	
	public String getString(String key) {
		return bundle.getString(key);
	}
	
	public String getString(final String key, final Object... args) {
		
		String value = getString(key);
		
		if(value == null) {
			value = key;
		}
		
		MessageFormat mf = new MessageFormat(value);
		mf.setLocale(locale);
		
		return mf.format(args, new StringBuffer(), null).toString();
	}
	
	public static StringManager getManager(Class<?> clazz) {
		
		synchronized(map) {
			Package p = clazz.getPackage();
			
			if(!map.containsKey(p)) {
				StringManager sm = new StringManager(p);
				map.put(p, sm);
				return sm;
			}
			
			return map.get(p);
		}
	}
}
