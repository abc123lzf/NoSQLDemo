package lzf.webserver.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 下午2:21:33
* @Description 错误信息管理类，每个包对应一个StringManager实例
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
			//默认读取Strings_zh_CN.properties文件
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
